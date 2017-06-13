package it.polimi.ingsw.progettolorenzo.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiClient extends Remote {
    String sIn() throws RemoteException;
    void sOut(String s) throws RemoteException;
    int sInPrompt(int m, int n) throws RemoteException;
    boolean sInPromptConf() throws RemoteException;
}
