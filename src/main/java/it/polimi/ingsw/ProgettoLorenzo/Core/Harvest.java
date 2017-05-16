package it.polimi.ingsw.ProgettoLorenzo.Core;

import com.google.gson.JsonObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class Harvest extends Action {
    private FamilyMember mainHarvest;
    private List<FamilyMember> secondaryHarvest = new ArrayList<>();

    public void claimFamMain(FamilyMember fam) throws FileNotFoundException {
        this.mainHarvest = fam;
        harv(fam.getParent(), fam.getActionValue());
    }
    //FIXME non gestisce l'incremento azione con servitori

    public void claimFamSec(FamilyMember fam) throws FileNotFoundException {
        this.secondaryHarvest.add(fam);
        harv(fam.getParent(), fam.getActionValue());
    }

    private JsonObject base(Card i) {
        return i.permanentEff.get("harvest").getAsJsonObject();
    }

    private void harvBonusTile() {
        //resources given by bonusTile
        this.addAction(new ResourcesAction("BonusTile", player.bonusT.getHarvestRes()));
    }

    private void harvStaticCards(Deck tempDeck) {
        //resources given by static Cards
        for (Card i : tempDeck) {
            Resources tmp = Resources.fromJson(base(i).get("resources").getAsJsonObject());
            this.addAction(new ResourcesAction("Resources", tmp));
        }
    }

    private void harvCouncPriv(Deck tempDeck) throws FileNotFoundException {
        //councilPrivilege given by static Cards
        for (Card i : tempDeck) {
            int priv = base(i).get("councilPrivilege").getAsInt();
            Set<Resources> privRes = (new Council().chooseMultiPrivilege(priv));
            for (Resources r : privRes) {
                this.addAction(new ResourcesAction("HarvCouncilPrivilege", r));
            }
        }
    }

    public void harv(Player player, int value) throws FileNotFoundException {
        Deck tempDeck = new Deck();
        Resources harvRes = new Resources.ResBuilder().build();
        if (value < 1) {
            System.out.println("You need an action value of at least 1");
            //TODO ABORT deve restituire errore al livello superiore
        }
        //filters the current player's deck, keeping Cards with permanentEffect=harvest
        //excludes Cards having too high of an action value
        for (Card i : player.listCards()) {
            if (i.permanentEff.containsKey("harvest") &&
                    i.permanentEff.get("harvest").getAsJsonObject().get("value").getAsInt() <= value) {
                tempDeck.add(i);
            }
        }
        harvBonusTile();
        harvStaticCards(tempDeck);
        harvCouncPriv(tempDeck);
    }
}