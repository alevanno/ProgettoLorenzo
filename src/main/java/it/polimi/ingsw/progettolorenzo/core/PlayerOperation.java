package it.polimi.ingsw.progettolorenzo.core;

import it.polimi.ingsw.progettolorenzo.Game;

import java.util.concurrent.Callable;


public class PlayerOperation implements Callable<Boolean> {
    private Game g;
    private Player pl;

    public PlayerOperation(Game game, Player player) {
        this.g = game;
        this.pl = player;
    }

    @Override
    public Boolean call() {
        //try {
            g.setCurrPlayer(pl);
            pl.sOut("Turn " + g.getHalfPeriod() + ": Player " + pl.playerName +
                    " is the next player for this round:");
            pl.sOut("Current Res: " + pl.currentRes.toString());
            boolean ret = false;
            while (true) {
                g.getBoard().displayBoard();
                pl.sOut("Which family member do you want to use?: ");
                pl.sOut(pl.displayFamilyMembers());
                FamilyMember famMem = pl.getAvailableFamMembers()
                        .get(pl.sInPrompt(1, pl.getAvailableFamMembers().size()) - 1);
                pl.sOut(famMem.getSkinColour() + " family member selected");
                g.rstFamMemIncrease(); //needed so it doesn't restore the previous player's famMemIncrease when the timer expires
                g.addFamMemIncrease(pl.increaseFamValue(famMem));
                //setFam(famMem); //TODO
                pl.sOut("Available actions:");
                pl.sOut(Utils.displayList(g.getActions()));
                pl.sOut("Which action do you want to try?: ");
                g.getBoard().displayBoard();
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
                    pl.sOut("Current Res: " + pl.currentRes.toString());
                    continue;
                } else if ("SkipRound".equalsIgnoreCase(action)) {
                    pl.sOut("You skipped the round");
                    ret = true;
                }
                if (ret) {
                    return true;
                    //break;
                } else {
                    // placed here to abort this operation if player is not satisfied, reverts the value increase by servants
                    pl.sOut("Reverting famMemIncrease");
                    pl.revertFamValue(famMem, g.getFamMemIncrease());
                    pl.sOut("Current Res: " + pl.currentRes.toString());
                    return true;
                }
            }
        //} catch () {
         //   return true;
        //}
    }
}
