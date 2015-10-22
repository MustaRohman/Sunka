package toucan.sunka;

import android.widget.Button;

/**
 * Created by andrei on 21/10/15.
 */
public class Crater {

    private Player Owner;
    private Crater nextCrater, oppositeCrater;
    private int stones;

    public Crater(){
        stones = 7;
    }

    public void placeAlong(int remainingStones)
    {
        if (remainingStones != 0)
        {
            if (remainingStones == 1 && nextCrater.isEmpty() && Owner.isPlayingTurn())
            {
                Crater ownerStore = Owner.getStore();
                ownerStore.setStones(nextCrater.getOppositeCrater().getStones() + ownerStore.getStones());
                nextCrater.getOppositeCrater().setStones(0);
            }
            nextCrater.setStones(nextCrater.getStones() + 1);
            nextCrater.placeAlong(remainingStones - 1);
        }
    }

    public boolean isEmpty(){
        return (stones == 0);
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
