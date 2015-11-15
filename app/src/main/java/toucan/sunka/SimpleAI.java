package toucan.sunka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
    private int[] currentState;

    public SimpleAI(Player p) {
        super(p.getPlayerName());
        //Tranfer the AI statistics from the player object that it first started life as:
        setGamesWon(p.getNumberOfGamesWon());
        setGamesLost(p.getNumberOfGamesLost());
        setPlayerRank(p.getPlayerRank());
        setPlayingTurnTo(p.isPlayingTurn());
        setStore(p.getStore());

        //Initialise collections:
        moveGeneratedFrom = new HashMap<>();
        movesWithFreeTurn = new ArrayList<>();
        movesWhichPreventSteals = new ArrayList<>();
        opponentChoicesToSteal = new HashMap<>();
        opponentBoardsBeforeSteal = new HashMap<>();
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
            if (getsFreeMoveWith(i + offset, state[i + offset], storeIndex)) movesWithFreeTurn.add(state);
            else if (preventsSteal(state, i + offset, false, storeIndex)) movesWhichPreventSteals.add(state);
            //Make the move on the copyBoard and store the result to access the crater which
            //would recreate it
            state = makeMoveFrom(state, i + offset, true, storeIndex);
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

    public int[] makeMoveFrom(int[] board, int choice, boolean performTheSteal, int storeIndex) {
        int stones = board[choice];
        board[choice] = 0; // picked up the stones.

        //Variables in while loop:
        int offset = 1;
        int currentIndex = -1;
        int oppositeIndex = -1;

        //Index of crater to skip:
        int otherPlayerStoreIndex = getOtherPlayerStoreIndex(storeIndex);

        //Move:
        while(stones != 0) {
            if (choice + offset > 15) offset = -choice; //backtrack to simulate the linked craters
            currentIndex = choice + offset;
            oppositeIndex = 16 - currentIndex;
            if ((currentIndex) != otherPlayerStoreIndex) {
                //Try to steal:
                if (currentIndex != storeIndex && stones == 1 &&
                        board[currentIndex] == 0 &&
                        isIndexOnTheCorrectSide(currentIndex, choice, storeIndex)) {
                    if (board[oppositeIndex] > 0) {
                        if (performTheSteal) {
                            //Steal:
                            //Take stones from opponent
                            board[storeIndex] += board[oppositeIndex];
                            board[oppositeIndex] = 0;
                            //Self reward
                            board[storeIndex] += stones;
                            board[currentIndex] = 0;
                        }
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
        int[] moveWithBestStore = getMoveWithBestStore();
        bestMoveMoveSoFar = moveWithBestStore;
        boolean weDidNotChooseATurnWithAFreeMove = true;
        //Pick if some move equal the moveWithBestScore pick the one which will
        //give us a free turn:
        for(int[] move: movesWithFreeTurn) {
            if (bestMoveMoveSoFar[storeIndex] == move[storeIndex]) {
                bestMoveMoveSoFar = move;
                weDidNotChooseATurnWithAFreeMove = false;
            }
        }
        if (weDidNotChooseATurnWithAFreeMove) {
            //Check (if any) prevents of steals are worth it:
            for (int[] move : movesWhichPreventSteals) {
                //Check store of the opponent if we give him the opportunity to steal
                int opponentChoiceForSteal;
                if (!opponentChoicesToSteal.isEmpty()) {
                    opponentChoiceForSteal = getOpponentChoiceForSteal(move).intValue();
                    if (bestMoveMoveSoFar[storeIndex] >
                            makeMoveFrom(opponentBoardsBeforeSteal.get(opponentChoiceForSteal),
                                    opponentChoiceForSteal,
                                    true, getOtherPlayerStoreIndex(storeIndex))[getOtherPlayerStoreIndex(storeIndex)]) {
                        bestMoveMoveSoFar = move;
                    }
                }
            }
        }

        bestMove = bestMoveMoveSoFar;
    }

    public int[] getMoveWithBestStore() {
        int[] bestYet = sevenStates[0];
        for(int i = 1; i < sevenStates.length; ++i) {
            if (bestYet[storeIndex] < sevenStates[i][storeIndex]) {
                bestYet = sevenStates[i];
            }
        }
        return bestYet;
    }

    public boolean getsFreeMoveWith(int craterIndex, int stones, int storeIndex) {
        if (getLastIndex(craterIndex, stones) == storeIndex) return true;
        return false;
    }

    public boolean performsSteal(int[] board, int craterIndex, int stones, int storeIndex) {
        board = board.clone();
        int lastIndex = getLastIndex(craterIndex, stones);
        board = makeMoveFrom(board, craterIndex, false, storeIndex);
        //Check condition:
        if (isIndexOnTheCorrectSide(lastIndex, craterIndex, storeIndex) &&
                board[lastIndex] == 0 &&
                board[16 - lastIndex] > stones) return true;
        return false;
    }

    public boolean preventsSteal(int[] board, int craterIndex, boolean query, int storeIndex) {
        int[] screenBoardCopy = makeMoveFrom(board.clone(), craterIndex, true, storeIndex);

        int startIndex;
        int lengthPosition;
        boolean foundAtLeastOne = false;

        if (getOtherPlayerStoreIndex(storeIndex) == 8) {
            startIndex = 1;
            lengthPosition = 8;
        } else {
            startIndex = 9;
            lengthPosition = 16;
        }

        for(int i = startIndex; i < lengthPosition; ++i) {
            if (performsSteal(screenBoardCopy, i, screenBoardCopy[i], getOtherPlayerStoreIndex(storeIndex))) {
                if (!query) opponentChoicesToSteal.put(currentState, i);
                foundAtLeastOne = true;
            }
        }
        if (foundAtLeastOne) return false;
        else return true;
    }

    public boolean isIndexOnTheCorrectSide(int index, int choice, int storeIndex) {
        if (storeIndex == 8) return index < 8 && 0 < index;
        else return index > 8 && 16 > index;
    }

    public int getLastIndex(int craterIndex, int stones) {
        int tempLastIndex = (craterIndex + stones);
        int lastIndex = 0;
        //Get to the last index:
        if (tempLastIndex >= 16) {
            //Do some working out to derive the last index.
            for (int i = 0; i <= tempLastIndex; ++i) {
                if (i == 16) lastIndex = 0;
                else ++lastIndex;
            }
        } else return tempLastIndex;
        return lastIndex;
    }

    public boolean choiceBelongsToOtherPlayer(int choice) {
        return choice < 8 && choice > 0;
    }

    public int getOtherPlayerStoreIndex(int storeIndex) {
        if (storeIndex == 0) return 8;
        else return 0;
    }

    public Crater[] getButtonChoices() {return buttonChoices;}

    public int[][] getSevenStates() {return sevenStates;}

    public int getStoreIndex() {
        return storeIndex;
    }

    public void setSevenStates(int[][] states) {sevenStates = states;}

    public void setButtonChoices() {buttonChoices = getStore().getTruePlayerCraters(SimpleAI.this);}

    public void setStoreIndex(int index) {storeIndex = index;}


    public Crater getBestCrater() {
        return (Crater) searchThrough(moveGeneratedFrom.entrySet().iterator(), bestMove);
    }

    public Integer getOpponentChoiceForSteal(int[] key) {
        return (Integer) searchThrough(opponentChoicesToSteal.entrySet().iterator(), key);
    }

    public Object searchThrough(Iterator it, int[] key) {
        boolean isEquals;
        while (it.hasNext()){
            HashMap.Entry pair = (HashMap.Entry) it.next();
            isEquals = true;
            for (int i = 0; i < key.length; i++)
                if ( ((int[]) pair.getKey())[i] != key[i] ) {
                    isEquals = false;
                    break;
                }
            if (isEquals){
                return pair.getValue();
            }
        }
        return null;
    }

    public void clearCollections() {
//        moveGeneratedFrom = new HashMap<>();
//        movesWithFreeTurn = new ArrayList<>();
//        movesWhichPreventSteals = new ArrayList<>();
//        opponentChoicesToSteal = new HashMap<>();
//        opponentBoardsBeforeSteal = new HashMap<>();
        sevenStates = new int[7][16];
        moveGeneratedFrom.clear();
        movesWithFreeTurn.clear();
        movesWhichPreventSteals.clear();
        opponentChoicesToSteal.clear();
        opponentBoardsBeforeSteal.clear();
    }

}
