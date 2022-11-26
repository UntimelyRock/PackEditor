package untimelyRock.packManager.entities;

public class PackIntegrityException extends Exception{
    public PackIntegrityException(String message){
        this.message = message;
    }
    public final String message;
}
