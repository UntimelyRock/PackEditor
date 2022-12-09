package untimelyRock.packManager;

import untimelyRock.packManager.javaPackManager.BlockVariantContainer;
import untimelyRock.packManager.entities.PackTreeViewObject;
import untimelyRock.packManager.entities.PackIntegrityException;
import untimelyRock.packManager.entities.TreeViewObjectType;
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
    @Override
    public String getPackName(){
        return "WIP";
    }
    @Override
    public File getPackIcon() {
        return new File(packLocation.getAbsolutePath() + "/pack.png");
    }


    @Override
    public void LoadPack(){

    }

    @Override
    public TreeItem<PackTreeViewObject> getPackTree() throws IOException {//TODO auto generate
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        TreeItem<PackTreeViewObject> blockNode = new TreeItem<>(new PackTreeViewObject(TreeViewObjectType.TEXT, "Blocks-Title", "Blocks"));
        List<String> lines = Files.readAllLines(new File(Objects.requireNonNull(classLoader.getResource("defaults/blocklist.txt")).getPath()).toPath(), StandardCharsets.UTF_8);
        HashMap<Integer, TreeItem<PackTreeViewObject>> expectedDepth = new HashMap<>();
        expectedDepth.put(-1,blockNode);//add as depth increases, if depth decreases delete all deeper entries


        for (String line : lines){
            if (!line.startsWith("//") && !line.isBlank()){//TODO make guard if
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
                PackTreeViewObject newPackTreeViewObject = new PackTreeViewObject(TreeViewObjectType.BLOCK, line.strip(), line.strip());
                TreeItem<PackTreeViewObject> newTreeItem = new TreeItem<>(newPackTreeViewObject);
                expectedDepth.get(previousMaxDepth).getChildren().add(newTreeItem);
                expectedDepth.put(spaceCount, newTreeItem);
            }
        }
        return blockNode;
    }
    @Override
    public BlockVariantContainer getBlockVariantsByName(String blockName) throws PackIntegrityException, IOException {
        Gson gson = new Gson();
        blockName = blockName.replace("*", "");

        File variantFile = new File(packLocation.getAbsolutePath() + "/assets/minecraft/blockstates/" + blockName + ".json");
        if(!variantFile.exists()){
            variantFile = new File(defaultPack.getAbsolutePath() + "/assets/minecraft/blockstates/" + blockName + ".json");
        }
        if(!variantFile.exists()){
            throw new PackIntegrityException("Default pack is not complete, could not find file "
                    + defaultPack.getAbsolutePath() + "/assets/minecraft/blockstates/" + blockName
                    + ".json The default pack is likely incomplete");
        }
        String variantJSONString = String.join("", Files.readAllLines(variantFile.toPath(), StandardCharsets.UTF_8));


        return gson.fromJson(variantJSONString, BlockVariantContainer.class);
    }
}
