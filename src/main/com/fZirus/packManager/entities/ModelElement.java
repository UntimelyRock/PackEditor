package fZirus.packManager.entities;

import java.util.HashMap;

public class ModelElement {
    public int[] from;
    public int[] to;
    public Rotation rotation;
    public boolean shade;
    public HashMap<String, Face> faces;
    public class Down{
        public int[] uv;
        public String texture;
    }

    public class Face{
        String texture;
        String cullFace;
    }

    public class Rotation{
        public int[] origin;
        public String axis;
        public double angle;
        public boolean rescale;
    }

}
