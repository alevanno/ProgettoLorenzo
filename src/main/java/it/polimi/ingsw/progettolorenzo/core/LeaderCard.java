package it.polimi.ingsw.progettolorenzo.core;


import it.polimi.ingsw.progettolorenzo.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class LeaderCard {
    protected String name;
    protected List<Integer> activationCost;
    protected List<String> types;
    protected boolean activation;
    protected boolean onePerRound;
    protected boolean onePerRoundUsage;

    public LeaderCard(String name, List<Integer> activationCost,
                      List<String> types,
                      boolean activation, boolean onePerRound,
                      boolean onePerRoundUsage) {
        this.name = name;
        this.activationCost = activationCost;
        this.types = types;
        this.activation = activation;
        this.onePerRound = onePerRound;
        this.onePerRoundUsage = onePerRoundUsage;
    }

    public abstract boolean apply();
    public String getName(){
        return this.name;
    }    public List<Integer> getActivationCost() {
        return activationCost;
    }
    public List<String> getCardCostType() {
        return this.types;
    }
    public boolean isActivated() {
        return this.activation;
    }
    public void permanentAbility(){};
    public void onePerRoundAbility(){};
    public boolean hasOnePerRoundAbility() {
        return this.onePerRound;
    }
    public void setOnePerRoundUsage(boolean bool){
        this.onePerRoundUsage = bool;
    }}

class FrancescoSforza extends LeaderCard {
    private Player owner;
    public FrancescoSforza(Player pl) {
        super("FrancescoSforza", Arrays.asList(5),
                Arrays.asList("ventures"), false,
                true, false );
        this.owner = pl;
    }

    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils
                .checkCardTypeSatisfaction(owner, types.get(0), activationCost.get(0));
        return LeaderUtils.commonApply(owner, this, checkT, false);
    }

    @Override
    public void onePerRoundAbility(){
        LeaderUtils.OneHarvProd("production", owner, this);
    }
}

class FilippoBrunelleschi extends LeaderCard {
    Player owner;
    public FilippoBrunelleschi(Player pl) {
        super("Filippo Brunelleschi", Arrays.asList(5),
                Arrays.asList("buildings"), false, false,
                false);
        this.owner = pl;
    }

    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils
                .checkCardTypeSatisfaction(owner , types.get(0), activationCost.get(0));
        return LeaderUtils.commonApply(owner, this, checkT, false);
    }
    @Override
    public void permanentAbility() {
        owner.sOut("Permanent ability activated: you will never have to pay" +
                "the additional cost if a tower is already occupied");
    }
}

class LucreziaBorgia extends LeaderCard {
    Player owner;
    public LucreziaBorgia(Player pl) {
        super("Lucrezia Borgia", Arrays.asList(6),
                Arrays.asList("buildings",
                        "territories",
                        "ventures",
                        "characters"), false, false,
                false);
        this.owner = pl;
    }
    @Override
    public boolean apply() {
        boolean checkT = false;
        for (String t : types){
            checkT = LeaderUtils
                    .checkCardTypeSatisfaction(owner, t, activationCost.get(0));
            if (checkT) {
                break;
            }
        }
        boolean ret = LeaderUtils.commonApply(owner, this, checkT, false);
        return ret;
    }
    @Override
    public void permanentAbility() {
        owner.sOut("Permanent ability activated: your colored family members" +
                " have a bonus of +2 on their value");
        // increasing available fam members; next turn they will be increased
        // at their birth (not blank for both situations)
        for (FamilyMember fam : owner.getAvailableFamMembers()) {
            if (!"Blank".equals(fam.getSkinColour())) {
                fam.setActionValue(fam.getActionValue() + 2);
            }
        }
    }
}

class LudovicoAriosto extends LeaderCard {
    Player owner;
    public LudovicoAriosto(Player pl) {
        super("Ludovico Ariosto", Arrays.asList(5),
                Arrays.asList("characters"), false,
                false, false);
        this.owner = pl;
    }

    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils
                .checkCardTypeSatisfaction(owner, types.get(0), activationCost.get(0));
        return LeaderUtils.commonApply(owner, this, checkT, false);
    }
    @Override
    public void permanentAbility() {
        owner.sOut("Permanent ability activated: you can place your " +
                "family Members in occupied action spaces. ");
    }
}

