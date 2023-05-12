package org.openjfx.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ClientPane extends Application{
    private double xOffset = 0;
    private double yOffset = 0;
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("../Client.fxml"));
        Parent root = fxmlLoader.load();
        ClientPaneController controller = fxmlLoader.getController();
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
        primaryStage.setScene(new Scene(root));
        primaryStage.setX(800);
        primaryStage.setY(500);
        primaryStage.setOpacity(0.5);//设置透明度Window方法
        primaryStage.show();

    }




    public static void main(String[] args) {
        launch(args);
    }


}
