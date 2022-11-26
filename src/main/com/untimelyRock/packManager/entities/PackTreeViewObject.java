package untimelyRock.packManager.entities;

public class PackTreeViewObject {


    final String name;
    final String displayName;

    @Override
    public String toString() {
        return displayName;
    }

    public String getName(){
        return name;
    }

    final TreeViewObjectType type;

    public PackTreeViewObject(TreeViewObjectType type, String name, String displayName){
        this.type = type;
        this.name = name;
        this.displayName = displayName;
    }
    public PackTreeViewObject(TreeViewObjectType type, String name){
        this.type = type;
        this.name = name;
        this.displayName = name;
    }
}
