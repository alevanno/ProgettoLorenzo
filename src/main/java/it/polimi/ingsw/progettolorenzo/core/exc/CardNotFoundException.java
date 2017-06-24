package it.polimi.ingsw.progettolorenzo.core.exc;

import it.polimi.ingsw.progettolorenzo.core.Card;

public class CardNotFoundException extends Exception {

    public CardNotFoundException(Card card) {
        super(String.format("The specified card %s was not found", card.cardName));
    }

    public CardNotFoundException(String cardName) {
        super(String.format("The specified card %s was not found", cardName));
    }
}
