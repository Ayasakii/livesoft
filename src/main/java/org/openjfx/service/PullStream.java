package org.openjfx.service;

import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class PullStream {


    private CanvasFrame canvasFrame;
    public void getPullStream(String inputPath) throws Exception, org.bytedeco.javacv.FrameRecorder.Exception {
        //创建+设置采集器
        FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(inputPath);
        grabber.setOption("rtsp_transport", "tcp");
        grabber.setImageWidth(960);
        grabber.setImageHeight(540);

        //开启采集器
        grabber.start();

        //直播播放窗口
        Thread viewThread = new Thread(() -> {
            canvasFrame = new CanvasFrame("直播------来自"+inputPath);
            canvasFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            canvasFrame.setAlwaysOnTop(true);
            OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

            //播流
            while (true){
                //拉流
                Frame frame = null;
                try {
                    frame = grabber.grabImage();
                } catch (FrameGrabber.Exception e) {
                    e.printStackTrace();
                }
                if(null!=frame) {
                    //播放
                    canvasFrame.showImage(frame);
                }
            }
        });
        viewThread.start();


    }
}
