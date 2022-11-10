package fZirus.packManager;

import com.google.gson.Gson;
import fZirus.packManager.entities.GenericBlock;

import java.io.FileReader;
import java.io.Reader;

public class BlockReader {
    Gson gson;
    String customPackLocation = "";

    BlockReader(String customPackLocation){
        gson = new Gson();
        this.customPackLocation = customPackLocation;

    }

    GenericBlock getBlockOfName(String blockName){
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        //Reader reader = new FileReader(new File(classLoader.getResource("")));
        //GenericBlock genericBlock = gson.fromJson()
        return new GenericBlock();
    }
}
