package untimelyRock.gui;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import com.sun.javafx.font.PrismMetrics;
import com.sun.prism.impl.PrismSettings;
import com.sun.prism.j2d.PrismPrintPipeline;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import untimelyRock.gui.entities.ImageCube;
import untimelyRock.packManager.PackManager;
import untimelyRock.packManager.entities.PackIntegrityException;
import untimelyRock.packManager.entities.PackTreeViewObject;
import untimelyRock.packManager.entities.TreeViewObjectType;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static untimelyRock.gui.ErrorHandler.logAndShowException;

public class TextureEditor implements Initializable {
//    public PerspectiveCamera editorCamera;
    @FXML
    private JFXDrawer variantDrawer;

    @FXML private JFXHamburger variantHamburger;

    @FXML private TreeView<PackTreeViewObject> packObjectTreeView;

    @FXML private SubScene editorSubScene;
    @FXML private AnchorPane editorSubScenePane;

    private SmartCamera editorCamera;
    private PackManager packManager;
    private VariantSettings variantSettingsController;
    private Group viewObjects;
    HamburgerSlideCloseTransition hamburgerTransition;

    Point3D cameraRotation = new Point3D(0d,0d,0d);

    WritableImage currentTexture;

    Point2D mousePos;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Group viewRoot = new Group();

        editorSubScene.setFill(Color.GREY);
        editorSubScene.heightProperty().bind(editorSubScenePane.heightProperty());
        editorSubScene.widthProperty().bind(editorSubScenePane.widthProperty());
//        editorSubScene.setCamera(editorCamera);
        editorSubScene.setManaged(false);


        editorSubScene.setOnMousePressed((MouseEvent event) -> {
            //THe code seems to work without this but im scared to remove it
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
                if(event.isAltDown()){

                    editorCamera.zoomBy(mouseDelta.getY());
                }else if(event.isShiftDown()){

                }else{
                    editorCamera.rotateCameraBy(new Point3D(mouseDelta.getY(), -mouseDelta.getX(), 0));
                }
            }
        });

        editorCamera = new SmartCamera(editorSubScene);

        editorCamera.setNearClip(0.01);

        viewObjects = new Group();
        viewRoot.getChildren().add(viewObjects);
        viewRoot.getChildren().add(editorCamera);
        editorSubScene.setRoot(viewRoot);


        Image texture = new Image(Objects.requireNonNull(classLoader.getResourceAsStream("nonTexture.png")),16,16,false,false);
        currentTexture = new WritableImage(16,16);

        for (int i = 0; i < 16; i++) {
            for (int i2 = 0; i2 < 16; i2++) {
//                currentTexture.getPixelWriter().setColor(i,i2, Color.WHITE);
                currentTexture.getPixelWriter().setColor(i,i2, texture.getPixelReader().getColor(i,i2));
            }
        }



        Box block = new Box(16,16,16);

        PhongMaterial phongMaterial = new PhongMaterial();

        phongMaterial.setDiffuseMap(currentTexture);


        block.setMaterial(phongMaterial);
        viewObjects.getChildren().add(block);

        //Initialize Variant Drawer
        try {
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
            variantDrawer.setDisable(false);
            hamburgerTransition.setRate(hamburgerTransition.getRate() * -1);
            hamburgerTransition.play();

            variantDrawer.setMinWidth(116);
        });
        variantDrawer.setOnDrawerClosed((event) -> {
            variantDrawer.setDisable(true);
            hamburgerTransition.setRate(hamburgerTransition.getRate() * -1);
            hamburgerTransition.play();
            variantDrawer.setMinWidth(0);
        });
        variantDrawer.setDisable(true);

    }
    //TODO finish converting this to work on a sub screen so to speak

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

    static class SmartCamera extends PerspectiveCamera{
        double minZoom = -700;//TODO scale with scene size
        double maxZoom = 5000;
        SubScene connectedScene;
//        Rotate rotationX, rotationY, rotationZ;

        SmartCamera(SubScene sceneToConnectTo){
            super();
            sceneToConnectTo.setCamera(this);
            this.connectedScene = sceneToConnectTo;
            ChangeListener<Number> centerCameraListener = (observable, oldValue, newValue) -> centerCamera();
            connectedScene.heightProperty().addListener(centerCameraListener);
            connectedScene.widthProperty().addListener(centerCameraListener);
        }

        public void zoomBy(double amount){
            this.getTransforms().clear();

            this.setTranslateZ(Math.max(minZoom, Math.min(maxZoom, getTranslateZ()  + amount)));
        }

        public void centerCamera(){
            this.getTransforms().clear();
            this.setTranslateX(connectedScene.getWidth() / -2d);//TODO Fix off center when resizing subscene wile looking at side
            this.setTranslateY(connectedScene.getHeight() / -2d);
        }

        public void rotateCameraBy(Point3D rotation){
            double editorX = connectedScene.getWidth() / 2d;
            double editorY = connectedScene.getHeight() / 2d;

            //cameraRotation = cameraRotation.add(rotation);
            Rotate rotateX = new Rotate(rotation.getX(),editorX,editorY,-getTranslateZ(), Rotate.X_AXIS);
            Rotate rotateY = new Rotate(rotation.getY(),editorX,editorY,-getTranslateZ(), Rotate.Y_AXIS);
            Rotate rotateZ = new Rotate(rotation.getZ(),editorX,editorY,-getTranslateZ(), Rotate.Z_AXIS);

            getTransforms().addAll(rotateX,rotateY,rotateZ);
        }
    }
}
