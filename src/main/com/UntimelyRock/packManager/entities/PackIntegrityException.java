package UntimelyRock.packManager.entities;

public class PackIntegrityException extends Exception{
    public PackIntegrityException(String message){
        this.message = message;
    }
    public String message;
}
