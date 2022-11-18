package UntimelyRock.packManager;

import UntimelyRock.packManager.entities.BlockVariantManager;
import UntimelyRock.packManager.entities.PackTreeViewObject;
import UntimelyRock.packManager.entities.PackIntegrityException;
import com.google.gson.Gson;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class JavaPackManager extends PackManager{



    public JavaPackManager(File packLocation, File defaultPack){
        super(packLocation, defaultPack, PackType.JAVA_PACK);

    }


    public static boolean isPackFolder(File folder) throws SecurityException{
        File manifest = new File(folder.getAbsolutePath() + "/pack.mcmeta");
        return !manifest.exists();
    }

    public String getPackName(){
        return "WIP";
    }
    public File getPackIcon() {
        return new File(packLocation.getAbsolutePath() + "/pack.png");
    }



    public void LoadPack(){

    }

    @Override

    public TreeItem getPackTree() throws IOException {//TODO auto generate
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        TreeItem<PackTreeViewObject> blockNode = new TreeItem(new PackTreeViewObject(PackTreeViewObject.TREE_TYPE.TEXT, "Blocks-Title", "Blocks"));
        List<String> lines = Files.readAllLines(new File(Objects.requireNonNull(classLoader.getResource("defaults/blocklist.txt")).getPath()).toPath(), StandardCharsets.UTF_8);
        HashMap<Integer, TreeItem<PackTreeViewObject>> expectedDepth = new HashMap<>();
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
                    }
                }
                PackTreeViewObject newPackTreeViewObject = new PackTreeViewObject(PackTreeViewObject.TREE_TYPE.BLOCK, line.strip(), line.strip());
                TreeItem<PackTreeViewObject> newTreeItem = new TreeItem<>(newPackTreeViewObject);
                expectedDepth.get(previousMaxDepth).getChildren().add(newTreeItem);
                expectedDepth.put(spaceCount, newTreeItem);
            }
        }
        return blockNode;
    }

    public BlockVariantManager getBlockVarientsByName(String blockName) throws PackIntegrityException, IOException {
        Gson gson = new Gson();

        File varientFile = new File(packLocation.getAbsolutePath() + "/assets/minecraft/blockstates/" + blockName + ".json");
        if(!varientFile.exists()){
            varientFile = new File(defaultPack.getAbsolutePath() + "/assets/minecraft/blockstates/" + blockName + ".json");
        }
        if(!varientFile.exists()){
            throw new PackIntegrityException("Default pack is not complete, could not find file "
                    + defaultPack.getAbsolutePath() + "/assets/minecraft/blockstates/" + blockName
                    + ".json The default pack is likely incomplete");
        }
        String varientJSONString = String.join("", Files.readAllLines(varientFile.toPath(), StandardCharsets.UTF_8));
        return gson.fromJson(varientJSONString, BlockVariantManager.class);
    }
}
