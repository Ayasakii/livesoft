package org.openjfx.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.openjfx.domain.Audio;
import org.openjfx.domain.Video;
import org.openjfx.service.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalTime;

import org.bytedeco.javacv.FFmpegFrameGrabber;


public class MainPaneController {

    @FXML
    private Slider volumeSlider;
    private Stage primaryStage;
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;

    }
    private DoubleProperty volumeProperty = new SimpleDoubleProperty(0.5);
    public void initialize() {
        volumeProperty.bind(volumeSlider.valueProperty());
    }

    //  视频设置类
    private Video video;
    //  音频设置类
    private Audio audio;
    //  设置类
    private SettingUtils settingUtils;

    //　录制屏幕实例
    private VideoRecording videoRecording = new VideoRecording();
    //    录制摄像头实例
    private CameraRecording cameraRecording;
    //    录制屏幕时显示摄像头的内容
    private DisplayCamera displayCamera;

    //  时间线控件(计算时间差)
    private Timeline timeline;

    //    判断是否打开麦克风标志
    private boolean isMicroPhone = false;
    //    判断是否打开摄像头界面
    private boolean isDisplayCamera = false;

    //跳转设置界面
    @FXML
    private Button closeButton;
    //    录制按钮
    @FXML
    private ToggleButton recordingButton;

    //  选择是否使用麦克风
    @FXML
    private ToggleButton microPhoneButton;

    //    时间
    @FXML
    private Label timeLabel;

    //    截图
    @FXML
    private Button screenShotsButton;

    @FXML
    private Button SettingButton;

    //    录制摄像头按钮
    @FXML
    private ToggleButton cameraButton;

    //    是否打开摄像头按钮
    @FXML
    private ToggleButton showCamerasButton;
    //  暂停按钮
    @FXML
    private ToggleButton pauseButton;

    boolean openCamera = false;
    boolean openScreen = false;
    @FXML
    private ImageView videoView;

    //截图
    @FXML
    private void screenShots() {
        Stage stage = (Stage) cameraButton.getScene().getWindow();
        stage.hide();
        CaptureScreen captureScreen = new CaptureScreen();
        captureScreen.test();
        stage.show();
    }



    private void computationTime(boolean isRecording) {
        if (isRecording) {
            LocalTime startTime = LocalTime.now();
            timeline = new Timeline(new KeyFrame(Duration.millis(1000), arg1 -> {
                LocalTime endTime = LocalTime.now();
//            计算时间差
                java.time.Duration duration = java.time.Duration.between(startTime, endTime);
//            更换时间格式
                String hms = String.format("%d:%02d:%02d",
                        duration.toHoursPart(),
                        duration.toMinutesPart(),
                        duration.toSecondsPart());

                timeLabel.setText(hms);
            }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        } else {
            timeline.stop();
            timeLabel.setText("0:00:00");
        }
    }



    @FXML
    private void startRecording(ActionEvent event) {
//        判断是否点击录制按钮
        if (recordingButton.isSelected()) {

//            新建视频音频属性类
            settingUtils = new SettingUtils();
            settingUtils.checkJsonFile();
            video = settingUtils.readVidioJSON();
            audio = settingUtils.readAudioJSON();
//            开始录制屏幕
//            videoRecording=new VideoRecording(video,audio, isMicroPhone);
            videoRecording.initialize(video, audio, isMicroPhone);
            videoRecording.start();

//          打开摄像头
            if (isDisplayCamera) {
                displayCamera = new DisplayCamera();
                displayCamera.start();


            }

//            在开始后才开始计算时间
//            计算时间差
            computationTime(true);

        } else {
//            停止计算
            computationTime(false);
            videoRecording.stop();
//            关闭摄像头
            if (isDisplayCamera) {
                displayCamera.stop();
            }

        }
    }



    @FXML
    public void pauseRecording() {
        if (pauseButton.isSelected()) {
            System.out.println("暂停录制");
            videoRecording.pause();
        } else {
            System.out.println("取消暂停");
            videoRecording.start();
        }

    }


    @FXML
    public void startCameraRecording() {
        if (cameraButton.isSelected()) {
//            计算时间差
            computationTime(true);
//            新建视频音频属性类
            settingUtils = new SettingUtils();
            settingUtils.checkJsonFile();
            video = settingUtils.readVidioJSON();
            audio = settingUtils.readAudioJSON();

            cameraRecording = new CameraRecording(video, audio);
            cameraRecording.start();
        } else {
            //            停止计算
            computationTime(false);
            cameraRecording.stop();
        }
    }


    //跳转设置界面
    @FXML
    private void getSettingPane(ActionEvent event) throws IOException {
        SettingPane settingPane = new SettingPane();
        try {
            settingPane.start(new Stage());
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.hide();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //开始自动显示当前屏幕
    @FXML
    private ToggleButton showbtn;

    @FXML
    private void isScreenShow() throws Exception {
        if (showbtn.isSelected()) {
            if (openScreen == false) {
                FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("desktop");
                grabber.setFormat("gdigrab");
                grabber.start();
                timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> {
                    try {
                        Frame frame = grabber.grabImage();
                        if (frame != null) {

                            Image image = SwingFXUtils.toFXImage(FrameToBufferedImage(frame), null);

                            videoView.setImage(image);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play();
                System.out.println("打开屏幕源");
                openScreen = true;
            }

        } else {

            System.out.println("关闭屏幕源");
            timeline.stop();
            videoView.setImage(new Image("file:///path/to/blank/image.png"));
            openScreen = false;

        }
    }

    private BufferedImage FrameToBufferedImage(Frame frame) throws Exception {
        Java2DFrameConverter converter = new Java2DFrameConverter();
        return converter.getBufferedImage(frame);
    }


    //是否录制麦克风
    @FXML
    private void isOpenMicroPhone(ActionEvent event) {
//       判断是否点击麦克风录制按钮
        if (microPhoneButton.isSelected()) {
            isMicroPhone = true;
            System.out.println("使用麦克风");
        } else {
            isMicroPhone = false;
            System.out.println("麦克风");
        }

    }


    //    是否打开摄像头界面
    @FXML
    private void isShowCamera() {
        if (showCamerasButton.isSelected()) {
            if (openCamera == false) {
                isDisplayCamera = true;
                displayCamera = new DisplayCamera();
                displayCamera.start();
                System.out.println("打开摄像头");
                openCamera = true;
            }

        } else {

            System.out.println("关闭摄像头");
            displayCamera.stop();
            openCamera = false;

        }


    }


    @FXML
    private void closePane(ActionEvent event) throws IOException {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();


    }

    //  开启媒体库
    @FXML
    private Button playerButton;

    @FXML
    private void openPlayer(ActionEvent event) throws IOException {
        PlayerPane playerPane = new PlayerPane();
        try {
            playerPane.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //  开启互动
    @FXML
    private ToggleButton openbtn;
    private boolean openCommentbtn = false;
    private Thread ServerPaneThread;
    private ServerPane serverPane;

    @FXML
    private void openComment(ActionEvent event) throws IOException {
        if (openbtn.isSelected()) {
            if (openCommentbtn == false) {
                ServerPaneThread = new Thread(() -> {
                    Platform.runLater(() -> {
                        serverPane = new ServerPane();
                        try {
                            serverPane.start(new Stage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                });
                ServerPaneThread.start();
                openCommentbtn = false;
                System.out.println("开启");
            }
        }
    }

    //  开启摄像头推流
    private Process p1;
    private Thread cameraThread;

    @FXML
    private void startCameraShow() {
        System.out.println("开启摄像头推流");
        cameraThread = new Thread(() -> {

            try {
                ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-f", "dshow", "-i", "video=HP Wide Vision HD Camera", "-vcodec", "libx264", "-f", "flv", "rtmp://127.0.0.1:1935/live");
                pb.redirectErrorStream(true);
                p1 = pb.start();
                InputStream is = p1.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                int exitCode = p1.waitFor();
                System.out.println("进程已完成，退出码为：" + exitCode);
            } catch (IOException | InterruptedException e) {
                // 处理异常
            }
        });
        cameraThread.start();
        // 写入音量大小到进程的输入流中
        Platform.runLater(() -> {
            try (OutputStream os = p1.getOutputStream()) {
                String volumeCommand = "volume=" + volumeProperty.get();
                os.write(volumeCommand.getBytes());
                os.flush();
            } catch (IOException e) {
                // 处理异常
            }
        });

        volumeProperty.addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                try (OutputStream os = p1.getOutputStream()) {
                    String volumeCommand = "volume=" + newValue;
                    os.write(volumeCommand.getBytes());
                    System.out.println("当前音量："+newValue);
                    os.flush();
                } catch (IOException e) {
                    // 处理异常
                }
            });
        });
    }

    //  开启屏幕推流
    private Thread screenThread;
    private Process p2;

    private boolean isDoing = false;



//  开始推流
    @FXML
    public void startScreenShow() throws IOException {

        System.out.println("开启屏幕推流");
        screenThread = new Thread(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-f", "gdigrab", "-i", "desktop", "-vcodec", "libx264", "-f", "flv", "rtmp://127.0.0.1:1935/live");
                pb.redirectErrorStream(true);
                p2 = pb.start();
                InputStream is = p2.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                int exitCode = p2.waitFor();
                System.out.println("进程已完成，退出码为：" + exitCode);
            } catch (IOException | InterruptedException e) {
                // 处理异常
            }
        });
        screenThread.start();
        // 写入音量大小到进程的输入流中
        Platform.runLater(() -> {
            try (OutputStream os = p2.getOutputStream()) {
                String volumeCommand = "volume=" + volumeProperty.get();
                os.write(volumeCommand.getBytes());
                os.flush();
            } catch (IOException e) {
                // 处理异常
            }
        });

        volumeProperty.addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                try (OutputStream os = p2.getOutputStream()) {
                    String volumeCommand = "volume=" + newValue;
                    os.write(volumeCommand.getBytes());
                    System.out.println("当前音量："+newValue);
                    os.flush();
                } catch (IOException e) {
                    // 处理异常
                }
            });
        });
    }



//  文件推流
    private boolean isFile = false;
    @FXML
    private Button selectFileButton;
    private Thread streamThread;
    private Process p3;
    private StringProperty filePathProperty = new SimpleStringProperty();
    public void onSelectFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择文件");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MP4", "*.mp4")
        );
        File selectedFile = fileChooser.showOpenDialog(selectFileButton.getScene().getWindow());
        if (selectedFile != null) {
            isFile = true;
            filePathProperty.set(selectedFile.getAbsolutePath());
            System.out.println("已选");
            System.out.println("开始推流");
            streamThread = new Thread(() -> {
                try {
                    ProcessBuilder pb = new ProcessBuilder(
                            "ffmpeg", "-i", filePathProperty.get(), "-vcodec", "copy", "-f", "flv", "rtmp://127.0.0.1:1935/live"
                    );
                    pb.redirectErrorStream(true);
                    p3 = pb.start();
                    InputStream is = p3.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                    int exitCode = p3.waitFor();
                    System.out.println("进程已完成，退出码为：" + exitCode);
                } catch (IOException | InterruptedException e) {
                    // 处理异常
                }
            });
            streamThread.start();
        }
    }

