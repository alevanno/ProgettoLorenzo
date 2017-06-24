package it.polimi.ingsw.progettolorenzo;

import it.polimi.ingsw.progettolorenzo.client.RmiClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

// annotating @FunctionalInterface here is ridiculous, but let's do it
// to make sonar a tad happier, it should change nothing anyway
@FunctionalInterface
public interface Server extends Remote {
    void addPlayer(String n, String c, RmiClient r) throws RemoteException;
}
