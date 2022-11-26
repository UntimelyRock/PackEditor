package untimelyRock.gui;

import untimelyRock.EditorConfig;
import untimelyRock.packManager.JavaPackManager;
import untimelyRock.packManager.PackManager;
import untimelyRock.packManager.PackType;
import untimelyRock.packManager.entities.BlockVariantManager;
import untimelyRock.packManager.entities.PackIntegrityException;
import untimelyRock.packManager.entities.PackTreeViewObject;
import untimelyRock.packManager.entities.TreeViewObjectType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ConcurrentModificationException;
import java.util.Optional;
import java.util.ResourceBundle;


public class MainDisplayController implements Initializable {
    private PackManager packManager;

    @FXML private AnchorPane anchorPane;
    @FXML private Button openNewPackButton;
    @FXML private TreeView<PackTreeViewObject> fileTree;
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
        if(result.isEmpty())
            return null;

        if(result.get() != buttonSelectJava && result.get() != buttonSelectBedrock)
            return null;
        return (result.get() == buttonSelectJava ? PackType.JAVA_PACK : PackType.BEDROCK_PACK);
    }

    ///Returns null if interaction was canceled
    private File askForDefaultPack(PackType packType){
        Stage defaultPackDialogueStage = new Stage();
        FileChooser defaultPackFileChooser = new FileChooser();
        String properManifestName = (packType == PackType.BEDROCK_PACK) ? "manifest.json" : "pack.mcmeta";

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Default pack manifest", properManifestName);
        defaultPackFileChooser.getExtensionFilters().add(extFilter);
        defaultPackFileChooser.setTitle("select " + (properManifestName) + " of default pack");
        defaultPackFileChooser.setInitialDirectory(new File(EditorConfig.getDefaultPacksLocation()));

        File chosenFile = defaultPackFileChooser.showOpenDialog(defaultPackDialogueStage);
        if (chosenFile == null)
            return null;
        if (!chosenFile.getName().equals(properManifestName)){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error detecting pack");
            alert.show();
            return null;
        }
        return chosenFile;
    }

    public void openPackToEdit(ActionEvent ignoredE) {
        //https://code.makery.ch/blog/javafx-dialogs-official/
        PackType packType = askForPackType();
        String properManifestName = (packType == PackType.BEDROCK_PACK) ? "manifest.json" : "pack.mcmeta";
        if (packType == null)
            return;

        File defaultPackFile = askForDefaultPack(packType);
        if (defaultPackFile == null)
            return;

        Stage packFileChooserStage = new Stage();
        packFileChooserStage.setTitle("Open Resource Pack Folder");
        FileChooser packFileChooser = new FileChooser();
        packFileChooser.setTitle("select " + properManifestName + " of the pack you wish to edit");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Default pack manifest", properManifestName);
        packFileChooser.getExtensionFilters().add(extFilter);
        packFileChooser.setTitle("select " + (properManifestName) + " of pack");
        packFileChooser.setInitialDirectory(new File(EditorConfig.getDefaultPacksLocation()));//TODO better initial directory

        File selectedPackManifest = packFileChooser.showOpenDialog(packFileChooserStage);
        if (selectedPackManifest == null)
            return;
        if (!selectedPackManifest.getName().equals(properManifestName)){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error detecting pack");
            alert.show();
            return;
        }

        System.out.println(selectedPackManifest.getAbsolutePath());
        if(packType == PackType.JAVA_PACK){
            packManager = new JavaPackManager(selectedPackManifest.getParentFile(), defaultPackFile.getParentFile());
        }else if (packType == PackType.BEDROCK_PACK){
            return;//TODO Implement
            //packManager = new BedrockPackManager(selectedPackManifest.getParentFile(), selectedPackManifest.getParentFile());
        }

        Stage primStage = (Stage) this.anchorPane.getScene().getWindow();
        primStage.setTitle(packManager.getPackName());
        Image icon = new Image(packManager.getPackIcon().getAbsolutePath());
        primStage.getIcons().set(0, icon);
        populateFileTree(selectedPackManifest);
    }

    public void onFileSelect(){
        TreeItem<PackTreeViewObject> selectedItem = (fileTree.getSelectionModel().getSelectedItem());

        if (selectedItem == null || !selectedItem.isLeaf() )
            return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to change to open " +
                selectedItem.getValue() + " \n unsaved progress will be lost");
        alert.showAndWait().filter(response -> response == ButtonType.OK)
                .ifPresent(response -> {
                    try {
                        BlockVariantManager blockVariantManager;
                        blockVariantManager = packManager.getBlockVariantsByName(selectedItem.getValue().toString());
                        System.out.println(blockVariantManager);
                    } catch (IOException e) {
                        Alert exceptionAlert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                        exceptionAlert.show();
                    } catch (PackIntegrityException e) {
                        Alert exceptionAlert = new Alert(Alert.AlertType.ERROR, e.message);
                        exceptionAlert.show();
                    }
                });
    }


    public void populateFileTree(File packFolder){
//        currentTreeRoot = new TreeItem<>("Files");
//        currentTreeRoot.getChildren().add(get FileTree(new File(packFolder.getAbsoluteFile().getAbsolutePath() + "\\textures")));
//        currentTreeRoot.getChildren().add(getFileTree(new File(packFolder.getAbsoluteFile().getAbsolutePath() + "\\font")));
//        fileTree.setRoot(currentTreeRoot);
//        fileTree.setVisible(true);
        //TODO exclude blocks not in pack from file tree
        PackTreeViewObject rootTreeObject = new PackTreeViewObject(TreeViewObjectType.TEXT, packManager.getPackName());
        TreeItem<PackTreeViewObject> currentTreeRoot = new TreeItem<>(rootTreeObject);

        try {
            currentTreeRoot.getChildren().add(currentTreeRoot.getClass().cast(packManager.getPackTree()));
            fileTree.setRoot(currentTreeRoot);
            fileTree.setVisible(true);
        } catch (IOException | ConcurrentModificationException e) {
            Alert exceptionAlert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            exceptionAlert.show();
        }
    }

    public void open3dView(){
        ThreeDimDisplayController.launch();
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