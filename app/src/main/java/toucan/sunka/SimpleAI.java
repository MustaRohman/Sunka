package toucan.sunka;

import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
/**
 * Since the best store would result in the largest steal do not worry about which
 * move is getting us the largest store.
 *
 * There is flaw in which we do not decide if its better to block individual steals.
 * We are oblivious that a player may have multiple steal options. We either block all
 * or none.
 */
public class SimpleAI extends Player {

    private int storeIndex;
    private Crater[] buttonChoices; //includes the store at position 8.
    private int[][] sevenStates;
    private HashMap<int[], Crater> moveGeneratedFrom;
    private ArrayList<int[]> movesWithFreeTurn;
    private ArrayList<int[]> movesWhichPreventSteals;
    private HashMap<int[], Integer> opponentChoicesToSteal;
    private HashMap<Integer, int[]> opponentBoardsBeforeSteal;
    private int[] bestMove;
    private int[] currentState = new int[16];

    public SimpleAI(Player p) {
        super(p.getPlayerName());
        //Tranfer the AI statistics from the player object that it first started life as:
        setGamesWon(p.getNumberOfGamesWon());
        setGamesLost(p.getNumberOfGamesLost());
        setPlayerRank(p.getPlayerRank());
        setPlayingTurnTo(p.isPlayingTurn());
        setStore(p.getStore());

        //Initialise collections:

    }

    public void generateSevenStates() {
        clearCollections(); //Start from a clean slate so we don't introduce old data when deciding the best turn
        int offset = 9;
        int[] state;
        for(int i = 0; i < buttonChoices.length - 1; ++i) {
            //Create a state based on a button choice:
            state = buttonChoices[i].getArrayBoard(getStore()); //Starting from the player one store
            currentState = state;
            //The next stuff need you to not perform the move yet:
            if (getsFreeMoveWith(i + offset, state[i], storeIndex)) movesWithFreeTurn.add(state);

            if (preventsSteal(state, state[i + offset]))
                movesWhichPreventSteals.add(state);
            //Make the move on the copyBoard and store the result to access the crater which
            //would recreate it
            state = makeMoveFrom(state, i + offset, true);
            moveGeneratedFrom.put(state, buttonChoices[i]);
            //Add the state we just created
            placeStateAt(state, i);
        }
    }

    public void placeStateAt(int[] state, int position) {
        for(int i = 0; i < state.length; ++i) {
            sevenStates[position][i] = state[i];
        }
    }

    public int[] makeMoveFrom(int[] board, int choice, boolean performTheSteal) {
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
            if (choice + offset > 15) offset = -choice; //backtrack to simulate the linked craters
            currentIndex = choice + offset;
            oppositeIndex = 16 - currentIndex;
            if ((currentIndex) != otherPlayerStoreIndex) {
                //Try to steal:
                if (performTheSteal && stones == 1 && board[currentIndex] == 0 && isIndexOnMySideAndAChoice(currentIndex)) {
                    if (board[oppositeIndex] > 0) {
                        //Steal:

                        //Take stones from opponent
                        board[storeIndex] += board[oppositeIndex];
                        board[oppositeIndex] = 0;
                        //Self reward
                        board[storeIndex] += board[currentIndex];
                        board[currentIndex] = 0;
                    }
                } else {
                    //Regular move:
                    board[currentIndex] += 1;
                }
            } //skipped other player's store

            //Update status:
            --stones;
            ++offset;
        }

        return board;
    }

    public void generateBestMove() {
        int[] bestMoveMoveSoFar;
        int[] moveWithBestStore = getMoveWithBestStore(sevenStates);
        bestMoveMoveSoFar = moveWithBestStore;
        //Pick if some move equal the moveWithBestScore pick the one which will
        //give us a free turn:
        for(int[] move: movesWithFreeTurn) {
            if (bestMoveMoveSoFar[storeIndex] == move[storeIndex]) {
                bestMoveMoveSoFar = move;
            }
        }
        //Check (if any) prevents of steals are worth it:
        for(int[] move: movesWhichPreventSteals) {
            //Check store of the opponent if we give him the opportunity to steal
            int opponentChoiceForSteal;
            if (!opponentChoicesToSteal.isEmpty()) {
                opponentChoiceForSteal = opponentChoicesToSteal.get(move).intValue();
                if (bestMoveMoveSoFar[storeIndex] >
                        makeMoveFrom(opponentBoardsBeforeSteal.get(opponentChoiceForSteal),
                                opponentChoiceForSteal,
                                true)[getOtherPlayerStoreIndex()]) {
                    bestMoveMoveSoFar = move;
                }
            }
        }

        bestMove = bestMoveMoveSoFar;
    }

    public int[] getMoveWithBestStore(int[][] moves) {
        int[] bestYet = moves[0];
        int lastIndex = 0;
        for(int i = 1; i < moves.length; ++i) {
            if (bestYet[storeIndex] < moves[i][storeIndex]) {
                lastIndex = i;
                bestYet = moves[i];
            }
        }
        return moves[lastIndex];
    }

    public boolean getsFreeMoveWith(int craterIndex, int stones, int storeIndex) {
        int lastIndex = getLastIndex(craterIndex, stones);
        if (storeIndex == 8 && lastIndex == storeIndex) return true;
        else if (storeIndex == 0 && lastIndex == 16) return true;
        return false;
    }

    public boolean performsSteal(int[] board, int craterIndex, int stones) {
        int lastIndex = getLastIndex(craterIndex, stones);
        board = makeMoveFrom(board, craterIndex, false);
        //Check condition:
        if (isIndexOnMySideAndAChoice(lastIndex) &&
                board[lastIndex] == 0 &&
                board[16 - lastIndex] > 0) return true;
        return false;
    }

    public boolean preventsSteal(int[] board, int craterIndex) {
        int[] screenBoardCopy = getStore().getArrayBoard(getStore());
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
            if (performsSteal(screenBoardCopy, i, board[i])) {
                opponentChoicesToSteal.put(currentState, i);
                return false;
            }
        }
        return true;
    }

    public boolean isIndexOnMySideAndAChoice(int index) {
        if (storeIndex == 8) return index < 8 && 0 < index;
        else return index > 8 && 16 > index;
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

    public Crater getBestCrater(){
        Iterator it = moveGeneratedFrom.entrySet().iterator();
        boolean isEquals = true;
        while (it.hasNext()){
            HashMap.Entry pair = (HashMap.Entry) it.next();
            for (int i = 0; i < bestMove.length; i++)
                if ( ((int[]) pair.getKey())[i] != bestMove[i] ) {
                    isEquals = false;
                    Log.d("INFO", "hash item:" + ((int[]) pair.getKey())[i] + " bestMove item: "+ bestMove[i]);
                    break;
                }
            if (isEquals){
                Log.d("Info", "FOUND CRATER!!!");
                return (Crater) pair.getValue();
            }
        }
        return null;
    }

    public void clearCollections() {
        moveGeneratedFrom = new HashMap<>();
        movesWithFreeTurn = new ArrayList<>();
        movesWhichPreventSteals = new ArrayList<>();
        opponentChoicesToSteal = new HashMap<>();
        opponentBoardsBeforeSteal = new HashMap<>();
        sevenStates = new int[7][16];
        moveGeneratedFrom.clear();
        movesWithFreeTurn.clear();
        movesWhichPreventSteals.clear();
        opponentChoicesToSteal.clear();
        opponentBoardsBeforeSteal.clear();
    }

}
