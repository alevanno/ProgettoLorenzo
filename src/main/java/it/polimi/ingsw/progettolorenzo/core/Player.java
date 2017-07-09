package it.polimi.ingsw.progettolorenzo.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.progettolorenzo.Game;
import it.polimi.ingsw.progettolorenzo.client.RmiClient;
import it.polimi.ingsw.progettolorenzo.core.player.PlayerIO;
import it.polimi.ingsw.progettolorenzo.core.player.PlayerIOLocal;
import it.polimi.ingsw.progettolorenzo.core.player.PlayerIORMI;
import it.polimi.ingsw.progettolorenzo.core.player.PlayerIOSocket;

import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * The Player class contains all the information of a real player.
 * Name and Colour identify the player into the game.
 * The io field represent the type of connection (rmi, socket, or local) and the different
 * implementation of the IO to be used for that player.
 * This means that Player class is the direct connection between {@link it.polimi.ingsw.progettolorenzo.client.Client}
 * and model by using a specific type of IO.
 * It is initialized with some basic Resources;
 * it handles by itself the family members birth and the assigment
 * of the bonusTile.
 * The excommunication list is a field (empty by default) which will
 * contain the eventual excommunications.
 * The leader cards list is filled by {@link Game#assignLeaderCards()}
 * only if the game has started with advanced rules.
 * There's a ref to the parent Game.
 * The field lastFamMemIncrease indicates the last family member increased by {@link #increaseFamValue(FamilyMember)}.
 *
 * @see it.polimi.ingsw.progettolorenzo.client.Client
 * @see PlayerIOSocket
 * @see PlayerIORMI
 * @see PlayerIOLocal
 * @see Game
 */
public class Player {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    public final String playerName;
    public final String playerColour;
    private final PlayerIO io;
    private Resources currentRes = new Resources.ResBuilder().servant(3).stone(2).wood(2).build();
    private List<FamilyMember> famMemberList = new ArrayList<>();
    private Deck cards = new Deck();
    private List<JsonObject> excommunications = new ArrayList<>(Arrays.asList(new JsonObject(), new JsonObject(), new JsonObject()));
    private List<LeaderCard> leaderCards = new ArrayList<>();
    private BonusTile bonusT;
    private Game parentGame;
    private int lastFamMemIncrease;

    /**
     * The constructor to instantiate a new socket Player. It maps the IO
     with a new {@link PlayerIOSocket}.
     * @param name the player's name
     * @param colour the player's color
     * @param socket the socket to map with the player.
     */
    public Player(String name, String colour, Socket socket) {
        this.playerName = name;
        this.playerColour = colour;
        this.io = new PlayerIOSocket(socket);
        log.info(String.format(
                "New player: %s (colour: %s, resources: %s) [socket]",
                name, colour, this.getCurrentRes()));
    }

    /**
     * The constructor to instantiate a new socket Player. It maps the IO
     * with a new {@link PlayerIORMI}.
     * @param name the player's name
     * @param colour the player's color
     * @param rmi the RmiClient to map with the player.
     */
    public Player(String name, String colour, RmiClient rmi) {
        this.playerName = name;
        this.playerColour = colour;
        this.io = new PlayerIORMI(rmi);
        log.info(String.format(
            "New player: %s (colour: %s, resources: %s) [RMI]",
            name, colour, this.getCurrentRes()));
    }

    /**
     * The constructor to instantiate a new local Player. It maps the IO
     * with a new {@link PlayerIOLocal}.
     * @param name the player's name
     * @param colour the player's color
     */
    public Player(String name, String colour) {
        this.playerName = name;
        this.playerColour = colour;
        this.io = new PlayerIOLocal();
        log.info(String.format(
                "New player: %s (colour: %s, resources: %s) [local]",
                name, colour, this.getCurrentRes()));
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

    /**
     * It handles by itself the creation of new instances of {@link FamilyMember} class.
     * The colored family member are instantiated with the values of the param map.
     * The blank one has a default value of 0.
     * Values to assign can change by owning Lucrezia Borgia || Ludovico il Moro leader card ||
     * Sigismondo Malatesta;
     * excommunications have effect too.
     *
     * @see FamilyMember
     * @see LeaderCard
     * @param famValues the map containing the colored family member values to assign
     */
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
            }
            if (excommunications.get(0).has("actionValMalus")) {
                val -= 1;
            }
            this.famMemberList.add(new FamilyMember(this, val, s));
        }
        for(LeaderCard leader : leaderCards) {
            if ("Sigismondo Malatesta".equals(leader.getName()) && leader.isActivated()) {
                blankValue += 3;
            }
        }
        this.famMemberList.add(
                new FamilyMember(this, blankValue, "Blank"));
        log.fine("4 family members attached to " + this);
    }

    /**
     * @see #famMembersBirth(Map)
     * @param bt the bonus tile to be set
     */
    public void setBonusTile(BonusTile bt) {
        log.info(String.format("[%s] Set bonus tile %s",
                this, bt));
        this.bonusT = bt;
    }

    /**
     * It builds a string with a specific format containing the family members information
     * @return
     */
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
        // this won't fail as we don't want it to fail.  Right?
        this.famMemberList.remove(famMember);
    }

    /**
     * It allows to increase an action NOT done through a family Member.
     * A player can spent servant in order to increase the value of an action.
     * Utilized together with {@link #revertFamValue(FamilyMember, int)} if the action is not accepted.
     * @return the action increase value.
     */
    public int increaseValue() {
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
            this.currentResMerge(new
                    Resources.ResBuilder().servant(servantSpent)
                    .build().inverse());
            return increase;
        } else {
            return 0;
        }
    }

    /**
     * It allows to increase an action done through a family member.
     * It modifies the family member's value.
     * @see #increaseValue()
     * @param famMember the family member which value has to be increased.
     * @return the action value increase
     */
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

    /**
     * It allows to revert the family member value increase by servants and calls {@link
     * #revertIncreaseValue(int)} to give the servants back to the player.
     * @see #revertIncreaseValue(int)
     * @param famMem the family member to revert
     * @param increase the increase to subtract to the family member's action value.
     *                 It also represents the number of servants to give back to the player.
     */
    public void revertFamValue(FamilyMember famMem, int increase) {
        famMem.setActionValue(famMem
                .getActionValue() - increase);
        this.revertIncreaseValue(increase);
    }

    /**
     * It Gives the servants (spent to increase an action value) back to the player.
     * @param increase it represents the number of servants to give back to the player.
     */
    public void revertIncreaseValue(int increase) { //gives the servants (spent to increase an action value) back to the player
        int servantSpent;
        if (excommunications.get(1).has("servantExpense")) {
            int servantExp = excommunications.get(1).get("servantExpense").getAsInt();
            servantSpent = increase * servantExp;
        } else { servantSpent = increase; }
        this.currentResMerge(new
                Resources.ResBuilder().servant(servantSpent)
                .build());
    }

    /**
     * It handles both the activation (permanent ability) and the usage of a one per turn ability.
     *
     * @see #displayLeaderCards()
     * @return the boolean value representing the activation (or not) of a leader card.
     */
    public boolean activateLeaderCard() {
        this.sOut("Which Leader card do you want to activate?");
        if (leaderCards.isEmpty()) {
            this.sOut("You don't have any Leader Card anymore");
            return false;
        }
        this.displayLeaderCards();
        int choice = this.sInPrompt(1, leaderCards.size());
        return leaderCards.get(choice - 1).apply();

    }

    /**
     * It removes the desired leader card from the leader cards list and it calls
     * the {@link Council#choosePrivilege(Player)} method to choose a privilege.
     */
    public void discardLeaderCard() {
        this.sOut("Which Leader card do you want to discard? ");
        this.displayLeaderCards();
        int choice = this.sInPrompt(1, leaderCards.size());
        LeaderCard leader = leaderCards.get(choice - 1);
        this.sOut("You will discard " + leader.getName() + " leader card" +
                "and you will immediately receive 1 council privilege");
        this.sOut("Confirm?");
        int counter = 0;
        if(this.sInPromptConf()) {
            for (LeaderCard leaderCard : leaderCards) {
                if(leader.getName().equals(leaderCard.getName())) {
                    leaderCards.remove(counter);
                    Resources privRes = this.getParentGame()
                            .getBoard().councilPalace.choosePrivilege(this);
                    this.currentResMerge(privRes);
                    log.info("Council privilege gave -> " + privRes.toString() +
                    " to " + playerName);
                    break;
                }
                counter++;
            }
        }
    }

    /**
     * It format a string in order to display leader cards information to the player.
     */
    public void displayLeaderCards() {
        for (LeaderCard card : leaderCards) {
            String toDisplay = card.getName() + " -> cost: " +
                    card.getActivationCost() +" : " + card.getCardCostType();
            if (card.isActivated()) {
                toDisplay += " activated";
            }
            this.sOut(toDisplay);
        }
    }

    private void endgameLostVictoryRes(Resources loseVictoryRes) {
        currentRes.resourcesList.forEach((x, y) -> { //iterates over currentRes<key,value>
            if (loseVictoryRes.getByString(x) != 0) {
                int a = loseVictoryRes.getByString(x);
                int b = currentRes.getByString(x);
                currentResMerge(new Resources.ResBuilder().victoryPoint(b / a).build().inverse());
                log.info("Player " + playerName + " loses a victoryPoint every " + a + " " + x + " due to excommunication");
            }
        });
    }

    private void endgameLostVictoryCost(Resources loseVictoryCost) {
        AtomicInteger lostVictoryPts = new AtomicInteger(0);
        for (Card c : listCards()) {
            if ("buildings".equals(c.cardType)) {
                Resources cardCost = c.getCardCost(this);
                loseVictoryCost.resourcesList.forEach((x, y) -> {
                    if (y != 0) {
                        lostVictoryPts.addAndGet(cardCost.getByString(x));
                    }
                });
            }
        }
        currentResMerge(new Resources.ResBuilder().victoryPoint(lostVictoryPts.get()).build().inverse());
        log.info("Player " + playerName + " loses " + lostVictoryPts + " victoryPoint due to excommunication (buildingCardCost)");
    }

    public void endgame() {
        List<Integer> territoriesVictory = Arrays.asList(0, 0, 0, 1, 4, 10, 20);
        List<Integer> charactersVictory = Arrays.asList(0, 1, 3, 6, 10, 15, 21);
        int countTerritories = 0;
        int countCharacters = 0;
        Resources purpleFinal = new Resources.ResBuilder().build();
        JsonObject excomBase = excommunications.get(2);
        int sumResources = (getCurrentRes().coin + getCurrentRes().servant + getCurrentRes().stone + getCurrentRes().wood);
        for (Card i : listCards()) {
            String noVictoryType = "";
            if (excomBase.has("noVictoryType")) {
                noVictoryType = excomBase.get("noVictoryType").getAsString();
                log.info("Player " + playerName + " doesn't receive resources from " + noVictoryType + " cards due to excommunication");
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
        currentResMerge(purpleFinal);
        log.info("Ventures cards gave " + purpleFinal.victoryPoint + " victoryPoint");
        currentResMerge(new Resources.ResBuilder().victoryPoint(territoriesVictory.get(countTerritories)).build());
        log.info("Territories cards gave " + territoriesVictory.get(countTerritories) + " victoryPoint");
        currentResMerge(new Resources.ResBuilder().victoryPoint(charactersVictory.get(countCharacters)).build());
        log.info("Characters cards gave " + charactersVictory.get(countCharacters) + " victoryPoint");
        currentResMerge(new Resources.ResBuilder().victoryPoint(sumResources / 5).build());
        log.info("Personal Resources gave " + sumResources / 5 + " victoryPoint");

        if (excomBase.has("lostVictoryRes")) {
            Resources loseVictoryRes = Resources.fromJson(excomBase.get("lostVictoryRes").getAsJsonObject().get("resources"));
            endgameLostVictoryRes(loseVictoryRes);
        }

        if (excomBase.has("lostVictoryCost")) {
            Resources loseVictoryCost = Resources.fromJson(excomBase.get("lostVictoryCost").getAsJsonObject().get("resources"));
            endgameLostVictoryCost(loseVictoryCost);
        }

        log.info("Final Resources " + currentRes);
    }

    /**
     * Simple method to check if a player owns and has activated
     * the leader card specified by the param string
     * @param leaderName the leader card name
     * @return the boolean value representing the activation or not of the leader card.
     */
    public boolean leaderIsActive(String leaderName) {
        for (LeaderCard leader : this.getLeaderCards()) {
            if (leaderName.equals(leader.getName())
                    && leader.isActivated()) {
                return true;
            }
        }
        return false;
    }

    public PlayerIO getIo() {
        return io;
    }

    public String toString() {
        return this.playerName;
    }

    public BonusTile getBonusT() {
        return bonusT;
    }

    public Game getParentGame() {
        return parentGame;
    }

    public List<JsonObject> getExcommunications() { return excommunications; }

    public void setExcommunication(JsonObject e, int index) {
        this.excommunications.add(index, e);
    }

    public void setParentGame(Game parentGame) {
        this.parentGame = parentGame;
    }

    public void currentResMerge(Resources toMerge) {
        this.currentRes = this.currentRes.merge(toMerge);
    }

    public Resources getCurrentRes() {
        return currentRes;
    }

    public void addCard(Card toAdd) {
        this.cards.add(toAdd);
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

    public void addFamMemIncrease(int incomingIncrease) {
        this.lastFamMemIncrease += incomingIncrease;
    }

    public void rstFamMemIncrease() { this.lastFamMemIncrease = 0; }

    public int getLastFamMemIncrease() { return lastFamMemIncrease; }

    /**
     * It serialize into a JsonObject all the player information.
     * @return the JsonObject containing all the player information
     */
    public JsonObject serialize() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("playerName", this.playerName);
        ret.put("playerColour", this.playerColour);
        ret.put("resources", this.currentRes);
        List<JsonObject> famMembersJ = new ArrayList<>();
        this.famMemberList.forEach(f -> famMembersJ.add(f.serialize()));
        ret.put("famMembers", famMembersJ);
        ret.put("excomm", this.excommunications);
        ret.put("bonusTile", this.bonusT.serialize());
        ret.put("cards", this.cards.serialize());
        return new Gson().fromJson(new Gson().toJson(ret), JsonObject.class);
    }

}