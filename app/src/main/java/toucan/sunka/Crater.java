package toucan.sunka;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.Button;



/**
 * Created by andrei on 21/10/15.
 */
public class Crater extends Button {

    public static final int ACTION_DELAY = 300; // Miliseconds
    private Player owner, activePlayer, inactivePlayer;
    private Crater nextCrater, oppositeCrater;
    protected boolean sideOne, sideTwo;
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
                    if ((boolean) params[2]) switchPlayers();
                    publishProgress(params[0], params[1], params[3]); //steal
                    break;
                case 3:
                    switchPlayers();
                    publishProgress(params[0],params[1]); //last move
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
            if (params.length == 3) ((Crater) params[2]).setText(String.format("%d", 0));
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
            if (!checkSide(this.getOppositeCrater().getOwner()))
                disableActivesEnableInactiveCrater();
            int stones = getStones();
            updateCrater(this, 0);
            placeAlong(stones);
            if (checkGameOver()) {
                // Create Intent here!!!
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

    /**
     * Method checks if both sides of the table are empty
     * @return value is true if the game is over, false otherwise
     */
    public boolean checkGameOver(){
        updateGameStatus();
        return sideOne && sideTwo;
    }

    /**
     * Updates active & inactive players, also checks if the game is over
     */
    public void updateGameStatus(){
        updatePlayers();
        sideOne = checkSide(activePlayer);
        sideTwo = checkSide(inactivePlayer);
    }

    /**
     * Checks to see if a player has his side of the table empty
     * @param player is the player whose side is to be checked
     * @return is true if the player has no remaining moves to do
     */
    public boolean checkSide(Player player){
        Crater currentCrater = player.getStore().getOppositeCrater().getNextCrater();
        while(!currentCrater.isStore()) {
            if ( !currentCrater.isEmpty() ) return false;
            currentCrater = currentCrater.getNextCrater();
        }
        return true;
    }

    /**
     * Method sets the stones of the crater to the stones parameter. It also calls a new
     * AsyncTask with four parameters, signaling a new move that results in a steal.
     * If the other player has no moves left, it doesn't change the turns
     * @param store represents the store where the stones will be moved
     * @param stones represents the last stone and the oppositeCrater's stones that need to be
     *               put in the active player's store
     * @param steal is a boolean put to signal that this is a steal move. Should there be 3 params,
     *              it would've been harder to differentiate between this method and the one that
     *              signals last move
     * @param oppositeCrater represents the crater from which the stones will be stolen
     */
    public void updateCrater(Crater store, int stones, boolean steal, Crater oppositeCrater) {
        updateGameStatus();
        store.stones = stones;
        oppositeCrater.stones = 0;
        steal = !checkSide(oppositeCrater.getOwner());
        new setCraterStones().execute(store, stones, steal, oppositeCrater);
    }

    /**
     * Method sets the stones of the crater to the stones parameter. It also calls a new
     * AsyncTask with three parameters, signaling that this is the last move.
     * If the other player has no moves left to do, it does not change the turn
     * @param crater represents the crater where the stones will be moved
     * @param stones represents new number of stones to be set for the new crater
     * @param lastMove signals that this is the last move
     */
    public void updateCrater(Crater crater, int stones, boolean lastMove){
        updateGameStatus();
        crater.stones = stones;
        if (!checkSide(crater.getOppositeCrater().getOwner()))
            new setCraterStones().execute(crater, stones, lastMove);
        else new setCraterStones().execute(crater,stones);
    }

    /**
     * Method sets the stones of the crater to the stones parameter. It also calls a new
     * AsyncTask with two parameters, making a normal move.
     * @param crater represents the crater where the stones will be moved
     * @param stones represents new number of stones to be set for the new crater
     */
    public void updateCrater(Crater crater, int stones){
        updateGameStatus();
        crater.stones = stones;
        new setCraterStones().execute(crater,stones);
    }

    /**
     * Gets an array of the craters owned by the active player
     * @return value is an array of length 8, containing either first eight (store included)
     *         Craters, or the last 8, depending on the active player
     */
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

    /**
     * Checks if the move that is about to happen will award an extra move
     * @return value is true if the move will generate an extra move
     */
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

    /**
     * Iterates through getActivePlayerCraters and disables them
     */
    public void disableActivesEnableInactiveCrater(){
        if (!getsFreeMove())
            for (Crater crater : getActivePlayerCraters()) {
                crater.setEnabled(false);
                crater.getOppositeCrater().setEnabled(true);
            }
    }

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
}
