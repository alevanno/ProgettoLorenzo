package it.polimi.ingsw.progettolorenzo.core;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class LeaderCard {
    protected String name;
    protected List<Integer> activationCost;
    protected String type;
    protected boolean activation;
    protected boolean onePerRound;
    protected boolean onePerRoundUsage;

    public LeaderCard(String name, List<Integer> activationCost,
                      String type,
                      boolean activation, boolean onePerRound,
                      boolean onePerRoundUsage) {
        this.name = name;
        this.activationCost = activationCost;
        this.type = type;
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
    public String getCardCostType() {
        return this.type;
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
                "ventures", false,
                true, false );
        this.owner = pl;
    }

    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils
                .checkCardTypeSatisfaction(owner, type, activationCost.get(0));
        return LeaderUtils.commonApply(owner, this, checkT, false);
    }

    @Override
    public void onePerRoundAbility(){
        int value = 0;
        boolean ok = false;
        owner.sOut("It allows to call a production of value 1");
        owner.sOut("Do you want to increase your it?: ");
        if (owner.sInPromptConf()) {
            while (!ok) {
                value = owner.increaseValue();
                owner.sOut("Current harvest value: " + value);
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
        owner.getParentGame().getBoard().harvestArea.harv(owner, value);
        owner.sOut("One per round ability accomplished");
        onePerRoundUsage = true;
    }
}

class FilippoBrunelleschi extends LeaderCard {
    Player owner;
    public FilippoBrunelleschi(Player pl) {
        super("Filippo Brunelleschi", Arrays.asList(5),
                "buildings", false, false,
                false);
        this.owner = pl;
    }

    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils
                .checkCardTypeSatisfaction(owner , type, activationCost.get(0));
        return LeaderUtils.commonApply(owner, this, checkT, false);
    }
    @Override
    public void permanentAbility() {
        this.activation = true;
        owner.sOut("Permanent ability activated: you will never have to pay" +
                "the additional cost if a tower is already occupied");
    }
}

class LucreziaBorgia extends LeaderCard {
    Player owner;
    public LucreziaBorgia(Player pl) {
        super("Lucrezia Borgia", Arrays.asList(6),
                "same type", false, false,
                false);
        this.owner = pl;
    }
    @Override
    public boolean apply() {
        List<String> types = new ArrayList<>(Arrays
                .asList("buildings", "territories", "ventures",
                        "characters"));
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
        this.activation = true;
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
                "characters", false,
                false, false);
        this.owner = pl;
    }

    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils
                .checkCardTypeSatisfaction(owner, type, activationCost.get(0));
        return LeaderUtils.commonApply(owner, this, checkT, false);
    }
    @Override
    public void permanentAbility() {
        this.activation = true;
        owner.sOut("Permanent ability activated: you can place your " +
                "family Members in occupied action spaces. ");
    }
}

class FedericoDaMontefeltro extends LeaderCard {
    Player owner;
    public FedericoDaMontefeltro(Player pl) {
        super("Federico Da Montafeltro", Arrays.asList(5), "territories",
                false, true, false);
        this.owner = pl;
    }
    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils
                .checkCardTypeSatisfaction(owner, type, activationCost.get(0));
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
        super("Girolamo Savonarola", Arrays.asList(18), "coin",
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
    }
}

class SistoIV extends LeaderCard {
    Player owner;
    public SistoIV(Player pl) {
        super("SistoIV", Arrays.asList(6),
                "coin, wood, stone, servant",
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
        this.activation = true;
        owner.sOut("You gain 5 additional victory points when you support the" +
                "Church in a Vatican Report phase ");
    }
}

class SigismondoMalatesta extends LeaderCard {
    Player owner;
    public SigismondoMalatesta(Player pl) {
        super("Sigismondo Malatesta", Arrays.asList(7, 3),
                "militaryPoint, faithPoint", false,
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
        this.activation = true;
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
                "buildings, coin, faithPoint", false,
                false, false);
        this.owner = pl;
    }
    @Override
    public boolean apply() {
        boolean checkC = LeaderUtils
                .checkCostResSatisfaction(owner, new Resources
                        .ResBuilder().coin(12).faithPoint(2).build());
        boolean checkT = LeaderUtils.checkCardTypeSatisfaction(owner, this.type,
                this.activationCost.get(0));
        return LeaderUtils.commonApply(owner, this, checkT, checkC);
    }
    @Override
    public void permanentAbility() {
        this.activation = true;
        owner.sOut("You don’t need to satisfy the military points requirement when " +
                "you take territory cards ");
    }
}

class MichelangeloBuonarroti extends LeaderCard {
    Player owner;
    public MichelangeloBuonarroti(Player pl) {
        super("Michelangelo Buonarroti", Arrays.asList(10),
                "stone", false, true, false);
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
        owner.sOut("You gained 10 stone.");
        owner.currentRes = owner.currentRes.merge(new Resources
                .ResBuilder().stone(10).build());
    }
}

class SantaRita extends LeaderCard {
    Player owner;
    public SantaRita(Player pl) {
        super("Santa Rita", Arrays.asList(8), "faithPoint",
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
        this.activation = true;
        owner.sOut("You don’t need to satisfy the military points requirement when " +
                "you take territory cards ");
    }
}
