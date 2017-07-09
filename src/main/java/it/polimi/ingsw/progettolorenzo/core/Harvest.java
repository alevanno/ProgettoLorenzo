package it.polimi.ingsw.progettolorenzo.core;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.*;
import java.util.logging.Logger;

/**
 *
 */
public class Harvest extends ActionProdHarv {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private FamilyMember mainHarvest;
    private List<FamilyMember> secondaryHarvest = new ArrayList<>();

    /**
     * Player can claim the space if mainHarv == null or if he has Ariosto.
     * with Ariosto a player can claim the space even if he did so himself
     * previously, granted that one of the famMem is the Blank one
     * @param fam the family member claiming the place.
     * @return
     */
    public boolean claimFamMain(FamilyMember fam) {
        Player p = fam.getParent();
        //
        if (this.mainHarvest == null || fam.getParent().leaderIsActive("Ludovico Ariosto")&&
                (!p.equals(this.mainHarvest.getParent()) || p.equals(this.mainHarvest.getParent()) &&
                        ("Blank".equals(fam.getSkinColour()) || "Blank".equals(this.mainHarvest.getSkinColour())))) {
            if (harv(fam.getParent(), fam.getActionValue())) {
                this.addAction(new TakeFamilyMember(fam));
                if(this.mainHarvest == null) {
                    this.addAction(new PlaceFamMemberInProdHarv(fam, this, true));
                }
                return true;
            }
        }
        return false;
    }

    //TODO testing
    public boolean claimFamSec(FamilyMember fam) {
        Player p = fam.getParent();
        if (!("Blank".equals(fam.getSkinColour())) &&
                secondaryHarvest.stream().anyMatch(fMem -> p.equals(fMem.getParent()) && !("Blank".equals(fMem.getSkinColour())))) {
            return false;
        }
        this.addAction(new TakeFamilyMember(fam));
        this.addAction(new PlaceFamMemberInProdHarv(fam, this, false));
        harv(fam.getParent(), fam.getActionValue() - 3);
        return false;
    }

    protected void placeFamilyMember(FamilyMember fam, boolean isMainSpace) {
        if (isMainSpace) {
            this.mainHarvest = fam;
        } else {
            this.secondaryHarvest.add(fam);
        }
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
            if (base(i).has("resources")) {
                Resources tmp = Resources.fromJson(base(i).get("resources").getAsJsonObject());
                this.addAction(new ResourcesAction("Resources", tmp, player));
                log.info("Harvest: Card " + i.cardName + " gave " + tmp.toString());
            }
        }
    }

    private void harvCouncPriv(Deck tempDeck, Player player) {
        //councilPrivilege given by static Cards
        for (Card i : tempDeck) {
            if (base(i).has("councilPrivilege")) {
                int priv = base(i).get("councilPrivilege").getAsInt();
                log.info("Harvest: Card " + i.cardName + " gave " + priv + " Council privilege");
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
        value = checkValue(player, value, "harvestPlusValue", "harvMalus");
        if (value < 1) {
            player.sOut("You need an action value of at least 1");
            return false;
        }
        Deck tempDeck = new Deck();
        //filters the current player's deck, keeping Cards with permanentEffect=harvest
        //excludes Cards having too high of an action value
        for (Card i : player.listCards()) {
            if (i.permanentEff.containsKey("harvest") &&
                    i.permanentEff.get("harvest").getAsJsonObject().get("value").getAsInt() <= value) {
                tempDeck.add(i);
            }
            if (tempDeck.size() == 0) {
                log.info("Action value too low: player " + player + " only receives Resources from BonusTile");
            }
        }
        harvBonusTile(player);
        harvStaticCards(tempDeck, player);
        harvCouncPriv(tempDeck, player);
        return true;
    }

    public JsonObject serialize() {
        Map<String, Object> ret = new HashMap<>();
        List<JsonObject> secondaryJ = new ArrayList<>();
        this.secondaryHarvest.forEach(f -> secondaryJ.add(f.serialize()));
        if (this.mainHarvest != null) {
            ret.put("main", this.mainHarvest.serialize());
        }
        ret.put("secondary", secondaryJ);
        return new Gson().fromJson(new Gson().toJson(ret), JsonObject.class);
    }
}
