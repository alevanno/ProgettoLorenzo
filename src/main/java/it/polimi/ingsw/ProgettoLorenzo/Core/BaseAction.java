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

class NestedAction extends BaseAction {
    private Action action;

    protected NestedAction(Action action) {
        super("Nested action");
        this.action = action;
    }

    public void apply() {
        // FIXME doit!
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

class CardFromFloorAction extends BaseAction {
    private final Card card;
    private final Floor floor;
    private final Player player;

    public CardFromFloorAction(Card card, Floor floor, Player player) {
        super("Move card from Floor to Player");
        this.card = card;
        this.floor = floor;
        this.player = player;
    }

    public void apply() {
        this.floor.removeCard();
        this.player.addCard(this.card);
    }

    @Override
    public String toString() {
        return "Card move: " + this.card + " → " + this.player;
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

class PlaceFamilyMemberInFloor extends BaseAction {
    private final FamilyMember famMember;
    private final Floor dest;

    public PlaceFamilyMemberInFloor(FamilyMember famMember, Floor dest) {
        super("Add a family member somewhere");
        this.famMember = famMember;
        this.dest = dest;
    }

    public void apply() {
        this.dest.placeFamilyMember(this.famMember);
    }

    @Override
    public String toString() {
        return "Family member move: " + this.famMember + " → " + this.dest;
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

class PlaceFamilyMemberInBooth extends BaseAction {
    private final FamilyMember famMember;
    private final MarketBooth dest;

    public PlaceFamilyMemberInBooth(FamilyMember famMember, MarketBooth m) {
        super("Add a FamilyMember in somewhere in the Market");
        this.famMember = famMember;
        this.dest = m;
    }

    public void apply() {
        this.dest.placeFamilyMember(this.famMember);
    }

    @Override
    public String toString() {
        return "Family member move: " + this.famMember + " → " + this.dest;
    }

}
