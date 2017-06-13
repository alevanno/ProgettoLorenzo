package it.polimi.ingsw.progettolorenzo.core;

import com.google.gson.JsonObject;
import it.polimi.ingsw.progettolorenzo.Game;
import it.polimi.ingsw.progettolorenzo.client.RmiClient;

import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;


public class Player {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    public final String playerName;
    private final String playerColour;
    private final PlayerIO io;
    // FIXME make private
    public Resources currentRes = new Resources.ResBuilder().servant(3).stone(2).wood(2).build();
    private List<FamilyMember> famMemberList = new ArrayList<>();
    private Deck cards = new Deck();
    private List<JsonObject> excommunications = new ArrayList<>(Arrays.asList(new JsonObject(), new JsonObject(), new JsonObject()));
    private List<LeaderCard> leaderCards = new ArrayList<>();
    private BonusTile bonusT;
    private Game parentGame;


    public Player(String name, String colour, Socket socket) {
        this.playerName = name;
        this.playerColour = colour;
        this.io = new PlayerIOSocket(socket);
        log.info(String.format(
                "New player: %s (colour: %s, resources: %s) [socket]",
                name, colour, this.currentRes));
    }

    public Player(String name, String colour, RmiClient rmi) {
        this.playerName = name;
        this.playerColour = colour;
        this.io = new PlayerIORMI(rmi);
        log.info(String.format(
            "New player: %s (colour: %s, resources: %s) [RMI]",
            name, colour, this.currentRes));
    }

    public String sIn()  {
        return this.io.sIn();
    }

    public int sInPrompt(int minValue, int maxValue) {
        return this.io.sInPrompt(minValue, maxValue);
    }

    public boolean sInPromptConf() {
        return this.io.sInPromptConf();
    }

    public void sOut(String s) {
        this.io.sOut(s);
    }

    public void famMembersBirth(Map<String, Integer> famValues) {
        List <String> colorList = Arrays.asList("Orange", "Black", "White");
        int blankValue = 0;
        for (String s: colorList) {
            int val = famValues.get(s);
            for(LeaderCard leader : leaderCards) {
                if("Lucrezia Borgia".equals(leader.getName()) && leader.isActivated()){
                    val += 2;
                    // exclusive?
                } else if ("Ludovico Il Moro"
                        .equals(leader.getName()) && leader.isActivated()) {
                    val = 5;
                }
                if ("Sigismondo Malatesta".equals(leader.getName()) && leader.isActivated()){
                    blankValue += 3;
                }
            }
            if (excommunications.get(0).has("harvMalus")) {
                val -= 1;
            }
            this.famMemberList.add(new FamilyMember(this, val, s));
        }
        this.famMemberList.add(
                new FamilyMember(this, blankValue, "Blank"));
        log.fine("4 family members attached to " + this);
    }

    public BonusTile getBonusT() {
        return bonusT;
    }

    public Game getParentGame() {
        return parentGame;
    }

    public List<JsonObject> getExcommunications() {
        return excommunications;
    }

    public void setBonusTile(BonusTile bt) {
        log.info(String.format("[%s] Set bonus tile %s",
                this, bt));
        this.bonusT = bt;
    }

    public void setExcommunication(JsonObject e, int index) {
        this.excommunications.add(index, e);
    }

    public void setParentGame(Game parentGame) {
        this.parentGame = parentGame;
    }

    public void addCard(Card toadd) {
        this.cards.add(toadd);
    }

    public Deck listCards() {
        return this.cards.listCards();
    }

    protected Card takeCard(int idx) {
        return this.cards.remove(idx);
    }

    public List<FamilyMember> getAvailableFamMembers() {
        return this.famMemberList;
    }

    public List<LeaderCard> getLeaderCards() {
        return leaderCards;
    }

    public String displayFamilyMembers() {
        int i = 1;
        StringBuilder ret = new StringBuilder();
        for (FamilyMember fam : this.famMemberList) {
            ret.append(i + "." + " " + fam.getSkinColour() + " -> " + fam.getActionValue());
            ret.append(" | ");
            i++;
        }
        return ret.toString();
    }

    protected void takeFamilyMember(FamilyMember famMember) {
        if (!this.famMemberList.remove(famMember)) {
            System.exit(1);
        }
    }

    //TODO this should be moved to action, so that ResourceAction can be used
    //call this if you want to increase an action NOT done through a FamMem
    public int increaseValue() { //remember to use revertIncreaseValue if the action is not accepted
        int increase;
        int servantSpent;
        int currServant = currentRes.servant;
        this.sOut("Current servants: " + currServant);
        this.sOut("How many do you want to use? ");
        if (excommunications.get(1).has("servantExpense")) {
            int servantExp = excommunications.get(1).get("servantExpense").getAsInt();
            sOut("(Due to your excommunication you have to use " + servantExp + " servants to increase the action value)");
            do {
                sOut("It has to be a multiple of " + servantExp);
                servantSpent = this.sInPrompt(0, currServant);
            } while (servantSpent % servantExp != 0);
            increase = servantSpent / servantExp;
        } else {
            increase = this.sInPrompt(0, currServant);
            servantSpent = increase;
        }
        this.sOut("Confirm?: y/n");
        if (this.sInPromptConf()) {
            this.currentRes = this.currentRes.merge(new
                    Resources.ResBuilder().servant(servantSpent)
                    .build().inverse());
            return increase;
        } else {
            return 0;
        }
    }

    //call this if you want to increase an action done through a FamMem
    public int increaseFamValue(FamilyMember famMember) { //remember to use revertFamValue if the action is not accepted
        this.sOut("Do you want to increase your "
                + famMember.getSkinColour() + " family member value?" );
        int increase = 0;
        if (this.sInPromptConf()) {
            increase = this.increaseValue();
            famMember.setActionValue(famMember.getActionValue() + increase);
            this.sOut("Current " + famMember.getSkinColour()
                    + " family member value: " + (famMember.getActionValue()));
        }
        return increase;
    }

