package it.polimi.ingsw.progettolorenzo.core.player;

import it.polimi.ingsw.progettolorenzo.Game;
import it.polimi.ingsw.progettolorenzo.core.FamilyMember;
import it.polimi.ingsw.progettolorenzo.core.Move;
import it.polimi.ingsw.progettolorenzo.core.Player;
import it.polimi.ingsw.progettolorenzo.core.Utils;

import java.util.concurrent.Callable;

/**
 * This class is the starting point for the chain of a player's
 * operations (such as Production, Market, Floor etc...).
 */
public class PlayerOperation implements Callable<Boolean> {
    private Game g;
    private Player pl;

    public PlayerOperation(Game game, Player player) {
        this.g = game;
        this.pl = player;
    }

    /**
     * This method implements the call method from the Callable interface.
     * It allows the player to choose a family member, possibly increase its value
     * and to choose which category of action he wants to start. If the
     * advanced rules are active it allows to activate Leader Cards. If the
     * action is not successful it restores the possible family member's value
     * increase.
     */
    @Override
    public Boolean call() {
            g.setCurrPlayer(pl);
            pl.sOut("Turn " + g.getHalfPeriod() + ": Player " + pl.playerName +
                    " is the next player for this round:");
            pl.sOut("Current Res: " + pl.getCurrentRes());
            boolean ret = false;
            while (true) {
                g.displayGame();
                pl.sOut("Which family member do you want to use?: ");
                pl.sOut(pl.displayFamilyMembers());
                FamilyMember famMem = pl.getAvailableFamMembers()
                        .get(pl.sInPrompt(1, pl.getAvailableFamMembers().size()) - 1);
                pl.sOut(famMem.getSkinColour() + " family member selected");
                pl.rstFamMemIncrease(); //needed so it doesn't restore the previous player's famMemIncrease when the timer expires
                pl.addFamMemIncrease(pl.increaseFamValue(famMem));
                // TODO
                //setFam(famMem);
                pl.sOut("Available actions:");
                pl.sOut(Utils.displayList(g.getActions()));
                pl.sOut("Which action do you want to try?: ");
                g.displayGame();
                String action = g.getActions().get(pl.sInPrompt(1, g.getActions().size()) - 1);
                if ("Floor".equalsIgnoreCase(action)) {
                    ret = Move.floorAction(g.getBoard(), famMem);
                } else if ("Market".equalsIgnoreCase(action)) {
                    ret = Move.marketAction(g.getBoard(), famMem);
                } else if ("CouncilPalace".equalsIgnoreCase(action)) {
                    ret = Move.councilAction(g.getBoard(), famMem);
                } else if ("Production".equalsIgnoreCase(action)) {
                    ret = Move.prodAction(g.getBoard(), famMem);
                } else if ("Harvest".equalsIgnoreCase(action)) {
                    ret = Move.harvAction(g.getBoard(), famMem);
                } else if ("ActivateLeaderCard".equalsIgnoreCase(action)) {
                    pl.activateLeaderCard();
                    continue;
                } else if ("DiscardLeaderCard".equalsIgnoreCase(action)){
                    pl.discardLeaderCard();
                    pl.sOut("Current Res: " + pl.getCurrentRes());
                    continue;
                } else if ("SkipRound".equalsIgnoreCase(action)) {
                    pl.sOut("You skipped the round");
                    ret = true;
                }
                if (ret) {
                    return true;
                } else {
                    // placed here to abort this operation if player is not satisfied, reverts the value increase by servants
                    pl.sOut("Reverting famMemIncrease");
                    pl.revertFamValue(famMem, pl.getLastFamMemIncrease());
                    pl.sOut("Current Res: " + pl.getCurrentRes());
                }
            }
    }
}
