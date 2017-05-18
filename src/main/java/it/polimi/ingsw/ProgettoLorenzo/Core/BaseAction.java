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

class TakeFamilyMember extends BaseAction {
    private final FamilyMember famMember;

    public TakeFamilyMember(FamilyMember famMember) {
        super("Remove family member from the Player");
        this.famMember = famMember;
    }

    public void apply() {
        this.famMember.getParent().takeFamilyMember(this.famMember);
    }
}

class PlaceFamilyMemberInCouncil extends BaseAction {
    private final FamilyMember famMember;
    private final Council dest;

    public PlaceFamilyMemberInCouncil(FamilyMember famMember, Council c) {
        super("Add a FamilyMember in the Council");
        this.famMember = famMember;
        this.dest = c;
    }

    public void apply() {
        this.dest.placeFamilyMember(this.famMember);
    }
}
