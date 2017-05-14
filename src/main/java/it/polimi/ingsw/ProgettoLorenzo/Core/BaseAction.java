package it.polimi.ingsw.ProgettoLorenzo.Core;

abstract class BaseAction {
    public final String actionName;

    protected BaseAction (String name) {
        this.actionName = name;
    }

    public abstract void apply(Player pl);
}


class ResourcesAction extends BaseAction {
    private Resources op;

    public ResourcesAction (String name, Resources op) {
        super(name);
        this.op = op;
    }

    public void apply(Player pl) {
        pl.currentRes = pl.currentRes.merge(this.op);
    }

    @Override
    public String toString() {
        return "+ " + this.actionName + ": " + this.op;
    }
}
