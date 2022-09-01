package GUI;

import com.sun.source.tree.Tree;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import packManager.PackManager;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
            TreeItem<TreeFile> selectedItem = (TreeItem<TreeFile>) fileTree.getSelectionModel().getSelectedItem();
            if (!selectedItem.isLeaf())
                return;

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to change to open " +
                    selectedItem.getValue() + " \n unsaved progress will be lost");
            alert.showAndWait().filter(response -> response == ButtonType.OK)
                    .ifPresent(response -> {
                        Image image = new Image(selectedItem.getValue().getFile().getAbsolutePath(),
                                texture2D.getWidth(), texture2D.getHeight(), true, false);
                        texture2D.getGraphicsContext2D().clearRect(0,0, texture2D.getWidth(), texture2D.getHeight());
                        texture2D.getGraphicsContext2D().drawImage(image, 0, 0);
                    });
        }catch (NullPointerException e){

        }
    }






    public void populateFileTree(File packFolder){
        currentTreeRoot = new TreeItem<>("Files");
        currentTreeRoot.getChildren().add(getFileTree(new File(packFolder.getAbsoluteFile().getAbsolutePath() + "\\textures")));
        currentTreeRoot.getChildren().add(getFileTree(new File(packFolder.getAbsoluteFile().getAbsolutePath() + "\\font")));
        fileTree.setRoot(currentTreeRoot);
        fileTree.setVisible(true);
    }

    static TreeItem getFileTree(File file) {

        if (file.isDirectory()) {
            System.out.println(file.getName());
            TreeItem directoryTree = new TreeItem(file.getName());
            File[] files = file.listFiles();
            assert files != null;
            for (File value : files) directoryTree.getChildren().add(getFileTree(value));
            return directoryTree;
        }else /*if (file.getName().contains(".png"))*/{
            System.out.println(file.getName());
            return new TreeItem(new TreeFile(file.getName(), file));

        }
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        this.packManager = new PackManager();
    }
}