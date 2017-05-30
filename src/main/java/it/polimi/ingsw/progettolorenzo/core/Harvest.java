package it.polimi.ingsw.progettolorenzo.core;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;


public class Harvest extends Action {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private FamilyMember mainHarvest;
    private List<FamilyMember> secondaryHarvest = new ArrayList<>();

    //TODO Game will handle the return value;
    public boolean claimFamMain(FamilyMember fam) {
        if (this.mainHarvest != null && harv(fam.getParent(), fam.getActionValue())) {
            this.mainHarvest = fam;
            return true;
        }
        return false;
    }
    //FIXME non gestisce l'incremento azione con servitori
    //FIXME the secondary space is not available in a 2 player game
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
                "BonusTile", player.getBonusT().getHarvestRes(), player));
        log.info("Harvest: The Player's BonusTile gave " + player.getBonusT().getHarvestRes().toString());
    }

    private void harvStaticCards(Deck tempDeck, Player player) {
        //resources given by static Cards
        for (Card i : tempDeck) {
            if (base(i).get("resources") != null) {
                Resources tmp = Resources.fromJson(base(i).get("resources").getAsJsonObject());
                this.addAction(new ResourcesAction("Resources", tmp, player));
                log.info("Harvest: Card " + i.cardName + " gave " + tmp.toString());
            }
        }
    }

    private void harvCouncPriv(Deck tempDeck, Player player) {
        //councilPrivilege given by static Cards
        for (Card i : tempDeck) {
            if (base(i).get("councilPrivilege") != null) {
                int priv = base(i).get("councilPrivilege").getAsInt();
                log.info("Harvest: Card " + i.cardName + " gave " + String.valueOf(priv) + " Council privilege");
                Set<Resources> privRes = (new Council().chooseMultiPrivilege(priv, player));
                for (Resources r : privRes) {
                    this.addAction(new ResourcesAction(
                            "HarvCouncilPrivilege", r, player));
                    log.info("Harvest: Council privilege gave " + r.toString());
                }
            }
        }
    }

    public boolean harv(Player player, int value) {
        Deck tempDeck = new Deck();
        if (player.getExcommunications().get(0).has("harvMalus")) {
            int harvMalus = player.getExcommunications().get(0).get("harvMalus").getAsInt();
            player.sOut("Your excommunication lowers the value of this action by " + harvMalus);
            value -= harvMalus;
        }
        if (value < 1) {
            player.sOut("You need an action value of at least 1");
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
                log.info("Action value too low: you will only receive Resources from your BonusTile");
            }
        }
        harvBonusTile(player);
        harvStaticCards(tempDeck, player);
        harvCouncPriv(tempDeck, player);
        return true;
    }
}
