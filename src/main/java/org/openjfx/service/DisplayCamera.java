package org.openjfx.service;


import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.librealsense.frame;
import org.openjfx.domain.Audio;
import org.openjfx.domain.Video;

import javax.sound.sampled.AudioFormat;
import javax.swing.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class DisplayCamera {

    //录屏线程池 cameraThread
    private ScheduledThreadPoolExecutor cameraThread;
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




    public DisplayCamera() {
        grabber = new OpenCVFrameGrabber(WEBCAM_DEVICE_INDEX);
        grabber.setImageWidth(captureWidth);
        grabber.setImageHeight(captureHeight);


    }


    public void showCamera() throws org.bytedeco.javacv.FrameGrabber.Exception {
//        摄像头显示
        cFrame = new CanvasFrame("Capture Preview", CanvasFrame.getDefaultGamma() / grabber.getGamma());
        cFrame.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        cFrame.setCanvasSize(640,480);
        cFrame.setAlwaysOnTop(true);
        Frame capturedFrame = null;
//        合并图片成视频
        // 执行抓取（capture）过程
        while ((capturedFrame = grabber.grab()) != null) {
            if (cFrame.isVisible()) {
                //本机预览要发送的帧
                cFrame.showImage(capturedFrame);
            }
        }
    }


    public void start(){
        System.out.println("窗口显示摄像头...");
        try {
            grabber.start();
        } catch (Exception e) {
            System.out.println(e);
        }
        // 如果有录音设备则启动录音线程

        // 录屏
        cameraThread = new ScheduledThreadPoolExecutor(1);
        cameraThread.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try{
                    showCamera();
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
        if (null != cameraThread){
            cameraThread.shutdownNow();

        }
//        关闭录制资源
        try {
            if(null!=grabber){
                grabber.stop();
                grabber.release();
                grabber.close();
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }

}
