package untimelyRock.gui;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import untimelyRock.Main;
import untimelyRock.packManager.PackManager;
import untimelyRock.packManager.entities.PackIntegrityException;
import untimelyRock.packManager.entities.PackTreeViewObject;
import untimelyRock.packManager.entities.TreeViewObjectType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

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
    private VariantSettings variantSettingsController;
    private Group viewObjects;
    HamburgerSlideCloseTransition hamburgerTransition;

    Point3D cameraRotation = new Point3D(0d,0d,0d);

    Point2D mousePos;

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

        editorSubScene.setOnMousePressed((MouseEvent event) -> {
            mousePos = new Point2D(
                    event.getX(),
                    event.getY()
            );
        });

        editorSubScene.setOnMouseDragged((MouseEvent event) -> {
            Point2D mouseOld = mousePos;
            mousePos = new Point2D(
                    event.getSceneX(),
                    event.getSceneY()
            );

            Point2D mouseDelta = mouseOld.subtract(mousePos);

            double modifier = 10.0;
            mouseDelta.multiply(modifier);
            if (event.isPrimaryButtonDown()) {

            } else if (event.isSecondaryButtonDown()) {

            } else if (event.isMiddleButtonDown()) {
                rotateCameraBy(new Point3D(mouseDelta.getY(), -mouseDelta.getX(), 0));
            }
        });


        viewObjects = new Group();
        viewRoot.getChildren().add(viewObjects);

        Box block = new Box(16,16,16);
        viewObjects.getChildren().add(block);



        Main.getPrimaryStage().addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.W) {
                rotateCameraBy(new Point3D(1d, 0d, 0d));
            } else if (event.getCode() == KeyCode.S) {
                rotateCameraBy(new Point3D(-1d, 0d, 0d));
            } else if (event.getCode() == KeyCode.D) {
                rotateCameraBy(new Point3D(0d, 1d, 0d));
            } else if (event.getCode() == KeyCode.A) {
                rotateCameraBy(new Point3D(0d, -1d, 0d));
            } else if (event.getCode() == KeyCode.Q) {
                rotateCameraBy(new Point3D(0d, 0d, 1d));
            } else if (event.getCode() == KeyCode.E) {
                rotateCameraBy(new Point3D(0d, 0d, -1d));
            } else if (event.getCode() == KeyCode.R) {
                editorCamera.setTranslateZ(editorCamera.getTranslateZ() + 10);
            }
        });

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

        hamburgerTransition = new HamburgerSlideCloseTransition(variantHamburger);
        hamburgerTransition.setRate(-1);
        variantHamburger.addEventHandler(MouseEvent.MOUSE_CLICKED, (Event event) -> {
            variantDrawer.toggle();
        });
        variantDrawer.setOnDrawerOpening((event) -> {
            hamburgerTransition.setRate(hamburgerTransition.getRate() * -1);
            hamburgerTransition.play();
            variantDrawer.setMinWidth(116);
        });
        variantDrawer.setOnDrawerClosed((event) -> {
            hamburgerTransition.setRate(hamburgerTransition.getRate() * -1);
            hamburgerTransition.play();
            variantDrawer.setMinWidth(0);
        });
    }
    //TODO finish converting this to work on a sub screen so to speak

    public void centerCamera(){
        editorCamera.getTransforms().clear();
        editorCamera.setTranslateX(editorSubScene.getWidth() / -2d);//TODO Fix off center when resizing subscene wile looking at side
        editorCamera.setTranslateY(editorSubScene.getHeight() / -2d);
    }

    public void rotateCameraBy(Point3D rotation){
        double editorX = editorSubScene.getWidth() / 2d;
        double editorY = editorSubScene.getHeight() / 2d;


        //cameraRotation = cameraRotation.add(rotation);
        Rotate rotateX = new Rotate(rotation.getX(),editorX,editorY,0, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(rotation.getY(),editorX,editorY,0, Rotate.Y_AXIS);
        Rotate rotateZ = new Rotate(rotation.getZ(),editorX,editorY,0, Rotate.Z_AXIS);

        editorCamera.getTransforms().addAll(rotateX,rotateY,rotateZ);
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

    public void buildBlockInDisplay(){
        viewObjects.getChildren().clear();
//        variantSettingsController

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
                            String blockName = selectedItem.getValue().toString();
                            packManager.openBlockOfName(blockName);
                            Map<String, Set<String>> blockVariants = packManager.getCurrentVariantOptions();
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
