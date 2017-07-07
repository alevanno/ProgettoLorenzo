package it.polimi.ingsw.progettolorenzo.core;

import java.util.*;

public class LeaderUtils {

    private LeaderUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean checkCardTypeSatisfaction(Player owner, String type, int cost) {
        int counter = 0;
        for (Card card : owner.listCards()) {
            if (type.equals(card.cardType)) {
                counter++;
            }
        }
        return counter >= cost;
    }

    public static boolean checkCostResSatisfaction(Player owner, Resources cost) {
        Resources plRes = owner.getCurrentRes();
        return (!plRes.merge(cost.inverse()).isNegative());
    }


    private static boolean onePerRoundApply(Player owner, LeaderCard card) {
        owner.sOut("Would you like to activate the one per round ability? ");
        if (owner.sInPromptConf()) {
            if (card.onePerTurnUsage) {
                owner.sOut("You have already activated the one per round ability in this turn");
                return false;
            }
            card.onePerRoundAbility();
            return true;
        } else {
            owner.sOut("You didn't activate the one per round ability");
            return false;
        }
    }

    public static boolean commonApply(Player owner, LeaderCard card, boolean checkT, boolean checkC) {
        if (card.isActivated()) {
            if (card.hasOnePerRoundAbility()) {
                return onePerRoundApply(owner, card);
            }
            owner.sOut("You have already activated this Leader Card!");
            return false;
        }
        if ((checkC && checkT) || (checkC ^ checkT)) {
            owner.sOut("You satisfy the "+ card.name + " leader card " +
                    "activation cost");
            owner.sOut("Would you activate it?");
            boolean ret = owner.sInPromptConf();
            if(ret){
                card.activation = true;
                card.permanentAbility();
                owner.sOut("Leader card activated");
                return true;
            } else {
                owner.sOut("You didn't activate the Leader card");
                return false;
            }
        } else {
            owner.sOut("You still don't satisfy the activation cost");
            return false;
        }
    }

    public static void oneHarvProd(String action, Player owner, LeaderCard card, int value) {
        owner.sOut("It allows to call a " + action + " of value " + value);
        owner.sOut("Do you want to increase its value?: ");
        if (owner.sInPromptConf()) {
            value += owner.increaseValue();
            owner.sOut("Current " + action + " value: " + value);
        }
        if("harvest".equals(action)) {
            owner.getParentGame().getBoard().harvestArea.harv(owner, value);
        } else {
            owner.getParentGame().getBoard().productionArea.prod(owner, value);
        }
        owner.sOut("One per round ability accomplished");
        card.onePerTurnUsage = true;
    }

    public static boolean checkMultiType(List<String> types,
                                         List<Integer> cost,
                                         Player owner){
        for (String type : types) {
            int i = 0;
            if(!checkCardTypeSatisfaction(owner, type, cost.get(i))){
                return false;
            }
        }
        // if it arrives here, all the check are satisfied together
        return true;
    }

    public static Map<String, LeaderCard> leadersBirth() {
        Map<String, LeaderCard> map = new HashMap<>();
        List<LeaderCard> list = new ArrayList<>();
        list.addAll(Arrays.asList(
                new BartolomeoColleoni(),
                new CesareBorgia(),
                new CosimoDeMedici(),
                new FedericoDaMontefeltro(),
                new FilippoBrunelleschi(),
                new FrancescoSforza(),
                new GiovanniDalleBandeNere(),
                new GirolamoSavonarola(),
                new LorenzoDeMedici(),
                new LeonardoDaVinci(),
                new LucreziaBorgia(),
                new LudovicoAriosto(),
                new LudovicoIIIGonzaga(),
                new LudovicoIlMoro(),
                new MichelangeloBuonarroti(),
                new PicoDellaMirandola(),
                new SandroBotticelli(),
                new SantaRita(),
                new SigismondoMalatesta(),
                new SistoIV()));
        for (LeaderCard card : list) {
            map.put(card.getName(), card);
        }
        return map;
    }
}
