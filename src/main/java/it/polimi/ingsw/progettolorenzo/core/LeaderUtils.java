package it.polimi.ingsw.progettolorenzo.core;

import it.polimi.ingsw.progettolorenzo.Game;

import java.util.*;

/**
 * This is an utility class for LeaderCard.
 * All the checks and common parts are inserted here to avoid duplications.
 */
public class LeaderUtils {

    private LeaderUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * it checks if a player owns enough development cards to activate the LeaderCard
     * @param owner is the owner of the leader card
     * @param type is the development card type
     * @param cost is the number of development cards required
     * @return a boolean value to be used in {@link #commonApply(Player, LeaderCard, boolean, boolean)}.
     */
    public static boolean checkCardTypeSatisfaction(Player owner, String type, int cost) {
        int counter = 0;
        for (Card card : owner.listCards()) {
            if (type.equals(card.cardType)) {
                counter++;
            }
        }
        return counter >= cost;
    }

    /**
     * it checks if a player owns enough resources to activate the LeaderCard
     * @param owner the owner of the leader card
     * @param cost the resource to be satisfied
     * @return boolean value to be used in {@link #commonApply(Player, LeaderCard, boolean, boolean)}.
     */
    public static boolean checkCostResSatisfaction(Player owner, Resources cost) {
        Resources plRes = owner.getCurrentRes();
        return (!plRes.merge(cost.inverse()).isNegative());
    }

    /**
     * it handles the activation of the one per turn ability.
     * @see #commonApply(Player, LeaderCard, boolean, boolean).
     * @param owner the owner of the card.
     * @param card the leader card in which one per turn ability have to be activated.
     * @return boolean value representing the activation or not (it may be already activated) of the card.
     */
    private static boolean onePerTurnApply(Player owner, LeaderCard card) {
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

    /**
     * The main handler of the activation of a permanent or one per turn ability.
     * The two boolean value are combined into a complex boolean condition.
     * They can't be both false to satisfy the activation cost.
     * @see #onePerTurnApply(Player, LeaderCard)
     * @param owner the owner of the card
     * @param card the leader card to be activated
     * @param checkT the boolean value representing the card cost type satisfaction
     * @param checkC the boolean value representing the resources cost satisfaction
     * @return the boolean value that will be used in {@link LeaderCard#apply()}
     */
    public static boolean commonApply(Player owner, LeaderCard card, boolean checkT, boolean checkC) {
        if (card.isActivated()) {
            if (card.hasOnePerRoundAbility()) {
                return onePerTurnApply(owner, card);
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

    /**
     * Utility method that call an harvest/production action with the selected value.
     * @param action the action type
     * @param owner the owner of the card
     * @param card the card caller of the action
     * @param value the value of the action
     */
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

    /**
     * it is used when multiple development card type cost have to be satisfied.
     * @see #checkCardTypeSatisfaction(Player, String, int).
     * @param types list of development card types.
     * @param cost list of numbers of development card required.
     * @param owner the owner of the card.
     * @return a boolean value to be used in {@link #commonApply(Player, LeaderCard, boolean, boolean)}.
     */
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

    /**
     * Utility method to lead the birth of all the leader cards.
     * @return The leader card map used in {@link Game#assignLeaderCards()}
     */
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
