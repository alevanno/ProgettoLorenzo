package it.polimi.ingsw.progettolorenzo.core;


import it.polimi.ingsw.progettolorenzo.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LeaderCard class is firstly declared abstract to incorporate
 * all the common characteristics of the various Leader Cards.
 * All the <i> instantiated </i> cards have the following attributes:
 * owner of type {@link Player} set by {@link #setPlayer(Player)}, the card name, 2 lists which contain
 * the activation cost and the types of this cost (they could refer
 * to both to Development cards {@link Card} and Resources {@link Resources}.
 * Card's activation is checked by a boolean variable;
 * onePerTurn boolean variable check the presence of a one per turn ability;
 * onePerTurnUsage boolean variable check the activation of that ability.
 */

public class LeaderCard {
    protected Player owner;
    protected final String name;
    protected final  List<Integer> activationCost;
    protected final List<String> types;
    protected boolean activation;
    protected boolean onePerRound;
    protected boolean onePerTurnUsage;

    /**
     * All the child classes has a personal constructor in which the LeaderCard's
     * one is called and set with the correct parameters. A new LeaderCard object
     * is created.
     */
    public LeaderCard(String name, List<Integer> activationCost,
                      List<String> types,
                      boolean activation, boolean onePerRound,
                      boolean onePerRoundUsage) {
        this.name = name;
        this.activationCost = activationCost;
        this.types = types;
        this.activation = activation;
        this.onePerRound = onePerRound;
        this.onePerTurnUsage = onePerRoundUsage;
    }

    private final static String NOTIMPLEMENTED  = "Not available in the base class";

    /**
     * Implemented due to Lorenzo De Medici card permanent ability.
     * @return a new instance of the same card.
     */
    public LeaderCard cloneCard() {
        return new LeaderCard(this.name, this.activationCost, this.types,
                this.activation, this.onePerRound, this.onePerTurnUsage);
    }

    /**
     * This usually first set a boolean variable by
     * checking resources cost {@link LeaderUtils#checkCostResSatisfaction(Player, Resources)} or development card type cost
     * {@link LeaderUtils#checkCardTypeSatisfaction(Player, String, int)} )}.
     * They may appear together.
     * The boolean variable (can be 2) is used in {@link LeaderUtils#commonApply(Player, LeaderCard, boolean, boolean)}.
     *
     * @return the accomplishment of one of the possible action given in commonApply by a boolean value.
     */
    public boolean apply() {
        throw new UnsupportedOperationException(NOTIMPLEMENTED);
    }

    /**
     * Set the card owner. It is used in {@link Game#assignLeaderCards()}.
     *
     * @param pl the owner of the card
     */
    public void setPlayer(Player pl) {
        this.owner = pl;
    }

    public String getName(){
        return this.name;
    }
    public List<Integer> getActivationCost() {
        return activationCost;
    }
    public List<String> getCardCostType() {
        return this.types;
    }
    public boolean isActivated() {
        return this.activation;
    }


    /**
     * Every child Card that have a permanent ability override
     * this method. Generally it consists into a string message
     * (containing the description of the action) sent to Player's Client
     * by {@link Player#sOut(String)} and eventually the implementation of the action;
     * in fact the here the implementation can be empty and declared where the check for
     * the ability has to be done.
     */
    public void permanentAbility(){
        // it should be empty
    }

    /**
     * This has void return value because the method is used every time
     * that commonApply() is called; it should be empty.
     * Generally it contains a string message like in permanentAbility()
     * and the action implementation.
     * At the end of the action onePerTurnUsage is set to true.
     * @see #setOnePerRoundUsage(boolean).
     */
    public void onePerRoundAbility() {
        // it should be empty
    }

    public void printAbility() {}
    public boolean hasOnePerRoundAbility() {
        return this.onePerRound;
    }

    public void setOnePerRoundUsage(boolean bool){
        this.onePerTurnUsage = bool;
    }
}



class FrancescoSforza extends LeaderCard {
    public FrancescoSforza() {
        super("Francesco Sforza", Arrays.asList(5),
                Arrays.asList("ventures"), false,
                true, false );
    }

    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils.checkMultiType(types, activationCost, owner);
        return LeaderUtils.commonApply(owner, this, checkT, false);
    }


    @Override
    /**
     * This action allows for an harvest action of value 1.
     * @see Harvest#harv(Player, int);
     * It calls {@link LeaderUtils#oneHarvProd(String, Player, LeaderCard, int)}.
     */
    public void printAbility() {
        owner.sOut("It allows you to call an Harvest action of value 1");
    }

    public void onePerRoundAbility(){
        LeaderUtils.oneHarvProd("harvest", owner, this, 1);
    }
}

class FilippoBrunelleschi extends LeaderCard {
    public FilippoBrunelleschi() {
        super("Filippo Brunelleschi", Arrays.asList(5),
                Arrays.asList("buildings"), false, false,
                false);
    }

    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils.checkMultiType(types, activationCost, owner);
        return LeaderUtils.commonApply(owner, this, checkT, false);
    }

    public void printAbility() {
        owner.sOut("You will never have to pay" +
                " the additional cost if a tower is already occupied");
    }

    @Override
    public void permanentAbility() {
        owner.sOut("Permanent ability activated");
        printAbility();
    }
}

