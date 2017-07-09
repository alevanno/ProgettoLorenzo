package it.polimi.ingsw.progettolorenzo.core.player;


import it.polimi.ingsw.progettolorenzo.client.RmiClient;
import java.rmi.RemoteException;
import java.util.logging.Logger;

/**
 * Implementation of {@link PlayerIO} using RMI.
 */
public class PlayerIORMI implements PlayerIO {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private RmiClient rmi;

    public PlayerIORMI(RmiClient rmi) {
        this.rmi = rmi;
    }

    /**
     * {@link PlayerIO#sIn()} implementation using
     * @return
     */
    @Override
    public String sIn() {
        try {
            return this.rmi.sIn();
        } catch (RemoteException e) {
            log.severe(e.getMessage());
            return "";  // FIXME ...
        }
    }

    /**
     * @see PlayerIO#sInPrompt(int, int)
     * @param minValue the minimum accepted value
     * @param maxValue the maximum accepted value
     * @return the inserted value
     */
    @Override
    public int sInPrompt(int minValue, int maxValue) {
        try {
            return this.rmi.sInPrompt(minValue, maxValue);
        } catch (RemoteException e) {
            log.severe(e.getMessage());
            return 0;  // FIXME ...
        }
    }

    /**
     * @see PlayerIO#sInPromptConf()
     * @return the boolean value representing the confirmation
     */
    @Override
    public boolean sInPromptConf() {
        try {
            return this.rmi.sInPromptConf();
        } catch (RemoteException e) {
            log.severe(e.getMessage());
            return false;  // FIXME ...
        }
    }

    /**
     * @see PlayerIO#sOut(String)
     * @param s the string to be sent
     */
    @Override
    public void sOut(String s) {
        try {
            this.rmi.sOut(s);
        } catch (RemoteException e) {
            log.severe(e.getMessage());
        }
    }
}