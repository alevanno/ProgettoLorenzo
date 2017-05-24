package it.polimi.ingsw.ProgettoLorenzo.Core;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;


public class Deck implements Iterable<Card> {
    private final Logger log = Logger.getLogger(this.getClass().getName());
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

    public boolean remove(Card c) {
        if (!this.cards.remove(c)) {
            log.severe("Specified Card not present in the Deck.");
            System.exit(1);
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
}
