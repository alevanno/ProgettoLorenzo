package it.polimi.ingsw.ProgettoLorenzo.Core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.FileNotFoundException;
import java.util.*;

public class Production extends Action {
    private FamilyMember mainProduction;
    private List<FamilyMember> secondaryProduction = new ArrayList<>();

    public void claimFamMain(FamilyMember fam) throws FileNotFoundException {
        this.mainProduction = fam;
        prod(fam.getParent(), fam.getActionValue());
    }
    //FIXME non gestisce l'incremento azione con servitori

    public void claimFamSec(FamilyMember fam) throws FileNotFoundException { //TODO valore decrementato di 3
        this.secondaryProduction.add(fam);
        prod(fam.getParent(), fam.getActionValue() - 3);
    }

    private JsonObject base(Card i) {
        return i.permanentEff.get("production").getAsJsonObject();
    }

    private void prodMultiplier(Deck tempDeck, Player player) {
        //handles the "multiplier" type of production
        Resources prodRes = new Resources.ResBuilder().build();
        for (Card i : tempDeck) {
            JsonObject mult = base(i).get("multiplier").getAsJsonObject();
            if (mult != null) {
                String tmpType = mult.get("type").getAsString();
                Resources tmpRes = Resources.fromJson(mult.get("bonus").getAsJsonObject());
                int count = 0;
                for (Card c : player.listCards()) {
                    if (c.cardType.equals(tmpType)) { count++; }
                }
                prodRes.merge(tmpRes.multiplyRes(count));
                this.addAction(new ResourcesAction(
                        "ProdMultiplier", tmpRes.multiplyRes(count), player));
                System.out.println("Production: Multiplier Card " + i.getCardName() + " gave " + tmpRes.toString());
            }
        }
    }

    private void prodConversion(Deck tempDeck, Player player) {
        //TODO
        for (Card i : tempDeck) {
            JsonArray arr = base(i).get("conversion").getAsJsonArray();
            for (JsonElement conv : arr) { //SINGOLA CONV
                JsonArray src = conv.getAsJsonObject().get("src").getAsJsonArray();
                JsonArray dest = conv.getAsJsonObject().get("dest").getAsJsonArray();
                List<Resources> resSrc = new ArrayList<>();
                System.out.println("Production: Card " + i.getCardName() + " allows you to convert some resources");
                for (JsonElement a : src) {
                    resSrc.add(Resources.fromJson(a.getAsJsonObject()));
                }
                Resources resDest = Resources.fromJson(dest.get(0).getAsJsonObject().get("resources").getAsJsonObject());
                int p = dest.get(1).getAsJsonObject().get("councilPrivilege").getAsInt();
                System.out.println("Available conversions:  \n0: None");
                int count = 1;
                for (Resources r1 : resSrc) {
                    if(resDest != null) {
                        System.out.println(String.valueOf(count) + ": " + r1.toString() + " -> " + resDest.toString());
                        count++;
                    } else if (p != 0) {
                        System.out.println(String.valueOf(count) + ": " + r1.toString() + " -> " + String.valueOf(p) +" CouncilPrivilege");
                        count++;
                    }
                }
                System.out.println("Insert an int between 0 and " + resSrc.size());
                Scanner in = new Scanner(System.in);
                int choice = in.nextInt();
                if (choice == 0) {}
                else if (choice != 0) {
                    this.addAction(new ResourcesAction("Conversion source", resSrc.get(choice-1).inverse(), player));
                    if((resDest != null)) {
                        this.addAction(new ResourcesAction("Conversion dest", resDest, player));
                    } else if (p != 0) {
                        Set<Resources> privRes = (new Council().chooseMultiPrivilege(p));
                        for (Resources r : privRes) {
                            this.addAction(new ResourcesAction(
                                    "Coversion CouncilPrivilege", r, player));
                        }
                    }
                }
            }
        }
    }


    private void prodBonusTile(Player player) {
        //resources given by BonusTile
        this.addAction(new ResourcesAction(
                "BonusTile", player.bonusT.getProductionRes(), player));
        System.out.println("Production: The Player's BonusTile gave " + player.bonusT.getProductionRes().toString());
    }

    private void prodStaticCards(Deck tempDeck, Player player) {
        //resources given by static Cards
        for (Card i : tempDeck) {
            Resources tmp = Resources.fromJson(base(i).get("resources").getAsJsonObject());
            this.addAction(new ResourcesAction("Resources", tmp, player));
            System.out.println("Production: Card " + i.getCardName() + " gave " + tmp.toString());
        }
    }

    private void prodCouncPriv(Deck tempDeck, Player player) {
        //councilPrivilege given by static Cards
        for (Card i : tempDeck) {
            int priv = base(i).get("councilPrivilege").getAsInt();
            System.out.println("Production: Card " + i.getCardName() + " gave " + String.valueOf(priv) + " Council privilege");
            Set<Resources> privRes = (new Council().chooseMultiPrivilege(priv));
            for (Resources r : privRes) {
                this.addAction(new ResourcesAction(
                        "ProdCouncilPrivilege", r, player));
                System.out.println("Production: Council privilege gave " + r.toString());
            }
        }
    }


    public void prod(Player player, int value) {
        Deck tempDeck = new Deck();

        if (value < 1) {
            System.out.println("You need an action value of at least 1");
            //TODO ABORT, deve restituire errore al livello superiore
        }
        //filters the current player's deck, keeping Cards with permanentEffect=production
        //excludes Cards having too high of an action value
        for (Card i : player.listCards()) {
            if (i.permanentEff.containsKey("production") &&
                    base(i).get("value").getAsInt() <= value) {
                tempDeck.add(i);
            }
        }
        //Chiamare tutte le funzioni
        prodConversion(tempDeck, player);
        /*prodMultiplier(tempDeck, player);
        prodBonusTile(player);
        prodStaticCards(tempDeck, player);
        prodCouncPriv(tempDeck, player);*/
    }
}
