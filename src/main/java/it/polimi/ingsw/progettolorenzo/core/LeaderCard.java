package it.polimi.ingsw.progettolorenzo.core;



public abstract class LeaderCard {
    public abstract boolean apply();
    public abstract String getName();
    public abstract int getActivationCost();
    public abstract String getCardCostType();
    public abstract boolean isActivated();
    public abstract boolean hasOnePerTurnAbility();
}

class FrancescoSforza extends LeaderCard {
    private String name = "Francesco Sforza";
    private int activationCost = 5;
    private String type = "ventures";
    private Player owner;
    private boolean activation = false;
    private boolean onePerRound = true;


    public FrancescoSforza(Player pl) {
        this.owner = pl;
    }

    @Override
    public boolean apply() {
        int counter = 0;
        for (Card card : owner.listCards()) {
            if (type.equals(card.cardType)) {
                counter++;
            }
        }
        if (this.hasOnePerTurnAbility() && this.isActivated()) {
            owner.sOut("Would you like to activate the one per round ability? ");
            boolean ret = owner.sInPromptConf();
            if (ret) {
                this.onePerRoundAbility();
                return true;
            } else {
                owner.sOut("You didn't activate the one per round ability");
                return false;
            }
        }
        if (counter >= activationCost) {
            activation = true;
            owner.sOut("Leader card activated");
            return true;
        } else {
            owner.sOut("you still don't satisfy the activationCost");
            return false;
        }
    }


    @Override
    public int getActivationCost() {
        return activationCost;
    }

    public void onePerRoundAbility(){
        owner.sOut("It allows to call a production of value 1");
        owner.getParentGame().getBoard().productionArea.prod(owner, 1);
        owner.sOut("One per round ability accomplished");
    }

    @Override
    public boolean isActivated() {
        return this.activation;
    }

    @Override
    public String getName(){
        return this.name;
    }

    @Override
    public String getCardCostType() {
        return this.type;
    }

    @Override
    public boolean hasOnePerTurnAbility() {
        return this.onePerRound;
    }
}

class FilippoBrunelleschi extends LeaderCard {
    private String name = "Flippo Brunelleschi";
    private int activationCost = 5;
    private String type = "buildings";
    private Player owner;
    private boolean activation = false;
    private boolean onePerRound = false;


    public FilippoBrunelleschi(Player pl) {
        this.owner = pl;
    }

    @Override
    public boolean apply() {
        int counter = 0;
        for (Card card : owner.listCards()) {
            if (type.equals(card.cardType)) {
                counter++;
            }
        }
        if (counter >= activationCost) {
            owner.sOut("You have enough buildings Cards to activate the " +
                    "permanent ability of " + name + "leader card");
            owner.sOut("Would you activate it?");
            boolean ret = owner.sInPromptConf();
            if(ret){
                this.permanentAbility();
                return true;
            } else {
                owner.sOut("You didn't activate the Leader card permanent ability");
                return false;
            }
        } else {
            owner.sOut("You still don't satisfy the activationCost");
            return false;
        }
    }

    public void permanentAbility() {
        this.activation = true;
        owner.sOut("Permanent ability activated: you will never have to pay" +
                "the additional cost if a tower is already occupied");
    }

    public boolean isActivated() {
        return activation;
    }

    @Override
    public String getName(){
        return this.name;
    }

    @Override
    public int getActivationCost() {
        return activationCost;
    }

    @Override
    public String getCardCostType() {
        return this.type;
    }

    @Override
    public boolean hasOnePerTurnAbility() {
        return this.onePerRound;
    }
}
