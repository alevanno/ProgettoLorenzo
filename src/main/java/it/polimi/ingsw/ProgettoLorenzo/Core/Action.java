package it.polimi.ingsw.ProgettoLorenzo.Core;

import java.util.ArrayList;
import java.util.List;

public abstract class Action {
    private List<BaseAction> actions = new ArrayList<>();
    protected Player player;

    public void setPlayer(Player p) {
        this.player = p;
    }

    public void addAction(BaseAction action) {
        this.actions.add(action);
    }

    public void emptyActions() {
        this.actions.clear();
    }

    public void apply() {
        for (int i=0; i < this.actions.size(); i++) {
            this.actions.get(i).apply(this.player);
        }
    }
}