class LucreziaBorgia extends LeaderCard {
    public LucreziaBorgia() {
        super("Lucrezia Borgia", Arrays.asList(6),
                Arrays.asList("buildings",
                        "territories",
                        "ventures",
                        "characters"), false, false,
                false);
    }
    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils.checkMultiType(types, activationCost, owner);

        return LeaderUtils.commonApply(owner, this, checkT, false);
    }
    /**
     * It increase the action value of available fam members; next turn they will be increased
     * at their birth (not blank for both situations).
     */

    public void printAbility() {
        owner.sOut("Your colored family members" +
                " have a bonus of +2 on their value");
    }

    @Override
    public void permanentAbility() {
        owner.sOut("Permanent ability activated");
        printAbility();
        for (FamilyMember fam : owner.getAvailableFamMembers()) {
            if (!"Blank".equals(fam.getSkinColour())) {
                fam.setActionValue(fam.getActionValue() + 2);
            }
        }
    }
}

class LudovicoAriosto extends LeaderCard {
    public LudovicoAriosto() {
        super("Ludovico Ariosto", Arrays.asList(5),
                Arrays.asList("characters"), false,
                false, false);
    }

    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils.checkMultiType(types, activationCost, owner);
        return LeaderUtils.commonApply(owner, this, checkT, false);
    }

    public void printAbility() {
        owner.sOut("You can place your " +
                "family Members in occupied action spaces.");
    }
    @Override
    public void permanentAbility() {
        owner.sOut("Permanent ability activated");
        printAbility();
    }
}

class FedericoDaMontefeltro extends LeaderCard {
    public FedericoDaMontefeltro() {
        super("Federico Da Montefeltro", Arrays.asList(5),
                Arrays.asList("territories"),
                false, true, false);
    }
    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils.checkMultiType(types, activationCost, owner);
        return LeaderUtils.commonApply(owner, this, checkT, false);
    }

    public void printAbility() {
        owner.sOut("One of your colored Family Members " +
                "has a value of 6, regardless of its related die.");
    }

    @Override
    public void onePerRoundAbility() {
        int increasedValue = 6;
        printAbility();
        owner.sOut("Which one do you want to increase?");
        owner.displayFamilyMembers();
        FamilyMember famMem = owner.getAvailableFamMembers().get(
                owner.sInPrompt(1, owner.getAvailableFamMembers().size()) - 1);
        famMem.setActionValue(increasedValue);
        owner.sOut(famMem.getSkinColour() + " " +
                "family member increased by " + increasedValue);
        onePerTurnUsage = true;
    }
}

class GirolamoSavonarola extends LeaderCard {
    public GirolamoSavonarola() {
        super("Girolamo Savonarola", Arrays.asList(18),
                Arrays.asList("coin"),
                false, true, false);
    }
    @Override
    public boolean apply() {
        boolean checkC = LeaderUtils.checkCostResSatisfaction(owner,
                new Resources.ResBuilder().coin(activationCost.get(0)).build());
        return LeaderUtils.commonApply(owner, this, false, checkC);

    }

    public void printAbility() {
        owner.sOut("You gain 1 faith point.");

    }

    /**
     * @see Resources#merge(Resources).
     * It merges the gained faith point with Player current resources.
     */
    @Override
    public void onePerRoundAbility() {
        printAbility();
        owner.currentResMerge(new Resources
                .ResBuilder().faithPoint(1).build());
        onePerTurnUsage = true;

    }
}

