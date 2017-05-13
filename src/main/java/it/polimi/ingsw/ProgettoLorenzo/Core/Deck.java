package it.polimi.ingsw.ProgettoLorenzo.Core;

import java.util.ArrayList;
import java.util.Collections;
/**
 * Created by luca on 13/05/17.
 */
public class Deck extends ArrayList<Card> {
    @Override
    public String toString(){
        String ret = new String();
        for (Card x : this){
            ret = ret + "☛ " + x + "\n";
        }
        return ret;
    }
    public void shuffleCards() {
        Collections.shuffle(this);
    }
}
