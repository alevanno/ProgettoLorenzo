package it.polimi.ingsw.progettolorenzo.core;

public class Move {
    public static boolean bool = false;
    private Move() {
        throw new IllegalStateException("Not designed to be instantiated");
    }

    public static boolean confirmation(Player pl, Action act) {
        pl.sOut("Action attempted successfully");
        act.logActions();
        pl.sOut("Do you want to confirm?");
        if (pl.sInPromptConf()) {
            act.apply();
            pl.sOut(pl.currentRes.toString());
            return true;
        } else {
            act.emptyActions();
            pl.sOut("Ok, aborting action as requested");
            return false;
        }
    }

    public static boolean floorAction(Board board, FamilyMember famMem) {
        Player pl = famMem.getParent();
        int coinToPay = 3;        // FIXME make me configurable?
        pl.sOut("Which card do you want to obtain?: ");
        String cardName = pl.sIn();
        Floor floor = null;
        for (Tower t : board.towers) {
            for (Floor fl : t.getFloors()) {
                if (fl.getCard() != null && fl.getCard().cardName.equalsIgnoreCase(cardName)) {
                        if(!t.checkTowerOcc(famMem, coinToPay)) {
                            if (bool) {
                                pl.currentRes = pl.currentRes.merge(
                                        new Resources.ResBuilder().coin(coinToPay).build().inverse());
                                bool = false;
                            }
                        } else {
                            pl.sOut("Action not allowed! Please enter a valid action:");
                            return false;
                        }
                        floor = fl;
                        break;
                    }
                    continue;
                }
            }
        if (floor != null) {
            boolean ret = floor.claimFloor(famMem);
            if (!ret) {
                pl.sOut("Action not allowed! Please enter a valid action:");
                return false;
            } else {
                pl.sOut("Action attempted successfully");
                floor.logActions();
                pl.sOut("Do you want to confirm?");
                if (pl.sInPromptConf()) {
                    floor.apply();
                    pl.sOut(pl.currentRes.toString());
                    return true;
                } else {
                    floor.emptyActions();
                    //next actions to do if confirmation == false
                    pl.currentRes = pl.currentRes.merge(new Resources.ResBuilder().coin(coinToPay).build());
                    pl.sOut("Ok, aborting action as requested");
                    return false;
                }
            }
        } else {
            pl.sOut("Card " + cardName
                    + " does not exist!: please choose another action: ");
            return false;
        }
    }


    // FIXME it contains duplication from floorAction...
    // add some checks(?)
    public static void claimFloorWithCard(Board board, Player pl,String type, int value, Resources discount) {
        FamilyMember dummy = new FamilyMember(pl, value, "Dummy");
        int coinToPay = 3;
        pl.getAvailableFamMembers().add(dummy);
        final Resources toMerge = new Resources.ResBuilder().build();
        // FIXME handle better that nullable floor for sonar
        Floor floor = new Floor(null, null, null, 0);
        boolean ok = false;
        while (!ok) {
            pl.sOut("Which card do you want to obtain?: ");
            String cardName = pl.sIn();
            for (Tower t : board.towers) {
                for (Floor fl : t.getFloors()) {
                    if (fl.getCard() != null &&
                            fl.getCard().cardName.equals(cardName)) {
                        if (!t.checkTowerOcc(dummy, coinToPay)) {
                            ok = true;
                            if (bool) {
                                pl.currentRes = pl.currentRes.merge(
                                        new Resources.ResBuilder().coin(coinToPay).build().inverse());
                                bool = false;
                            }
                        } else {
                            pl.sOut("Action not allowed! Please enter a valid action:");
                        }
                        floor = fl;
                        break;
                    }
                }
            }
        }
        Resources cardCost = floor.getCard().getCardCost();
        int servantSub = pl.increaseFamValue(dummy);
        cardCost.resourcesList.forEach((x, y) -> {
            int val = discount.getByString(x);
            if (y != 0 && val != 0) {
                toMerge.merge(new Resources.ResBuilder().setByString(x, val).build());
                pl.sOut(toMerge.toString());
                pl.currentRes = pl.currentRes.merge(toMerge);
            }
        });
        boolean ret = floor.claimFloor(dummy);
        if (!ret) {
            pl.sOut("Action not allowed! Please enter a valid action:");
            dummy.setActionValue(dummy
                    .getActionValue() - servantSub);
            pl.currentRes = pl.currentRes.merge(new
                    Resources.ResBuilder().servant(servantSub).build().inverse());

        } else {
            pl.sOut("Action attempted successfully");
            floor.logActions();
            pl.sOut("Do you want to confirm?");
            if (pl.sInPromptConf()) {
                floor.apply();
                pl.sOut(pl.currentRes.toString());
            } else {
                floor.emptyActions();
                pl.sOut("Ok, aborting action as requested");
                //next actions to do if confirmation == false
                dummy.setActionValue(dummy
                        .getActionValue() - servantSub);
                pl.currentRes = pl.currentRes.merge(new
                        Resources.ResBuilder().servant(servantSub).build().inverse());
            }
        }
    }

    public static boolean marketAction(Board board, FamilyMember fam) {
        Player pl = fam.getParent();
        pl.sOut("Select your market booth: ");
        board.marketSpace.displayBooths(pl);
        int in = pl.sInPrompt(1,4);
        MarketBooth booth =  board.marketSpace.getBooths().get(in - 1);
        boolean ret = booth.claimSpace(fam);
        if(!ret) {
            pl.sOut("Please enter a valid action:");
            return false;
        } else {
            pl.sOut("Action attempted successfully");
            booth.logActions();
            pl.sOut("Do you want to confirm?");
            if (pl.sInPromptConf()) {
                booth.apply();
                pl.sOut(pl.currentRes.toString());
                return true;
            } else {
                booth.emptyActions();
                pl.sOut("Ok, aborting action as requested");
                return false;
            }
        }
    }


    public static boolean councilAction(Board board, FamilyMember fam) {
        //TODO
        Player pl = fam.getParent();
        return board.councilPalace.claimSpace(fam) && confirmation(pl, board.councilPalace);
    }

    public static boolean prodAction(Board board, FamilyMember fam) {
        Player pl = fam.getParent();
        boolean ret = board.productionArea.claimFamMain(fam);
        if (!ret) { //TODO check number of players
            pl.sOut("Main space is occupied");
            if (pl.getParentGame().getNumOfPlayers() > 2) {
                pl.sOut("Would you like to put your FamMem in the secondary space?");
                if (pl.sInPromptConf()) {
                    board.productionArea.claimFamSec(fam); //the value reduction is handled in Production
                    confirmation(pl, board.productionArea);
                } else {
                    return false;
                } //TODO
            } else {
                return false;
            }
        } else {
            confirmation(pl, board.productionArea);
        }
        return false;
    }

    public static boolean harvAction(Board board, FamilyMember fam) {
        Player pl = fam.getParent();
        boolean ret = board.harvestArea.claimFamMain(fam);
        if (!ret) { //TODO check number of players
            pl.sOut("Main space is occupied");
            if (pl.getParentGame().getNumOfPlayers() > 2) {
                pl.sOut("Would you like to put your FamMem in the secondary space?");
                if (pl.sInPromptConf()) {
                    board.harvestArea.claimFamSec(fam); //the value reduction is handled in Production
                    return confirmation(pl, board.harvestArea);
                } else {
                    return false;
                } //TODO
            } else {
                return false;
            }
        } else {
            return confirmation(pl, board.harvestArea);
        }
    }
}