class SistoIV extends LeaderCard {
    public SistoIV() {
        super("Sisto IV", Arrays.asList(6),
                Arrays.asList("coin", "wood", "stone", "servant"),
                false, false, false);
    }
    @Override
    public boolean apply() {
        boolean checkC = LeaderUtils.checkCostResSatisfaction(owner,
                new Resources.ResBuilder()
                        .coin(6).wood(6).servant(6).stone(6).build());
        return LeaderUtils.commonApply(owner, this, false, checkC);
    }

    public void printAbility() {
        owner.sOut("You gain 5 additional victory points when you support the" +
                "Church in a Vatican Report phase");
    }

    @Override
    public void permanentAbility() {
        owner.sOut("Permanent ability activated");
        printAbility();
    }
}

class SigismondoMalatesta extends LeaderCard {
    public SigismondoMalatesta() {
        super("Sigismondo Malatesta", Arrays.asList(7, 3),
                Arrays.asList("militaryPoint", "faithPoint"), false,
                false, false);
    }
    @Override
    public boolean apply() {
        boolean checkC = LeaderUtils.checkCostResSatisfaction(owner,
                new Resources.ResBuilder()
                        .militaryPoint(7).faithPoint(3).build());
        return LeaderUtils.commonApply(owner, this, false, checkC);
    }

    public void printAbility() {
        owner.sOut("Your Blank family member has a bonus of +3 on its value");

    }
    @Override
    public void permanentAbility() {
        owner.sOut("Permanent ability activated");
        printAbility();
        for (FamilyMember fam : owner.getAvailableFamMembers()) {
            if ("Blank".equals(fam.getSkinColour())) {
                fam.setActionValue(fam.getActionValue() + 3);
            }
        }
    }
}

class CesareBorgia extends LeaderCard {
    public CesareBorgia() {
        super("Cesare Borgia", Arrays.asList(3, 12, 2),
                Arrays.asList("buildings", "coin", "faithPoint"), false,
                false, false);
    }
    @Override
    public boolean apply() {
        boolean checkC = LeaderUtils
                .checkCostResSatisfaction(owner, new Resources
                        .ResBuilder().coin(12).faithPoint(2).build());
        boolean checkT = LeaderUtils.checkMultiType(types, activationCost, owner);
        return LeaderUtils.commonApply(owner, this, checkT, checkC);
    }

    public void printAbility() {
        owner.sOut("You don’t need to satisfy the military points requirement when " +
                "you take territory cards ");
    }
    @Override
    public void permanentAbility() {
        owner.sOut("Permanent ability activated");
        printAbility();
    }
}

class MichelangeloBuonarroti extends LeaderCard {
    public MichelangeloBuonarroti() {
        super("Michelangelo Buonarroti", Arrays.asList(10),
                Arrays.asList("stone"), false, true, false);
    }
    @Override
    public boolean apply() {
        boolean checkC = LeaderUtils.checkCostResSatisfaction(owner,
                new Resources.ResBuilder().stone(10).build());
        return LeaderUtils.commonApply(owner, this, false, checkC);
    }
    public void printAbility() {
        owner.sOut("You gain 3 coin.");

    }
    @Override
    public void onePerRoundAbility() {
        printAbility();
        owner.currentResMerge(new Resources
                .ResBuilder().coin(3).build());
        onePerTurnUsage = true;

    }
}

class SantaRita extends LeaderCard {
    public SantaRita() {
        super("Santa Rita", Arrays.asList(8),
                Arrays.asList("faithPoint"),
                false, false, false);
    }
    @Override
    public boolean apply() {
        boolean checkC = LeaderUtils.checkCostResSatisfaction(owner,
                new Resources.ResBuilder().faithPoint(8).build());
        return LeaderUtils.commonApply(owner, this, false, checkC);
    }
    public void printAbility() {
        owner.sOut("Each time you receive wood, stone, coins, or servants " +
                "as an immediate effect from Development Cards (not from an action space), " +
                "you receive the resources twice");
    }
    @Override
    public void permanentAbility() {
        owner.sOut("Permanent ability activated");
        printAbility();
    }
}

