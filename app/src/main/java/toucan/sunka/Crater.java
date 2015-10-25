package toucan.sunka;

/**
 * Created by andrei on 21/10/15.
 */
public class Crater {

    public static final int ACTION_DELAY = 60;
    private Player owner;
    private Crater nextCrater, oppositeCrater;
    private int stones;
    private boolean store;

    public Crater(boolean store){
        this.store = store;
        stones = this.store ? 0 : 7;
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
        if ( stones != 0 )
            if (!nextCrater.isStore()) performMove(nextCrater, stones - 1);
            else if ( belongsToActivePlayer(nextCrater) ) moveStone(nextCrater, stones - 1);
                 else performMove(nextCrater.getNextCrater(), stones - 1);
    }
    /**
     * Method first checks if the remaining stones are 0 - if it's the last move
     *    if it is, it changes turn and it checks if it can perform a steal and does so.
     *              if it can't perform a steal it does the last move,
     *              and does NOT call placeAlong(0), which means the player does
     *              NOT receive an extra turn
     *    if it isn't the last move, it calls moveStone()
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
        else moveStone(crater, remainingStones); //recursive call
    }

    /**
     * Adds 1 stone to the next crater and calls placeAlong()
     * It can call placeAlong(0) which would mean the player would get an extra turn
     * @param crater is the next crater on which the stone will be added
     * @param remainingStones
     */
    public void moveStone(Crater crater, int remainingStones){
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
