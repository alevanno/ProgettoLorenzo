package it.polimi.ingsw.progettolorenzo.core;

import java.util.logging.Logger;

/**
 * The class representing all the "basic" little actions.
 * It cannot be instantiated; all the child implement
 * the apply() method.
 *
 * All the BaseAction are effectively applied by the
 * calling {@link Action}
 */
abstract class BaseAction {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    public final String actionName;

    protected BaseAction (String name) {
        this.actionName = name;
    }

    public void clear(){
        // usually nothing to do here.
    }

    public abstract void apply();

    public void logAction() {
        log.info(this.toString());
    }
}

class NestedAction extends BaseAction {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private Action action;

    protected NestedAction(Action action) {
        super("Nested action");
        this.action = action;
    }

    @Override
    public void clear() {
        this.action.emptyActions();
    }

    @Override
    public void apply() {
        this.action.apply();
    }

    @Override
    public void logAction() {
        log.fine("Nested actions:");
        this.action.logActions();
        log.fine ("End nested actions ↑");
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

    @Override
    public void apply() {
        if (player.getExcommunications().get(0).has("resMalus")) {
            Resources malus = Resources.fromJson(player.getExcommunications().get(0).get("resMalus")).inverse();
            Resources reducedRes = this.op.merge(malus);
            player.sOut("Due to your excommunication, you gain less resources");
            this.player.currentResMerge(reducedRes);
        } else {
            this.player.currentResMerge(this.op);
        }
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public void apply() {
        this.dest.placeFamilyMember(this.famMember);
    }
}

class PlaceFamilyMemberInBooth extends BaseAction {
    private final FamilyMember famMember;
    private final MarketBooth dest;

    public PlaceFamilyMemberInBooth(FamilyMember famMember, MarketBooth m) {
        super("Add a FamilyMember somewhere in the Market");
        this.famMember = famMember;
        this.dest = m;
    }

    @Override
    public void apply() {
        this.dest.placeFamilyMember(this.famMember);
    }

    @Override
    public String toString() {
        return "Family member move: " + this.famMember + " → " + this.dest;
    }
}

class PlaceFamMemberInProdHarv extends BaseAction {
    private final FamilyMember famMember;
    private final ActionProdHarv dest;
    private final boolean isMainSpace;

    public PlaceFamMemberInProdHarv(FamilyMember famMember, ActionProdHarv dest, boolean isMainSpace) {
        super("Add a FamilyMember in the " + dest.getClass().getSimpleName() + " area");
        this.famMember = famMember;
        this.dest = dest;
        this.isMainSpace = isMainSpace;
    }

    @Override
    public void apply() {
        this.dest.placeFamilyMember(this.famMember, isMainSpace);
    }

    @Override
    public String toString() {
        return "Family member move: " + this.famMember + " → " + this.dest;
    }
}