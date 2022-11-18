package UntimelyRock.packManager;

import UntimelyRock.packManager.entities.BlockVariantManager;
import UntimelyRock.packManager.entities.PackIntegrityException;
import javafx.scene.control.TreeItem;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
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

    public abstract TreeItem getPackTree() throws IOException;

    public abstract BlockVariantManager getBlockVarientsByName(String blockName) throws PackIntegrityException, IOException;

}
