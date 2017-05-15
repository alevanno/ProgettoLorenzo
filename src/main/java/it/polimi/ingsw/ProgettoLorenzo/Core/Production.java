package it.polimi.ingsw.ProgettoLorenzo.Core;

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
            //leggere le carte di player che hanno effetto permanente produzione
            //escludere quelle di valore superiore all'azione compiuta
            for (Card i : p.listCards()) {
                if (i.permanentEff.containsKey("production") &&
                    i.permanentEff.get("production").getAsJsonObject().get("value").getAsInt() <= value) {
                        tempDeck.add(i);
                }
            }
            //TODO proporre scelte
            //TODO moltiplicatori
            //risorse dalla bonusTile
            prodRes.merge(p.BonusT.getProductionRes());

            //risorse dalle carte
            for (Card i : tempDeck) {
                Resources tmp = Resources.fromJson(i.permanentEff.get("production")
                        .getAsJsonObject().get("production").getAsJsonObject());
                prodRes.merge(tmp);
            }
        }

    }
}
//containsKey("production")