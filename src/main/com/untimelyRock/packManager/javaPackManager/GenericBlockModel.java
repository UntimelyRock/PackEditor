package untimelyRock.packManager.javaPackManager;

import java.util.HashMap;

@SuppressWarnings("SpellCheckingInspection")
public class GenericBlockModel {
    public String parent = null;
    public boolean ambientocclusion = false;
    public HashMap<String,String> textures;
    public HashMap<String, String> display;
    public ModelElement[] elements;
    public String gui_light;

    public void copyIfNotNull(GenericBlockModel inputBlock){
        parent = (inputBlock.parent != null ? inputBlock.parent : parent  );
        ambientocclusion = inputBlock.ambientocclusion;
        textures = (inputBlock.textures != null ? inputBlock.textures : textures  );
        display = (inputBlock.display != null ? inputBlock.display : display  );
        elements = (inputBlock.elements != null ? inputBlock.elements : elements  );
        gui_light = (inputBlock.gui_light != null ? inputBlock.gui_light : gui_light  );
    }
}
