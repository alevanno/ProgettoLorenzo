package it.polimi.ingsw.progettolorenzo.core;

import com.google.gson.JsonElement;

public abstract class ActionProdHarv extends Action {

    public abstract boolean claimFamMain(FamilyMember fam);

    public abstract void claimFamSec(FamilyMember fam);

    protected abstract void placeFamilyMember(FamilyMember fam, boolean isMainSpace);

    int checkValue(Player pl, int value, String plusVal, String prodMal) {
        for (Card c: pl.listCards()) {
            JsonElement permEff = c.permanentEff.get(plusVal);
            if(permEff != null) {
                value += permEff.getAsInt();
            }
        }
        if (pl.getExcommunications().get(0).has(prodMal)) {
            int prodMalus = pl.getExcommunications().get(0).get(prodMal).getAsInt();
            pl.sOut("Your excommunication lowers the value of this action by " + prodMalus);
            value -= prodMalus;
        }
        return value;
    }
}
