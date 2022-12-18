package untimelyRock.packManager.entities;

import java.util.*;

public class MinecraftBlock {
    HashMap<String, BlockVariant> variants;
    MultipartPart[] multipart;
    

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
            for (MultipartPart part : multipart) {
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


    static class MultipartPart{
        BlockVariant apply;
        HashMap<String, String> when;
    }
}