class FedericoDaMontefeltro extends LeaderCard {
    Player owner;
    public FedericoDaMontefeltro(Player pl) {
        super("Federico Da Montafeltro", Arrays.asList(5),
                Arrays.asList("territories"),
                false, true, false);
        this.owner = pl;
    }
    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils
                .checkCardTypeSatisfaction(owner, types.get(0), activationCost.get(0));
        return LeaderUtils.commonApply(owner, this, checkT, false);
    }
    @Override
    public void onePerRoundAbility() {
        int increasedValue = 6;
        owner.sOut("One of your colored family members has a value of 6, " +
                "regardless of its related dice");
        owner.sOut("Which one do you want to increase?");
        owner.displayFamilyMembers();
        FamilyMember famMem = owner.getAvailableFamMembers().get(
                owner.sInPrompt(1, owner.getAvailableFamMembers().size()) - 1);
        famMem.setActionValue(increasedValue);
        owner.sOut(famMem.getSkinColour() + " " +
                "family member increased by " + increasedValue);
        onePerRoundUsage = true;
    }
}

class GirolamoSavonarola extends LeaderCard {
    Player owner;
    public GirolamoSavonarola(Player pl) {
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
    @Override
    public void onePerRoundAbility() {
        owner.sOut("You gained 1 faith point.");
        owner.currentRes = owner.currentRes.merge(new Resources
                .ResBuilder().faithPoint(1).build());
        onePerRoundUsage = true;

    }
}

class SistoIV extends LeaderCard {
    Player owner;
    public SistoIV(Player pl) {
        super("SistoIV", Arrays.asList(6),
                Arrays.asList("coin", "wood", "stone", "servant"),
                false, false, false);
        this.owner = pl;
    }
    @Override
    public boolean apply() {
        boolean checkC = LeaderUtils.checkCostResSatisfaction(owner,
                new Resources.ResBuilder()
                        .coin(6).wood(6).servant(6).stone(6).build());
        return LeaderUtils.commonApply(owner, this, false, checkC);
    }

    @Override
    public void permanentAbility() {
        owner.sOut("You gain 5 additional victory points when you support the" +
                "Church in a Vatican Report phase ");
    }
}

class SigismondoMalatesta extends LeaderCard {
    Player owner;
    public SigismondoMalatesta(Player pl) {
        super("Sigismondo Malatesta", Arrays.asList(7, 3),
                Arrays.asList("militaryPoint", "faithPoint"), false,
                false, false);
        this.owner = pl;
    }
    @Override
    public boolean apply() {
        boolean checkC = LeaderUtils.checkCostResSatisfaction(owner,
                new Resources.ResBuilder()
                        .militaryPoint(7).faithPoint(3).build());
        return LeaderUtils.commonApply(owner, this, false, checkC);
    }
    @Override
    public void permanentAbility() {
        owner.sOut("Your Blank family member has a bonus of +3 on its value");
        for (FamilyMember fam : owner.getAvailableFamMembers()) {
            if ("Blank".equals(fam.getSkinColour())) {
                fam.setActionValue(fam.getActionValue() + 3);
            }
        }
    }
}

class CesareBorgia extends LeaderCard {
    Player owner;
    public CesareBorgia(Player pl) {
        super("Cesare Borgia", Arrays.asList(3, 12, 2),
                Arrays.asList("buildings", "coin", "faithPoint"), false,
                false, false);
        this.owner = pl;
    }
    @Override
    public boolean apply() {
        boolean checkC = LeaderUtils
                .checkCostResSatisfaction(owner, new Resources
                        .ResBuilder().coin(12).faithPoint(2).build());
        boolean checkT = LeaderUtils.checkCardTypeSatisfaction(owner, this.types.get(0),
                this.activationCost.get(0));
        return LeaderUtils.commonApply(owner, this, checkT, checkC);
    }
    @Override
    public void permanentAbility() {
        owner.sOut("You don’t need to satisfy the military points requirement when " +
                "you take territory cards ");
    }
}

class MichelangeloBuonarroti extends LeaderCard {
    Player owner;
    public MichelangeloBuonarroti(Player pl) {
        super("Michelangelo Buonarroti", Arrays.asList(10),
                Arrays.asList("stone"), false, true, false);
        this.owner = pl;
    }
    @Override
    public boolean apply() {
        boolean checkC = LeaderUtils.checkCostResSatisfaction(owner,
                new Resources.ResBuilder().stone(10).build());
        return LeaderUtils.commonApply(owner, this, false, checkC);
    }

