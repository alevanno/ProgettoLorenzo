package it.polimi.ingsw.progettolorenzo.client;

import java.io.IOException;

/**
 * The interface that every type of client has to implement.
 */
public interface ClientInterface {

    void startClient() throws IOException;
    void endClient();
}
