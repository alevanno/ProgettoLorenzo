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
                    // FIXME aborting action doesn't reverse .apply()
                    // card no more in floor?
                    floor.apply();
                    pl.sOut(pl.currentRes.toString());
                    return true;
                } else {
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


}
