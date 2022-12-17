package untimelyRock.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.layout.BorderPane;
import untimelyRock.EditorConfig;
import untimelyRock.packManager.JavaPackManager;
import untimelyRock.packManager.PackManager;
import untimelyRock.packManager.PackType;
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
    TextureEditor textureEditor;
    Node textureEditorNode;
    @FXML private BorderPane basePane;
    @FXML private AnchorPane editorAnchor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            FXMLLoader textureEditorLoader = new FXMLLoader(classLoader.getResource("GUI/TextureEditor.fxml"));
            textureEditorNode = textureEditorLoader.load();
            editorAnchor.getChildren().add(textureEditorNode);
            textureEditor = textureEditorLoader.getController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
        textureEditor.updatePackManager(packManager);
    }





    private void logAndShowException(Exception e){
        //TODO add logger
        Alert exceptionAlert = new Alert(Alert.AlertType.ERROR, e.getMessage());
        exceptionAlert.show();
    }


}