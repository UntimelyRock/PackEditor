package untimelyRock.gui;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import untimelyRock.Main;
import untimelyRock.packManager.PackManager;
import untimelyRock.packManager.entities.PackIntegrityException;
import untimelyRock.packManager.entities.PackTreeViewObject;
import untimelyRock.packManager.entities.TreeViewObjectType;
import untimelyRock.packManager.javaPackManager.BlockVariantContainer;

import javax.xml.crypto.dsig.Transform;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import static untimelyRock.gui.ErrorHandler.logAndShowException;

public class TextureEditor implements Initializable {
    @FXML
    private JFXDrawer variantDrawer;

    @FXML private JFXHamburger variantHamburger;

    @FXML private TreeView<PackTreeViewObject> packObjectTreeView;

    @FXML private SubScene editorSubScene;

    @FXML private Camera editorCamera;

    @FXML private AnchorPane editorSubScenePane;

    private PackManager packManager;
    private BlockVariantContainer blockVariantManager;
    private VariantSettings variantSettingsController;
    private Group viewObjects;

    Point3D cameraRotation = new Point3D(0,0,0);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Group viewRoot = new Group();


        editorSubScene.setFill(Color.GREY);
        editorSubScene.heightProperty().bind(editorSubScenePane.heightProperty());
        editorSubScene.widthProperty().bind(editorSubScenePane.widthProperty());
        editorSubScene.setCamera(editorCamera);
        editorSubScene.setManaged(false);
        ChangeListener<Number> centerCameraListener = (observable, oldValue, newValue) -> centerCamera();
        editorSubScene.heightProperty().addListener(centerCameraListener);
        editorSubScene.widthProperty().addListener(centerCameraListener);

        viewObjects = new Group();
        viewRoot.getChildren().add(viewObjects);
        Box block = new Box(20,5,10);
        viewObjects.getChildren().add(block);




        //editorCamera.getTransforms().setAll(rotateX, rotateY);

        Main.getPrimaryStage().addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case W -> rotateCameraBy(new Point3D(1,0,0));
                case S -> rotateCameraBy(new Point3D(-1,0,0));
                case D -> rotateCameraBy(new Point3D(0,1,0));
                case A -> rotateCameraBy(new Point3D(0,-1,0));
                case Q -> rotateCameraBy(new Point3D(0,0,1));
                case E -> rotateCameraBy(new Point3D(0,0,-1));
            }
        });
//        editorSubScene.setOnKeyPressed(event -> {
//            switch (event.getCode()) {
//                case A -> editorCamera.translateZProperty().set(editorCamera.getTranslateZ() + 100);
//                case D -> editorCamera.translateZProperty().set(editorCamera.getTranslateZ() - 100);
//            }
//        });

        //Initialize 3D View
        viewRoot.getChildren().add(editorCamera);


        editorSubScene.setRoot(viewRoot);



        //Initialize Variant Drawer
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            FXMLLoader loader = new FXMLLoader(classLoader.getResource("GUI/VariantSettings.fxml"));
            Parent root = loader.load();
            variantDrawer.setSidePane(root);
            variantDrawer.setMinWidth(0);
            variantSettingsController = loader.getController();
        } catch (IOException e) {
            logAndShowException(e);
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
    //TODO finish converting this to work on a sub screen so to speak

    public void centerCamera(){
        editorCamera.setTranslateX(editorSubScene.getWidth() / -2d);
        editorCamera.setTranslateY(editorSubScene.getHeight() / -2d);
    }

    public void rotateCameraBy(Point3D rotation){
        double editorX = editorSubScene.getWidth() / 2d;
        double editorY = editorSubScene.getHeight() / 2d;

        cameraRotation = cameraRotation.add(rotation);
        Rotate rotateX = new Rotate(cameraRotation.getX(),editorX,editorY,0, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(cameraRotation.getY(),editorX,editorY,0, Rotate.Y_AXIS);
        Rotate rotateZ = new Rotate(cameraRotation.getZ(),editorX,editorY,0, Rotate.Z_AXIS);

        editorCamera.getTransforms().setAll(rotateX, rotateY, rotateZ);
    }


    public void updatePackManager(PackManager packManager){
        this.packManager = packManager;

        PackTreeViewObject rootTreeObject = new PackTreeViewObject(TreeViewObjectType.TEXT, packManager.getPackName());
        TreeItem<PackTreeViewObject> currentTreeRoot = new TreeItem<>(rootTreeObject);
        try {
            currentTreeRoot.getChildren().add(packManager.getPackTree());
            packObjectTreeView.setRoot(currentTreeRoot);
            packObjectTreeView.setVisible(true);
        } catch (IOException | ConcurrentModificationException | URISyntaxException e) {
            logAndShowException(e);
        }
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
                            Map<String, Set<String>> blockVariants = blockVariantManager.getVariantOptions();
                            variantSettingsController.populateVariantSettings(blockVariants);
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
}
