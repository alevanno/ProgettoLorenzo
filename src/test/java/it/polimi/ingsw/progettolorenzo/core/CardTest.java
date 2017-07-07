package it.polimi.ingsw.progettolorenzo.core;

import org.junit.Test;
import static org.junit.Assert.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class CardTest {
    String cardCostStr = "{'wood': 1, 'woody': 3, 'faithPoint': 0}";
    String cardStr = "{'id': 0, 'name': 'c', 'period': 1, 'type': 'foo', " +
        "'cost': [%s]}";

    @Test
    public void load1() {
        JsonObject cardObj = new Gson().fromJson(
                String.format(cardStr, cardCostStr), JsonObject.class
        );
        Card card = new Card(cardObj);
        assertTrue(true);  // load successful
    }

    @Test
    public void load2() {
        // a card without any cost
        JsonObject cardObj = new Gson().fromJson(
                String.format(cardStr, ""), JsonObject.class
        );
        Card card = new Card(cardObj);
        assertTrue(true); // load successful
        System.out.println(card.getCardCost(new Player("test", "test")));
        assertTrue(true); // dealt correctly with a missing cost
    }
}
