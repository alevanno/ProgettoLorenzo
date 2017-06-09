package it.polimi.ingsw.progettolorenzo.core;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class LeaderCard {
    protected String name;
    protected int activationCost;
    protected String type;
    protected boolean activation;
    protected boolean onePerRound;
    protected boolean onePerRoundUsage;

    public LeaderCard(String name, int activationCost,
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
    }    public int getActivationCost() {
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
        super("FrancescoSforza", 5,
                "ventures", false,
                true, false );
        this.owner = pl;
    }

    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils
                .checkCardTypeSatisfaction(owner, type, activationCost);
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
        super("Filippo Brunelleschi", 5,
                "buildings", false, false,
                false);
        this.owner = pl;
    }

    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils
                .checkCardTypeSatisfaction(owner , type, activationCost);
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
        super("Lucrezia Borgia", 6,
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
                    .checkCardTypeSatisfaction(owner, t, activationCost);
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
        super("Ludovico Ariosto", 5,
                "characters", false,
                false, false);
        this.owner = pl;
    }

    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils
                .checkCardTypeSatisfaction(owner, type, activationCost);
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
        super("Federico Da Montafeltro", 5, "territories",
                false, true, false);
        this.owner = pl;
    }
    @Override
    public boolean apply() {
        boolean checkT = LeaderUtils
                .checkCardTypeSatisfaction(owner, type, activationCost);
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
        super("Girolamo Savonarola", 18, "coin",
                false, true, false);
    }
    @Override
    public boolean apply() {

        boolean checkC = LeaderUtils.checkCostResSatisfaction(owner,
                new Resources.ResBuilder().coin(activationCost).build());
        return LeaderUtils.commonApply(owner, this, false, checkC);

    }
    @Override
    public void onePerRoundAbility() {
        owner.sOut("You gained 1 faith point.");
        owner.currentRes = owner.currentRes.merge(new Resources
                .ResBuilder().faithPoint(1).build());
    }
}
