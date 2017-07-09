package it.polimi.ingsw.progettolorenzo.core;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * This is one of the main classes of the model. It cannot be
 * instantiated; all the child classes (except for {@link Card}) have
 * a main method in which there are a series of {@link BaseAction} performed.
 * Every BaseAction is added in the actions ArrayList attribute of the class.
 * They are applied by {@link #apply()}, following a certain order.
 * The actions list can be empty to revert a macro action (i.e, when the
 * player is not satisfied of the result or he cannot in part accomplish it.
 * A {@link Logger} is declared here (and in all the child classes to) to monitor
 * all the single BaseAction in the {@link it.polimi.ingsw.progettolorenzo.ServerImpl}.
 *
 * @see BaseAction
 * @see Floor
 * @see Council
 * @see Production
 * @see Harvest
 * @see MarketBooth
 * @see Card
 */
public abstract class Action {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private List<BaseAction> actions = new ArrayList<>();

    /**
     * This method is used by the child classes: every time a BaseAction
     * become part of a "macro" action, it is added here.
     * All the actions are applied only at the end of
     * @param action the BaseAction to be added in the actions list.
     */
    public void addAction(BaseAction action) {
        this.actions.add(action);
    }

    /**
     * The method to empty the actions list in order to revert a macro action.
     * It basically calls the {@link ArrayList#clear()} method to clear the
     * ArrayList representing the actions.
     */
    public void emptyActions() {
        this.actions.forEach(a -> a.clear());
        this.actions.clear();
        log.fine("Actions cleared");
    }

    /**
     * The child class calling this method accomplishes all the {@link BaseAction} effectively.
     */
    public void apply() {
        for (int i=0; i < this.actions.size(); i++) {
            this.actions.get(i).apply();
        }
    }

    /**
     * The method that logs all the {@link BaseAction} in actions.
     * @see BaseAction#logAction()
     */
    public void logActions() {
        log.fine("Actions staged for " + this + ":");
        this.actions.forEach(BaseAction::logAction);
    }
}
