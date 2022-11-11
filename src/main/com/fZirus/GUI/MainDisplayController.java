package fZirus.GUI;

import fZirus.packManager.entities.GenericBlockModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import fZirus.packManager.PackManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;


public class MainDisplayController implements Initializable {
    private File selectedPack;
    private TreeItem currentTreeRoot;
    private PackManager packManager;

    @FXML private AnchorPane anchorPane;
    @FXML private Button openNewPackButton;
    @FXML private TreeView fileTree;
    @FXML private Canvas texture2D;

    public void openNewPack(ActionEvent e) {
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Open Resource Pack Folder");

        DirectoryChooser directoryChooser = new DirectoryChooser();

        File selectedDirectory = directoryChooser.showDialog(primaryStage);
        if (selectedDirectory == null) {
            return;
        }
        this.packManager = new PackManager(selectedDirectory.getPath());
        try {
            if (packManager.isPackFolder(selectedDirectory)) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Try Selecting a folder that has a manifest.json file");
                alert.setTitle("Pack not found");
                alert.showAndWait();
                return;
            }
        } catch (SecurityException exception) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Folder was in restricted area, try running as administrator or moving the folder to your desktop");
            alert.setTitle("Pack not found");
            alert.showAndWait();
        }


        System.out.println(selectedDirectory.getAbsolutePath());
        this.selectedPack = selectedDirectory;


        Stage primStage = (Stage) this.anchorPane.getScene().getWindow();
        primStage.setTitle(selectedDirectory.getName());
        Image icon = new Image(selectedDirectory.getAbsolutePath() + "/pack_icon.png");
        primStage.getIcons().set(0, icon);
        populateFileTree(selectedDirectory);
    }

    public void onFileSelect(){
        
        try{
            TreeItem<PackManager.PackObject> selectedItem = (TreeItem<PackManager.PackObject>) fileTree.getSelectionModel().getSelectedItem();
            if (!selectedItem.isLeaf())
                return;
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to change to open " +
                    selectedItem.getValue() + " \n unsaved progress will be lost");
            alert.showAndWait().filter(response -> response == ButtonType.OK)
                    .ifPresent(response -> {
                        try {
                            GenericBlockModel genericBlockModel = packManager.getBlockReader().getBlockModel(selectedItem.getValue().toString());
                            System.out.println(genericBlockModel);
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }

//                        Image image = new Image(selectedItem.getValue().getFile().getAbsolutePath(),
//                                texture2D.getWidth(), texture2D.getHeight(), true, false);
//                        texture2D.getGraphicsContext2D().clearRect(0,0, texture2D.getWidth(), texture2D.getHeight());
//                        texture2D.getGraphicsContext2D().drawImage(image, 0, 0);
                    });
        }catch (NullPointerException e){
            System.out.println(e.getMessage());
        }

    }

    public void populateFileTree(File packFolder){
//        currentTreeRoot = new TreeItem<>("Files");
//        currentTreeRoot.getChildren().add(getFileTree(new File(packFolder.getAbsoluteFile().getAbsolutePath() + "\\textures")));
//        currentTreeRoot.getChildren().add(getFileTree(new File(packFolder.getAbsoluteFile().getAbsolutePath() + "\\font")));
//        fileTree.setRoot(currentTreeRoot);
//        fileTree.setVisible(true);
        //TODO exclude blocks not in pack from file tree
        currentTreeRoot = new TreeItem<>(packManager.getPackName());
        try {
            currentTreeRoot.getChildren().add(packManager.getPackTree());
            fileTree.setRoot(currentTreeRoot);
            fileTree.setVisible(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ConcurrentModificationException e){
            System.out.println(e.getCause());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
    }
}