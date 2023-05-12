package org.openjfx.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.openjfx.controller.sql.GetDBConnection;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class indexController implements Initializable {

    @FXML
    private Button onload;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;

    @FXML
    private Label prompt;

    private Stage primaryStage;
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;

    }
    @FXML
    private AnchorPane myAnchorPane;
    private double xOffset = 0;
    private double yOffset = 0;
    private void ChangeScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../MainPane.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) myAnchorPane.getScene().getWindow();
        stage.setScene(scene);
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });
        stage.show();
    }

    @FXML
    public void loadIn() throws Exception {
        System.out.println(username.getText() + " " + password.getText());

        // 设置了一个管理员
        if (username.getText().equals("root") && password.getText().equals("123456")) {
            ChangeScene();
        } else {
            // 判断用户名是否为空
            if (username.getText().equals("")) {
                prompt.setText("请输入账号");
            } else {
                // 判断密码是否为空
                if (password.getText().equals("")) {
                    prompt.setText("请输入密码");
                } else {

                    // 排除以上情况
                    // 开始判断是否存在此用户
                    Connection con = GetDBConnection.connectDB("broadcast", "root", "123456");
                    Statement stm = con.createStatement();

                    String qname = "SELECT username from user where username=" + "'" + username.getText() + "'";
                    ResultSet re = stm.executeQuery(qname);
                    if (!re.next()) {
                        prompt.setText("账号不存在");
                    } else {
                        // 用户存在,验证密码，一般使用密码的hash比对,此次使用原密码
                        String qpwd = "SELECT password from user WHERE username = '" + username.getText() + "';";
                        ResultSet re2 = stm.executeQuery(qpwd);

                        // 密码错误
                        if (re2.next()) {
                            // getString()方法一定要在next()方法下运行，不然报错
                            if (!re2.getString(1).equals(password.getText()))
                                prompt.setText("密码错误");
                            else {
                                /*
                                密码正确，进入系统
                                 */
                                ChangeScene();
                            }
                        }

                    }


                }
            }


        }
    }


    public void ToRegister(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../Register.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private Button closeButton;
    @FXML
    private void closePane(){
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}