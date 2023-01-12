package untimelyRock.gui.entities;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Rotate;

import java.util.HashMap;

public class ImageCube extends Group{
    HashMap<String, ImageView> faces = new HashMap<>();


    private final static int[][][] faceMatrix = new int[][][]{
            //Offset  || Rotation
            //x  y  z    x  y  z
            {{0, 0, 0}, {90, 0, 0}},//up
            {{0, 1, 0}, {-90, 0, 0}},//down
            {{0, 0, 0}, {0, 0, 0}},//north
            {{0, 0, 0}, {0, 180, 0}},//south
            {{0, 0, 0}, {0, 0, -90}},//east
            {{0, 0, 0}, {0, 0, 90}},//west
    };
    private final static String[] faceNameOrder = new String[]{
            "up",
            "down",
            "north",
            "south",
            "east",
            "west"
    };

    public ImageCube(double x, double y, double z, javafx.event.EventHandler<? super javafx.scene.input.MouseEvent> brushEvent){
        super();
        for (int i = 0; i < faceNameOrder.length; i++) {
            ImageView faceView = new ImageView();
            faceView.setScaleX(x);
            faceView.setScaleY(y);
            faceView.setScaleZ(z);

            faceView.setTranslateX(faceMatrix[i][0][0] * x);
            faceView.setTranslateY(faceMatrix[i][0][1] * y);
            faceView.setTranslateZ(faceMatrix[i][0][2] * z);

            Rotate rotateX = new Rotate(faceMatrix[i][1][0], 0,0,0, Rotate.X_AXIS);
            Rotate rotateY = new Rotate(faceMatrix[i][1][1], 0,0,0, Rotate.Y_AXIS);
            Rotate rotateZ = new Rotate(faceMatrix[i][1][2], 0,0,0, Rotate.Z_AXIS);

            faceView.getTransforms().addAll(rotateX,rotateY,rotateZ);

            faceView.setOnMouseClicked(brushEvent);
            faceView.setOnMouseClicked(brushEvent);

            faces.put(faceNameOrder[i], faceView);
            getChildren().add(faceView);
        }
    }

    public void drawTextureAtlas(HashMap<String, Image> images){
        for (String face:
             faces.keySet()) {
            faces.get(face).setImage(images.getOrDefault(face, new WritableImage(16,16)));
        }
    }
}