//  直播效果展示
    private Thread ClientPaneThread;
    private ClientPane clientPane;
    private boolean isOpenClient = false;
    @FXML
    public void showView(ActionEvent event) throws IOException{

        //rtmp服务器拉流地址
        String inputPath = "rtmp://127.0.0.1:1935/live";
        PullStream pullStream = new PullStream();
        try {
            pullStream.getPullStream(inputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(isOpenClient == false){
            ClientPaneThread = new Thread(() -> {
                Platform.runLater(() -> {
                    clientPane=new ClientPane();
                    try{
                        clientPane.start(new Stage());
                    } catch (Exception e){e.printStackTrace();}
                });
            });
            ClientPaneThread.start();
            isOpenClient = true;
        }

    }

//  停止所有推流
    @FXML
    public void stopAllShow() {
        /*if (p != null) {
            System.out.println("关闭摄像头推流");
            p.destroy();

        }*/
        // 等待线程完成执行
        if (cameraThread != null) {
            cameraThread.interrupt();
            System.out.println("关闭摄像头推流");
            p1.destroy();
        }else if(isFile==true){
            streamThread.interrupt();
            p3.destroy();
            isFile = false;
            System.out.println("关闭文件推流");
        }
        else{
            screenThread.interrupt();
            System.out.println("关闭屏幕推流");
            p2.destroy();
            isDoing = false;
        }

    }

}