package untimelyRock;

public class EditorConfig {
    private static String defaultPacksLocation = System.getenv("LOCALAPPDATA") + "/PackEditor/DefaultTexturePacks";

    public static String getDefaultPacksLocation() {
        return defaultPacksLocation;
    }

    public static void setDefaultPacksLocation(String newDefaultPacksLocation) {
        defaultPacksLocation = newDefaultPacksLocation;
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static String language = "engl-us";

    public static String getLanguage() {
        return language;
    }

    public static void setLanguage(String language) {
        EditorConfig.language = language;
    }
}
