package untimelyRock.packManager;

import java.io.IOException;

public abstract class SelectableTextureObject {
    public enum OBJECT_TYPE{
        BLOCK,
        ITEM
    }
    OBJECT_TYPE objectType;

    public abstract EditableTexture getAsEditableTexture(String variant) throws IOException;

    public void setObjectType(OBJECT_TYPE object_type) {
        this.objectType = object_type;
    }
}
