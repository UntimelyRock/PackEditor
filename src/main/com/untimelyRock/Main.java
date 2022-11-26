package untimelyRock;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    public static void main(String[] args){
        //TODO setup args https://stackoverflow.com/questions/7341683/parsing-arguments-to-a-java-command-line-program
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Parent root = FXMLLoader.load((Objects.requireNonNull(classLoader.getResource("GUI/MainDisplay.fxml"))));
        Scene scene = new Scene(root);
//        scene.setFill(Color.BLACK);
        Image icon = new Image("icon.jpg");
        primaryStage.getIcons().add(icon);

        primaryStage.setTitle("MCPackRecolor");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
