package it.polimi.ingsw.progettolorenzo.core;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.*;
import java.util.logging.Logger;
import static java.lang.String.valueOf;

public class Production extends ActionProdHarv {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private FamilyMember mainProduction = null;
    private List<FamilyMember> secondaryProduction = new ArrayList<>();

    public boolean claimFamMain(FamilyMember fam) {
        Player p = fam.getParent();
        //Player can claim the space if mainProd == null or if he has Ariosto.
        //With Ariosto a player can claim the space even if he did so himself
        // previously, granted that one of the famMem is the Blank one
        if (this.mainProduction == null || p.leaderIsActive("Ludovico Ariosto") &&
                (!p.equals(this.mainProduction.getParent()) || p.equals(this.mainProduction.getParent()) &&
                        ("Blank".equals(fam.getSkinColour()) || "Blank".equals(this.mainProduction.getSkinColour())))) {
            if (prod(p, fam.getActionValue())) {
                this.addAction(new TakeFamilyMember(fam));
                if(this.mainProduction == null) {
                    this.addAction(new PlaceFamMemberInProdHarv(fam, this, true));
                }
                return true;
            }
        }
        return false;
    }

    public boolean claimFamSec(FamilyMember fam) {
        Player p = fam.getParent();
        if (!("Blank".equals(fam.getSkinColour())) &&
                secondaryProduction.stream().anyMatch(fMem -> p.equals(fMem.getParent()) && !("Blank".equals(fMem.getSkinColour())))) {
            return false;
        }
        this.addAction(new TakeFamilyMember(fam));
        this.addAction(new PlaceFamMemberInProdHarv(fam, this, false));
        prod(fam.getParent(), fam.getActionValue() - 3);
        return true;
    }

    protected void placeFamilyMember(FamilyMember fam, boolean isMainSpace) {
        if (isMainSpace) {
            this.mainProduction = fam;
        } else {
            this.secondaryProduction.add(fam);
        }
    }

    private JsonObject base(Card i) {
        return i.permanentEff.get("production").getAsJsonObject();
    }

    private void prodMultiplier(Deck tempDeck, Player player) {
        //handles the "multiplier" type of production
        for (Card i : tempDeck) {
            if (base(i).has("multiplier")) {
                JsonObject mult = base(i).get("multiplier").getAsJsonObject();
                String tmpType = mult.get("type").getAsString();
                Resources tmpRes = Resources.fromJson(mult.get("bonus"));
                int count = 0;
                for (Card c : player.listCards()) {
                    if (c.cardType.equals(tmpType)) { count++; }
                }
                this.addAction(new ResourcesAction(
                        "ProdMultiplier", tmpRes.multiplyRes(count), player));
                log.info("Production: Multiplier Card " + i.getCardName() + " gave " + tmpRes.multiplyRes(count).toString());
            }
        }
    }

