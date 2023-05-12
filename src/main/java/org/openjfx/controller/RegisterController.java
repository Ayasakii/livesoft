package org.openjfx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegisterController {
    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField repassword;

    @FXML
    private TextField phone;

    @FXML
    private TextField name;

    @FXML
    private Label prompt;

    @FXML
    public void Register() throws Exception {
        // 查看是否以存在


        // 判断两次密码是否一致


        // 插入用户记录 !! 先测试是否有账号密码输入
        if(!username.getText().equals("") && !password.getText().equals("") && !repassword.getText().equals("")&& !phone.getText().equals("")&& !name.getText().equals("")) {

            System.out.println("hello");
            Connection con = GetDBConnection.connectDB("broadcast", "root", "123456");

            String sql = "Insert into user(id,username,password,phone,name) values(NULL ,?,?,?,?)";  //可以md5
            // 本项目数据表包括密码的hash,也包含原密码（不安全），只为测试。

            PreparedStatement pstm = con.prepareStatement(sql);
            pstm.setString(1, username.getText());
            pstm.setString(2, password.getText());
            pstm.setString(3, phone.getText());
            pstm.setString(4, name.getText());
            pstm.executeUpdate();

            prompt.setText("注册成功");

        } else {
            if(username.getText().equals("")) {
                prompt.setText("请输入账号");
            } else{
                if(password.getText().equals("")) {
                    prompt.setText("请输入密码");
                } else {
                    prompt.setText("请确认密码");
                }
            }
        }

        // 测试
        System.out.println(username.getText() + "  " + password.getText());


    }

    @FXML
    private Button closeButton;
    @FXML
    private void closePane(){
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private AnchorPane mYAnchorPane;
    @FXML
    public void ReturnLand() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../index.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) mYAnchorPane.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
