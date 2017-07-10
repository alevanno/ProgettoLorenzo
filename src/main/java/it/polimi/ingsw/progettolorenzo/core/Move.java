package it.polimi.ingsw.progettolorenzo.core;

/**
 * The class contains all the different kinds of
 * interaction between {@link it.polimi.ingsw.progettolorenzo.Game} and {@link Player}.
 * This is not an instantiable class, but contains static methods representing interactions.
 * {@link Player} (or one of his family members) is one of the param in every interaction.
 */
public class Move {
    private Move() {
        throw new IllegalStateException("Not designed to be instantiated");
    }

    /**
     * This method is called at the end of every action.
     * It basically asks the player for confirmation, and he {@link Action#apply()} the
     * Action and then {@link Action#emptyActions()} to give to the next player an empty
     * ready one;
     * In case of reversing, the actions list is cleared.
     * @param pl the player to ask the confirmation
     * @param act the action to confirm
     * @return the boolean value representing the result of the confirmation
     */
    public static boolean confirmation(Player pl, Action act) {
        pl.sOut("Action attempted successfully");
        act.logActions();
        pl.sOut("Do you want to confirm?");
        if (pl.sInPromptConf()) {
            pl.sOut("Previous Res: " + pl.getCurrentRes().toString());
            act.apply();
            act.emptyActions();
            pl.sOut("Current Res: " + pl.getCurrentRes().toString());
            return true;
        } else {
            act.emptyActions();
            pl.sOut("Ok, aborting action as requested");
            return false;
        }
    }

    /**
     * Utility method to search a Card in the Board, given its name.
     * @see Board
     * @see Floor
     *
     * @param cardName The card name
     * @param board the Game Board
     * @param pl the player attempting the search
     * @param type it specify the type to match in case of constraints on the choice
     * @return the Floor containing the card
     */
    public static Floor searchCard(String cardName, Board board, Player pl, String type) {
        Floor floor = null;
        for (Tower t : board.towers) {
            for (Floor fl : t.getFloors()) {
                if (fl.getCard() != null && fl.getCard().cardName.equalsIgnoreCase(cardName)) {
                    if ("any".equals(type) || type.equals(fl.getParentTower().getType())) {
                        floor = fl;
                    } else {
                        pl.sOut("It must be of type " + type);
                    }
                }
            }
        }
        if (floor == null) {
            // this appears even if it exists but it is of the wrong type
            pl.sOut("Card " + cardName + " does not exist!");
        }
        return floor;
    }

    /**
     * It represents the interaction in which the player is claiming a {@link Floor}.
     * It asks for the cards to obtain, searches ,it with {@link #searchCard(String, Board, Player, String)},
     * checks the possibility to access first the Tower {@link Tower#checkTowerOcc(FamilyMember)}
     * then the Floor {@link Floor#accessFloor(Player, int)}.
     * If the action is allowed, it asks for {@link #confirmation(Player, Action)}
     * @param board the Game Board
     * @param famMem the family member attempting the claim
     * @return the boolean value representing the result of the action
     */
    public static boolean floorAction(Board board, FamilyMember famMem) {
        Player pl = famMem.getParent();
        Floor floor;
        do {
            pl.sOut("Which card do you want to obtain?: ");
            String cardName = pl.sIn();
            floor = searchCard(cardName, board, pl, "any");
        } while (floor == null);
        int towerOcc = floor.getParentTower().checkTowerOcc(famMem);
        if (!floor.accessFloor(pl, towerOcc)) {
            return false;
        }
        boolean ret = floor.claimFloor(famMem);
        if (!ret) {
            pl.sOut("Action not allowed! Please enter a valid action:");
            return false;
        } else {
            return confirmation(pl, floor);
        }
    }

