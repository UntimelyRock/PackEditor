package packManager.entities;

import java.util.HashMap;
import java.util.Set;
//TODO This is not elegant, do better
public class BlocksManifest {

    public static final HashMap<String, BlockTreeObject> blockTree = new HashMap<>(){{
        put("Aquatic", new BlockTreeObject(new HashMap<>(){{
            put("Coral", new BlockTreeObject(new HashMap<>(){{

            }}));

            put("Dead Coral", new BlockTreeObject(new HashMap<>(){{

            }}));

            put("Snow and Ice", new BlockTreeObject(new HashMap<>(){{
                put("Blue Ice", new BlockTreeObject(""));
                put("Packed Ice", new BlockTreeObject(""));
                put("Snow Layer", new BlockTreeObject("snow_layer"));
                put("Snow Block", new BlockTreeObject("snow"));
                put("Powder Snow", new BlockTreeObject("powder_snow"));
            }}));

            put("Kelp and Seagrass", new BlockTreeObject(new HashMap<>(){{

            }}));

            put("Sponge", new BlockTreeObject(new HashMap<>(){{

            }}));

            put("Prismarine", new BlockTreeObject(new HashMap<>(){{

            }}));

            put("Prismarine", new BlockTreeObject(new HashMap<>(){{

            }}));

        }}));

        put("Banner", new BlockTreeObject(new HashMap<>(){{

        }}));

        put("Concrete", new BlockTreeObject(new HashMap<>(){{

        }}));

        put("Creative", new BlockTreeObject(new HashMap<>(){{

        }}));

        put("SurfaceBlocks", new BlockTreeObject(new HashMap<>(){{

        }}));

        put("End", new BlockTreeObject(new HashMap<>(){{

        }}));

        put("Glass", new BlockTreeObject(new HashMap<>(){{

        }}));

        put("Head", new BlockTreeObject(new HashMap<>(){{

        }}));

        put("Light", new BlockTreeObject(new HashMap<>(){{

        }}));

        put("Mineral", new BlockTreeObject(new HashMap<>(){{

        }}));

        put("Mob_Interacting", new BlockTreeObject(new HashMap<>(){{

        }}));

        put("Nether", new BlockTreeObject(new HashMap<>(){{

        }}));

        put("Nether", new BlockTreeObject(new HashMap<>(){{

        }}));

        put("Plants", new BlockTreeObject(new HashMap<>(){{

        }}));

        put("Redstone", new BlockTreeObject(new HashMap<>(){{

        }}));

        put("Stone", new BlockTreeObject(new HashMap<>(){{

        }}));

        put("Terracotta", new BlockTreeObject(new HashMap<>(){{

        }}));

        put("Utility", new BlockTreeObject(new HashMap<>(){{

        }}));

        put("Wood", new BlockTreeObject(new HashMap<>(){{

        }}));

        put("Wool", new BlockTreeObject(new HashMap<>(){{

        }}));

        put("msc", new BlockTreeObject(new HashMap<>(){{

        }}));





    }};



    public static class BlockTreeObject{
        boolean isCategory = false;
        HashMap<String, BlockTreeObject> children;
        String blockID = null;


        BlockTreeObject(HashMap<String, BlockTreeObject> children){
            isCategory = false;
            this.children = children;
        }
        BlockTreeObject(String blockID){
            this.blockID = blockID;
        }
    }




}
