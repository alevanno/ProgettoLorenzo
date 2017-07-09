package it.polimi.ingsw.progettolorenzo.client;

import it.polimi.ingsw.progettolorenzo.Config;
import it.polimi.ingsw.progettolorenzo.Server;
import it.polimi.ingsw.progettolorenzo.client.inf.Interface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RmiClientImpl extends UnicastRemoteObject implements ClientInterface, RmiClient {
    private final transient Logger log = Logger.getLogger(this.getClass().getName());
    private final transient Interface c;
    public final String name;
    public final String colour;
    Server srv;

    public RmiClientImpl(String name, String colour, Interface inf) throws
        RemoteException {
        super();
        this.name = name;
        this.colour = colour;
        this.c = inf;
    }

    @Override
    public String sIn() throws RemoteException {
        return this.c.readLine();
    }

    @Override
    public void sOut(String msg) throws RemoteException {
        if (msg.equals("quit")) {
            throw new RemoteException();
        } else {
            this.c.printLine(msg);
        }
    }


    @Override
    public int sInPrompt(int minValue, int maxValue) throws RemoteException {
        int choice;
        String in;
        do {
            this.sOut("Input an int between " + minValue + " and " +
                maxValue);
            while(true) {
                in = this.sIn();
                try {
                    choice = Integer.valueOf(in).intValue();
                } catch (NumberFormatException e) {
                    this.sOut("Please input an int");
                    continue;
                }
                break;
            }
        } while (choice < minValue || choice > (maxValue));
        return choice;
    }

    @Override
    public boolean sInPromptConf() throws RemoteException {
        String choice;
        do {
            sOut("Input 'y' (yes) or 'n' (no)");
            choice = this.sIn().substring(0, 1);
        }
        while (!"y".equalsIgnoreCase(choice) && !"n".equalsIgnoreCase(choice));
        if ("y".equalsIgnoreCase(choice)) {
            return true;
        }
        if ("n".equalsIgnoreCase(choice)) {
            return false;
        }
        return false;
    }

    @Override
    public void startClient() throws RemoteException {
        Registry reg = LocateRegistry.getRegistry(
            Config.Client.rmi.get("port").getAsInt()
        );
        try {
            srv = (Server) reg.lookup("Lorenzo");
            srv.addPlayer(this.name, this.colour, this);
        } catch (NotBoundException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }

    }

    @Override
    public void endClient() {

        try {
            UnicastRemoteObject.unexportObject(this, true);
            //srv.removeRmiClient(this);
        } catch (RemoteException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        // TODO properly terminate the RMI connection
    }

    @Override
    public boolean equals(Object obj) {
        // sonar wants that all subclasses of classes that overrides equals()
        // override equals() themselves.
        // Just return false, two RmiClientImpl objects are never equal really.
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
