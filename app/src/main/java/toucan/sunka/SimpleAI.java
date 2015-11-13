package toucan.sunka;

public class SimpleAI {

    private int storeIndex;

    public SimpleAI() {

    }

    public boolean getsFreeMoveWith(int craterIndex, int stones, int storeIndex) {
        if (storeIndex == 8 && ((craterIndex + stones) + 1) == storeIndex) return true;
        else if (storeIndex == 0 && ((craterIndex + stones) + 1) == 16) return true;
        return false;
    }

    public boolean performsSteal(int[] board, int craterIndex, int stones) {
        if (board[(craterIndex + stones) + 1] == 0) return true;
        return false;
    }

    public int[] getMoveWithBestStore(int[][] moves) {
        int[] bestYet = moves[0];
        for(int i = 1; i < moves.length; ++i) {
            if (bestYet[storeIndex] < moves[i][storeIndex]) bestYet = moves[i];
        }
        return bestYet;
    }
}
