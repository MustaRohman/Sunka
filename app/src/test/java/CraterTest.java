import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import toucan.sunka.Crater;
import toucan.sunka.Player;

import static org.junit.Assert.*;

public class CraterTest{

    Crater regularCrater, storeCrater;
    Crater[] board;

    @Before
    public void init(){
        regularCrater = new Crater(false);
        storeCrater = new Crater(true);
        board = initialiseBoard();
        board[10].setStones(1);
        board[11].setStones(0);
        board[5].setStones(5);
        board[8].setStones(0);
        board[0].setStones(0);
    }

    public Crater[] initialiseBoard() {

        Player player1 = new Player("John");
        Player player2 = new Player("Tim");
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

        for(int i = 0; i < 16; ++i) {
            if (i != 15)
                board[i].setNextCrater(board[i + 1]);
            else
                board[i].setNextCrater(board[0]);
        }

        for(int i = 0; i < 16; ++i) {
            if (i != 0 && i != 8)
                board[i].setOppositeCrater(board[16 - i]);
        }

        player1.setStore(board[0]);
        player2.setStore(board[8]);
        return board;
    }


    //A crater should be both able to hold 7 stones and 0 stones in case it is a store
    @Test
    public void testCreation(){
        assertEquals(regularCrater.getStones(), 7);
        assertEquals(storeCrater.getStones(), 0);
    }

    //When a user chooses a crater the stones should move according to the rules
    @Test
    public void testPlaceAlong() {
        board[10].placeAlong(board[10].getStones());
        board[10].setStones(0);
        int[] result = {board[10].getStones(), board[11].getStones(), board[5].getStones(), board[0].getStones()};
        int[] expected = {0, 0, 0, 6};
        assertArrayEquals(expected, result);
    }

}