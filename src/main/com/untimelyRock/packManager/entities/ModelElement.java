package untimelyRock.packManager.entities;

import java.util.HashMap;

public class ModelElement {
    public int[] from;
    public int[] to;
    public Rotation rotation;
    public boolean shade;
    public HashMap<String, Face> faces;

    public static class Face{
        int[] uv;
        public String texture;
        String cullFace;
    }

    public static class Rotation{
        public int[] origin;
        public String axis;
        public double angle;
        public boolean rescale;
    }

}