    @Override
    public void onePerRoundAbility() {
        owner.sOut("You gained 3 coin.");
        owner.currentRes = owner.currentRes.merge(new Resources
                .ResBuilder().coin(3).build());
        onePerRoundUsage = true;

    }
}

class SantaRita extends LeaderCard {
    Player owner;
    public SantaRita(Player pl) {
        super("Santa Rita", Arrays.asList(8),
                Arrays.asList("faithPoint"),
                false, false, false);
        this.owner = pl;
    }
    @Override
    public boolean apply() {
        boolean checkC = LeaderUtils.checkCostResSatisfaction(owner,
                new Resources.ResBuilder().faithPoint(8).build());
        return LeaderUtils.commonApply(owner, this, false, checkC);
    }
    @Override
    public void permanentAbility() {
        owner.sOut("You don’t need to satisfy the military points requirement when " +
                "you take territory cards ");
    }
}

class GiovanniDalleBandeNere extends LeaderCard {
    Player owner;
    public GiovanniDalleBandeNere(Player pl) {
        super("Giovanni Dalle Bande Nere",
                Arrays.asList(12),
                Arrays.asList("militaryPoint"), false,
                true, false);
        this.owner = pl;
    }
    @Override
    public boolean apply() {
        boolean checkC = LeaderUtils.checkCostResSatisfaction(owner,
                new Resources.ResBuilder().militaryPoint(12).build());
        return LeaderUtils.commonApply(owner, this, false, checkC);
    }

    @Override
    public void onePerRoundAbility() {
        owner.sOut("You gained 12 militaryPoint.");
        owner.currentRes = owner.currentRes.merge(new Resources
                .ResBuilder().wood(1).stone(1).coin(1).build());
        onePerRoundUsage = true;

    }
}

class CosimoDeMedici extends LeaderCard {
    Player owner;
    public CosimoDeMedici(Player pl) {
        super("Cosimo Dè Medici", Arrays.asList(2, 4),
                Arrays.asList("characters", "buildings"),
                false, true, false);
        this.owner = pl;
    }
    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils.checkMultiType(this.types,
                this.activationCost,owner);
        return LeaderUtils.commonApply(owner, this, checkT, false);
    }

    @Override
    public void onePerRoundAbility() {
        owner.sOut("You received 3 servant and gained 1 victoryPoint");
        owner.currentRes = owner.currentRes.merge(new Resources
                .ResBuilder().servant(3).victoryPoint(1).build());
        onePerRoundUsage = true;
    }
}

class LeonardoDaVinci extends LeaderCard {
    Player owner;
    public LeonardoDaVinci(Player pl) {
        super("Leonardo Da Vinci",
                Arrays.asList(4,2),
                Arrays.asList("characters", "territories"),
                false, true, false);
        this.owner = pl;
    }
    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils.checkMultiType(this.types,this.activationCost, owner);
        return LeaderUtils.commonApply(owner, this, checkT, false);
    }
    @Override
    public void onePerRoundAbility() {
        LeaderUtils.OneHarvProd("harvest", owner, this);
    }
}

class BartolomeoColleoni extends LeaderCard {
    Player owner;
    public BartolomeoColleoni(Player pl){
        super("Bartolomeo Colleoni",
                Arrays.asList(2,4),
                Arrays.asList("ventures", "territories"),
                false, true, false);
        this.owner = pl;
    }
    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils.checkMultiType(this.types, this.activationCost,
                owner);
        return LeaderUtils.commonApply(owner, this, checkT, false);
    }
    @Override
    public void onePerRoundAbility() {
        owner.sOut("You gained 4 victoryPoint");
        owner.currentRes = owner.currentRes.merge(new Resources
                .ResBuilder().victoryPoint(4).build());
        onePerRoundUsage = true;
    }
}

