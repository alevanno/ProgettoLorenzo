package it.polimi.ingsw.progettolorenzo.core;

public class LeaderUtils {
    public static int incCardTypeCounter(Player owner, String type) {
        int counter = 0;
        for (Card card : owner.listCards()) {
            if (type.equals(card.cardType)) {
                counter++;
            }
        }
        return counter;
    }
}
