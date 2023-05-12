package org.openjfx.controller;


import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerPaneController {
//  关闭
    private Stage primaryStage;
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    @FXML
    private void closebtn(){
        if (primaryStage != null) {
            // 执行操作
            primaryStage.close();
        }
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
            try {
                // 1.创建一个服务端的套接字
                ServerSocket serverSocket = new ServerSocket(8888);

                //2.等待客户端的连接
                Socket socket = serverSocket.accept();
                System.out.println("正常等待连接......");
                // 3.获取socket通道的输入流(输入流的读取方式为一行一行的读取方式 ----> readLine())
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // 4.获取通道的输入流(也是一行一行的写出  BufferedWriter ->newLine())
                // 当用户点击“发送”按钮的时候才会，写出数据
                bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                String line = null;
                while ((line = br.readLine()) != null) {
                    // 将读取的数据拼接到文本域中显示
                    jta.appendText(line + "\n");
                }

                // 5.关闭socket通道
                serverSocket.close();
            } catch (IOException e) {
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
        text = "主播：" + text;
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
