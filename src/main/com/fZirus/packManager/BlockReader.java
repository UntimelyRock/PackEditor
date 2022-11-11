package fZirus.packManager;

import com.google.gson.Gson;
import fZirus.packManager.entities.BlockVariant;
import fZirus.packManager.entities.GenericBlockModel;
import fZirus.packManager.entities.ModelElement;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class BlockReader {
    Gson gson;
    String customPackLocation;

    BlockReader(String customPackLocation) {
        gson = new Gson();
        this.customPackLocation = customPackLocation;

    }

    public BlockVariant[] getBlockVarientsByName(String name){
        return new BlockVariant[0];
    }

    public GenericBlockModel getBlockModel(String blockName) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();//TODO add support for modds
        File baseModelFile = new File(classLoader.getResource("assets/minecraft/models/block/" + blockName + ".json").getPath());
        List<String> lines = Files.readAllLines(baseModelFile.toPath(), StandardCharsets.UTF_8);
        GenericBlockModel baseBlock = gson.fromJson(String.join("", lines), GenericBlockModel.class);
        while (baseBlock.elements == null) {
            File modelFile = new File(classLoader.getResource("assets/minecraft/models/" + baseBlock.parent.replace("minecraft:", "") + ".json").getPath());
            String modelJSON = String.join("", Files.readAllLines(modelFile.toPath(), StandardCharsets.UTF_8));
            GenericBlockModel nextBlock = gson.fromJson(modelJSON, GenericBlockModel.class);
            if(nextBlock.textures != null){
                for (String texture: nextBlock.textures.keySet()) {
                    if(nextBlock.textures.get(texture).startsWith("#"))
                        nextBlock.textures.replace(texture, baseBlock.textures.get(nextBlock.textures.get(texture).substring(1)));
                }
            }
            if(nextBlock.elements != null){
                for (ModelElement element: nextBlock.elements) {
                    for (String face: element.faces.keySet()) {
                        if(element.faces.get(face).texture.startsWith("#"))
                            element.faces.get(face).texture = baseBlock.textures.get(element.faces.get(face).texture.substring(1));
                    }
                }
            }

            baseBlock.copyIfNotNull(nextBlock);
        }
        return baseBlock;
    }
}