    public void revertFamValue(FamilyMember famMem, int increase) {
        //reverts the value increase by servants
        famMem.setActionValue(famMem
                .getActionValue() - increase);
        this.revertIncreaseValue(increase);
    }

    public void revertIncreaseValue(int increase) { //gives the servants (spent to increase an action value) back to the player
        int servantSpent;
        if (excommunications.get(1).has("servantExpense")) {
            int servantExp = excommunications.get(1).get("servantExpense").getAsInt();
            servantSpent = increase * servantExp;
        } else { servantSpent = increase; }
        this.currentRes = this.currentRes.merge(new
                Resources.ResBuilder().servant(servantSpent)
                .build());
    }

    // TODO this method affects only the activation of a leader card;
    // we should create an other method to use the One per Round ability
    public boolean activateLeaderCard() {
        this.sOut("Which Leader card do you want to activate?");
        if (leaderCards.isEmpty()) {
            this.sOut("You don't have any Leader Card anymore");
            return false;
        }
        for (LeaderCard card : leaderCards) {
            for (int i : card.activationCost) {
                StringBuilder toDisplay = new StringBuilder(String.format("%s %d %s", i + 1 + " -> " + card.getName(),
                        card.getActivationCost().get(i), " : " + card.getCardCostType()));
                if (card.isActivated()) {
                    toDisplay.append(" activated");
                }
                this.sOut(toDisplay.toString());
            }
        }
        int choice = this.sInPrompt(1, leaderCards.size());
        return leaderCards.get(choice - 1).apply();

    }

    public void discardLeaderCard(LeaderCard leader) {
        this.sOut("You will discard " + leader.getName() + " leader card" +
                "and you will immediately receive 1 council privilege");
        this.sOut("Confirm?");
        int counter = 0;
        if(this.sInPromptConf()) {
            for (LeaderCard card : leaderCards) {
                if(leader.getName().equals(card.getName())) {
                    leaderCards.remove(counter);
                    Resources privRes = this.getParentGame()
                            .getBoard().councilPalace.choosePrivilege(this);
                    this.currentRes = currentRes.merge(privRes);
                    break;
                }
                counter++;
            }
        }
    }

    private void endgameLostVictoryRes(Resources loseVictoryRes) {
        currentRes.resourcesList.forEach((x, y) -> { //iterates over currentRes<key,value>
            if (loseVictoryRes.getByString(x) != 0) {
                int a = loseVictoryRes.getByString(x);
                int b = currentRes.getByString(x);
                currentRes = currentRes.merge(
                        new Resources.ResBuilder().victoryPoint(b / a).build().inverse());
            }
        });
    }

    private void endgameLostVictoryCost(Resources loseVictoryCost) {
        AtomicInteger lostVictoryPts = new AtomicInteger(0);
        for (Card c : listCards()) {
            if ("buildings".equals(c.cardType)) {
                Resources cardCost = c.getCardCost();
                loseVictoryCost.resourcesList.forEach((x, y) -> {
                    if (y != 0) {
                        lostVictoryPts.addAndGet(cardCost.getByString(x));
                    }
                });
            }
        }
        currentRes = currentRes.merge(
                new Resources.ResBuilder().victoryPoint(lostVictoryPts.get()).build().inverse());
    }

    public void endgame() {
        List<Integer> territoriesVictory = Arrays.asList(0, 0, 0, 1, 4, 10, 20);
        List<Integer> charactersVictory = Arrays.asList(0, 1, 3, 6, 10, 15, 21);
        int countTerritories = 0;
        int countCharacters = 0;
        Resources purpleFinal = new Resources.ResBuilder().build();
        JsonObject excomBase = excommunications.get(2);
        int sumResources = (currentRes.coin + currentRes.servant + currentRes.stone + currentRes.wood);
        for (Card i : listCards()) {
            String noVictoryType = "";
            if (excomBase.has("noVictoryType")) {
                noVictoryType = excomBase.get("noVictoryType").getAsString();
            }
            if (i.cardType.equals("territories") && !noVictoryType.equals("territories")) {
                countTerritories++;
            }
            if (i.cardType.equals("characters") && !noVictoryType.equals("characters")) {
                countCharacters++;
            }
            if (i.cardType.equals("ventures") && !noVictoryType.equals("ventures")) {
                purpleFinal = purpleFinal.merge(Resources.fromJson(i.permanentEff.get("purpleFinal")));
            }
        }
        currentRes = currentRes.merge(purpleFinal);
        currentRes = currentRes.merge(new Resources.ResBuilder().victoryPoint(territoriesVictory.get(countTerritories)).build());
        currentRes = currentRes.merge(new Resources.ResBuilder().victoryPoint(charactersVictory.get(countCharacters)).build());
        currentRes = currentRes.merge(new Resources.ResBuilder().victoryPoint(sumResources / 5).build());

        if (excomBase.has("lostVictoryRes")) {
            Resources loseVictoryRes = Resources.fromJson(excomBase.get("lostVictoryRes").getAsJsonObject().get("resources"));
            endgameLostVictoryRes(loseVictoryRes);
        }

        if (excomBase.has("lostVictoryCost")) {
            Resources loseVictoryCost = Resources.fromJson(excomBase.get("lostVictoryCost").getAsJsonObject().get("resources"));
            endgameLostVictoryCost(loseVictoryCost);
        }

        //TEST
        System.out.println("excomm " + currentRes);
    }

    public String toString() {
        return this.playerName;
    }
}