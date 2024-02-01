package lk.ijse.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginFormController {

    @FXML
    private AnchorPane LoginPage;

    @FXML
    private TextField txtUserName;

    public void initialize(){}

    @FXML
    void btnLoginOnAction(ActionEvent event) throws IOException {
        if (!txtUserName.getText().isEmpty()&&txtUserName.getText().matches("[A-Za-z0-9]+")){
            Stage primaryStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(ClientFormController.class.getResource("/view/ClientForm.fxml"));
            Parent root = fxmlLoader.load();

            ClientFormController controller = fxmlLoader.getController();
            System.out.println(txtUserName.getText());
            controller.setClientName(txtUserName.getText()); // Set the parameter


            primaryStage.setScene(new Scene(root));

            primaryStage.setTitle(txtUserName.getText());
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.setOnCloseRequest(windowEvent -> {
                controller.shutdown();
            });
            primaryStage.show();

            txtUserName.clear();
        }else{
            new Alert(Alert.AlertType.ERROR, "Please enter your name").show();
        }
    }
}
