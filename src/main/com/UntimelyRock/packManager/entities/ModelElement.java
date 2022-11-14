package UntimelyRock.packManager.entities;

import java.util.HashMap;

public class ModelElement {
    public int[] from;
    public int[] to;
    public Rotation rotation;
    public boolean shade;
    public HashMap<String, Face> faces;

    public class Face{
        int[] uv;
        public String texture;
        String cullFace;
    }

    public class Rotation{
        public int[] origin;
        public String axis;
        public double angle;
        public boolean rescale;
    }

}
