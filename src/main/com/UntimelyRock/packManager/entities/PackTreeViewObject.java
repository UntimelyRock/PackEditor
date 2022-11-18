package UntimelyRock.packManager.entities;

public class PackTreeViewObject {
    public enum TREE_TYPE {
        TEXT,
        BLOCK
    }

    String name = "";
    String displayName;

    @Override
    public String toString() {
        return displayName;
    }

    public String getName(){
        return name;
    }

    TREE_TYPE type;

    public PackTreeViewObject(TREE_TYPE type, String name, String displayName){
        this.type = type;
        this.name = name;
        this.displayName = displayName;
    }
}
