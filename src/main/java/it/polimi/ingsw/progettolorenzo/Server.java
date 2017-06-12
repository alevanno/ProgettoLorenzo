package it.polimi.ingsw.progettolorenzo;

import it.polimi.ingsw.progettolorenzo.client.RmiClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
    void addPlayer(String n, String c, RmiClient r) throws RemoteException;
}
