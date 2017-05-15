package it.polimi.ingsw.ProgettoLorenzo.Core;

import com.google.gson.JsonObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


public class Harvest {
    private FamilyMember mainProduction;
    private List<FamilyMember> secondaryProduction = new ArrayList<>();

    public void claimFamMain(FamilyMember fam) {
        this.mainProduction = fam;
        //harv(fam.parent, fam.actionValue);
    }
    //FIXME non gestisce l'incremento azione con servitori

    public void claimFamSec(FamilyMember fam) {
        this.secondaryProduction.add(fam);
        //harv(fam.parent, fam.ActionValue);
    }

    public void harv(Player p, int value) throws FileNotFoundException {
        Deck tempDeck = new Deck();
        Resources harvRes = new Resources.ResBuilder().build();
        if (value < 1) {
            System.out.println("You need an action value of at least 1");
            //TODO deve restituire errore al livello superiore
        } else {
            //leggere le carte di player che hanno effetto permanente raccolto
            //escludere quelle di valore superiore all'azione compiuta
            for (Card i : p.listCards()) {
                if (i.permanentEff.containsKey("harvest") &&
                        i.permanentEff.get("harvest").getAsJsonObject().get("value").getAsInt() <= value) {
                    tempDeck.add(i);
                }
            }
            //risorse dalla bonusTile
            harvRes.merge(p.BonusT.getHarvestRes());
            //risorse dalle carte
            for (Card i : tempDeck) {
                Resources tmp = Resources.fromJson(i.permanentEff.get("harvest")
                        .getAsJsonObject().get("resources").getAsJsonObject());
                harvRes.merge(tmp);
            }
            //gestisce il caso CouncilPrivilege
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