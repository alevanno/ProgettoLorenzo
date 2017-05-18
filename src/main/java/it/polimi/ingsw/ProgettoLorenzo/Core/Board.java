package it.polimi.ingsw.ProgettoLorenzo.Core;

import java.util.ArrayList;
import java.util.List;

public class Board {
    public List<Tower> towers = new ArrayList<>();
    public Production productionArea = new Production();
    public Harvest harvestArea = new Harvest();
    public Council councilPalace = new Council();
    public Market marketSpace = new Market();

    // FIXME
    // of course this has get larger and instantiate an
    // arbitrary number of towers
    public Board(Deck cardList) {
        this.towers.add(new Tower(1, cardList));
    }
}
