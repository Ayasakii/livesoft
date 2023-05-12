package org.openjfx.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.openjfx.Main;
import org.openjfx.domain.Audio;
import org.openjfx.domain.Video;
import org.openjfx.service.SettingUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class SettingPaneController implements Initializable {
    //    文件输入栏
    @FXML
    private TextField fileText;
    //  帧率选择
    @FXML
    private ChoiceBox frameChoiceBox;
    //  保存格式选择
    @FXML
    private ChoiceBox formatChoiceBox;
    //    分辨率选择框
    @FXML
    private ChoiceBox resolutionChoiceBox;

//    音频选项
    @FXML
    private ChoiceBox audioQualityBox;
    @FXML
    private ChoiceBox sampleRateBox;
    @FXML
    private ChoiceBox audioBitrateBox;

    //    保存按钮
    @FXML
    private Button saveSettingButton;
    /* 选择文件夹按钮*/
    @FXML
    private Button showFileButton;
//恢复默认值
    @FXML
    private Button recoverButton;
//关闭按钮
    @FXML
    private Button closeButton;

    //    设置保存类
    private SettingUtils settingUtils;

    // 视频选项集合
    private String[] frames;
    private String[] formats;
    private String[] resolutions;
//    音频选项卡
    private String[] audioQualitys;
    private String[] sampleRates;
    private String[] audioBitrates;



    private Window stage;
    private Stage primaryStage;
    //  文件路径
    private String videoPath;
    //  视频音频类
    private Video video;
    private Audio audio;



    @FXML
    private void showFile() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(stage);
        videoPath = file.getPath();//选择的文件夹路径
        fileText.setText(videoPath);


    }


    @FXML
    private void saveSetting() {
        video.setSaveFormat(formatChoiceBox.getValue().toString());
//        将object转成string再转成double类型
        video.setFrameRate(Double.valueOf(frameChoiceBox.getValue().toString()));
//        设置视频长宽
        video.setWidthAndHeiht(resolutionChoiceBox.getValue().toString());
//      设置保存文件位置
        video.setPath(videoPath);
        System.out.println(video.getPath());
        settingUtils.writeJsonFile(video, audio);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        videoBoxInitialize();
        audioBoxInitialize();
//      初始化音频视频类
        settingUtils = new SettingUtils();
        settingUtils.checkJsonFile();
        video=settingUtils.readVidioJSON();
        audio=settingUtils.readAudioJSON();
//        设置文件路径
        fileText.setText(video.getPath());
    }

    private void videoBoxInitialize(){
        //      初始化选项
        frames = new String[]{"10", "15", "30", "60"};
        formats = new String[]{"flv", "mp4"};
        resolutions = new String[]{"1920*1080", "1650*1050", "1024*768"};
//      添加到选择框处
        frameChoiceBox.getItems().addAll(frames);
        formatChoiceBox.getItems().addAll(formats);
        resolutionChoiceBox.getItems().addAll(resolutions);
//        设置默认选项
        frameChoiceBox.getSelectionModel().select(0);
        formatChoiceBox.getSelectionModel().select(0);
        resolutionChoiceBox.getSelectionModel().select(0);
    }


    private void audioBoxInitialize(){
        //      初始化选项
        audioQualitys = new String[]{"0","1","2"};
        sampleRates = new String[]{"192000"};
        audioBitrates = new String[]{"44100"};
//      添加到选择框处
        audioQualityBox.getItems().addAll(audioQualitys);
        sampleRateBox.getItems().addAll(sampleRates);
        audioBitrateBox.getItems().addAll(audioBitrates);
//        设置默认选项
        audioBitrateBox.getSelectionModel().select(0);
        sampleRateBox.getSelectionModel().select(0);
        audioQualityBox.getSelectionModel().select(0);

    }



    @FXML
    private void recover(){
        //        设置默认选项
        frameChoiceBox.getSelectionModel().select(0);
        formatChoiceBox.getSelectionModel().select(0);
        resolutionChoiceBox.getSelectionModel().select(0);
        //        设置文件路径
        fileText.setText("");
//        视频默认设置
        video.setPath("");
        video.setFrameRate(10);
        video.setVideoHeigth(1080);
        video.setVideoWidth(1920);
        video.setSaveFormat("flv");

    }




    @FXML
    private void getmainPane(ActionEvent event) throws IOException {
        MainPane mainPane=new MainPane();
        try{
            mainPane.start(new Stage());
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.hide();

        } catch (Exception e){e.printStackTrace();}

    }



}



