package packManager;

import javafx.scene.control.TreeItem;

import java.io.File;

public class PackManager {
    public PackManager(){

    }
    public boolean isPackFolder(File folder) throws SecurityException{
        File manifest = new File(folder.getAbsolutePath() + "/manifest.json");
        return !manifest.exists();
    }

    public void LoadPack(){

    }

//    public TreeItem generateTreeView(){
//
//    }

}
