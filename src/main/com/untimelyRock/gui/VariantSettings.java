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
    final List<VariantOption> variantOptions = new ArrayList<>();

    public void populateVariantSettings(Map<String, Set<String>> variantOptions) throws IOException {
        this.variantOptions.clear();

        variantVBox.getChildren().clear();

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL variantOptionURL = classLoader.getResource("GUI/VariantOption.fxml");
        for(String optionName:variantOptions.keySet()){
            FXMLLoader loader = new FXMLLoader(variantOptionURL);
            Parent root = loader.load();
            variantVBox.getChildren().add(root);
            VariantOption variantOption = loader.getController();
            this.variantOptions.add(variantOption);
            variantOption.setup(optionName, variantOptions.get(optionName));
        }
    }

    public String getVariantString(){
        StringBuilder stringBuilder = new StringBuilder();
        for (String key: currentOptions.keySet()) {
            stringBuilder.append(key)
                    .append("=")
                    .append(currentOptions.get(key))
                    .append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
