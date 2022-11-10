package fZirus.GUI;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TreeItem;

import java.io.File;

public class TreeFile {
    private SimpleStringProperty name;
    private File file;
    public String getName() {
        return this.name.get();
    }

    public void setName(String fName) {
        this.name.set(fName);
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public File getFile() {
        return file;
    }

    public TreeFile(String name, File file){
        this.name = new SimpleStringProperty(name);
        this.file = file;
    }

    static TreeItem getFileTree(File file) {

        if (file.isDirectory()) {
            System.out.println(file.getName());
            TreeItem directoryTree = new TreeItem(file.getName());
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++)
                directoryTree.getChildren().add(getFileTree(files[i]));
            return directoryTree;
        }else if (file.getName().contains(".png")){
            System.out.println(file.getName());
            return new TreeItem(new TreeFile(file.getName(), file));

        }
        else return null;
    }
}
