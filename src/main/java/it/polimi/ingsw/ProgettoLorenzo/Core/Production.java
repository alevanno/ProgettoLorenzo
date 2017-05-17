package it.polimi.ingsw.ProgettoLorenzo.Core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    private void prodMultiplier(Deck tempDeck) {
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
                this.addAction(new ResourcesAction("ProdMultiplier", tmpRes.multiplyRes(count)));
                System.out.println("Production: Multiplier Card " + i.getCardName() + " gave " + tmpRes.toString());
            }
        }
    }

    private void prodConversion(Deck tempDeck) {
        //TODO
        for (Card i : tempDeck) {
            JsonArray arr = base(i).get("conversion").getAsJsonArray();
            for (JsonElement conv : arr) {
                JsonArray src = conv.getAsJsonObject().get("src").getAsJsonArray();
                JsonArray dest = conv.getAsJsonObject().get("dest").getAsJsonArray();
                List<Resources> resSrc = new ArrayList<>();
                for (JsonElement a : src) {
                        resSrc.add(Resources.fromJson(a.getAsJsonObject()));
                }

                //gestire scelta origine se l'array contiene più di un elem
                //gestire scelta destinazione come sopra
            }
        }
    }

    private void prodBonusTile() {
        //resources given by BonusTile
        this.addAction(new ResourcesAction("BonusTile", player.bonusT.getProductionRes()));
        System.out.println("Production: Bonus Tile gave " + player.bonusT.getProductionRes().toString());
    }

    private void prodStaticCards(Deck tempDeck) {
        //resources given by static Cards
        for (Card i : tempDeck) {
            Resources tmp = Resources.fromJson(base(i).get("resources").getAsJsonObject());
            this.addAction(new ResourcesAction("Resources", tmp));
            System.out.println("Production: Cards gave " + tmp.toString());
        }
    }

    private void prodCouncPriv(Deck tempDeck) throws FileNotFoundException {
        //councilPrivilege given by static Cards
        for (Card i : tempDeck) {
            int priv = base(i).get("councilPrivilege").getAsInt();
            Set<Resources> privRes = (new Council().chooseMultiPrivilege(priv));
            for (Resources r : privRes) {
                this.addAction(new ResourcesAction("ProdCouncilPrivilege", r));
                System.out.println("Production: Cards gave " + r.toString());
            }
        }
    }


    public void prod(Player player, int value) throws FileNotFoundException {
        this.setPlayer(player);
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
        //prodConversion(tempDeck);
        prodMultiplier(tempDeck);
        prodBonusTile();
        prodStaticCards(tempDeck);
        prodCouncPriv(tempDeck);
    }
}