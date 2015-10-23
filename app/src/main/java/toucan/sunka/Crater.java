package toucan.sunka;

import android.widget.Button;

/**
 * Created by andrei on 21/10/15.
 */
public class Crater {

    private Player Owner;
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
            if (!(nextCrater.isStore() && !Owner.getStore().equals(nextCrater))) {
                // Checks if it's last move & conditions are met to steal other players stones
                if (remainingStones == 1 && nextCrater.isEmpty() && Owner.isPlayingTurn()) {
                    Crater ownerStore = Owner.getStore();
                    ownerStore.setStones(nextCrater.getOppositeCrater().getStones() + ownerStore.getStones() + 1);
                    nextCrater.getOppositeCrater().setStones(0);
                }
                
                else {
                    nextCrater.setStones(nextCrater.getStones() + 1);
                    nextCrater.placeAlong(remainingStones - 1);
                }
            }
            else {
                placeAlong(remainingStones);
            }
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
        return Owner;
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
        Owner = player;
    }
}
