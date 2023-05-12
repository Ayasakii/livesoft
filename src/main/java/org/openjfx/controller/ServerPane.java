package org.openjfx.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;



public class ServerPane extends Application{
    private double xOffset = 0;
    private double yOffset = 0;
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("../Server.fxml"));
        Parent root = fxmlLoader.load();
        ServerPaneController controller = fxmlLoader.getController();
        controller.setPrimaryStage(primaryStage);
        // 添加鼠标按下事件监听器
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        // 添加鼠标拖动事件监听器
        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });
//        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.initStyle(StageStyle.UNDECORATED);


        primaryStage.setScene(new Scene(root));
        primaryStage.setX(0);
        primaryStage.setY(600);
        primaryStage.setOpacity(0.5);//设置透明度Window方法
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();

    }




    public static void main(String[] args) {
        launch(args);
    }


}
