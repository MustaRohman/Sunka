import org.junit.Before;
import org.junit.Test;

import toucan.sunka.Crater;
import toucan.sunka.Player;
import toucan.sunka.SimpleAI;

import static org.junit.Assert.*;

public class AITest {

    Crater regularCrater, storeCrater;
    Crater[] board;
    Player player1 = new Player("John");
    SimpleAI aiPlayer;

    @Before
    public void init() {
        Player player2 = new Player("Tim");
        regularCrater = new Crater(false);
        storeCrater = new Crater(true);
        aiPlayer = new SimpleAI(player2);
        board = initialiseBoard();
    }

    public Crater[] initialiseBoard() {
        player1.setPlayingTurnTo(true);

        Crater[] board = new Crater[16];
        for (int i = 0; i < 16; ++i) {
            if (i != 0 && i != 8)
                board[i] = new Crater(false);
            else
                board[i] = new Crater(true);
            if (i > 0 && i < 9)
                board[i].setOwner(player1);
            else
                board[i].setOwner(aiPlayer);
        }

        board[15].setNextCrater(board[0]);
        for (int i = 0; i < 15; ++i) {
            board[i].setNextCrater(board[i + 1]);
        }
        board[0].setOppositeCrater(board[8]);
        board[8].setOppositeCrater(board[0]);
        for (int i = 0; i < 16; ++i) {
            if (i != 0 && i != 8)
                board[i].setOppositeCrater(board[16 - i]);

        }

        player1.setStore(board[8]);
        aiPlayer.setStore(board[0]);
        board[0].setOppositeCrater(board[8]);
        board[8].setOppositeCrater(board[0]);
        aiPlayer.setButtonChoices();
        aiPlayer.setStoreIndex(0);
        return board;
    }

    public int[] getResultsFrom(Crater[] board) {
        int[] result = {
                board[0].getStones(), board[1].getStones(), board[2].getStones(),
                board[3].getStones(), board[4].getStones(), board[5].getStones(),
                board[6].getStones(), board[7].getStones(), board[8].getStones(),
                board[9].getStones(), board[10].getStones(), board[11].getStones(),
                board[12].getStones(), board[13].getStones(), board[14].getStones(),
                board[15].getStones()};
        return result;
    }

    @Test
    public void TestGetArrayBoard1() {
        int[] result = board[5].getArrayBoard(board[5]);
        int[] expected = {7, 7, 7, 0, 7, 7, 7, 7, 7, 7, 7, 0, 7, 7, 7, 7};

        assertArrayEquals(expected, result);
    }

    @Test
    public void TestGetArrayBoard2() {
        board[2].setStones(10);
        board[6].setStones(6);
        board[9].setStones(15);
        board[14].setStones(3);
        board[15].setStones(8);

        int[] result = board[5].getArrayBoard(board[5]);
        int[] expected = {7, 6, 7, 0, 15, 7, 7, 7, 7, 3, 8, 0, 7, 10, 7, 7};

        assertArrayEquals(expected, result);
    }

    @Test
    public void TestGetArrayBoard3() {
        board[0].setStones(12);
        board[5].setStones(5);
        board[8].setStones(15);
        board[13].setStones(10);
        board[15].setStones(8);

        int[] result = board[10].getArrayBoard(board[10]);
        int[] expected = {7, 7, 7, 10, 7, 8, 12, 7, 7, 7, 7, 5, 7, 7, 15, 7};

        assertArrayEquals(expected, result);
    }

    @Test
    public void TestGetArrayBoard4() {
        board[0].setStones(21);
        board[2].setStones(3);
        board[3].setStones(10);
        board[7].setStones(12);
        board[8].setStones(37);
        board[10].setStones(9);
        board[13].setStones(16);

        int[] result = board[0].getArrayBoard(board[0]);
        int[] expected = {21, 7, 3, 10, 7, 7, 7, 12, 37, 7, 9, 7, 7, 16, 7, 7};

        assertArrayEquals(expected, result);
    }

    @Test
    public void getsFreeMoveTest1() {
        assertTrue(aiPlayer.getsFreeMoveWith(15, 1, 0));
    }

    @Test
    public void makeMoveTest1() {
        int[] testBoard = {0, 7, 7, 7, 7, 8, 7, 7, 0, 7, 1, 0, 7, 7, 7, 7};
        int choice = 10;
        int[] expected = {9, 7, 7, 7, 7, 0, 7, 7, 0, 7, 0, 0, 7, 7, 7, 7};
        int [] result = aiPlayer.makeMoveFrom(testBoard, choice, true, aiPlayer.getStoreIndex());
        assertArrayEquals(expected, result);
    }

    @Test
    public void makeMoveTest2() {
        int[] testBoard = {0, 7, 7, 7, 20, 7, 7, 7, 0, 7, 2, 7, 0, 7, 7, 7};
        int choice = 10;
        int[] expected = {21, 7, 7, 7, 0, 7, 7, 7, 0, 7, 0, 8, 0, 7, 7, 7};
        int [] result = aiPlayer.makeMoveFrom(testBoard, choice, true, aiPlayer.getStoreIndex());
        assertArrayEquals(expected, result);
    }

    @Test
    public void performsSteal1() {
        int[] testBoard = {0, 7, 7, 7, 20, 7, 7, 7, 0, 7, 2, 7, 0, 7, 7, 7};
        assertTrue(aiPlayer.performsSteal(testBoard, 10, 2, aiPlayer.getStoreIndex()));
    }

    @Test
    public void performsSteal2() {
        int[] testBoard = {21, 7, 3, 10, 7, 7, 7, 12, 37, 7, 9, 7, 7, 16, 7, 7};
        assertFalse(aiPlayer.performsSteal(testBoard, 10, 2, aiPlayer.getStoreIndex()));
    }

