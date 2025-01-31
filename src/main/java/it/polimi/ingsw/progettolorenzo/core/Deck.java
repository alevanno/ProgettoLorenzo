package it.polimi.ingsw.progettolorenzo.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.polimi.ingsw.progettolorenzo.core.exc.CardNotFoundException;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;


public class Deck implements Iterable<Card> {
    private List<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>();
    }

    private Deck(List<Card> oldList) {
        this.cards = new ArrayList<>(oldList);
    };

    public void add(Card x) {
        this.cards.add(x);
    }

    public void addAll(Deck d) {
        d.forEach(this::add);
    }

    public Card remove(int idx) {
        return this.cards.remove(idx);
    }

    public boolean remove(Card c) throws CardNotFoundException {
        if (!this.cards.remove(c)) {
            throw new CardNotFoundException(c);
        }
        return true;
    }

    public Deck listCards() {
        return new Deck(this.cards);
    }

    public int size() {
        return this.cards.size();
    }

    @Override
    public Iterator<Card> iterator() {
        return this.cards.iterator();
    }

    @Override
    public String toString(){
        StringBuilder ret = new StringBuilder();
        for (Card x : this.cards){
            ret.append("☛ " + x + "\n");
        }
        return ret.toString();
    }

    public void shuffleCards() {
        Collections.shuffle(this.cards);
    }

    public JsonArray serialize() {
        List<JsonObject> ret = new ArrayList<>();
        this.cards.forEach(
            c -> ret.add(c.serialize())
        );
        return new Gson().fromJson(new Gson().toJson(ret), JsonArray.class);
    }
}
