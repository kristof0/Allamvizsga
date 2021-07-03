package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;

public class NewNameInput implements Initializable {
    private static String newCollectionName;
    private static  HashSet<String>  usedNames;
    @FXML
    private TextField CollectionName;
    @FXML
    private Button okButton;
    @FXML
    private Label errorLabel;
    private Stage primaryStage;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    public String display( HashSet<String> usedNames){
        NewNameInput.usedNames =usedNames;
        primaryStage=new Stage();
        primaryStage.initModality(Modality.APPLICATION_MODAL);
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("gui/NewNameInput.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.setTitle("Enter a name");
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.showAndWait();
        return newCollectionName;
    }

    public void okButtonOnClick(){
        newCollectionName=CollectionName.getText();
        if(usedNames.contains(newCollectionName)) {
            errorLabel.setText("Collection name is already in use !");
            errorLabel.setVisible(true);
            return;
        }
        else {
            if(newCollectionName.equals("")) {
                errorLabel.setText("Please enter a collection name !");
                errorLabel.setVisible(true);
                return;
            }


        }

        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }
}
