import android.util.Log;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import toucan.sunka.Crater;
import toucan.sunka.Player;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CraterTest{

    Crater regularCrater, storeCrater;
    Crater[] board;
    Player player1 = new Player("John");
    Player player2 = new Player("Tim");

    @Before
    public void init(){
        regularCrater = new Crater(false);
        storeCrater = new Crater(true);
        board = initialiseBoard();
    }

    public Crater[] initialiseBoard() {


        player1.setPlayingTurnTo(true);

        Crater[] board = new Crater[16];
        for(int i = 0; i < 16; ++i) {
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
        for(int i = 0; i < 15; ++i) {
                board[i].setNextCrater(board[i + 1]);
        }
        board[0].setOppositeCrater(board[8]);
        board[8].setOppositeCrater(board[0]);
        for(int i = 0; i < 16; ++i) {
            if (i != 0 && i != 8)
                board[i].setOppositeCrater(board[16 - i]);

        }

        player1.setStore(board[0]);
        player2.setStore(board[8]);
        board[0].setOppositeCrater(board[8]);
        board[8].setOppositeCrater(board[0]);
        return board;
    }

    public int[] getResultsFrom(Crater[] board)
    {
        int[] result =  {
                board[0].getStones(), board[1].getStones(), board[2].getStones(),
                board[3].getStones(),board[4].getStones(), board[5].getStones(),
                board[6].getStones(), board[7].getStones(), board[8].getStones(),
                board[9].getStones(), board[10].getStones(), board[11].getStones(),
                board[12].getStones(), board[13].getStones(), board[14].getStones(),
                board[15].getStones()};
        return result;
    }
    //A crater should be both able to hold 7 stones and 0 stones in case it is a store
    @Test
    public void testCreation(){
        assertEquals(regularCrater.getStones(), 7);
        assertEquals(storeCrater.getStones(), 0);
    }

    // Steal
    @Test
    public void testPlaceAlong1() {
        board[10].setStones(1);
        board[11].setStones(0);
        board[5].setStones(5);
        board[8].setStones(0);
        board[0].setStones(0);

        board[10].makeMoveFromHere();

        int[] result = getResultsFrom(board);
        int[] expected = {
                6, 7, 7,
                7, 7, 0,
                7, 7, 0,
                7, 0, 0,
                7, 7, 7,
                7};
        assertArrayEquals(expected, result);
    }
    @Test
    public void testPlaceAlong2(){
        board[10].setStones(1);
        board[11].setStones(0);
        board[5].setStones(5);
        board[8].setStones(0);
        board[0].setStones(0);

        board[12].makeMoveFromHere();

        int[] result = getResultsFrom(board);
        int[] expected = {
                1, 8, 8,
                8, 7, 5,
                7, 7, 0,
                7, 1, 0,
                0, 8, 8,
                8};
        assertArrayEquals(expected, result);
    }
    @Test
    public void testPlaceAlong3(){
        board[15].setStones(9);

        board[15].makeMoveFromHere();

        int[] result = getResultsFrom(board);
        int[] expected = {
                1, 8, 8,
                8, 8, 8,
                8, 8, 0,
                8, 7, 7,
                7, 7, 7,
                0};
        assertArrayEquals(expected, result);
    }
    @Test
    public void testPlaceAlong4()
    {
        board[15].setStones(1);

        board[15].makeMoveFromHere();

        int[] result = getResultsFrom(board);
        int[] expected = {
                1, 7, 7,
                7, 7, 7,
                7, 7, 0,
                7, 7, 7,
                7, 7, 7,
                0};
        assertArrayEquals(expected, result);
    }

    @Test
    public void checkGameOverTest(){
        board[1].setStones(0);
        board[2].setStones(0);
        board[3].setStones(0);
        board[4].setStones(0);
        board[5].setStones(0);
        board[6].setStones(0);
        board[7].setStones(0);
        assertTrue(board[0].checkGameOver(board[0]));
        assertTrue(board[1].checkGameOver(board[1]));
        assertTrue(board[2].checkGameOver(board[2]));
        assertTrue(board[3].checkGameOver(board[3]));
        assertTrue(board[4].checkGameOver(board[4]));
        assertTrue(board[5].checkGameOver(board[5]));
        assertTrue(board[6].checkGameOver(board[6]));
        assertTrue(board[7].checkGameOver(board[7]));
        assertTrue(board[8].checkGameOver(board[8]));
        assertTrue(board[9].checkGameOver(board[9]));
        assertTrue(board[10].checkGameOver(board[10]));
        assertTrue(board[11].checkGameOver(board[11]));
        assertTrue(board[12].checkGameOver(board[12]));
        assertTrue(board[13].checkGameOver(board[13]));
        assertTrue(board[14].checkGameOver(board[14]));
        assertTrue(board[15].checkGameOver(board[15]));
        board[1].setStones(1);
        assertFalse(board[0].checkGameOver(board[0]));
        assertFalse(board[1].checkGameOver(board[1]));
        assertFalse(board[2].checkGameOver(board[2]));
        assertFalse(board[3].checkGameOver(board[3]));
        assertFalse(board[4].checkGameOver(board[4]));
        assertFalse(board[5].checkGameOver(board[5]));
        assertFalse(board[6].checkGameOver(board[6]));
        assertFalse(board[7].checkGameOver(board[7]));
        assertFalse(board[8].checkGameOver(board[8]));
        assertFalse(board[9].checkGameOver(board[9]));
        assertFalse(board[10].checkGameOver(board[10]));
        assertFalse(board[11].checkGameOver(board[11]));
        assertFalse(board[12].checkGameOver(board[12]));
        assertFalse(board[13].checkGameOver(board[13]));
        assertFalse(board[14].checkGameOver(board[14]));
        assertFalse(board[15].checkGameOver(board[15]));
     }
}