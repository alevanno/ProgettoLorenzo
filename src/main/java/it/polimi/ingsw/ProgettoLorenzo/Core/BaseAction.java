package it.polimi.ingsw.ProgettoLorenzo.Core;

abstract class BaseAction {
    public final String actionName;

    public BaseAction (String name) {
        this.actionName = name;
    }

    public abstract void apply(Player pl);
}

