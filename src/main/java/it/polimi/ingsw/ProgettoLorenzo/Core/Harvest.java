package it.polimi.ingsw.ProgettoLorenzo.Core;

import com.google.gson.JsonObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class Harvest extends Action {
    private FamilyMember mainHarvest;
    private List<FamilyMember> secondaryHarvest = new ArrayList<>();

    //TODO Game will handle the return value;
    public boolean claimFamMain(FamilyMember fam) {
        if (this.mainHarvest != null) {
            if (harv(fam.getParent(), fam.getActionValue())) {
                this.mainHarvest = fam;
                return true;}
        }
        return false;
    }
    //FIXME non gestisce l'incremento azione con servitori

    public void claimFamSec(FamilyMember fam) {
        this.secondaryHarvest.add(fam);
        harv(fam.getParent(), fam.getActionValue());
    }

    private JsonObject base(Card i) {
        return i.permanentEff.get("harvest").getAsJsonObject();
    }

    private void harvBonusTile(Player player) {
        //resources given by bonusTile
        this.addAction(new ResourcesAction(
                "BonusTile", player.bonusT.getHarvestRes(), player));
        System.out.println("Harvest: The Player's BonusTile gave " + player.bonusT.getHarvestRes().toString());
    }

    private void harvStaticCards(Deck tempDeck, Player player) {
        //resources given by static Cards
        for (Card i : tempDeck) {
            if (base(i).get("resources") != null) {
                Resources tmp = Resources.fromJson(base(i).get("resources").getAsJsonObject());
                this.addAction(new ResourcesAction("Resources", tmp, player));
                System.out.println("Harvest: Card " + i.getCardName() + " gave " + tmp.toString());
            }
        }
    }

    private void harvCouncPriv(Deck tempDeck, Player player) {
        //councilPrivilege given by static Cards
        for (Card i : tempDeck) {
            if (base(i).get("councilPrivilege") != null) {
                int priv = base(i).get("councilPrivilege").getAsInt();
                System.out.println("Harvest: Card " + i.getCardName() + " gave " + String.valueOf(priv) + " Council privilege");
                Set<Resources> privRes = (new Council().chooseMultiPrivilege(priv));
                for (Resources r : privRes) {
                    this.addAction(new ResourcesAction(
                            "HarvCouncilPrivilege", r, player));
                    System.out.println("Harvest: Council privilege gave " + r.toString());
                }
            }
        }
    }

    public boolean harv(Player player, int value) {
        Deck tempDeck = new Deck();
        if (value < 1) {
            System.out.println("You need an action value of at least 1");
            return false;
        }
        //filters the current player's deck, keeping Cards with permanentEffect=harvest
        //excludes Cards having too high of an action value
        for (Card i : player.listCards()) {
            if (i.permanentEff.containsKey("harvest") &&
                    i.permanentEff.get("harvest").getAsJsonObject().get("value").getAsInt() <= value) {
                tempDeck.add(i);
            }
            if (tempDeck.size() == 0) {
                System.out.println("Action value too low: you will only receive Resources from your BonusTile");
            }
        }
        harvBonusTile(player);
        harvStaticCards(tempDeck, player);
        harvCouncPriv(tempDeck, player);
        return true;
    }
}
