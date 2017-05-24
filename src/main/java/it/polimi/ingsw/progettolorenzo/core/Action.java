package it.polimi.ingsw.progettolorenzo.core;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public abstract class Action {
    private final Logger log = Logger.getLogger(this.getClass().getName());
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

    public void logActions() {
        log.fine("Actions staged for " + this + ":");
        this.actions.forEach(x -> x.logAction());
    }
}
