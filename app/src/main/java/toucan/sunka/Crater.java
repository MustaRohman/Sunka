package toucan.sunka;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

/**
 * Created by andrei on 21/10/15.
 */
public class Crater extends Button {

    public static final int ACTION_DELAY = 1000;
    private Player owner;
    private Crater nextCrater, oppositeCrater;
    private int stones;
    private boolean store;

    public Crater(Context context, AttributeSet attrs){
        super(context, attrs);
        initialise(store);
    }

    public Crater(boolean store){
        super(null, null);
        initialise(store);
    }

    private void initialise(boolean store)
    {
        this.store = store;
        setStones(this.store ? 0 : 7);
    }

    /**
     * Method encapsulates the picking up of stones and setting the just now chosen
     * crater's stones to zero.
     */
    public void makeMoveFromHere()
    {
        placeAlong(this.stones);
        setStones(0);
    }

    /**
     * Method first checks if the next crater is not a store.
     *  If it isn't, it calls performMove()
     *  Otherwise, it checks if the next store belongs to the active player
     *    if so, it does a move.
     *    Otherwise, it does the regular move, skipping the other player's store.
     * @param stones represents how many stones are left to be placed
     */
    public void placeAlong(int stones){
        try {
            if (stones != 0)
                if (!nextCrater.isStore()) performMove(nextCrater, stones - 1);
                else if (belongsToActivePlayer(nextCrater))
                    performRegularMove(nextCrater, stones - 1);
                else performMove(nextCrater.getNextCrater(), stones - 1);
            Thread.sleep(ACTION_DELAY);
        }
        catch (InterruptedException e){
            Log.d("Exception", String.format("Sleep thread interrupted, error: %s", e));
        }
    }
    /**
     * Method first checks if the remaining stones are 0 - if it's the last move
     *    if it is, it changes turn and it checks if it can perform a steal and does so.
     *              if it can't perform a steal it does the last move,
     *              and does NOT call placeAlong(0), which means the player does
     *              NOT receive an extra turn
     *    if it isn't the last move, it calls performRegularMove()
     * @param crater is the next crater on which the stone will be added
     * @param remainingStones is the number of remaining stones to be added
     */
    public void performMove(Crater crater, int remainingStones){
        if ( remainingStones == 0 ) {
            if ( crater.isEmpty() && belongsToActivePlayer(crater) ) {
                Crater oppositeCrater = crater.getOppositeCrater();
                Crater ownerStore = owner.getStore();
                ownerStore.setStones(oppositeCrater.getStones() + ownerStore.getStones() + 1);
                oppositeCrater.setStones(0);
            }
            else crater.setStones(crater.getStones() + 1);
            changeTurn();
        }
        else performRegularMove(crater, remainingStones); //recursive call
    }

    /**
     * Adds 1 stone to the next crater and calls placeAlong()
     * It can call placeAlong(0) which would mean the player would get an extra turn
     * @param crater is the next crater on which the stone will be added
     * @param remainingStones
     */
    public void performRegularMove(Crater crater, int remainingStones){
        crater.setStones(crater.getStones() + 1);
        crater.placeAlong(remainingStones);
    }

    private void changeTurn() {
        Player otherPlayer = nextCrater.getOwner().equals(owner) ?
                nextCrater.getOppositeCrater().getOwner() :
                nextCrater.getOwner();

        if (owner.isPlayingTurn())
        {
            owner.setPlayingTurnTo(false);
            otherPlayer.setPlayingTurnTo(true);
        }
        else
        {
            otherPlayer.setPlayingTurnTo(false);
            owner.setPlayingTurnTo(true);
        }
    }

    // Checks if the crater belongs to the active player
    public boolean belongsToActivePlayer(Crater crater) {
        return crater.getOwner().isPlayingTurn();
    }

    public boolean isEmpty(){
        return (stones == 0);
    }

    public boolean isStore()
    {
        return store;
    }

    public Crater getNextCrater(){
        return nextCrater;
    }

    public Crater getOppositeCrater(){
        return oppositeCrater;
    }

    public Player getOwner(){
        return owner;
    }

    public int getStones(){
        return stones;
    }

    public void setStones(int stones){
        this.stones = stones;
        setText(Integer.toString(stones));
    }

    public void setNextCrater(Crater crater){
        nextCrater = crater;
    }

    public void setOppositeCrater(Crater crater) {
        oppositeCrater = crater;
    }

    public void setOwner(Player player){
        owner = player;
    }
}
