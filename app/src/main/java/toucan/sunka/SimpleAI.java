package toucan.sunka;

public class SimpleAI extends Player {

    private int storeIndex;

    public SimpleAI(Player p) {
        super(p.getPlayerName());
        setGamesWon(p.getNumberOfGamesWon());
        setGamesLost(p.getNumberOfGamesLost());
        setPlayerRank(p.getPlayerRank());
        setPlayingTurnTo(p.isPlayingTurn());
        setStore(p.getStore());
    }

    public void setStoreIndex(int index) {
        storeIndex = index;
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
