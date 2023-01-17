package untimelyRock.packManager.javaPackManager;

import com.google.gson.Gson;
import untimelyRock.packManager.javaPackManager.GenericBlockModel;
import untimelyRock.packManager.javaPackManager.ModelElement;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("ConstantConditions")
public class BlockVariant {
    String model;
    boolean uvlock;

    //Rotation Values?
    float x;
    float y;
    float z;
    public GenericBlockModel getBlockModel() throws IOException {
        Gson gson = new Gson();
        String blockName = model.replace("minecraft:", "");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();//TODO add support for mods
        File baseModelFile = new File(Objects.requireNonNull(classLoader.getResource("assets/minecraft/models/" + blockName + ".json")).getPath());
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
