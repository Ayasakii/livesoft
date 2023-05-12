package org.openjfx.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Video {

    private int videoWidth;
    private int videoHeigth;

    private String savePath;

    private String fileName;

    private String saveFormat;

    private double frameRate;
    /*目录路径*/
    private String path;


    public Video() {
        videoWidth = 1920;
        videoHeigth = 1080;
//        处理保存的时间
        savePath="";
        path="";
        saveFormat = "flv";
        frameRate = 25;
    }


//  分辨率
    public void setWidthAndHeiht(String resolutions) {
        if ("1920*1080".equals(resolutions)) {
            videoWidth = 1920;
            videoHeigth = 1080;
        } else if ("1650*1050".equals(resolutions)) {
            videoWidth = 1650;
            videoHeigth = 1050;
        } else if ("1024*768".equals(resolutions)) {
            videoWidth = 1024;
            videoHeigth = 768;
        }
    }


    public double getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(double frameRate) {
        this.frameRate = frameRate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSaveFormat() {
        return saveFormat;
    }

    public void setSaveFormat(String saveFormat) {
        this.saveFormat = saveFormat;
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
    }

    public int getVideoHeigth() {
        return videoHeigth;
    }

    public void setVideoHeigth(int videoHeigth) {
        this.videoHeigth = videoHeigth;
    }

    public String getSavePath() {
//        重新设置文件名
        fileName = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss").format(LocalDateTime.now());
        if("".equals(path)){
            savePath=fileName+"."+saveFormat;
        }else{
            savePath=path+"/"+fileName+"."+saveFormat;
        }

        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath=savePath;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
