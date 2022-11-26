package untimelyRock.packManager;

import com.google.gson.Gson;
import untimelyRock.packManager.entities.BlockVariantManager;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class BlockReader {
    final Gson gson;
    final String customPackLocation;
    String defaultPack;

    BlockReader(String customPackLocation, String defaultPack) {
        gson = new Gson();
        this.customPackLocation = customPackLocation;

    }

    public BlockVariantManager getBlockVariantsByName(String blockName) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File varientFile = new File(classLoader.getResource("assets/minecraft/blockstates/" + blockName + ".json").getPath());
        String varientJSONString = String.join("", Files.readAllLines(varientFile.toPath(), StandardCharsets.UTF_8));
        BlockVariantManager blockVariantManager = gson.fromJson(varientJSONString, BlockVariantManager.class);
        return blockVariantManager;
    }


}
