package lk.ijse.controller;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.util.Duration;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ClientFormController {

    public javafx.scene.control.Button Cameraman;
    public javafx.scene.control.Button emoji1;

    @FXML
    private Label lblClientName;

    @FXML
    private AnchorPane emoji;

    @FXML
    private Button emojibtn;

    @FXML
    private VBox msgVbox;

    @FXML
    private TextField txtMessage;

    public ScrollPane scrollPane;
    static boolean openWindow = false;
    private static final double PANE_HEIGHT = 500;
    public AnchorPane emojiPane;

    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String clientName ;



    public void initialize(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    socket = new Socket("localhost", 3030);
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    System.out.println("Client connected");
                    ServerFormController.receiveMessage(clientName+" joined.");

                    while (socket.isConnected()){
                        String receivingMsg = dataInputStream.readUTF();
                        receivingMsg(receivingMsg, ClientFormController.this.msgVbox);
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();

        this.msgVbox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                scrollPane.setVvalue((Double) newValue);
            }
        });
        //emoji();
        emojiPane.setVisible(false);
    }

    public void setClientName(String name) {
        lblClientName.setText(name);
        clientName = name;
    }

    private void receivingMsg(String receivingMsg, VBox msgVbox) {
        if (receivingMsg.matches(".*\\.(png|jpe?g|gif)$")){
            File imageFile = new File(receivingMsg);
            Image image = new Image(imageFile.toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(200);
            imageView.setFitWidth(200);
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.setPadding(new Insets(5, 5, 5, 10));
            hBox.getChildren().add(imageView);
            Platform.runLater(() -> {
                msgVbox.getChildren().add(hBox);

            });

        }else {
            String name = receivingMsg.split(":")[0];
            String msgFromServer = receivingMsg.split(":")[1];

            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.setPadding(new Insets(5,5,5,10));

            HBox hBoxName = new HBox();
            hBoxName.setAlignment(Pos.CENTER_LEFT);
            Text textName = new Text(name);
            TextFlow textFlowName = new TextFlow(textName);
            hBoxName.getChildren().add(textFlowName);

            Text text = new Text(msgFromServer);
            TextFlow textFlow = new TextFlow(text);
            textFlow.setStyle("-fx-background-color: #abb8c3; -fx-font-weight: bold; -fx-background-radius: 20px");
            textFlow.setPadding(new Insets(5,10,5,10));
            text.setFill(Color.color(0,0,0));

            hBox.getChildren().add(textFlow);

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    msgVbox.getChildren().add(hBoxName);
                    msgVbox.getChildren().add(hBox);
                }
            });
        }
    }

    private void sendMsg(String msgToSend) {
        if (!msgToSend.isEmpty()){
            if(!msgToSend.matches(".*\\.(png|jpe?g|gif)$")){

                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER_RIGHT);
                hBox.setPadding(new Insets(5, 5, 0, 10));

                Text text = new Text(msgToSend);
                text.setStyle("-fx-font-size: 14");
                TextFlow textFlow = new TextFlow(text);

                textFlow.setStyle("-fx-background-color: #0693e3; -fx-font-weight: bold; -fx-color: white; -fx-background-radius: 20px");
                textFlow.setPadding(new Insets(5, 10, 5, 10));
                text.setFill(Color.color(1, 1, 1));

                hBox.getChildren().add(textFlow);

                HBox hBoxTime = new HBox();
                hBoxTime.setAlignment(Pos.CENTER_RIGHT);
                hBoxTime.setPadding(new Insets(0, 5, 5, 10));
                String stringTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                Text time = new Text(stringTime);
                time.setStyle("-fx-font-size: 8");

                hBoxTime.getChildren().add(time);

                msgVbox.getChildren().add(hBox);
                msgVbox.getChildren().add(hBoxTime);


                try {
                    dataOutputStream.writeUTF(clientName + ":" + msgToSend);
                    dataOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                txtMessage.clear();
            }
        }
    }


    @FXML
    void sendbtnOnAction(ActionEvent event) { sendMsg(txtMessage.getText()); }

    @FXML
    void txtMessageOnAction(ActionEvent actionEvent) {
        sendButtonOnAction(actionEvent);
    }

    private void sendButtonOnAction(ActionEvent actionEvent) { sendMsg(txtMessage.getText()); }

    @FXML
    void CamerabtnOnAction(ActionEvent actionEvent) throws IOException {


        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select File to Open");
        int userSelection = fileChooser.showOpenDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();
            sendImage(fileToOpen.getPath());
           // sendImage(file);
            System.out.println(fileToOpen.getPath() + " chosen.");
        }



    }

    private void sendImage(String file) {


        File imageFile = new File(file);
        Image image = new Image(imageFile.toURI().toString());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(5, 5, 5, 10));
        hBox.getChildren().add(imageView);
        hBox.setAlignment(Pos.CENTER_RIGHT);

        msgVbox.getChildren().add(hBox);

        try {
            dataOutputStream.writeUTF(file);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void emojibtnonAction() {
        emojiPane.setVisible(true);
    }

    @FXML
    void emojiPaneOnAction(MouseEvent event) { }






    public void sad(MouseEvent mouseEvent) {
        String emoji = new String(Character.toChars(128546));
        txtMessage.setText(emoji);
        emojiPane.setVisible(false);
    }



    public void love(MouseEvent mouseEvent) {
        String emoji = new String(Character.toChars(128525));
        txtMessage.setText(emoji);
        emojiPane.setVisible(false);
    }

    public void green_sad(MouseEvent mouseEvent) {
        String emoji = new String(Character.toChars(128560));
        txtMessage.setText(emoji);
        emojiPane.setVisible(false);
    }

    public void smile_one_eyy(MouseEvent mouseEvent) {
        String emoji = new String(Character.toChars(128540));
        txtMessage.setText(emoji);
        emojiPane.setVisible(false);
    }




    public void real_amile(MouseEvent mouseEvent) {
        String emoji = new String(Character.toChars(128514));
        txtMessage.setText(emoji);
        emojiPane.setVisible(false);
    }

    public void heart(MouseEvent mouseEvent) {
        String emoji = new String(Character.toChars(0x2764)); // Unicode for the heart emoji
        txtMessage.setText(emoji);
        emojiPane.setVisible(false);
    }

    public void thumbsUp(MouseEvent mouseEvent) {
        String emoji = new String(Character.toChars(128077)); // Thumbs up emoji Unicode
        txtMessage.setText(emoji);
        emojiPane.setVisible(false);
    }



    public void tuin(MouseEvent mouseEvent) {
        String emoji = new String(Character.toChars(128519));
        txtMessage.setText(emoji);
        emojiPane.setVisible(false);
    }

    public void woow(MouseEvent mouseEvent) {
        String emoji = new String(Character.toChars(128559));
        txtMessage.setText(emoji);
        emojiPane.setVisible(false);
    }







    public void shutdown() {
        ServerFormController.receiveMessage(clientName+ "left");
    }


}
