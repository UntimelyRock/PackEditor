package untimelyRock.packManager.javaPackManager;

import untimelyRock.packManager.EditableTexture;
import untimelyRock.packManager.PackManager;
import untimelyRock.packManager.PackType;
import untimelyRock.packManager.SelectableTextureObject;
import untimelyRock.packManager.entities.*;
import com.google.gson.Gson;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaPackManager extends PackManager {
    private SelectableTextureObject selectedTextureObject;//TODO make "Texture item" abstract to be used for blocks, items, and entities

    public JavaPackManager(File packLocation, File defaultPack){
        super(packLocation, defaultPack, PackType.JAVA_PACK);

    }

    @Override
    public EditableTexture getEditableTextureOfSelected(String variant) throws IOException {
        return selectedTextureObject.getAsEditableTexture(variant);
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
    public TreeItem<PackTreeViewObject> getPackTree() throws IOException, URISyntaxException {
        TreeItem<PackTreeViewObject> blockNode = new TreeItem<>(new PackTreeViewObject(TreeViewObjectType.TEXT, "Blocks-Title", "Blocks"));
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Path blockListPath = Paths.get(Objects.requireNonNull(classLoader.getResource("defaults/blocklist.txt")).toURI());
        List<String> lines = Files.readAllLines(blockListPath, StandardCharsets.UTF_8);

        HashMap<Integer, TreeItem<PackTreeViewObject>> depthMap = new HashMap<>();
        depthMap.put(-1,blockNode);//add as depth increases, if depth decreases delete all deeper entries

        for (String line : lines){
            if (line.trim().startsWith("//") || line.isBlank()) continue;

            long spaceCount = line.chars().filter(character -> character == "\s".charAt(0)).count();
            int previousMaxDepth = depthMap.keySet().stream()
                    .filter(depth -> depth < spaceCount)
                    .max(Integer::compareTo)
                    .orElse(-1);

            PackTreeViewObject newPackTreeViewObject = new PackTreeViewObject(TreeViewObjectType.BLOCK, line.trim(), line.trim());
            TreeItem<PackTreeViewObject> newTreeItem = new TreeItem<>(newPackTreeViewObject);
            depthMap.get(previousMaxDepth).getChildren().add(newTreeItem);
            depthMap.put(Math.toIntExact(spaceCount), newTreeItem);
        }
        return blockNode;
    }

    public Map<String, Set<String>> getCurrentVariantOptions()throws PackIntegrityException, IOException{
        return currentBlock.getVariantOptions();
    }


    @Override
    public void openBlockOfName(String blockName) throws PackIntegrityException, IOException {
        Gson gson = new Gson();
        blockName = blockName.replace("*", "");

        Path variantJSONPath = Paths.get(packLocation.getAbsolutePath(), "assets/minecraft/blockstates", blockName + ".json");
        if(!Files.exists(variantJSONPath)){
            variantJSONPath = Paths.get(defaultPack.getAbsolutePath(), "assets/minecraft/blockstates", blockName + ".json");
        }
        if(!Files.exists(variantJSONPath)){
            throw new PackIntegrityException("Default pack is not complete, could not find file "
                    + defaultPack.getAbsolutePath() + "/assets/minecraft/blockstates/" + blockName
                    + ".json The default pack is likely incomplete");
        }

        try(Stream<String> lines = Files.lines(variantJSONPath, StandardCharsets.UTF_8)){
            String variantJSONString = lines.collect(Collectors.joining());
            currentBlock = gson.fromJson(variantJSONString, MinecraftBlock.class);
        }
        selectedTextureObject.setObjectType(SelectableTextureObject.OBJECT_TYPE.BLOCK);
    }
}