    private void prodConversion(Deck tempDeck, Player player) {
        //conversions provided by cards
        for (Card i : tempDeck) {
            if (base(i).has("conversion")) {
                JsonArray arr = base(i).get("conversion").getAsJsonArray();
                player.sOut("Production: Card " + i.getCardName() + " allows you to convert some resources");
                player.sOut("Available conversions:  \n0: None");
                List<ResConv> resConvList = new ArrayList<>();
                int count = 1;
                for (int conv = 0; conv < arr.size(); conv++) {
                    JsonArray src = arr.get(conv).getAsJsonObject().get("src").getAsJsonArray();
                    JsonArray dest = arr.get(conv).getAsJsonObject().get("dest").getAsJsonArray();
                    //List<Resources> resSrcList = new ArrayList<>(); never used?
                    for (JsonElement a : src) {
                        Resources resSrc = Resources.fromJson(a);
                        Resources resDest;
                        int councDest;
                        if (dest.get(0).getAsJsonObject().has("resources")) {
                            resDest = Resources.fromJson(dest.get(0).getAsJsonObject().get("resources"));
                            resConvList.add(new ResConv(count, resSrc, resDest));
                            count++;
                        } else if (dest.get(0).getAsJsonObject().has("councilPrivilege")) {
                            councDest = dest.get(0).getAsJsonObject().get("councilPrivilege").getAsInt();
                            resConvList.add(new ResConv(count, resSrc, councDest));
                            count++;
                        }
                    }
                }
                for (ResConv rc : resConvList) {
                    player.sOut(rc.toString());
                }
                int choice = player.sInPrompt(0, count-1);
                if (choice != 0) {
                    this.addAction(new ResourcesAction("Conversion source", resConvList.get(choice - 1).getResSrc().inverse(), player));
                    player.sOut("Conversion removed " + resConvList.get(choice - 1).getResSrc());
                    if ((resConvList.get(choice - 1).getResDst() != null)) {
                        this.addAction(new ResourcesAction("Conversion dest", resConvList.get(choice - 1).getResDst(), player));
                        player.sOut("Conversion added " + resConvList.get(choice - 1).getResDst().toString());
                    } else if (resConvList.get(choice - 1).getCouncDst() != 0) {
                        Set<Resources> privRes = (new Council().chooseMultiPrivilege(resConvList.get(choice - 1).getCouncDst(), player));
                        for (Resources co : privRes) {
                            this.addAction(new ResourcesAction(
                                    "Conversion CouncilPrivilege", co, player));
                            log.info("Conversion gave a privilege, which gave " + co.toString());
                        }
                    }
                }
            }
        }
    }

    private void prodBonusTile(Player player) {
        //resources given by BonusTile
        this.addAction(new ResourcesAction(
                "BonusTile", player.getBonusT().getProductionRes(), player));
        log.info("Production: The Player's BonusTile gave " + player.getBonusT().getProductionRes().toString());
    }

    private void prodStaticCards(Deck tempDeck, Player player) {
        //resources given by static Cards
        for (Card i : tempDeck) {
            if (base(i).has("resources")) {
                Resources tmp = Resources.fromJson(base(i).get("resources"));
                this.addAction(new ResourcesAction("Resources", tmp, player));
                log.info("Production: Card " + i.getCardName() + " gave " + tmp.toString());
            }
        }
    }

    private void prodCouncPriv(Deck tempDeck, Player player) {
        //councilPrivilege given by static Cards
        for (Card i : tempDeck) {
            if (base(i).has("councilPrivilege")) {
                int priv = base(i).get("councilPrivilege").getAsInt();
                player.sOut("Production: Card " + i.getCardName() + " gave " + valueOf(priv) + " Council privilege");
                Set<Resources> privRes = (new Council().chooseMultiPrivilege(priv, player));
                for (Resources r : privRes) {
                    this.addAction(new ResourcesAction(
                            "ProdCouncilPrivilege", r, player));
                    log.info("Production: Council privilege gave " + r.toString());
                }
            }
        }
    }


    public boolean prod(Player player, int value) {
        value = checkValue(player, value, "productionPlusValue", "prodMalus");
        if (value < 1) {
            player.sOut("You need an action value of at least 1");
            return false;
        }
        Deck tempDeck = new Deck();
        //filters the current player's deck, keeping Cards with permanentEffect=production
        //excludes Cards having too high of an action value
        for (Card i : player.listCards()) {
            if (i.permanentEff.containsKey("production") &&
                    base(i).get("value").getAsInt() <= value) {
                tempDeck.add(i);
            }
        }
        if (tempDeck.size() == 0) {
            log.info("Action value too low: player " + player + " only receives Resources from BonusTile");
        }

        //finally calls all the prodMethods on tempDeck

        prodConversion(tempDeck, player);
        prodMultiplier(tempDeck, player);
        prodBonusTile(player);
        prodStaticCards(tempDeck, player);
        prodCouncPriv(tempDeck, player);
        return true;
    }
}