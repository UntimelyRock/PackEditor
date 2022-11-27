package untimelyRock.packManager.javaPackManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class BlockVariantContainer {
    HashMap<String, BlockVariant> variants;
    MultipartPart[] multipart;


    public HashMap<String, Set<String>> getVariantOptions(){
        HashMap<String, Set<String>> variantOptions = new HashMap<>();
        if (variants != null){
            for (String variantName : variants.keySet()) {
                String[] variantOptionsSplit = variantName.split(",");
                for (String option : variantOptionsSplit) {
                    int splitIndex = option.indexOf("=");
                    String optionName = option.substring(0, splitIndex);
                    String optionValue = option.substring(splitIndex + 1);
                    if (!variantOptions.containsKey(optionName)) {
                        variantOptions.put(optionName, new HashSet<>());
                    }
                    variantOptions.get(optionName).add(optionValue);
                }
            }
            return variantOptions;
        }else if(multipart != null){
            for (MultipartPart part: multipart) {
                for(String whenKey : part.when.keySet()){
                    String optionValue = part.when.get(whenKey);
                    if (variantOptions.containsKey(whenKey)) {
                        variantOptions.get(whenKey).add(optionValue);
                    }
                    variantOptions.put(whenKey, new HashSet<>());
                }
            }
            return variantOptions;
        }else {
            return null;
        }
    }


    static class MultipartPart{
        BlockVariant apply;
        HashMap<String, String> when;
    }
}