    @Test
    public void preventsSteal1() {
        int[] testBoard = {21, 6, 3, 10, 7, 7, 7, 0, 37, 9, 5, 7, 7, 16, 7, 7};
        assertFalse(aiPlayer.canOpponentStealThisIndex(testBoard, 10, true, 0));
    }

    @Test
    public void preventsSteal2() {
        int[] testBoard = {21, 6, 3, 10, 7, 7, 7, 12, 37, 6, 9, 7, 7, 16, 7, 0};
        assertTrue(aiPlayer.canOpponentStealThisIndex(testBoard, 1, true, 8));
    }

    @Test
    public void preventsSteal3() {
        int[] testBoard = {21, 6, 4, 10, 7, 7, 7, 0, 37, 6, 9, 7, 7, 16, 7, 7};
        assertFalse(aiPlayer.canOpponentStealThisIndex(testBoard, 10, true, 0));
    }

    @Test
    public void generateSevenStates() {
        aiPlayer.generateSevenStates();
        int[][] result = aiPlayer.getSevenStates();
        int[][] expected = new int[7][16];
        int[] tempExpected0 = {1, 7, 7, 7, 7, 7, 7, 7, 0, 0, 8, 8, 8, 8, 8, 8};
        expected[0] = tempExpected0;
        int[] tempExpected1 = {1, 8, 7, 7, 7, 7, 7, 7, 0, 7, 0, 8, 8, 8, 8, 8};
        expected[1] = tempExpected1;
        int[] tempExpected2 = {1, 8, 8, 7, 7, 7, 7, 7, 0, 7, 7, 0, 8, 8, 8, 8};
        expected[2] = tempExpected2;
        int[] tempExpected3 = {1, 8, 8, 8, 7, 7, 7, 7, 0, 7, 7, 7, 0, 8, 8, 8};
        expected[3] = tempExpected3;
        int[] tempExpected4 = {1, 8, 8, 8, 8, 7, 7, 7, 0, 7, 7, 7, 7, 0, 8, 8};
        expected[4] = tempExpected4;
        int[] tempExpected5 = {1, 8, 8, 8, 8, 8, 7, 7, 0, 7, 7, 7, 7, 7, 0, 8};
        expected[5] = tempExpected5;
        int[] tempExpected6 = {1, 8, 8, 8, 8, 8, 8, 7, 0, 7, 7, 7, 7, 7, 7, 0};
        expected[6] = tempExpected6;
        assertArrayEquals(expected[0], result[0]);
        assertArrayEquals(expected[1], result[1]);
        assertArrayEquals(expected[2], result[2]);
        assertArrayEquals(expected[3], result[3]);
        assertArrayEquals(expected[4], result[4]);
        assertArrayEquals(expected[5], result[5]);
        assertArrayEquals(expected[6], result[6]);
    }

    @Test
    public void testGetMoveWithBestScore() {
        int[][] states = new int[7][16];
        int[] state0 = {1, 7, 7, 7, 7, 7, 7, 7, 0, 0, 8, 8, 8, 8, 8, 8};
        states[0] = state0;
        int[] state1 = {2, 8, 7, 7, 7, 7, 7, 7, 0, 7, 0, 8, 8, 8, 8, 8};
        states[1] = state1;
        int[] state2 = {3, 8, 8, 7, 7, 7, 7, 7, 0, 7, 7, 0, 8, 8, 8, 8};
        states[2] = state2;
        int[] state3 = {4, 8, 8, 8, 7, 7, 7, 7, 0, 7, 7, 7, 0, 8, 8, 8};
        states[3] = state3;
        int[] state4 = {5, 8, 8, 8, 8, 7, 7, 7, 0, 7, 7, 7, 7, 0, 8, 8};
        states[4] = state4;
        int[] state5 = {6, 8, 8, 8, 8, 8, 7, 7, 0, 7, 7, 7, 7, 7, 0, 8};
        states[5] = state5;
        int[] state6 = {7, 8, 8, 8, 8, 8, 8, 7, 0, 7, 7, 7, 7, 7, 7, 0};
        states[6] = state6;
        aiPlayer.setSevenStates(states);
        int[] result = aiPlayer.getMoveWithBestStore();
        assertArrayEquals(state6, result);
    }

    @Test
    public void testGetBestCrater1() {
        aiPlayer.generateSevenStates();
        aiPlayer.generateBestMove();
        assertTrue(aiPlayer.getButtonChoices()[0] == aiPlayer.getBestCrater());
    }

//    @Test
//    public void testGenerateBestMove() {
//        int[] expected = {33, 7, 7, 7, 1, 0, 7, 7, 31, 0, 0, 0, 1, 1, 1, 2};
//        board[0].setStones(33);
//        board[4].setStones(1);
//        board[5].setStones(0);
//        board[8].setStones(31);
//        board[9].setStones(0);
//        board[10].setStones(0);
//        board[11].setStones(4);
//        board[12].setStones(0);
//        board[13].setStones(0);
//        board[14].setStones(0);
//        board[15].setStones(1);
//        aiPlayer.generateSevenStates();
//        aiPlayer.generateBestMove();
//        aiPlayer.getBestCrater().makeMoveFromHere();
//        int[] result = board[0].getArrayBoard(aiPlayer.getStore());
//        //assertArrayEquals(expected, result);
//        boolean b = board[11] == aiPlayer.getBestCrater();
//        String one = board[11].toString();
//        String two = aiPlayer.getBestCrater().toString();
//        assertTrue(b);
//    }


}
