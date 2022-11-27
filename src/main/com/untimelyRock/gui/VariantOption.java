package untimelyRock.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class VariantOption implements Initializable {
    @FXML Label variantLabel;
    @FXML CheckBox variantTrueFalse;
    @FXML ComboBox<String> variantCombobox;

    public void setup(String vName, Set<String> options){
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
