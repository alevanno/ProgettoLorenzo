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
            g.currPlayer = pl;
            pl.sOut("Turn " + g.halfPeriod + ": Player " + pl.playerName +
                    " is the next player for this round:");
            pl.sOut("Current Res: " + pl.currentRes.toString());
            boolean ret = false;
            while (true) {
                g.board.displayBoard();
                pl.sOut("Which family member do you want to use?: ");
                pl.sOut(pl.displayFamilyMembers());
                FamilyMember famMem = pl.getAvailableFamMembers()
                        .get(pl.sInPrompt(1, pl.getAvailableFamMembers().size()) - 1);
                pl.sOut(famMem.getSkinColour() + " family member selected");
                g.famMemIncrease = 0; //needed so it doesn't restore the previous player's famMemIncrease when the timer expires
                g.famMemIncrease = pl.increaseFamValue(famMem);
                //setFam(famMem); //TODO
                pl.sOut("Available actions:");
                pl.sOut(Utils.displayList(g.actions));
                pl.sOut("Which action do you want to try?: ");
                g.board.displayBoard();
                String action = g.actions.get(pl.sInPrompt(1, g.actions.size()) - 1);
                if ("Floor".equalsIgnoreCase(action)) {
                    ret = Move.floorAction(g.board, famMem);
                } else if ("Market".equalsIgnoreCase(action)) {
                    ret = Move.marketAction(g.board, famMem);
                } else if ("CouncilPalace".equalsIgnoreCase(action)) {
                    ret = Move.councilAction(g.board, famMem);
                } else if ("Production".equalsIgnoreCase(action)) {
                    ret = Move.prodAction(g.board, famMem);
                } else if ("Harvest".equalsIgnoreCase(action)) {
                    ret = Move.harvAction(g.board, famMem);
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
                    pl.revertFamValue(famMem, g.famMemIncrease);
                    pl.sOut("Current Res: " + pl.currentRes.toString());
                    return true;
                }
            }
        //} catch () {
         //   return true;
        //}
    }
}
