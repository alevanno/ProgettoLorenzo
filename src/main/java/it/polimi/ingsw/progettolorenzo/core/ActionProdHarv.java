package it.polimi.ingsw.progettolorenzo.core;

public abstract class ActionProdHarv extends Action {

    public abstract boolean claimFamMain(FamilyMember fam);

    public abstract void claimFamSec(FamilyMember fam);

    protected abstract void placeFamilyMember(FamilyMember fam, boolean isMainSpace);
}
