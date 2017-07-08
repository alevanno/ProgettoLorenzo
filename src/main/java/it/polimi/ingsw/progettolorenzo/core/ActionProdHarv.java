package it.polimi.ingsw.progettolorenzo.core;

import com.google.gson.JsonElement;

/**
 * This is the class that collect the common methods of {@link Production}
 * and {@link Harvest} to avoid duplications.
 * @see Action
 * @see Harvest
 * @see Production
 */
public abstract class ActionProdHarv extends Action {
    /**
     * abstract method that is really implemented by the 2 child classes.
     * @param fam the family member claiming the place.
     * @return the boolean value of the success of the action.
     */
    public abstract boolean claimFamMain(FamilyMember fam);

    /**
     * @see #claimFamMain(FamilyMember)
     * @param fam the family member claiming the place.
     * @return the boolean value of the success of the action.
     */
    public abstract boolean claimFamSec(FamilyMember fam);

    /**
     * @see Production#placeFamilyMember(FamilyMember, boolean)
     * @see Harvest#placeFamilyMember(FamilyMember, boolean)
     * @param fam the family member claiming the place
     * @param isMainSpace the boolean value that says if the place is the Main space.
     */
    protected abstract void placeFamilyMember(FamilyMember fam, boolean isMainSpace);

    /**
     * The method that checks the {@link Harvest}\{@link Production} action value.
     * It can be increased by a permanent effect of a development
     * {@link Card}s. It can be decreased due to an excommunication card.
     * @param pl the player attempting the action
     * @param value the value of the action
     * @param plusVal the string representing the permanent effect to find in the
     *                Card permanent effect's list.
     * @param prodMal The string representing the type of excommunication
     * @return the value of the card permanent action.
     */
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
