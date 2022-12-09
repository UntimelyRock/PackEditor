package untimelyRock.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

public class VariantOption implements Initializable {
    String variantName;
    @FXML Label variantLabel;
    @FXML CheckBox variantTrueFalse;
    @FXML ComboBox<String> variantCombobox;

    public void setup(String vName, Set<String> options){
        variantName = vName;
        variantLabel.setText(vName);
        if(options.size() == 2){
            if(options.stream().anyMatch("true"::equalsIgnoreCase) && options.stream().anyMatch("false"::equalsIgnoreCase)){
                variantTrueFalse.setVisible(true);
                return;
            }
        }
        variantCombobox.getItems().addAll(options);
        variantCombobox.setVisible(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
