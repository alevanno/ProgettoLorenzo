package it.polimi.ingsw.progettolorenzo.core.player;


import it.polimi.ingsw.progettolorenzo.client.RmiClient;
import java.rmi.RemoteException;
import java.util.logging.Logger;

public class PlayerIORMI implements PlayerIO {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private RmiClient rmi;

    public PlayerIORMI(RmiClient rmi) {
        this.rmi = rmi;
    }

    @Override
    public String sIn() {
        try {
            return this.rmi.sIn();
        } catch (RemoteException e) {
            log.severe(e.getMessage());
            return "";  // FIXME ...
        }
    }

    @Override
    public int sInPrompt(int minValue, int maxValue) {
        try {
            return this.rmi.sInPrompt(minValue, maxValue);
        } catch (RemoteException e) {
            log.severe(e.getMessage());
            return 0;  // FIXME ...
        }
    }

    @Override
    public boolean sInPromptConf() {
        try {
            return this.rmi.sInPromptConf();
        } catch (RemoteException e) {
            log.severe(e.getMessage());
            return false;  // FIXME ...
        }
    }

    @Override
    public void sOut(String s) {
        try {
            this.rmi.sOut(s);
        } catch (RemoteException e) {
            log.severe(e.getMessage());
        }
    }
}