package UntimelyRock.packManager;

import javafx.scene.control.TreeItem;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import UntimelyRock.packManager.entities.PackTreeViewObject;

public class BedrockPackManager /*extends PackManager*/{



    public BedrockPackManager(File packLocation, File defaultPack){
        //super(packLocation, defaultPack, PackType.BEDROCK_PACK);
    }

    public static boolean isPackFolder(File folder) throws SecurityException{
        File manifest = new File(folder.getAbsolutePath() + "/manifest.json");
        return !manifest.exists();
    }

    public String getPackName(){
        return "WIP";
    }

    public void LoadPack(){

    }

    //@Override
    public File getPackIcon() {
        return null;
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

                TreeItem<PackTreeViewObject> newTreeItem = new TreeItem<>(new PackTreeViewObject(PackTreeViewObject.TREE_TYPE.BLOCK, line.strip(), line.strip()));
                expectedDepth.get(previousMaxDepth).getChildren().add(newTreeItem);
                expectedDepth.put(spaceCount, newTreeItem);
            }
        }
        return blockNode;
    }
}
