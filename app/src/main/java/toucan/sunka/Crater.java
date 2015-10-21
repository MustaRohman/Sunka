package toucan.sunka;

import android.widget.Button;

/**
 * Created by andrei on 21/10/15.
 */
public class Crater {

    Player Owner;
    Crater nextCrater, oppositeCrater;
    int stones = 7; // Defaulted to 7

    public Crater(){

    }

    public void placeAlong(int remainingStones){

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
