package it.polimi.ingsw.ProgettoLorenzo.Core;

import com.google.gson.JsonObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


public class Harvest {
    private FamilyMember mainHarvest;
    private List<FamilyMember> secondaryHarvest = new ArrayList<>();

    public void claimFamMain(FamilyMember fam) {
        this.mainHarvest = fam;
        //harv(fam.parent, fam.actionValue);
    }
    //FIXME non gestisce l'incremento azione con servitori

    public void claimFamSec(FamilyMember fam) {
        this.secondaryHarvest.add(fam);
        //harv(fam.parent, fam.ActionValue);
    }

    public void harv(Player p, int value) throws FileNotFoundException {
        Deck tempDeck = new Deck();
        Resources harvRes = new Resources.ResBuilder().build();
        if (value < 1) {
            System.out.println("You need an action value of at least 1");
            //TODO deve restituire errore al livello superiore
        } else {
            //filters the current player's deck, keeping Cards with permanentEffect=harvest
            //excludes Cards having too high of an action value
            for (Card i : p.listCards()) {
                if (i.permanentEff.containsKey("harvest") &&
                        i.permanentEff.get("harvest").getAsJsonObject().get("value").getAsInt() <= value) {
                    tempDeck.add(i);
                }
            }
            //resources given by BonusTile
            harvRes.merge(p.bonusT.getHarvestRes());

            //resources given by static Cards
            for (Card i : tempDeck) {
                Resources tmp = Resources.fromJson(i.permanentEff.get("harvest")
                        .getAsJsonObject().get("resources").getAsJsonObject());
                harvRes.merge(tmp);
            }
            //SINGLE councilPrivilege given by static Cards
            for (Card i : tempDeck) {
                JsonObject tmp = i.permanentEff.get("harvest").getAsJsonObject()
                        .get("councilPrivilege").getAsJsonObject();
                if (tmp != null) {
                    harvRes.merge(new Council().choosePrivilege());
                }
            }

        }

    }
}