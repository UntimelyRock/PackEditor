package untimelyRock.packManager;

import untimelyRock.packManager.entities.MinecraftBlock;
import untimelyRock.packManager.entities.PackIntegrityException;
import untimelyRock.packManager.entities.PackTreeViewObject;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

abstract public class PackManager {
    protected MinecraftBlock currentBlock;
    protected final File packLocation;
    protected final File defaultPack;
    protected final PackType packType;


    public PackManager(File packLocation, File defaultPack, PackType packType){
        this.packLocation = packLocation;
        this.defaultPack = defaultPack;
        this.packType = packType;
    }

    public abstract String getPackName();

    public abstract File getPackIcon();

    public abstract TreeItem<PackTreeViewObject> getPackTree() throws IOException, URISyntaxException;

    public abstract void openBlockOfName(String blockName) throws PackIntegrityException, IOException;

    public abstract Map<String, Set<String>> getCurrentVariantOptions() throws PackIntegrityException, IOException;
}
