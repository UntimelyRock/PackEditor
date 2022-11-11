package fZirus.packManager;

import fZirus.Main;
import javafx.scene.control.TreeItem;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class PackManager {
    BlockReader blockReader;
    String packLocation = "";

    public BlockReader getBlockReader() {
        return blockReader;
    }

    public PackManager(String packLocation){
        this.packLocation = packLocation;
        blockReader = new BlockReader(packLocation);

    }
    public boolean isPackFolder(File folder) throws SecurityException{
        File manifest = new File(folder.getAbsolutePath() + "/manifest.json");
        return !manifest.exists();
    }

    public String getPackName(){
        return "WIP";
    }

    public void LoadPack(){

    }

    public TreeItem getPackTree() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        TreeItem blockNode = new TreeItem("Blocks");
        List<String> lines = Files.readAllLines(new File(Objects.requireNonNull(classLoader.getResource("defaults/blocklist.txt")).getPath()).toPath(), StandardCharsets.UTF_8);
        HashMap<Integer, TreeItem> expectedDepth = new HashMap<>();
        expectedDepth.put(-1,blockNode);//add as depth increases, if depth decreases delete all deeper entries

        for (String line : lines){
            if (!line.startsWith("//") && !line.isBlank()){//TODO make gaurd if
                int spaceCount = 0;
                for (char c : line.toCharArray()) {
                    if (c != "\s".charAt(0)) {
                        break;
                    }
                    spaceCount++;
                }

                int previousMaxDepth = -1;
                for (int depth : expectedDepth.keySet()) {
                    if(depth < spaceCount){
                        previousMaxDepth = depth;
                    }else if (depth >= spaceCount) {
                        //expectedDepth.remove(depth);
                        //TODO see if this actually need to delete stuff, if so find a way without concurrent whatever exeption
                    }
                }

                TreeItem<PackObject> newTreeItem = new TreeItem<>(new PackObject(PackObject.TREE_TYPE.BLOCK, line.strip()));
                expectedDepth.get(previousMaxDepth).getChildren().add(newTreeItem);
                expectedDepth.put(spaceCount, newTreeItem);
            }
        }
        return blockNode;
    }


    public class PackObject{
        enum TREE_TYPE {
            BLOCK
        }

        String name = "";

        @Override
        public String toString() {
            return name;
        }

        TREE_TYPE type;



        PackObject(TREE_TYPE type, String name){
            this.type = type;
            this.name = name;
        }


    }

}
