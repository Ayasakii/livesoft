package org.openjfx.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class MainPane extends Application {
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage primaryStage) throws IOException {


        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../MainPane.fxml"));
        Parent root = fxmlLoader.load();
        MainPaneController controller = fxmlLoader.getController();
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
        primaryStage.initStyle(StageStyle.TRANSPARENT);


        primaryStage.setScene(new Scene(root));
        primaryStage.show();


        /*scene = new Scene(loadFXML("MainPane"));
        //隐藏标题
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        AtomicReference<Double> xOffSet= new AtomicReference<>((double) 0);
        AtomicReference<Double> yOffSet= new AtomicReference<>((double) 0);
        scene.setOnMousePressed(event -> {
            xOffSet.set(event.getSceneX());
            yOffSet.set(event.getSceneY());
        });

        scene.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffSet.get());
            stage.setY(event.getScreenY() - yOffSet.get());
        });
        stage.show();

    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainPane.class.getResource("../" + fxml + ".fxml"));
        return fxmlLoader.load();
    }*/
    }
    public static void main(String[] args) {
        launch(args);
    }
}

