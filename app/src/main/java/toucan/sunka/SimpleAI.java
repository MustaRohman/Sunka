package toucan.sunka;

import java.util.ArrayList;
import java.util.TreeMap;

public class SimpleAI extends Player {

    private int storeIndex;
    private Crater[] buttonChoices; //includes the store at position 8.
    private int[][] sevenStates;
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

    public void generateSevenStates() {
        clearCollections();
        
    }

    public void placeStateAt(int[] state, int position) {
        for(int i = 0; i < state.length; ++i) {
            sevenStates[position][i] = state[i];
        }
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
        int otherPlayerStoreIndex = getOtherPlayerStoreIndex();

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

    public boolean performsSteal(int[] board, int craterIndex, int stones) {
        int lastIndex = getLastIndex(craterIndex, stones);
        //Check condition:
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
        int lastIndex = getLastIndex(craterIndex, stones);
        if (storeIndex == 8 && lastIndex == storeIndex) return true;
        else if (storeIndex == 0 && lastIndex == 16) return true;
        return false;
    }

    public boolean preventsSteal(int[] board, int craterIndex) {
        board = makeMoveFrom(board, craterIndex);

        int startIndex;
        int lengthPosition;
        if (getOtherPlayerStoreIndex() == 0) {
            startIndex = 1;
            lengthPosition = 8;
        } else {
            startIndex = 9;
            lengthPosition = 16;
        }

        for(int i = startIndex; i < lengthPosition; ++i) {
            if (performsSteal(board, i, board[i])) return false;
        }
        return true;
    }

    public int getLastIndex(int craterIndex, int stones) {
        int tempLastIndex = (craterIndex + stones) + 1;
        int lastIndex = 0;
        //Get to the last index:
        if (tempLastIndex > 16) {
            //Do some working out to derive the last index.
            for (int i = 0; i < tempLastIndex; ++i) {
                if (i == 16) lastIndex = 0;
                else lastIndex = i;
            }
        } else return tempLastIndex;
        return lastIndex;
    }

    public int getOtherPlayerStoreIndex() {
        if (storeIndex == 0) return 8;
        else return 0;
    }

    public Crater[] getButtonChoices() {return buttonChoices;}

    public void setButtonChoices() {buttonChoices = getStore().getTruePlayerCraters(SimpleAI.this);}

    public void setStoreIndex(int index) {storeIndex = index;}

    public void clearCollections() {
        sevenStates = new int[7][16];
        moveGeneratedFrom.clear();
        movesWithFreeTurn.clear();
        movesWhichPreventSteals.clear();
    }
}