    /**
     * A specific variation of {@link #floorAction(Board, FamilyMember)}.
     * It handles the action of taking a card from a {@link CardImmediateAction}.
     * It instantiates a new "dummy" family member for the player
     * in order to use it instead of a real one.
     *
     *
     * @param pl the player attempting the action
     * @param caller the Card calling the action
     * @param type the type of the card to obtain
     * @param value the value the dummy famMemb has to be set
     * @param discount the eventual discount on the cost of the card
     */
    public static void floorActionWithCard(Player pl, Card caller, String type, int value, Resources discount) {
        Board board = pl.getParentGame().getBoard();
        pl.sOut("Immediate floorActionWithCard from card " + caller + "\nDo you want to exploit it?");
        if (!pl.sInPromptConf()) {
            return;
        }
        FamilyMember dummy = new FamilyMember(pl, value, "Dummy");
        pl.getAvailableFamMembers().add(dummy);
        boolean ret = false;
        do {
            pl.sOut("Card " + caller.cardName + " allows you to take another card of type " + type);
            Floor floor;

            pl.getParentGame().displayGame();
            int famMemIncrease = pl.increaseFamValue(dummy);
            final Resources toMerge = new Resources.ResBuilder().build();
            do {
                pl.sOut("Which card do you want to obtain?: ");
                String cardName = pl.sIn();
                floor = searchCard(cardName, board, pl, type);
            } while (floor == null || floor.getCard().equals(caller));
            int towerOcc = floor.getParentTower().checkTowerOcc(dummy);
            if (!floor.accessFloor(pl, towerOcc)) { //TODO check where are the 3 coins going
                continue;
            }
            Resources cardCost = floor.getCard().getCardCost(pl);
            cardCost.resourcesList.forEach((x, y) -> {
                int val = discount.getByString(x);
                if (y != 0 && val != 0) {
                    toMerge.merge(new Resources.ResBuilder().setByString(x, val).build());
                    pl.sOut(toMerge.toString());
                    pl.currentResMerge(toMerge);
                }
            });
            Floor callerFloor = searchCard(caller.cardName, board, pl, "any");
            ret = floor.claimFloorWithCard(dummy, callerFloor);
            if (ret) {
                pl.addFamMemIncrease(famMemIncrease);
            } else {
                pl.revertFamValue(dummy, famMemIncrease);
                pl.currentResMerge(toMerge.inverse());
            }
        } while (!ret);
    }

    /**
     * Represents the interaction between player and market
     * @param board the game board
     * @param fam the family member claiming the marketBooth
     * @return the boolean value representing the result of the action
     */
    public static boolean marketAction(Board board, FamilyMember fam) {
        Player pl = fam.getParent();
        pl.sOut("Select your market booth: ");
        board.marketSpace.displayBooths(pl);
        int in = pl.sInPrompt(1, board.marketSpace.getNumOfBooths());
        MarketBooth booth = board.marketSpace.getBooths().get(in - 1);
        return booth.claimSpace(fam) && confirmation(pl, booth);
    }


    public static boolean councilAction(Board board, FamilyMember fam) {
        Player pl = fam.getParent();
        return board.councilPalace.claimSpace(fam) && confirmation(pl, board.councilPalace);
    }

    public static boolean prodAction(Board board, FamilyMember fam) {
        return prodHarvCommon(board.productionArea, fam);
    }

    public static boolean harvAction(Board board, FamilyMember fam) {
        return prodHarvCommon(board.harvestArea, fam);
    }

    /**
     * It handles common parts of Harvest\production moves.
     * @param area it represents the productionArea or harvestArea
     * @param fam the family membmer claiming the space.
     * @return the boolean value representing the result of the action
     */
    public static boolean prodHarvCommon(ActionProdHarv area, FamilyMember fam) {
        Player pl = fam.getParent();
        boolean ret = area.claimFamMain(fam);
        if (!ret) {
            pl.sOut("Main space is occupied");
            if (pl.getParentGame().getNumOfPlayers() > 2) {
                pl.sOut("Would you like to put your FamMem in the secondary space?");
                //the value reduction is handled in Production/Harvest
                return pl.sInPromptConf() && area.claimFamSec(fam) && confirmation(pl, area);
            } else {
                return false;
            }
        } else {
            return confirmation(pl, area);
        }
    }
}
