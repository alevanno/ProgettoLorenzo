package it.polimi.ingsw.progettolorenzo.core;

import com.google.gson.JsonObject;
import it.polimi.ingsw.progettolorenzo.Game;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Player {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    public final String playerName;
    public final String playerColour;
    public final Socket playerSocket;
    public Resources currentRes;  // FIXME make private
    private List<FamilyMember> famMemberList = new ArrayList<>();
    private Deck cards = new Deck();
    private List<JsonObject> excommunications = new ArrayList<>(Arrays.asList(new JsonObject(), new JsonObject(), new JsonObject()));
    private List<LeaderCard> leaderCards = new ArrayList<>();
    private BonusTile bonusT;
    private Game parentGame;
    private Scanner socketIn;
    private PrintWriter socketOut;


    public Player(String name, String colour, Socket socket) {
        this.playerName = name;
        this.playerColour = colour;
        this.currentRes = new Resources.ResBuilder().servant(3).stone(2).wood(2).build();
        this.playerSocket = socket;
        log.info(String.format(
                "New player: %s (colour: %s, resources: %s)",
                name, colour, this.currentRes));
    }

    private void sInInit() {
        try {
            if (this.socketIn == null) {
                this.socketIn = new Scanner(new
                        BufferedReader(new
                        InputStreamReader(this.playerSocket.getInputStream())));
            }
        } catch (IOException e) {
            // FIXME handle this better
            log.log(Level.WARNING, e.getMessage(), e);
        }
    }

    public String sIn()  {
        this.sInInit();
        return this.socketIn.nextLine();
    }

    public int sInPrompt(int minValue, int maxValue) {
        this.sInInit();
        int choice;

        do {
            sOut("Input an int between " + minValue + " and " + maxValue);
            while (!this.socketIn.hasNextInt()) {
                this.socketIn.nextLine();
                this.sOut("Please input an int");
            }
            choice = this.socketIn.nextInt();
            this.socketIn.nextLine();
        } while (choice < minValue || choice > (maxValue));
        return choice;
    }

    public boolean sInPromptConf() {
        this.sInInit();
        String choice;

        do {
            sOut("Input 'y' (yes) or 'n' (no)");
            choice = this.socketIn.next().substring(0,1);

        } while (!"y".equalsIgnoreCase(choice) && !"n".equalsIgnoreCase(choice));
        if ("y".equalsIgnoreCase(choice)) { return true; };
        if ("n".equalsIgnoreCase(choice)) { return false; };
        return false;
    }

    public void sOut(String s) {
        try {
            if (this.socketOut == null) {
                this.socketOut = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(
                        this.playerSocket.getOutputStream())));
            }
        } catch (IOException e) {
            // FIXME handle this better
            log.log(Level.WARNING, e.getMessage(), e);
        }
        this.socketOut.println(s);
        this.socketOut.flush();
    }

    public void famMembersBirth(Map<String, Integer> famValues) {
        List <String> colorList = Arrays.asList("Orange", "Black", "White");
        for (String s: colorList) {
            int val;
            if (excommunications.get(0).has("harvMalus")) {
                val = famValues.get(s) - 1;
            } else {
                val = famValues.get(s);
            }
            this.famMemberList.add(new FamilyMember(this, val, s));
        }
        this.famMemberList.add(
                new FamilyMember(this, 0, "Blank"));
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

    // TODO test excomm
    public int increaseFamValue(FamilyMember famMember) {
        this.sOut("Do you want to increase your "
                + famMember.getSkinColour() + " family member value?" );
        int servantSub = 0;
        if (this.sInPromptConf()) {
            // FIXME make me prettier after currentRes handling decision
            boolean ok = false;
            while (!ok) {
                int servant = currentRes.servant;
                this.sOut("Current servants: " + servant);
                this.sOut("How many do you want to use? ");
                int servantSpent;
                if (excommunications.get(1).has("servantExpense")) {
                    int servantExp = excommunications.get(1).get("servantExpense").getAsInt();
                    sOut("(Due to your excommunication you have to use " + servantExp + " servants to increase the action value)");
                    do {
                        sOut("It has to be a multiple of " + servantExp);
                        servantSpent = this.sInPrompt(1, servant);
                    } while (servantSpent%servantExp != 0);
                    servantSub = servantSpent/servantExp;
                }
                else {
                    servantSub = this.sInPrompt(1, servant);
                    servantSpent = servantSub;
                }
                this.sOut("Current " + famMember.getSkinColour()
                        + " family member value: " + (famMember.getActionValue() + servantSub));
                this.sOut("Confirm?: y/n");
                String answer = this.sIn();
                if ("y".equalsIgnoreCase(answer)) {
                    famMember.setActionValue(famMember.getActionValue() + servantSub);
                    this.currentRes = this.currentRes.merge(new
                            Resources.ResBuilder().servant(servantSpent)
                            .build().inverse());
                    ok = true;
                }
            }
            return servantSub;
        } return servantSub;
    }

    // TODO this method affects only the activation of a leader card;
    // we should create an other method to use the One per Round ability
    public boolean ActivateLeaderCard() {
        this.sOut("Which Leader card do you want to activate?");
        int count = 0;
        if (leaderCards.isEmpty()) {
            this.sOut("You don't have any Leader Card anymore");
            return false;
        }
        for (LeaderCard card : leaderCards) {
            this.sOut(String.format("%s %d %s",count + 1 + " -> " + card.getName(),
                    card.getActivationCost(), ": " + card.getCardCostType()));
            count++;
        }
        int choice = this.sInPrompt(1, count);
        boolean ret = leaderCards.get(choice - 1).apply();
        if (ret){
            return true;
        } else {
            return false;
        }
    }

    public void revertFamValue(FamilyMember famMem, int servantSub) {
        //reverts the value increase by servants
        famMem.setActionValue(famMem
                .getActionValue() - servantSub);
        this.currentRes = this.currentRes.merge(new
                Resources.ResBuilder()
                .servant(servantSub).build());
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