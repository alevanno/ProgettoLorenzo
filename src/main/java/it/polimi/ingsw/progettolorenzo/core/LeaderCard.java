package it.polimi.ingsw.progettolorenzo.core;



public abstract class LeaderCard {
    public abstract void apply();
    public abstract String getName();
    public abstract boolean isActivated();
}

class FrancescoSforza extends LeaderCard {
    private String name = "Francesco Sforza";
    private int activationCost = 5;
    private Player owner;
    private boolean activation = false;


    public FrancescoSforza(Player pl) {
        this.owner = pl;
    }

    @Override
    public void apply() {
        int counter = 0;
        for (Card card : owner.listCards()) {
            if ("ventures".equals(card.cardType)) {
                counter++;
            }
        }
        if (counter >= activationCost) {
            activation = true;
        } else {
            owner.sOut("you still don't satisfy the activationCost");
        }
    }

    public void onePerRoundAbility(){
        if(this.isActivated()) {
            // TODO first finish prodAction -> it calls a production of value 1
            // we should call prod(pl,value) without familyMember
        }
    }

    @Override
    public boolean isActivated() {
        return this.activation;
    }

    @Override
    public String getName(){
        return this.name;
    }
}

class FilippoBrunelleschi extends LeaderCard {
    private String name = "Flippo Brunelleschi";
    private int activationCost = 5;
    private Player owner;
    private boolean activation = false;

    public FilippoBrunelleschi(Player pl) {
        this.owner = pl;
    }

    @Override
    public void apply() {
        int counter = 0;
        for (Card card : owner.listCards()) {
            if ("buildings".equals(card.cardType)) {
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
            }
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
}
