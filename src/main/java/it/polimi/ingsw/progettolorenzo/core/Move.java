package it.polimi.ingsw.progettolorenzo.core;

public class Move {
    public static boolean bool = false;
    private Move() {
        throw new IllegalStateException("Not thought to be instantiated");
    }

    public static boolean floorAction(Board board, FamilyMember famMem) {
        Player pl = famMem.getParent();
        int coinToPay = 3;        // FIXME make me configurable?
        pl.sOut("Which card do you want to obtain?: ");
        String cardName = pl.sIn();
        Floor floor = null;
        for (Tower t : board.towers) {
            for (Floor fl : t.getFloors()) {
                if (fl.getCard() != null && fl.getCard().cardName.equals(cardName)) {
                        if(!t.checkTowerOcc(famMem, coinToPay)) {
                            if(bool == true) {
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
                pl.sOut("Are you fine with this?: y/n");
                String reply = pl.sIn();
                if ("y".equalsIgnoreCase(reply) || "s".equalsIgnoreCase(reply)) {
                    floor.apply();
                    pl.sOut(pl.currentRes.toString());
                    return true;
                } else {
                    floor.emptyActions();
                    pl.currentRes = pl.currentRes.merge(new Resources.ResBuilder().coin(coinToPay).build());
                    pl.sOut("Ok, aborting action as requested");
                    return false;
                }
            }
        } else {
            pl.sOut("Card " + cardName
                    + " was already taken!: please choose an other action: ");
            return false;
        }
    }


    // FIXME it contains duplication from floorAction...
    // FIXME it stops when you are looking for a Card that in not in the board..
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
            pl.sOut("Are you fine with this?: y/n");
            String reply = pl.sIn();
            if ("y".equalsIgnoreCase(reply) || "s".equalsIgnoreCase(reply)) {
                floor.apply();
                pl.sOut(pl.currentRes.toString());
            } else {
                floor.emptyActions();
                pl.sOut("Ok, aborting action as requested");
                dummy.setActionValue(dummy
                        .getActionValue() - servantSub);
                pl.currentRes = pl.currentRes.merge(new
                        Resources.ResBuilder().servant(servantSub).build().inverse());
            }
        }
    }
}
