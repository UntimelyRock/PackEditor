import GUI.Display;
import GUI.LaunchConfig;

public class Main {


    public static void main(String[] args){
        //TODO setup args https://stackoverflow.com/questions/7341683/parsing-arguments-to-a-java-command-line-program



        Display display = new Display(new LaunchConfig());
        display.start();

    }

}
