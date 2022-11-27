package untimelyRock.packManager;

import untimelyRock.packManager.javaPackManager.BlockVariantContainer;
import untimelyRock.packManager.entities.PackIntegrityException;
import untimelyRock.packManager.entities.PackTreeViewObject;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.io.IOException;
abstract public class PackManager {
    protected final File packLocation;
    protected final File defaultPack;
    protected final PackType packType;

    public PackManager(File packLocation, File defaultPack, PackType packType){
        this.packLocation = packLocation;
        this.defaultPack = defaultPack;
        this.packType = packType;
    }

    public abstract String getPackName();
    public abstract void LoadPack();
    public abstract File getPackIcon();

    public abstract TreeItem<PackTreeViewObject> getPackTree() throws IOException;

    public abstract BlockVariantContainer getBlockVariantsByName(String blockName) throws PackIntegrityException, IOException;

}
