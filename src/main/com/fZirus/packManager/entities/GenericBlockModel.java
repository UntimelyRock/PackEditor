package fZirus.packManager.entities;

import java.util.HashMap;

public class GenericBlockModel {
    public String parent = null;
    public boolean ambientocclusion = false;
    public HashMap<String,String> textures;
    public HashMap<String, String> display;
    public ModelElement[] elements;
    public String gui_light;

    public void copyIfNotNull(GenericBlockModel inputBlock){
        this.parent = (inputBlock.parent != null ? inputBlock.parent : parent  );
        this.ambientocclusion = inputBlock.ambientocclusion;
        this.textures = (inputBlock.textures != null ? inputBlock.textures : textures  );
        this.display = (inputBlock.display != null ? inputBlock.display : display  );
        this.elements = (inputBlock.elements != null ? inputBlock.elements : elements  );
        this.gui_light = (inputBlock.gui_light != null ? inputBlock.gui_light : gui_light  );
    }
}
