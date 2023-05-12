package org.openjfx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientPaneController {
    //  关闭
    private Stage primaryStage;
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    //  回车事件
    @FXML
    private TextField jtf;
    @FXML
    private TextArea jta;
    private BufferedWriter bw = null;

    public void initialize() {
        jtf.setOnKeyTyped(event -> {
            if (event.getCharacter().equals("\r")) { // 检测到回车键
                send();
            }
        });
        new Thread(() -> {
            try{
                /*******客户端 TCP协议*********/
                // 1.创建一个客户端的套接字（尝试连接）
                Socket socket = new Socket("127.0.0.1",8888);
                System.out.println("正常尝试连接......");
                // 2.获取socket通道的输入流
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // 3
                bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                String line = null;
                while((line = br.readLine()) !=null){
                    jta.appendText(line + "\n");
                }
                // 3. 获取输出流

                // 4.关闭流
                socket.close();

                /******************************/
            }catch(Exception e){
                e.printStackTrace();
            }

        }).start();


    }
    //  发送事件
    @FXML
    private void send() {
        // 1.获取文本框中需要发送的内容
        String text = jtf.getText();
        // 2. 拼接内容
        text = "观众：" + text;
        // 3.自己显示
        jta.appendText(text + "\n");
        try{
            // 4.发送
            bw.write(text);
            bw.newLine(); // 换行
            bw.flush();  // 刷新
            // 5.清空
            jtf.setText("");

        }catch(IOException e1){
            e1.printStackTrace();
        }
    }


}
