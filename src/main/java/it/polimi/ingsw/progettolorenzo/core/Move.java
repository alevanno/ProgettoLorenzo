package it.polimi.ingsw.progettolorenzo.core;

public class Move {
    private Move() {
        throw new IllegalStateException("Not designed to be instantiated");
    }

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
        if (floor == null) { pl.sOut("Card " + cardName + " does not exist!"); } //this appears even if it exists but it is of the wrong type
        return floor;
    }

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

    public static void floorActionWithCard(Player pl, Card caller, String type, int value, Resources discount) {
        Board board = pl.getParentGame().getBoard();
        pl.sOut("Immediate floorActionWithCard from card " + caller + "\nDo you want to exploit it?");
        if (!pl.sInPromptConf()) { return; }
        FamilyMember dummy = new FamilyMember(pl, value, "Dummy");
        pl.getAvailableFamMembers().add(dummy);
        boolean ret = false;
        do {
            pl.sOut("Card " + caller.cardName + " allows you to take another card of type " + type);
            Floor floor;
            board.displayBoard();
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