class SandroBotticelli extends LeaderCard {
    Player owner;
    public SandroBotticelli(Player pl){
        super("Sandro Botticelli",
                Arrays.asList(10),
                Arrays.asList("wood"),
                false, true, false);
        this.owner = pl;
    }
    @Override
    public boolean apply() {
        boolean checkC = LeaderUtils.checkCostResSatisfaction(owner,
                new Resources.ResBuilder().wood(10).build());
        return LeaderUtils.commonApply(owner, this, false, checkC);
    }
    @Override
    public void onePerRoundAbility() {
        owner.sOut("You gained 2 militaryPoint and 1 victoryPoint");
        owner.currentRes = owner.currentRes.merge(new Resources
                .ResBuilder().militaryPoint(2).victoryPoint(1).build());
        onePerRoundUsage = true;
    }
}

class LudovicoIIIGonzaga extends LeaderCard {
    Player owner;
    public LudovicoIIIGonzaga(Player pl){
        super("Ludovico III Gongaza",
                Arrays.asList(15),
                Arrays.asList("servant"),
                false, true, false);
        this.owner = pl;
    }
    @Override
    public boolean apply() {
        boolean checkC = LeaderUtils.checkCostResSatisfaction(owner,
                new Resources.ResBuilder().servant(15).build());
        return LeaderUtils.commonApply(owner, this, false, checkC);
    }
    @Override
    public void onePerRoundAbility() {
        owner.sOut("You gained 1 councilPrivilege");
        Resources privRes = owner.getParentGame()
                .getBoard().councilPalace.choosePrivilege(owner);
        owner.currentRes = owner.currentRes.merge(privRes);
        onePerRoundUsage = true;
    }
}

class LudovicoIlMoro extends LeaderCard {
    Player owner;
    public LudovicoIlMoro(Player pl){
        super("Ludovico Il Moro",
                Arrays.asList(2,2,2,2),
                Arrays.asList("territories", "characters", "buildings",
                        "ventures"),
                false, false, false);
        this.owner = pl;
    }
    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils.checkMultiType(this.types, this.activationCost,
                owner);
        return LeaderUtils.commonApply(owner, this, checkT, false);
    }
    @Override
    public void permanentAbility() {
        owner.sOut("Your colored Family Members has a value of 5, regardless of\n" +
                "their related dice");
        for (FamilyMember fam : owner.getAvailableFamMembers()) {
            if (!"Blank".equals(fam.getSkinColour())) {
                fam.setActionValue(5);
            }
        }
    }
}

class PicoDellaMirandola extends LeaderCard {
    Player owner;
    public PicoDellaMirandola(Player pl){
        super("Pico Della Mirandola",
                Arrays.asList(4,2),
                Arrays.asList("ventures", "buildings"),
                false, false, false);
        this.owner = pl;
    }
    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils.checkMultiType(this.types, this.activationCost,
                owner);
        return LeaderUtils.commonApply(owner, this, checkT, false);
    }
    @Override
    public void permanentAbility() {
        owner.sOut("When you take Development Cards, you get a discount of" +
                "3 coins (if the card you are taking has coins in its cost.) This is not a discount on the\n" +
                "coins you must spend if you take a Development Card from a Tower that’s already\n" +
                "occupied");
    }
}

class LorenzoDeMedici extends LeaderCard {
    Player owner;
    public LorenzoDeMedici(Player pl){
        super("Lorenzo Dè Medici",
                Arrays.asList(35),
                Arrays.asList("victoryPoint"),
                false, false, false);
        this.owner = pl;
    }
    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils.checkMultiType(this.types, this.activationCost,
                owner);
        return LeaderUtils.commonApply(owner, this, checkT, false);
    }
    @Override
    public void permanentAbility() {
        // needs testing
        owner.sOut("Copy the ability of another " +
                "Leader Card already played by another player. " +
                "Once you decide the ability to copy, it can’t be changed");
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
        owner.sOut("Which one do you want to copy?");
        LeaderCard toCopy = activatedCards.get(
                owner.sInPrompt(1, counter));
        toCopy.activation = this.activation;
        toCopy.onePerRoundUsage = false;
        owner.getLeaderCards().add(toCopy);
        // could be empty
        this.permanentAbility();
        owner.getLeaderCards().remove(this);
    }
}

