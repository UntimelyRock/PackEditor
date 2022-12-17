package untimelyRock.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class VariantSettings implements Initializable {
    @FXML public VBox variantVBox;
    HashMap<String,String> currentOptions;
    final List<VariantOption> variantOptionControllers = new ArrayList<>();

    public void populateVariantSettings(Map<String, Set<String>> variantOptions) throws IOException {
        variantOptionControllers.clear();

        variantVBox.getChildren().clear();

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL variantOptionURL = classLoader.getResource("GUI/VariantOption.fxml");
        for(String optionName:variantOptions.keySet()){
            FXMLLoader loader = new FXMLLoader(variantOptionURL);
            Parent root = loader.load();
            variantVBox.getChildren().add(root);
            VariantOption variantOption = loader.getController();
            variantOptionControllers.add(variantOption);
            variantOption.setup(optionName, variantOptions.get(optionName));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
