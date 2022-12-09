package untimelyRock.gui;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.SubScene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import untimelyRock.EditorConfig;
import untimelyRock.packManager.JavaPackManager;
import untimelyRock.packManager.PackManager;
import untimelyRock.packManager.PackType;
import untimelyRock.packManager.javaPackManager.BlockVariantContainer;
import untimelyRock.packManager.entities.PackIntegrityException;
import untimelyRock.packManager.entities.PackTreeViewObject;
import untimelyRock.packManager.entities.TreeViewObjectType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;


public class MainDisplayController implements Initializable {
    private PackManager packManager;
    private BlockVariantContainer blockVariantManager;
    private VariantSettings variantSettingsController;


    @FXML private JFXDrawer variantDrawer;
    @FXML private JFXHamburger variantHamburger;
    @FXML private BorderPane basePane;
    @FXML private Button openNewPackButton;
    @FXML private TreeView<PackTreeViewObject> packObjectTreeView;
    @FXML private SubScene editorSubScene;
    @FXML private Camera editorCamera;
    @FXML private AnchorPane editorSubScenePane;



    public PackType askForPackType(){
        Alert askForDefaultType = new Alert(Alert.AlertType.INFORMATION, "");
        askForDefaultType.setTitle("");
        askForDefaultType.setHeaderText("Do you want to make/edit a java or bedrock pack");

        ButtonType buttonSelectJava = new ButtonType("Java Edition");
        ButtonType buttonSelectBedrock = new ButtonType("Bedrock Edition");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        askForDefaultType.getButtonTypes().setAll(buttonSelectJava, buttonSelectBedrock, buttonTypeCancel);

        Optional<ButtonType> result = askForDefaultType.showAndWait();
        if (result.isEmpty() || (result.get() != buttonSelectJava && result.get() != buttonSelectBedrock))
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

        System.out.println("Opening pack: " + selectedPackManifest.getAbsolutePath());
        if(packType == PackType.JAVA_PACK){
            packManager = new JavaPackManager(selectedPackManifest.getParentFile(), defaultPackFile.getParentFile());
        }else if (packType == PackType.BEDROCK_PACK){
            return;//TODO Implement
            //packManager = new BedrockPackManager(selectedPackManifest.getParentFile(), selectedPackManifest.getParentFile());
        }

        Stage primStage = (Stage) basePane.getScene().getWindow();
        primStage.setTitle(packManager.getPackName());
        Image icon = new Image(packManager.getPackIcon().getAbsolutePath());
        primStage.getIcons().set(0, icon);
        populateFileTree(selectedPackManifest);
    }

    public void onFileSelect(){
        TreeItem<PackTreeViewObject> selectedItem = (packObjectTreeView.getSelectionModel().getSelectedItem());

        if (selectedItem == null || !selectedItem.isLeaf() )
            return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to change to open " +
                selectedItem.getValue() + " \n unsaved progress will be lost");
        alert.showAndWait().filter(response -> response == ButtonType.OK)
                .ifPresent((response) -> {
                    try {
                        if(selectedItem.getValue().type == TreeViewObjectType.BLOCK){
                            blockVariantManager = packManager.getBlockVariantsByName(selectedItem.getValue().toString());
                            variantSettingsController.populateVariantSettings(blockVariantManager.getVariantOptions());
                            System.out.println(selectedItem.getValue().getName());
                        }

                    } catch (IOException e) {
                        logAndShowException(e);
                    } catch (PackIntegrityException e) {
                        Alert exceptionAlert = new Alert(Alert.AlertType.ERROR, e.message);//TODO Make better
                        exceptionAlert.show();
                    }
                });
    }

    public void populateFileTree(File packFolder){
        //TODO exclude blocks not in pack from file tree
        PackTreeViewObject rootTreeObject = new PackTreeViewObject(TreeViewObjectType.TEXT, packManager.getPackName());
        TreeItem<PackTreeViewObject> currentTreeRoot = new TreeItem<>(rootTreeObject);

        try {
            currentTreeRoot.getChildren().add(currentTreeRoot.getClass().cast(packManager.getPackTree()));
            packObjectTreeView.setRoot(currentTreeRoot);
            packObjectTreeView.setVisible(true);
        } catch (IOException | ConcurrentModificationException e) {
            logAndShowException(e);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Box block = new Box();
        block.setDepth(16);
        block.setHeight(16);
        block.setWidth(16);

        block.setTranslateX(editorSubScene.getWidth() / 2d);
        block.setTranslateY(editorSubScene.getHeight() / 2d);

        editorCamera.setTranslateZ(-300);
        editorCamera.setRotate(40);

        Group viewObjects = new Group();

        viewObjects.getChildren().add(block);
        editorSubScene.setRoot(viewObjects);
        editorSubScene.setFill(Color.DARKGREY);


        editorSubScene.heightProperty().bind(editorSubScenePane.heightProperty());
        editorSubScene.widthProperty().bind(editorSubScenePane.widthProperty());



        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            FXMLLoader loader = new FXMLLoader(classLoader.getResource("GUI/VariantSettings.fxml"));
            Parent root = loader.load();
            variantSettingsController = loader.getController();
            variantDrawer.setSidePane(root);
            variantDrawer.setMinWidth(0);
        } catch (IOException e) {
            ErrorHandler.logAndShowException(e);
        }
        HamburgerSlideCloseTransition task = new HamburgerSlideCloseTransition(variantHamburger);
        task.setRate(-1);
        variantHamburger.addEventHandler(MouseEvent.MOUSE_CLICKED, (Event event) -> {
            variantDrawer.toggle();
        });
        variantDrawer.setOnDrawerOpening((event) -> {
            task.setRate(task.getRate() * -1);
            task.play();
            variantDrawer.setMinWidth(116);
        });
        variantDrawer.setOnDrawerClosed((event) -> {
            task.setRate(task.getRate() * -1);
            task.play();
            variantDrawer.setMinWidth(0);
        });

    }

    private void logAndShowException(Exception e){
        //TODO add logger
        Alert exceptionAlert = new Alert(Alert.AlertType.ERROR, e.getMessage());
        exceptionAlert.show();
    }


}