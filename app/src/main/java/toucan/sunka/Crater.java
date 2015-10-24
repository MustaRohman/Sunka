package toucan.sunka;

/**
 * Created by andrei on 21/10/15.
 */
public class Crater {

    private Player owner;
    private Crater nextCrater, oppositeCrater;
    private int stones;
    private boolean store;

    public Crater(boolean store){
        this.store = store;
        stones = this.store ? 0 : 7;
    }

    public void placeAlong(int stones){
        if ( stones != 0 ){
            if (!nextCrater.isStore()){ //checks if it's not a store
                if ( stones == 1 )  // checks if it's last stone
                    stealOrMove(nextCrater); // recursive tree ends
                else moveStone(nextCrater, stones - 1); //recursive call
            }
            else { // it is a store
                if ( belongsToCurrentPlayer(nextCrater) ) // checks if it's current player's store
                    moveStone(nextCrater, stones - 1 ); // recursive call - if stones = 1 then the next call will be
                                                         // moveStone( nextCrater, 0 ) which will signal the current player has an extra turn
                else  // it is other player's store
                    if ( stones == 1 ) stealOrMove(nextCrater.getNextCrater()); // recursive tree ends
                    else moveStone(nextCrater.getNextCrater(), stones - 1);
            }
        }
    }

    public void stealOrMove(Crater crater) {
        if ( crater.isEmpty() && belongsToCurrentPlayer(crater) ) //checks if you can steal
            stealCrater(crater.getOppositeCrater());
        else crater.setStones(crater.getStones() + 1); // does a normal move
        changeTurn();
    }

    public boolean belongsToCurrentPlayer(Crater crater) {
        return crater.getOwner().isPlayingTurn();
    }

    public void moveStone(Crater nextCrater, int remainingStones){
        nextCrater.setStones(nextCrater.getStones() + 1);
        nextCrater.placeAlong(remainingStones);
    }

    public void stealCrater(Crater oppositeCrater){
        Crater ownerStore = owner.getStore();
        ownerStore.setStones(oppositeCrater.getStones() + ownerStore.getStones() + 1);
        oppositeCrater.setStones(0);
    }

    private void changeTurn()
    {
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

    public boolean belongsTo(Player owner){
        return nextCrater.getOwner().equals(owner);
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
