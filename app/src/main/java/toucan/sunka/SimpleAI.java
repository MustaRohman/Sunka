package toucan.sunka;

/**
 * Created by Koceto on 13/11/2015.
 */
public class SimpleAI {

    private int storeIndex;

    public SimpleAI() {

    }

    public boolean getsFreeMove(int craterIndex, int stones, int storeIndex) {
        if (storeIndex == 8 && ((craterIndex + stones) - 1) == storeIndex) return true;
        else if (storeIndex == 0 && ((craterIndex + stones) - 1) == 16) return true;
        return true;
    }
}
