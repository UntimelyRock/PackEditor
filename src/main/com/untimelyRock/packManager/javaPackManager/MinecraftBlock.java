package untimelyRock.packManager.javaPackManager;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import untimelyRock.packManager.EditableTexture;
import untimelyRock.packManager.SelectableTextureObject;
import untimelyRock.packManager.entities.PackIntegrityException;

import java.io.IOException;
import java.util.*;

public class MinecraftBlock extends SelectableTextureObject {
    HashMap<String, BlockVariant> variants;
    BlockPart[] multipart;

    public BlockVariant getVariantOf(String input){
        if(variants == null){
            return null;
        }
        return variants.get(input);

    }

    public Map<String, Set<String>> getVariantOptions() throws PackIntegrityException {
        Map<String, Set<String>> variantOptions = new HashMap<>();

        if(variants != null){
            for (String variantName : variants.keySet()) {
                if (variantName.length() == 0) continue;

                String[] variantOptionsSplit = variantName.split(",");
                for (String option : variantOptionsSplit) {
                    int splitIndex = option.indexOf("=");
                    String optionName = option.substring(0, splitIndex);
                    String optionValue = option.substring(splitIndex + 1);
                    variantOptions.computeIfAbsent(optionName, k -> new HashSet<>()).add(optionValue);
                }
            }
        } else if (multipart != null) {
            for (BlockPart part : multipart) {
                if(part.when == null) continue;
                for (String whenKey : part.when.keySet()) {
                    String optionValue = part.when.get(whenKey);
                    variantOptions.computeIfAbsent(whenKey, k -> new HashSet<>()).add(optionValue);
                }
            }
        }

        //noinspection ConstantConditions
        if(variantOptions == null){
            throw new PackIntegrityException("Block variant was empty, The JSON file may exist but be empty");
        }

        return variantOptions;
    }

    @Override
    public EditableTexture getAsEditableTexture(String variant) throws IOException {
        BlockVariant passedVariant = getVariantOf(variant);
        HashMap<String, String> textureStrings = passedVariant.getBlockModel().textures;
        HashMap<String, WritableImage> textureImages = new HashMap<>();
        for (String key : textureStrings.keySet()) {
            Image texture = new Image(textureStrings.get(key));
            textureImages.put(key, new WritableImage(texture.getPixelReader(), (int)texture.getWidth() , (int)texture.getHeight()));
        }
        return new EditableTexture(){{
           setObjectType(OBJECT_TYPE.BLOCK);
           textures = textureImages;
        }};
    }



    static class BlockPart {
        BlockVariant apply;
        HashMap<String, String> when;
    }
}
