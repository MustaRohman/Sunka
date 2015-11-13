import org.junit.Before;
import org.junit.Test;

import toucan.sunka.Crater;
import toucan.sunka.Player;

import static org.junit.Assert.*;

public class AITest {

    Crater regularCrater, storeCrater;
    Crater[] board;
    Player player1 = new Player("John");
    Player player2 = new Player("Tim");

    @Before
    public void init() {
        regularCrater = new Crater(false);
        storeCrater = new Crater(true);
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
                board[i].setOwner(player2);
            else
                board[i].setOwner(player1);
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

        player1.setStore(board[0]);
        player2.setStore(board[8]);
        board[0].setOppositeCrater(board[8]);
        board[8].setOppositeCrater(board[0]);
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

}
