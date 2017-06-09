package it.polimi.ingsw.progettolorenzo.core;

public class LeaderUtils {
    public static int incCardTypeCounter(Player owner, String type) {
        int counter = 0;
        for (Card card : owner.listCards()) {
            if (type.equals(card.cardType)) {
                counter++;
            }
        }
        return counter;
    }


    public static boolean checkCardTypeSatisfaction(Player owner, String type, int cost) {
        int counter = 0;
        for (Card card : owner.listCards()) {
            if (type.equals(card.cardType)) {
                counter++;
            }
        }
        if (counter >= cost) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkCostResSatisfaction(Player owner, Resources cost) {
        Resources plRes = owner.currentRes;
        if (!plRes.merge(cost.inverse()).isNegative()){
           return true;
        } else {
            return false;
        }
    }


    public static boolean commonApply(Player owner, LeaderCard card, boolean checkT, boolean checkC) {
        if (card.hasOnePerRoundAbility() && card.isActivated()) {
            owner.sOut("Would you like to activate the one per round ability? ");
            boolean ret = owner.sInPromptConf();
            if (ret) {
                if(card.onePerRoundUsage) {
                    owner.sOut("You have already activated it in this round");
                    return false;
                }
                card.onePerRoundAbility();
                return true;
            } else {
                owner.sOut("You didn't activate the one per round ability");
                return false;
            }
        }
        if ((checkC && checkT) || (checkC ^ checkT)) {
            owner.sOut("You satisfy the "+ card.name + "leader card " +
                    "activation cost");
            owner.sOut("Would you activate it?");
            boolean ret = owner.sInPromptConf();
            if(ret){
                card.permanentAbility();
                owner.sOut("Leader card activated");
                return true;
            } else {
                owner.sOut("You didn't activate the Leader card permanent ability");
                return false;
            }
        } else {
            owner.sOut("you still don't satisfy the activation cost");
            return false;
        }
    }

    public static void OneHarvProd(String action, Player owner, LeaderCard card) {
        int value = 0;
        boolean ok = false;
        owner.sOut("It allows to call a " + action + " of value 1");
        owner.sOut("Do you want to increase your it?: ");
        if (owner.sInPromptConf()) {
            while (!ok) {
                value = owner.increaseValue();
                owner.sOut("Current " + action + " value: " + value);
                owner.sOut("Confirm?:");
                if (owner.sInPromptConf()) {
                    owner.currentRes = owner.currentRes.merge(new
                            Resources.ResBuilder().servant(value)
                            .build().inverse());
                    ok = true;
                }
            }
        } else {
            value = 1;
        }
        if("harvest".equals(action)) {
            owner.getParentGame().getBoard().harvestArea.harv(owner, value);
        } else {
            owner.getParentGame().getBoard().productionArea.prod(owner, value);
        }
        owner.sOut("One per round ability accomplished");
        card.onePerRoundUsage = true;
    }
}
