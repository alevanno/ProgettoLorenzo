package it.polimi.ingsw.progettolorenzo.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.progettolorenzo.core.exc.CardNotFoundException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DeckTest {
    private String cardStr = "{'id': 0, 'name': 'c', 'period': 1, 'type': 'foo'}";
    private Card testCard = new Card(new Gson().fromJson(cardStr, JsonObject.class));
    private Deck testDeck;

    @Test
    public void emptyDeck() {
        Deck d = new Deck();
        assertEquals(0, d.size());
    }

    @Test
    public void load1() {
        Deck d = new Deck();
        d.add(this.testCard);
        assertEquals(1, d.size());
        Deck got = d.listCards();
        assertEquals(1, got.size());
        assertEquals(this.testCard, got.remove(0));
    }

    @Before
    public void setup() {
        this.testDeck = new Deck();
        this.testDeck.add(this.testCard);
    }

    @Test
    public void removeTest1() {
        Card c = this.testDeck.remove(0);
        assertEquals(this.testCard, c);
        assertEquals(0, this.testDeck.size());
    }

    @Test
    public void removeTest2() throws CardNotFoundException {
        this.testDeck.remove(this.testCard);
        assertEquals(0, this.testDeck.size());
    }

    @Test
    public void addAllTest() {
        Deck d = new Deck();
        d.add(this.testCard);
        d.addAll(this.testDeck);
        assertEquals(2, d.size());
        assertTrue(d.remove(1).equals(d.remove(0)));
    }
}
