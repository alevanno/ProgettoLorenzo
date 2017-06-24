package it.polimi.ingsw.progettolorenzo.core.exc;

public class GameAlreadyStartedException extends Exception {
    public GameAlreadyStartedException() {
        super("This Game already started!");
    }
}
