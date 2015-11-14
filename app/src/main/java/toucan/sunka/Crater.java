package toucan.sunka;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.Button;



/**
 * Created by andrei on 21/10/15.
 */
/**
 * TODO for this class:
    -> game over
 */
public class Crater extends Button {

    public static final int ACTION_DELAY = 500; // Milliseconds
    private Player owner, activePlayer, inactivePlayer;
    private Crater nextCrater, oppositeCrater;
    protected int stones;
    private boolean store;

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

        if (store){
            updateStoreImage(this,0);
            setGravity(Gravity.BOTTOM);
        } else {
            updateCraterImage(this, stones);
        }
    }

    /**
     * Async task used to set the delay between each move. A new instance is called by
     * calling new setCraterStones().execute(...params). It can have between 2 and 4 parameters,
     * depending on the type of move that needs to be executed. The class starts with
     * doInBackground.
     *
     * The < Object, Object, Void > parameters for the AsyncTask represent the types of parameters
     * needed. The first Object refers to the type of parameters needed for doInBackground, the
     * second one is the type of parameters for onProgressUpdate and Void is what doInBackground
     * returns
     */
    private class setCraterStones extends AsyncTask<Object, Object, Void> {

        /**
         * Due to android limitations :( no method from the crater class can be called
         * in the doInBackground since Crater extends Button. The only backend task done done here
         * is switchPlayers().
         *
         * Depending on the number of parameters, the method calls publishProgress with either all 4
         * parameters - when a steal is triggered - or two parameters - the execution of a normal
         * move.
         *
         * It also adds the delay for the animation.
         * @param params is an array that contains all the parameters sent when creating a new
         *               instance of setCraterStones and executing it.
         */
        protected Void doInBackground(Object... params){
            switch (params.length) {
                case 4:
                    switchPlayers();
                    publishProgress(params[0], params[1], params[3], params[2]); //steal
                    break;
                case 3:
                    switchPlayers();
                    publishProgress(params[0],params[1],params[2]); //last move
                    break;
                case 2:
                    publishProgress(params[0],params[1]); //normal move
                    break;
            }
            try{
                Thread.sleep(ACTION_DELAY);
            }
            catch (InterruptedException e){}
            return null;
        }

        /**
         * This method gets 2 or 3 parameters. The first parameter is a crater which will
         * have it's setText cast on with the second parameter, the number of stones.
         * The third parameter is only sent in case of a steal, and it sets its text 0 in the same
         * thread.
         * @param params contains the crater that needs to be updated, the stones, and an optional
         *               parameter, the oppositeCrater when the steal is made
         */
        protected void onProgressUpdate(Object... params){
            Crater currentCrater = (Crater) params[0];
            int stones = (int) params[1];
            currentCrater.setText(String.format("%d",stones));
            if (params.length == 3) {
                activePlayer.unhighlightText();
                inactivePlayer.highlightText();
            }
            if (params.length == 4) {
                Crater oppositeCrater = ((Crater) params[2]);
                oppositeCrater.setText(String.format("%d", 0));
                Crater store = currentCrater.owner.getStore();
                updateStoreImage(store, store.getStones());
                updateCraterImage(oppositeCrater, 0);
                activePlayer.unhighlightText();
                inactivePlayer.highlightText();
            }
        }

        /**
         * Method that switches players rounds, activePlayer and inactivePlayer being updated
         * before all new moves.
         */
        protected void switchPlayers() {
            activePlayer.setPlayingTurnTo(false);
            inactivePlayer.setPlayingTurnTo(true);
        }

    }

    /**
     * Method encapsulates the picking up of stones and setting the just now chosen
     * crater's stones to zero. After each move it also checks if the game is over
     */
    public void makeMoveFromHere() {
        if (belongsToActivePlayer(this) && this.stones != 0 ) {
            disableActivesEnableInactiveCrater();
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
                if (!nextCrater.isStore())
                    performMove(nextCrater, stones - 1);
                else if (belongsToActivePlayer(nextCrater)) performRegularMove(nextCrater, stones - 1);
                else performMove(nextCrater.getNextCrater(), stones - 1);
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
                updateCrater(ownerStore, oppositeCrater.getStones() + ownerStore.getStones() + 1, true, oppositeCrater); //steal
            }
            else updateCrater(crater, crater.getStones() + 1, true); //last move

        }
        else performRegularMove(crater, remainingStones); //recursive call

    }

    /**
     * Adds 1 stone to the next crater and calls placeAlong()
     * It can call placeAlong(0) which would mean the player would get an extra turn
     * @param crater is the next crater on which the stone will be added
     * @param remainingStones signals how many stones are left to be placed
     */
    public void performRegularMove(Crater crater, int remainingStones){
        updateCrater(crater, crater.getStones() + 1); //regular move
        crater.placeAlong(remainingStones);
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

    /**
     * Method sets the stones of the crater to the stones parameter. It also calls a new
     * AsyncTask with four parameters, signaling a new move that results in a steal.
     * @param store represents the store where the stones will be moved
     * @param stones represents the last stone and the oppositeCrater's stones that need to be
     *               put in the active player's store
     * @param steal is a boolean put to signal that this is a steal move. Should there be 3 params,
     *              it would've been harder to differentiate between this method and the one that
     *              signals last move
     * @param oppositeCrater represents the crater from which the stones will be stolen
     */
    public void updateCrater(Crater store, int stones, boolean steal, Crater oppositeCrater) {
        updatePlayers();
        store.stones = stones;
        oppositeCrater.stones = 0;
        new setCraterStones().execute(store, stones, steal, oppositeCrater);
    }

    /**
     * Method sets the stones of the crater to the stones parameter. It also calls a new
     * AsyncTask with three parameters, signaling that this is the last move.
     * @param crater represents the crater where the stones will be moved
     * @param stones represents new number of stones to be set for the new crater
     * @param lastMove signals that this is the last move
     */
    public void updateCrater(Crater crater, int stones, boolean lastMove){
        updatePlayers();
        crater.stones = stones;
        new setCraterStones().execute(crater, stones, lastMove);
    }

    /**
     * Method sets the stones of the crater to the stones parameter. It also calls a new
     * AsyncTask with two parameters, making a normal move.
     * @param crater represents the crater where the stones will be moved
     * @param stones represents new number of stones to be set for the new crater
     */
    public void updateCrater(Crater crater, int stones){
        updatePlayers();
        crater.stones = stones;
        new setCraterStones().execute(crater,stones);
    }

    public Crater[] getActivePlayerCraters(){
        Crater[] craterList = new Crater[8];
        Crater startStore = owner.getStore().getOppositeCrater();
        Crater currentCrater = startStore.getNextCrater();
        int i = 0;
        while(!currentCrater.getNextCrater().isStore()) {
            craterList[i++] = currentCrater;
            currentCrater = currentCrater.nextCrater;
        }
        craterList[i]=currentCrater;
        craterList[i+1] = owner.getStore();
        return craterList;
    }

    public boolean getsFreeMove() {
        int stones = this.stones;
        Crater otherPlayerStore = this.getOwner().getStore().getOppositeCrater();
        Crater currentCrater = this;
        while(stones != 0) {
            if(currentCrater.getNextCrater() != otherPlayerStore) currentCrater = currentCrater.getNextCrater();
            else currentCrater = currentCrater.getNextCrater().getNextCrater();
            stones--;
        }
        return currentCrater.isStore();
    }

    public void disableActivesEnableInactiveCrater(){
        if (!getsFreeMove())
            for (Crater crater : getActivePlayerCraters()) {
                crater.setEnabled(false);
                crater.getOppositeCrater().setEnabled(true);
            }
    }

    // Checks if the crater belongs to the active player
    public boolean belongsToActivePlayer(Crater crater) {
        return crater.getOwner().isPlayingTurn();
    }

    public Player getActivePlayer(){
        if (nextCrater.getOwner().isPlayingTurn())
            return nextCrater.getOwner();
        return nextCrater.getOppositeCrater().getOwner();
    }

    public Player getInactivePlayer(){
        if (nextCrater.getOwner().isPlayingTurn())
            return nextCrater.getOppositeCrater().getOwner();
        return nextCrater.getOwner();
    }

    public void updatePlayers(){
        activePlayer = getActivePlayer();
        inactivePlayer = getInactivePlayer();
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

    public void setStones(int stones) {
        this.stones = stones;
        setText(String.format("%d", stones));
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

    public void setActiveImage(boolean active){

        if (active){
            updateCraterImage(this, stones);
        } else {
            switch (stones){
                case 0:setBackgroundResource(R.drawable.button_disabled);
                    break;
                case 1:setBackgroundResource(R.drawable.crater_1stone_disabled);
                    break;
                case 2:setBackgroundResource(R.drawable.crater_2stone_disabled);
                    break;
                case 3:setBackgroundResource(R.drawable.crater_3stone_disabled);
                    break;
                case 4:setBackgroundResource(R.drawable.crater_4stone_disabled);
                    break;
                case 5:setBackgroundResource(R.drawable.crater_5stone_disabled);
                    break;
                case 6:setBackgroundResource(R.drawable.crater_6stone_disabled);
                    break;
                case 7:setBackgroundResource(R.drawable.crater_7stone_disabled);
                    break;
                case 8:setBackgroundResource(R.drawable.crater_8stone_disabled);
                    break;
                case 9:setBackgroundResource(R.drawable.crater_9stone_disabled);
                    break;
                case 10:setBackgroundResource(R.drawable.crater_10stone_disabled);
                    break;
                case 11:setBackgroundResource(R.drawable.crater_11stone_disabled);
                    break;
                default:setBackgroundResource(R.drawable.crater_11stone_disabled);
                    break;
            }

        }

    }

    public static void updateStoreImage(Crater crater, int stones) {
        switch (stones) {
            case 0:
                crater.setBackgroundResource(R.drawable.store2);
                break;
            case 1:
                crater.setBackgroundResource(R.drawable.store_1stone);
                break;
            case 2:
                crater.setBackgroundResource(R.drawable.store_2stone);
                break;
            case 3:
                crater.setBackgroundResource(R.drawable.store_3stone);
                break;
            case 4:
                crater.setBackgroundResource(R.drawable.store_4stone);
                break;
            case 5:
                crater.setBackgroundResource(R.drawable.store_5stone);
                break;
            case 6:
                crater.setBackgroundResource(R.drawable.store_6stone);
                break;
            case 7:
                crater.setBackgroundResource(R.drawable.store_7stone);
                break;
            case 8:
                crater.setBackgroundResource(R.drawable.store_8stone);
                break;
            case 9:
                crater.setBackgroundResource(R.drawable.store_9stone);
                break;
            case 10:
                crater.setBackgroundResource(R.drawable.store_10stone);
                break;
            case 11:
                crater.setBackgroundResource(R.drawable.store_11stone);
                break;
            case 12:
                crater.setBackgroundResource(R.drawable.store_12stone);
                break;
            case 13:
                crater.setBackgroundResource(R.drawable.store_13stone);
                break;
            case 14:
                crater.setBackgroundResource(R.drawable.store_14stone);
                break;
            case 15:
                crater.setBackgroundResource(R.drawable.store_15stone);
                break;
            case 16:
                crater.setBackgroundResource(R.drawable.store_16stone);
                break;
            case 17:
                crater.setBackgroundResource(R.drawable.store_17stone);
                break;
            case 18:
                crater.setBackgroundResource(R.drawable.store_18stone);
                break;
            case 19:
                crater.setBackgroundResource(R.drawable.store_19stone);
                break;
            case 20:
                crater.setBackgroundResource(R.drawable.store_20stone);
                break;
            default:
                crater.setBackgroundResource(R.drawable.store_20stone);
        }
    }

    public static void updateCraterImage(Crater crater, int stones){

        switch (stones){
            case 0:crater.setBackgroundResource(R.drawable.button_enabled);
                break;
            case 1:crater.setBackgroundResource(R.drawable.crater_1stone);
                break;
            case 2:crater.setBackgroundResource(R.drawable.crater_2stone);
                break;
            case 3:crater.setBackgroundResource(R.drawable.crater_3stone);
                break;
            case 4:crater.setBackgroundResource(R.drawable.crater_4stone);
                break;
            case 5:crater.setBackgroundResource(R.drawable.crater_5stone);
                break;
            case 6:crater.setBackgroundResource(R.drawable.crater_6stone);
                break;
            case 7:crater.setBackgroundResource(R.drawable.crater_7stone);
                break;
            case 8:crater.setBackgroundResource(R.drawable.crater_8stone);
                break;
            case 9:crater.setBackgroundResource(R.drawable.crater_9stone);
                break;
            case 10:crater.setBackgroundResource(R.drawable.crater_10stone);
                break;
            case 11:crater.setBackgroundResource(R.drawable.crater_11stone);
                break;
            default:crater.setBackgroundResource(R.drawable.crater_11stone);
                break;
        }
    }
}
