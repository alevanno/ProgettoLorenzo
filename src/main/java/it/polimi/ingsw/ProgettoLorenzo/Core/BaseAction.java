package it.polimi.ingsw.ProgettoLorenzo.Core;

abstract class BaseAction {
    public final String actionName;

    protected BaseAction (String name) {
        this.actionName = name;
    }

    public abstract void apply();
}

class NullAction extends BaseAction {
    protected NullAction() {
        super("Null action.  To be implemented.  XXX");
    }

    public void apply() {
        // empty
    }
}

class ResourcesAction extends BaseAction {
    private final Resources op;
    private final Player player;

    protected ResourcesAction (String name, Resources op, Player p) {
        super(name);
        this.op = op;
        this.player = p;
    }

    public void apply() {
         this.player.currentRes = this.player.currentRes.merge(this.op);
    }

    @Override
    public String toString() {
        return "+ " + this.actionName + ": " + this.op;
    }
}
