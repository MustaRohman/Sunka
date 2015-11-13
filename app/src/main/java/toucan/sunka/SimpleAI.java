package toucan.sunka;

import java.util.ArrayList;
import java.util.TreeMap;

public class SimpleAI extends Player {

    private int storeIndex;
    private Crater[] buttonChoices; //includes the store at position 8.
    private TreeMap<int[], Crater> moveGeneratedFrom;
    private ArrayList<int[]> movesWithFreeTurn;
    private ArrayList<int[]> movesWhichPreventSteals;


    public SimpleAI(Player p) {
        super(p.getPlayerName());
        setGamesWon(p.getNumberOfGamesWon());
        setGamesLost(p.getNumberOfGamesLost());
        setPlayerRank(p.getPlayerRank());
        setPlayingTurnTo(p.isPlayingTurn());
        setStore(p.getStore());

        moveGeneratedFrom = new TreeMap<>();
        movesWithFreeTurn = new ArrayList<>();
        movesWhichPreventSteals = new ArrayList<>();
    }

    public int[] makeMoveFrom(int[] board, int choice) {
        int choiceStones = board[choice];
        int stones = board[choice];
        board[choice] = 0; // picked up the stones.

        //Variables in while loop:
        int offset = 1;
        int currentIndex = -1;
        int oppositeIndex = -1;

        //Index of crater to skip:
        int otherPlayerStoreIndex;
        if (storeIndex == 0) otherPlayerStoreIndex = 8;
        else otherPlayerStoreIndex = 0;

        //Move:
        while(stones != 0) {
            currentIndex = choice + offset;
            if ((currentIndex) != otherPlayerStoreIndex) {
                //Try to steal:
                if (stones == 1 && performsSteal(board, choice, choiceStones)) {
                    //Steal:
                    oppositeIndex = 16 - currentIndex;
                    //Take stones from opponent
                    board[storeIndex] += board[oppositeIndex];
                    board[oppositeIndex] = 0;
                    //Self reward
                    board[storeIndex] += board[currentIndex];
                    board[currentIndex] = 0;

                } else {
                    //Regular move:
                    board[currentIndex] += 1;
                }
            } //skipped other player's store

            //Update status:
            --stones;
            ++offset;
            if (offset == 16) offset = 0;
        }

        return board;
    }

    public void setButtonChoices() {buttonChoices = getStore().getTruePlayerCraters(SimpleAI.this);}

    public void setStoreIndex(int index) {storeIndex = index;}

    public Crater[] getButtonChoices() {return buttonChoices;}

    public boolean performsSteal(int[] board, int craterIndex, int stones) {
        int lastIndex = (craterIndex + stones) + 1;
        if (board[lastIndex] == 0 && board[16 - lastIndex] > 0) return true;
        return false;
    }

    public int[] getMoveWithBestStore(int[][] moves) {
        int[] bestYet = moves[0];
        for(int i = 1; i < moves.length; ++i) {
            if (bestYet[storeIndex] < moves[i][storeIndex]) bestYet = moves[i];
        }
        return bestYet;
    }

    public boolean getsFreeMoveWith(int craterIndex, int stones, int storeIndex) {
        if (storeIndex == 8 && ((craterIndex + stones) + 1) == storeIndex) return true;
        else if (storeIndex == 0 && ((craterIndex + stones) + 1) == 16) return true;
        return false;
    }

    public void clearCollections() {
        moveGeneratedFrom.clear();
        movesWithFreeTurn.clear();
        movesWhichPreventSteals.clear();
    }
}
