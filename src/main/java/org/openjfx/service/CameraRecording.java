package org.openjfx.service;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import org.openjfx.domain.Audio;
import org.openjfx.domain.Video;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class CameraRecording {

    //录屏线程池 screenTimer (定时执行)
    private ScheduledThreadPoolExecutor screenTimer;
    //    音频设置线程池
    private ScheduledThreadPoolExecutor exec;
    private long startTime = 0;
    private long videoTS = 0;

    //    摄像头设备指标
    private int WEBCAM_DEVICE_INDEX=0;
    //    音频设备指标
    private int AUDIO_DEVICE_INDEX=4;

    //    录制分辨率
    private int captureWidth=640;
    private int captureHeight=480;

    //  帧率
    private int frameRate=5;
    //  摄像头显示
    CanvasFrame cFrame;
    //    摄像头的录制类
    OpenCVFrameGrabber grabber;
    //    录像的实体类
    FFmpegFrameRecorder recorder;
    //  音频设置类
    AudioFormat audioFormat;
//    音频类选型
    TargetDataLine line;
    DataLine.Info dataLineInfo;


    public CameraRecording(Video video, Audio audio) {
//        设置分辨率
        captureWidth=video.getVideoWidth();
        captureHeight=video.getVideoHeigth();
//      设置帧率
        frameRate=(int) video.getFrameRate();


        grabber = new OpenCVFrameGrabber(WEBCAM_DEVICE_INDEX);
        grabber.setImageWidth(captureWidth);
        grabber.setImageHeight(captureHeight);
//        先防止名字相同不能保存
        recorder = new FFmpegFrameRecorder(video.getSavePath()+"test.flv", captureWidth, captureHeight, 2);
//        recorder = new FFmpegFrameRecorder("tset.flv", captureWidth, captureHeight, 2);
        recorder.setInterleaved(true);
        recorder.setVideoOption("tune", "zerolatency");
        recorder.setVideoOption("preset", "ultrafast");
        recorder.setVideoOption("crf", "25");
        // 2000 kb/s, 720P视频的合理比特率范围
        recorder.setVideoBitrate(2000000);
        // h264编/解码器
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        // 封装格式flv
        recorder.setFormat(video.getSaveFormat());
        // 视频帧率(保证视频质量的情况下最低25，低于25会出现闪屏)
        recorder.setFrameRate(frameRate);
        // 关键帧间隔，一般与帧率相同或者是视频帧率的两倍
        recorder.setGopSize(frameRate * 2);
        // 不可变(固定)音频比特率
        recorder.setAudioOption("crf", "0");


        // 不可变(固定)音频比特率
        recorder.setAudioOption("crf", "0");
        // 最高质量
        recorder.setAudioQuality(0);
        // 音频比特率
        recorder.setAudioBitrate(192000);
        // 音频采样率
        recorder.setSampleRate(44100);
        // 双通道(立体声)
        recorder.setAudioChannels(2);
        // 音频编/解码器
        recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);

    }

    public void caputre(){
        audioFormat = new AudioFormat(44100.0F, 16, 2, true, false);

        // 通过AudioSystem获取本地音频混合器信息
        Mixer.Info[] minfoSet = AudioSystem.getMixerInfo();
        // 通过AudioSystem获取本地音频混合器
        Mixer mixer = AudioSystem.getMixer(minfoSet[AUDIO_DEVICE_INDEX]);
        // 通过设置好的音频编解码器获取数据线信息
        dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
        try {
            // 打开并开始捕获音频
            // 通过line可以获得更多控制权
            // 获取设备：TargetDataLine line
            // =(TargetDataLine)mixer.getLine(dataLineInfo);
            line = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            line.open(audioFormat);
            line.start();
            // 获得当前音频采样率
            int sampleRate = (int) audioFormat.getSampleRate();
            // 获取当前音频通道数量
            int numChannels = audioFormat.getChannels();
            // 初始化音频缓冲区(size是音频采样率*通道数)
            int audioBufferSize = sampleRate * numChannels;
            byte[] audioBytes = new byte[audioBufferSize];

            exec = new ScheduledThreadPoolExecutor(1);
            exec.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 非阻塞方式读取
                        int nBytesRead = line.read(audioBytes, 0, line.available());
                        // 因为我们设置的是16位音频格式,所以需要将byte[]转成short[]
                        int nSamplesRead = nBytesRead / 2;
                        short[] samples = new short[nSamplesRead];
                        ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
                        // 将short[]包装到ShortBuffer
                        ShortBuffer sBuff = ShortBuffer.wrap(samples, 0, nSamplesRead);
                        // 按通道录制shortBuffer
                        recorder.recordSamples(sampleRate, numChannels, sBuff);
                    } catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, (long) 1000 / frameRate, TimeUnit.MILLISECONDS);
        } catch (LineUnavailableException e1) {
            e1.printStackTrace();
        }
    }


    public void dealImage() throws org.bytedeco.javacv.FrameGrabber.Exception {
//        摄像头显示
        // javaCV提供了优化非常好的硬件加速组件来帮助显示我们抓取的摄像头视频
        cFrame = new CanvasFrame("Capture Preview", CanvasFrame.getDefaultGamma() / grabber.getGamma());
        cFrame.setCanvasSize(640,480);
        Frame capturedFrame = null;
//        合并图片成视频
        // 执行抓取（capture）过程
        while ((capturedFrame = grabber.grab()) != null) {
            if (cFrame.isVisible()) {
                //本机预览要发送的帧
                cFrame.showImage(capturedFrame);
            }
            //定义我们的开始时间，当开始时需要先初始化时间戳
            if (startTime == 0){
                startTime = System.currentTimeMillis();
            }

            // 创建一个 timestamp用来写入帧中
            videoTS = 1000 * (System.currentTimeMillis() - startTime);
            //检查偏移量
            if (videoTS > recorder.getTimestamp()) {
//                System.out.println("Lip-flap correction: " + videoTS + " : " + recorder.getTimestamp() + " -> "
//                        + (videoTS - recorder.getTimestamp()));
                //告诉录制器写入这个timestamp
                recorder.setTimestamp(videoTS);
            }
            // 发送帧
            try {
                recorder.record(capturedFrame);
            } catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
                System.out.println("录制帧发生异常，什么都不做");
            }
        }


    }


    public void start(){
//        开始录制
        System.out.println("开始录制...");
        try {
            grabber.start();
            recorder.start();
        } catch (Exception e) {
            System.out.println(e);
        }
        // 如果有录音设备则启动录音线程

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                caputre();
            }
        }).start();

        // 录屏
        screenTimer = new ScheduledThreadPoolExecutor(1);
        screenTimer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try{
                    dealImage();
                }catch (Exception e){
                    System.out.println(e);
                }
            }
        }, (int) (1000 / frameRate), (int) (1000 / frameRate), TimeUnit.MILLISECONDS);

    }


    public void stop(){
        //        检测关闭
        cFrame.dispose();
//        关闭两个线程池
        if (null != screenTimer){
            screenTimer.shutdownNow();

        }
        if (null!=exec){
            exec.shutdownNow();
        }
//        关闭录制资源
        try {
            if(null!=recorder){
                recorder.stop();
                recorder.release();
                recorder.close();

            }
            if(null!=recorder){
                grabber.stop();
                grabber.release();
                grabber.close();
            }
            audioFormat = null;

        }catch (Exception e){
            System.out.println(e);
        }
    }


}
