package untimelyRock.gui;

import javafx.scene.control.Alert;

public class ErrorHandler {
    public static void logAndShowException(Exception e){
        //TODO add logger
        Alert exceptionAlert = new Alert(Alert.AlertType.ERROR, e.getMessage());
        exceptionAlert.show();
    }
}
