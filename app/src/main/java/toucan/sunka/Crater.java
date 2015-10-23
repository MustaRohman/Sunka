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

    public void placeAlong(int remainingStones) {
        if (remainingStones != 0) {
            // Checks if next Crater is a not a store
            if (!(nextCrater.isStore() && !owner.isPlayingTurn())){
                // Checks if it's last move & conditions are met to steal other players stones
                if (remainingStones == 1 && nextCrater.isEmpty() && owner.isPlayingTurn() && !nextCrater.isStore()) {
                    Crater ownerStore = owner.getStore();
                    ownerStore.setStones(nextCrater.getOppositeCrater().getStones() + ownerStore.getStones() + 1);
                    nextCrater.getOppositeCrater().setStones(0);
                    changeTurn();
                }
                // Makes a normal move
                else {
                    // If its a finishing turn and it doesn't finish on a store...
                    if (remainingStones == 1 && !nextCrater.isStore()) {
                        // Then swap the player turns
                        changeTurn();
                    }

                    nextCrater.setStones(nextCrater.getStones() + 1);
                    nextCrater.placeAlong(remainingStones - 1);
                }
            }
            //If next crater is other players' store, ignores it
            else {
                nextCrater.getNextCrater().setStones(nextCrater.getNextCrater().getStones() + 1);
                if (remainingStones == 1)
                {
                    // Finishing move
                    changeTurn();
                }
                else {
                    nextCrater.getNextCrater().placeAlong(remainingStones - 1);
                }
            }
        }
    }

    private void changeTurn()
    {
        Player otherPlayer = nextCrater.getOppositeCrater().getOwner();

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