class GiovanniDalleBandeNere extends LeaderCard {
    public GiovanniDalleBandeNere() {
        super("Giovanni Dalle Bande Nere",
                Arrays.asList(12),
                Arrays.asList("militaryPoint"), false,
                true, false);
    }
    @Override
    public boolean apply() {
        boolean checkC = LeaderUtils.checkCostResSatisfaction(owner,
                new Resources.ResBuilder().militaryPoint(12).build());
        return LeaderUtils.commonApply(owner, this, false, checkC);
    }

    public void printAbility() {
        owner.sOut("You gain 1 coin, 1 wood, 1 stone");
    }
    /**
     * @see GirolamoSavonarola#onePerRoundAbility().
     */
    @Override
    public void onePerRoundAbility() {
        printAbility();
        owner.currentResMerge(new Resources
                .ResBuilder().wood(1).stone(1).coin(1).build());
        onePerTurnUsage = true;

    }
}

class CosimoDeMedici extends LeaderCard {
    public CosimoDeMedici() {
        super("Cosimo Dè Medici", Arrays.asList(2, 4),
                Arrays.asList("characters", "buildings"),
                false, true, false);
    }
    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils.checkMultiType(this.types,
                this.activationCost,owner);
        return LeaderUtils.commonApply(owner, this, checkT, false);
    }

    public void printAbility() {
        owner.sOut("You receive 3 servant and gained 1 victoryPoint");
    }
    /**
     * @see GirolamoSavonarola#onePerRoundAbility().
     */
    @Override
    public void onePerRoundAbility() {
        printAbility();
        owner.currentResMerge(new Resources
                .ResBuilder().servant(3).victoryPoint(1).build());
        onePerTurnUsage = true;
    }
}

class LeonardoDaVinci extends LeaderCard {
    public LeonardoDaVinci() {
        super("Leonardo Da Vinci",
                Arrays.asList(4,2),
                Arrays.asList("characters", "territories"),
                false, true, false);
    }
    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils.checkMultiType(this.types,this.activationCost, owner);
        return LeaderUtils.commonApply(owner, this, checkT, false);
    }

    public void printAbility() {
        owner.sOut("It allows to call a production of value 0");

    }
    /**
     * It allows for a production action of value 0.
     * @see FrancescoSforza#onePerRoundAbility().
     */
    @Override
    public void onePerRoundAbility() {
        LeaderUtils.oneHarvProd("production", owner, this, 0);
    }
}

class BartolomeoColleoni extends LeaderCard {
    public BartolomeoColleoni(){
        super("Bartolomeo Colleoni",
                Arrays.asList(2,4),
                Arrays.asList("ventures", "territories"),
                false, true, false);
    }
    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils.checkMultiType(this.types, this.activationCost,
                owner);
        return LeaderUtils.commonApply(owner, this, checkT, false);
    }

    public void printAbility() {
        owner.sOut("You gained 4 victoryPoint");
    }
    /**
     * @see GirolamoSavonarola#onePerRoundAbility().
     */
    @Override
    public void onePerRoundAbility() {
        printAbility();
        owner.currentResMerge(new Resources
                .ResBuilder().victoryPoint(4).build());
        onePerTurnUsage = true;
    }
}

class SandroBotticelli extends LeaderCard {
    public SandroBotticelli(){
        super("Sandro Botticelli",
                Arrays.asList(10),
                Arrays.asList("wood"),
                false, true, false);
    }
    @Override
    public boolean apply() {
        boolean checkC = LeaderUtils.checkCostResSatisfaction(owner,
                new Resources.ResBuilder().wood(10).build());
        return LeaderUtils.commonApply(owner, this, false, checkC);
    }
    public void printAbility() {
        owner.sOut("You gain 2 militaryPoint and 1 victoryPoint");

    }
    /**
     * @see GirolamoSavonarola#onePerRoundAbility().
     */
    @Override
    public void onePerRoundAbility() {
        printAbility();
        owner.currentResMerge(new Resources
                .ResBuilder().militaryPoint(2).victoryPoint(1).build());
        onePerTurnUsage = true;
    }
}

class LudovicoIIIGonzaga extends LeaderCard {
    public LudovicoIIIGonzaga(){
        super("Ludovico III Gonzaga",
                Arrays.asList(15),
                Arrays.asList("servant"),
                false, true, false);
    }
    @Override
    public boolean apply() {
        boolean checkC = LeaderUtils.checkCostResSatisfaction(owner,
                new Resources.ResBuilder().servant(15).build());
        return LeaderUtils.commonApply(owner, this, false, checkC);
    }
    public void printAbility() {
        owner.sOut("You gain 1 councilPrivilege");
    }
    /**
     * Resources from council privilege are merged to the Player current res.
     * @see Council#choosePrivilege(Player);
     */
    @Override
    public void onePerRoundAbility() {
        printAbility();
        Resources privRes = owner.getParentGame()
                .getBoard().councilPalace.choosePrivilege(owner);
        owner.currentResMerge(privRes);
        onePerTurnUsage = true;
    }
}

