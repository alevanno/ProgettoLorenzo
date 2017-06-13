package it.polimi.ingsw.progettolorenzo.client;

import it.polimi.ingsw.progettolorenzo.Config;
import it.polimi.ingsw.progettolorenzo.Server;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RmiClientImpl extends UnicastRemoteObject implements ClientInterface, RmiClient {
    private transient final Logger log = Logger.getLogger(this.getClass().getName());
    private transient Scanner in = new Scanner(System.in);
    public final String name;
    public final String colour;

    public RmiClientImpl(String name, String colour) throws RemoteException {
        super();
        this.name = name;
        this.colour = colour;
    }

    @Override
    public String sIn() throws RemoteException {
        return this.in.nextLine();
    }

    @Override
    public void sOut(String msg) throws RemoteException {
        System.out.println(msg);
    }

    @Override
    public int sInPrompt(int minValue, int maxValue) throws RemoteException {
        int choice;
        do {
            this.sOut("Input an int between " + minValue + " and " +
                maxValue);
            while (!this.in.hasNextInt()) {
                this.in.nextLine();
                this.sOut("Please input an int");
            }
            choice = this.in.nextInt();
            this.in.nextLine();
        } while (choice < minValue || choice > (maxValue));
        return choice;
    }

    @Override
    public boolean sInPromptConf() throws RemoteException {
        String choice;
        do {
            sOut("Input 'y' (yes) or 'n' (no)");
            choice = this.in.next().substring(0, 1);
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
            Server srv = (Server) reg.lookup("Lorenzo");
            srv.addPlayer(this.name, this.colour, this);
        } catch (NotBoundException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }

    }

    @Override
    public void endClient() {}
}
