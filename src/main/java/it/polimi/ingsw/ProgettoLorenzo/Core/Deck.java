package it.polimi.ingsw.ProgettoLorenzo.Core;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;


public class Deck implements Iterator {
    private List<Card> cards;

    public Deck() {
        this.cards = new ArrayList<Card>();
    }

    private Deck(List<Card> oldList) {
        this.cards = new ArrayList<>(oldList);
    };

    public void add(Card x) {
        this.cards.add(x);
    }

    public Card remove(int idx) {
        return this.cards.remove(idx);
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
        String ret = new String();
        for (Card x : this.cards){
            ret = ret + "☛ " + x + "\n";
        }
        return ret;
    }

    public void shuffleCards() {
        Collections.shuffle(this.cards);
    }
}
