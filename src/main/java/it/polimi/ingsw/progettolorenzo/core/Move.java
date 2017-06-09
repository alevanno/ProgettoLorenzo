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
            pl.sOut("Previous Res: " + pl.currentRes.toString());
            act.apply();
            pl.sOut("Current Res: " + pl.currentRes.toString());
            return true;
        } else {
            act.emptyActions();
            pl.sOut("Ok, aborting action as requested");
            pl.sOut("Current Res: " + pl.currentRes.toString());
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
                boolean conf = confirmation(pl, floor);
                if (!conf) {
                    //next actions to do if confirmation == false
                    pl.currentRes = pl.currentRes.merge(new Resources.ResBuilder().coin(coinToPay).build());
                }
                return conf;
            }
        } else {
            pl.sOut("Card " + cardName
                    + " does not exist! Please choose another action: ");
            return false;
        }
    }


    // FIXME it contains duplication from floorAction...
    // add some checks(?)
    /*public static void floorActionWithCard(Board board, Player pl, String type, int value, Resources discount) {
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
        pl.increaseFamValue(dummy);
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
            if (!confirmation(pl, floor)) {
                //next actions to do if confirmation == false
                dummy.setActionValue(dummy
                        .getActionValue() - servantSub);
                pl.currentRes = pl.currentRes.merge(new
                        Resources.ResBuilder().servant(servantSub).build().inverse());
            }
        }
    }*/

    public static boolean marketAction(Board board, FamilyMember fam) {
        Player pl = fam.getParent();
        pl.sOut("Select your market booth: ");
        board.marketSpace.displayBooths(pl);
        int in = pl.sInPrompt(1, board.marketSpace.numOfBooths);
        MarketBooth booth =  board.marketSpace.getBooths().get(in - 1);
        if(!booth.claimSpace(fam)) {
            pl.sOut("Please enter a valid action:");
            return false;
        } else {
            return confirmation(pl, booth);
        }
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
                if (pl.sInPromptConf()) {
                    area.claimFamSec(fam); //the value reduction is handled in Production/Harvest
                    return confirmation(pl, area);
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return confirmation(pl, area);
        }
    }
}
