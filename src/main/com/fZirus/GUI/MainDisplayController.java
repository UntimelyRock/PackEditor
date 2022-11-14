package fZirus.GUI;

import fZirus.packManager.PackType;
import fZirus.packManager.entities.BlockVariantManager;
import fZirus.packManager.entities.GenericBlockModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
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

    ThreeDViewManager threeDViewManager;

    @FXML private AnchorPane anchorPane;
    @FXML private Button openNewPackButton;
    @FXML private TreeView fileTree;
    @FXML private Canvas texture2D;
    @FXML private SubScene threeDView;

    @FXML private Camera mainCamera;

    public PackType askForPackType(){
        Alert askForDefaultType = new Alert(Alert.AlertType.INFORMATION, "");
        askForDefaultType.setTitle("");
        askForDefaultType.setHeaderText("Do you want to make/edit a java or bedrock pack");

        ButtonType buttonSelectJava = new ButtonType("Java Edition");
        ButtonType buttonSelectBedrock = new ButtonType("Bedrock Edition");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        askForDefaultType.getButtonTypes().setAll(buttonSelectJava, buttonSelectBedrock, buttonTypeCancel);

        Optional<ButtonType> result = askForDefaultType.showAndWait();
        if(result.get() != buttonSelectJava && result.get() != buttonSelectBedrock)
            return null;
        return (result.get() == buttonSelectJava ? PackType.JAVA_PACK : PackType.BEDROCK_PACK);
    }

    public void openNewPack(ActionEvent e) {
        //https://code.makery.ch/blog/javafx-dialogs-official/
        PackType packType = askForPackType();
        if (packType == null) return;

        Stage defaultPackDialogueStage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("select "
                + ((packType == PackType.BEDROCK_PACK) ? "manifest.json" : "pack.mcmeta")
                + " of default pack");
        File chosenFile = fileChooser.showOpenDialog(defaultPackDialogueStage);
        if (chosenFile.getName() != ((packType == PackType.BEDROCK_PACK) ? "manifest.json" : "pack.mcmeta")){
            return;
        }

        Stage primaryStage = new Stage();
        primaryStage.setTitle("Open Resource Pack Folder");
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(primaryStage);
        if (selectedDirectory == null) {
            return;
        }


        try {
            if (PackManager.isPackFolder(selectedDirectory)) {
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
        TreeItem<PackManager.PackObject> selectedItem = (TreeItem<PackManager.PackObject>) fileTree.getSelectionModel().getSelectedItem();

        if (selectedItem == null || !selectedItem.isLeaf() )
            return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to change to open " +
                selectedItem.getValue() + " \n unsaved progress will be lost");
        alert.showAndWait().filter(response -> response == ButtonType.OK)
                .ifPresent(response -> {
                    try {
                        BlockVariantManager blockVariantManager = packManager.getBlockReader().getBlockVarientsByName(selectedItem.getValue().toString());
                        System.out.println(blockVariantManager);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
//                        Image image = new Image(selectedItem.getValue().getFile().getAbsolutePath(),
//                                texture2D.getWidth(), texture2D.getHeight(), true, false);
//                        texture2D.getGraphicsContext2D().clearRect(0,0, texture2D.getWidth(), texture2D.getHeight());
//                        texture2D.getGraphicsContext2D().drawImage(image, 0, 0);
                });
    }


    public void populateFileTree(File packFolder){
//        currentTreeRoot = new TreeItem<>("Files");
//        currentTreeRoot.getChildren().add(get FileTree(new File(packFolder.getAbsoluteFile().getAbsolutePath() + "\\textures")));
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

    public void open3dView(){
        ThreedimdisplayController.launch();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");

        Box block = new Box();
        block.setDepth(16);
        block.setHeight(16);
        block.setWidth(16);

        block.setTranslateX(threeDView.getWidth() / 2d);
        block.setTranslateY(threeDView.getHeight() / 2d);



        mainCamera.setTranslateZ(-300);
        mainCamera.setRotate(40);

        Group viewObjects = new Group();

        viewObjects.getChildren().add(block);
        threeDView.setRoot(viewObjects);
        threeDView.setFill(Color.DARKGREY);
    }
}