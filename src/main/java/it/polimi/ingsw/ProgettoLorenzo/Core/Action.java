package it.polimi.ingsw.ProgettoLorenzo.Core;

import java.util.ArrayList;
import java.util.List;

public abstract class Action {
    private List<BaseAction> actions = new ArrayList<>();

    public void addAction(BaseAction action) {
        this.actions.add(action);
    }

    public void emptyActions() {
        this.actions.clear();
    }

    public void apply() {
        for (int i=0; i < this.actions.size(); i++) {
            this.actions.get(i).apply();
        }
    }
}
