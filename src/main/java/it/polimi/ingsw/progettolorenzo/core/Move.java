package it.polimi.ingsw.progettolorenzo.core;

public class Move {
    private Move() {
        throw new IllegalStateException("Not thought to be instantiated");
    }

    public static boolean floorAction(Board board, FamilyMember famMem) {
        Player pl = famMem.getParent();

        pl.sOut("Which card do you want to obtain?: ");
        String cardName = pl.sIn();
        Floor floor = null;
        for (Tower t : board.towers) {
            for (Floor fl : t.getFloors()) {
                if (fl.getCard() != null) {
                    if (fl.getCard().cardName.equals(cardName)) {
                        floor = fl;
                        break;
                    }
                    continue;
                }
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
                pl.sOut("Are you fine with this?");
                String reply = pl.sIn();
                if ("y".equalsIgnoreCase(reply) || "s".equalsIgnoreCase(reply)) {
                    floor.apply();
                    pl.sOut(pl.currentRes.toString());
                    return true;
                } else {
                    floor.emptyActions();
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
    public static void claimFloorWithCard(Board board, Player pl,String type, int value, Resources discount) {
        FamilyMember dummy = new FamilyMember(pl, value, null);
        pl.getAvailableFamMembers().add(dummy);
        final Resources toMerge = new Resources.ResBuilder().build();
        Floor floor = null;
        pl.sOut("Which card do you want to obtain?: ");
        String cardName = pl.sIn();
        for (Tower t : board.towers) {
            for (Floor fl : t.getFloors()) {
                if (fl.getCard() != null) {
                    if (fl.getCard().cardName.equals(cardName)) {
                        floor = fl;
                        break;
                    }
                    continue;
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
            pl.sOut("Are you fine with this?");
            String reply = pl.sIn();
            if ("y".equalsIgnoreCase(reply) || "s".equalsIgnoreCase(reply)) {
                pl.sOut(floor.actions.toString());
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
