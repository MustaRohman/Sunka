package toucan.sunka;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.security.MessageDigest;
import java.util.logging.LogRecord;
import android.os.Handler;

/**
 * Created by andrei on 21/10/15.
 */
/**
 * TODO for this class:
 *  -> implement what happens when it's game over (high prio)
 *  -> separate logic from Button logic into two separate classes to ease readability (low prio)
 *  -> JavaDOC the big logic methods explaining what's what (low prio)
 */
public class Crater extends Button {

    public static final int ACTION_DELAY = 1000;
    private Player owner;
    private Crater nextCrater, oppositeCrater;
    protected int stones;
    private boolean store;
    public Activity activity;

    //headache below
    private class setCraterStones extends AsyncTask<Object, Object, Void> {

        protected Void doInBackground(Object... params){
            try{
                publishProgress(params[0],params[1],params[2]);
            }
            catch(IndexOutOfBoundsException e){
                publishProgress(params[0],params[1]);
            }
            try{
                Thread.sleep(ACTION_DELAY);
            }
            catch (InterruptedException e){}
            return null;
        }

        protected void onProgressUpdate(Object... params){
            Crater currentCrater = (Crater) params[0];
            try {
                ((Crater) params[2]).setText(Integer.toString(0));
            }
            catch (IndexOutOfBoundsException e){}
            int stones = (int) params[1];
            currentCrater.setText(Integer.toString(stones));
        }
    }

    public Crater(Context context, AttributeSet attrs){
        super(context, attrs);
        initialise(false);
    }

    public Crater(boolean store){
        super(null, null);
        initialise(store);
    }

    public void initialise(boolean store) {
        this.store = store;
        setStones(this.store ? 0 : 7);
    }

    /**
     * Method encapsulates the picking up of stones and setting the just now chosen
     * crater's stones to zero. After each move it also checks if the game is over
     */
    public void makeMoveFromHere() {
        if (belongsToActivePlayer(this)) {
            int stones = getStones();
            updateCrater(this, 0);
            placeAlong(stones);
            if (checkGameOver(nextCrater)) {
                // Game over
            }
        }
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
            if (stones != 0)
                if (!nextCrater.isStore()) {
                    performMove(nextCrater, stones - 1);
                } else if (belongsToActivePlayer(nextCrater)) {
                    performRegularMove(nextCrater, stones - 1);
                }
                else {
                    performMove(nextCrater.getNextCrater(), stones - 1);
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
            if ( crater.isEmpty() && belongsToActivePlayer(crater) && crater.getOppositeCrater().getStones() != 0 ) {
                Crater oppositeCrater = crater.getOppositeCrater();
                Crater ownerStore = owner.getStore();
                updateCrater(ownerStore, oppositeCrater.getStones() + ownerStore.getStones() + 1, oppositeCrater);
            }
            else {
                updateCrater(crater, crater.getStones() + 1);
            }
            changeTurn();
        }
        else {
            performRegularMove(crater, remainingStones); //recursive call
        }
    }

    /**
     * Adds 1 stone to the next crater and calls placeAlong()
     * It can call placeAlong(0) which would mean the player would get an extra turn
     * @param crater is the next crater on which the stone will be added
     * @param remainingStones signals how many stones are left to be placed
     */
    public void performRegularMove(Crater crater, int remainingStones){
        updateCrater(crater, crater.getStones() + 1);
        crater.placeAlong(remainingStones);
    }

    private void changeTurn() {
        Player otherPlayer = nextCrater.getOwner().equals(owner) ?
                nextCrater.getOppositeCrater().getOwner() :
                nextCrater.getOwner();

        if (owner.isPlayingTurn()) {
            owner.setPlayingTurnTo(false);
            otherPlayer.setPlayingTurnTo(true);
        }
        else {
            otherPlayer.setPlayingTurnTo(false);
            owner.setPlayingTurnTo(true);
        }
    }
    public boolean checkGameOver(Crater currentCrater){
        boolean sideOne, sideTwo;
        while (!currentCrater.isStore())
            currentCrater = currentCrater.getNextCrater();
        sideOne = checkSide(currentCrater.getNextCrater());
        currentCrater = currentCrater.getNextCrater();
        while (!currentCrater.isStore())
            currentCrater = currentCrater.getNextCrater();
        sideTwo = checkSide(currentCrater.getNextCrater());
        return sideOne || sideTwo;
    }

    public boolean checkSide(Crater currentCrater){
        while(!currentCrater.isStore()) {
            if ( !currentCrater.isEmpty() ) return false;
            currentCrater = currentCrater.getNextCrater();
        }
        return true;
    }

    public void updateCrater(Crater crater, int stones){
        crater.stones = stones;
        new setCraterStones().execute(crater,stones);
    }

    public void updateCrater(Crater store, int stones, Crater oppositeCrater) {
        new setCraterStones().execute(store, stones , oppositeCrater);
        store.stones = stones;
        oppositeCrater.stones = 0;
    }

    // Checks if the crater belongs to the active player
    public boolean belongsToActivePlayer(Crater crater) {
        return crater.getOwner().isPlayingTurn();
    }

    public boolean isEmpty(){
        return (stones == 0);
    }

    public boolean isStore() {
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

    public void setStones(final int stones) {
        this.stones = stones;
        setText(Integer.toString(stones));
    }

    public void setActivity(Activity activity) { this.activity = activity; }

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
