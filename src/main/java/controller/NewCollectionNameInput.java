package controller;

import customFeatures.ObjectDetection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Collections.Collection;
import net.semanticmetadata.lire.imageanalysis.features.GlobalFeature;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class NewCollectionNameInput implements Initializable {
    private static String newCollectionName;
    private static HashMap<String,Class> featureList;
    private static LinkedList<Class> fe;
    private static  HashSet<String>  usedNames;
    private static Collection newCollection;
    @FXML
    private TextField CollectionName;
    @FXML
    private Button okButton;
    @FXML
    private Label errorLabel;
    private Stage primaryStage;
    @FXML
    private ListView featuresListView;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        featuresListView.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );
        ObservableList<String> items = FXCollections.observableArrayList (featureList.keySet()
);
        featuresListView.setItems(items);
    }
    public Collection display(HashSet<String> usedNames, HashMap<String, Class> featureList){
        NewCollectionNameInput.featureList =featureList;
        NewCollectionNameInput.usedNames =usedNames;
        primaryStage=new Stage();
        primaryStage.initModality(Modality.APPLICATION_MODAL);
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("gui/NewCollection.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.setTitle("Enter a name");
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.showAndWait();
        return new Collection(newCollectionName,fe );


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
        ObservableList<String> selectedItems=featuresListView.getSelectionModel().getSelectedItems();
        if(selectedItems.isEmpty()){
            errorLabel.setText("Please select at least one feature!");
            errorLabel.setVisible(true);
            return;
        }
        fe=new LinkedList<>();
        for(String feature:selectedItems){
            if(feature.equals(ObjectDetection.class.getSimpleName())){

            }
            fe.addLast(featureList.get(feature));
        }
        //if(featuresListView.)


        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }
}
