package it.polimi.ingsw.ProgettoLorenzo.Core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Production extends Action {
    private FamilyMember mainProduction;
    private List<FamilyMember> secondaryProduction = new ArrayList<>();

    public void claimFamMain(FamilyMember fam) {
        this.mainProduction = fam;
        //prod(fam.parent, fam.actionValue);
    }
    //FIXME non gestisce l'incremento azione con servitori

    public void claimFamSec(FamilyMember fam) {
        this.secondaryProduction.add(fam);
        //prod(fam.parent, fam.ActionValue);
    }

    public void prod(Player p, int value) {
        Deck tempDeck = new Deck();
        Resources prodRes = new Resources.ResBuilder().build();
        if (value < 1) {
            System.out.println("You need an action value of at least 1");
            //TODO deve restituire errore al livello superiore
        } else {
            //filters the current player's deck, keeping Cards with permanentEffect=production
            //excludes Cards having too high of an action value
            for (Card i : p.listCards()) {
                if (i.permanentEff.containsKey("production") &&
                    i.permanentEff.get("production").getAsJsonObject().get("value").getAsInt() <= value) {
                        tempDeck.add(i);
                }
            }
            //TODO proporre scelte
            //handles the "multiplier" type of production
            for (Card i : tempDeck) {
                JsonObject mult = i.permanentEff.get("production")
                        .getAsJsonObject().get("multiplier").getAsJsonObject();
                if (mult != null) {
                    String tmpType = mult.get("type").getAsString();
                    Resources tmpRes = Resources.fromJson(mult.get("bonus").getAsJsonObject());
                    int count = 0;
                    for (Card c : p.listCards()) {
                        if (c.cardType == tmpType) { count++; }
                    }
                    prodRes.merge(tmpRes.multiplyRes(count));
                }
            }

            //resources given by BonusTile
            prodRes.merge(p.bonusT.getProductionRes());

            //resources given by static Cards
            for (Card i : tempDeck) {
                Resources tmp = Resources.fromJson(i.permanentEff.get("production")
                        .getAsJsonObject().get("resources").getAsJsonObject());
                prodRes.merge(tmp);
            }
        }

    }
}
//containsKey("production")