class LudovicoIlMoro extends LeaderCard {
    public LudovicoIlMoro(){
        super("Ludovico Il Moro",
                Arrays.asList(2,2,2,2),
                Arrays.asList("territories", "characters", "buildings",
                        "ventures"),
                false, false, false);
    }
    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils.checkMultiType(this.types, this.activationCost,
                owner);
        return LeaderUtils.commonApply(owner, this, checkT, false);
    }

    public void printAbility() {
        owner.sOut("Your colored Family Members has a value of 5, regardless of\n" +
                "their related dice");
    }
    @Override
    public void permanentAbility() {
        owner.sOut("Permanent ability activated");
        printAbility();
        for (FamilyMember fam : owner.getAvailableFamMembers()) {
            if (!"Blank".equals(fam.getSkinColour())) {
                fam.setActionValue(5);
            }
        }
    }
}

class PicoDellaMirandola extends LeaderCard {
    public PicoDellaMirandola(){
        super("Pico Della Mirandola",
                Arrays.asList(4,2),
                Arrays.asList("ventures", "buildings"),
                false, false, false);
    }
    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils.checkMultiType(this.types, this.activationCost,
                owner);
        return LeaderUtils.commonApply(owner, this, checkT, false);
    }

    public void printAbility() {
        owner.sOut("When you take Development Cards, you get a discount of" +
                "3 coins (if the card you are taking has coins in its cost.) This is not a discount on the\n" +
                "coins you must spend if you take a Development Card from a Tower that’s already\n" +
                "occupied");
    }

    @Override
    public void permanentAbility() {
        owner.sOut("Permanent ability activated");
        printAbility();
    }
}

class LorenzoDeMedici extends LeaderCard {
    public LorenzoDeMedici(){
        super("Lorenzo Dè Medici",
                Arrays.asList(35),
                Arrays.asList("victoryPoint"),
                false, false, false);
    }
    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils.checkMultiType(this.types, this.activationCost,
                owner);
        return LeaderUtils.commonApply(owner, this, checkT, false);
    }

    public void printAbility() {
        owner.sOut("Copy the ability of another " +
                "Leader Card already played by another player. " +
                "Once you decide the ability to copy, it can’t be changed");
    }
    /**
     * This is the most complex ability of all the leader cards.
     * First it displays to Player's client by {@link Player#sOut(String)}
     * the list of all activated leader cards in the game.
     * After the choice of the player (given by {@link Player#sInPrompt(int, int)}),
     * the card is cloned.
     * It first check that the card to copy is not this card itself;
     * then it set activation = true in the to copy card and onePerTurnUsage = false
     * in order to reset them.
     * Then the card is added in the LeaderCard list of the owner, setting its possession.
     * Finally, this card is removed from owner's deck.
     */
    @Override
    public void permanentAbility() {
        printAbility();
        Game currentGame = owner.getParentGame();
        int counter = 0;
        List<LeaderCard> activatedCards = new ArrayList<>();
        //print all the activated leader cards
        for (Player pl : currentGame.getPlayers()){
            for(LeaderCard played : pl.getLeaderCards()) {
                if (played.isActivated()) {
                    activatedCards.add(played);
                    counter++;
                    owner.sOut(counter + " -> " + played.getName());
                }
            }
        }
        while(true) {
            owner.sOut("Which one do you want to copy?");
            LeaderCard toCopy = activatedCards.get(
                    owner.sInPrompt(1, counter) - 1).cloneCard();
            owner.sOut(toCopy.name + " selected");
            if (toCopy.name != this.name) {
                toCopy.activation = this.activation;
                toCopy.onePerTurnUsage = false;
                owner.getLeaderCards().add(toCopy);
                toCopy.setPlayer(owner);
                // could be empty
                toCopy.permanentAbility();
                owner.getLeaderCards().remove(this);
                break;
            } else {
                owner.sOut("Please choose a valid leader card");
            }
        }
    }
}

