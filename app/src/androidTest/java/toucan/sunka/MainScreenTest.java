package toucan.sunka;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;


import java.security.KeyException;

import static org.junit.Assert.*;

/**
 * Created by andrei on 19/10/15.
 */

// Every test class extends ActivityInstrumentationTestCase2<MainScreen>
public class MainScreenTest extends ActivityInstrumentationTestCase2<MainScreen> {

    MainScreen mainScreen;
    int example;

    // Constructor
    public MainScreenTest() {
        super(MainScreen.class);
    }

    // Method used for initialization, calls itself before every Test method
    // Method super.setUp() calls the constructor from ActivityInstrumentationTest
    public void setUp() throws Exception{
        super.setUp();
        mainScreen = getActivity(); // Gets the activity under testing.
                                    // Each test needs to have this called before!
    }

    //This is a basic test to see if JUnit is configured properly
    public void testScreenExists() {
        assertNotNull(mainScreen);
    }

    // Testing the output of a method
    public void testRandomMethod() {
        example = mainScreen.randomMethod();
        assertEquals(example, 5);
    }


    public static PlayerCollection initialiseCollection()
    {
        PlayerCollection pC = new PlayerCollection();
        Player p1 = new Player("John");
        p1.setGamesWon(1);
        Player p2 = new Player("Beth");
        Player p3 = new Player("Simon");
        p3.setGamesWon(5);
        Player p4 = new Player("Jane");
        p4.setGamesWon(100);
        pC.addPlayer(p1);
        pC.addPlayer(p2);
        pC.addPlayer(p3);
        pC.addPlayer(p4);
        return pC;
    }
    public void testSort()
    {
        PlayerCollection pC = initialiseCollection();
        Object[] tempArray = pC.getAllPlayers().toArray();
        pC.sortByGamesWon();
        Object[] sortedList = pC.getAllPlayers().toArray();

        Object[] expected = {tempArray[3], tempArray[2], tempArray[0], tempArray[1]};
        assertArrayEquals(expected, sortedList);
    }
    public void testFindPlayer()
    {
        PlayerCollection pC = initialiseCollection();
        Player player = pC.findPlayer("John");
        assertTrue("John".equals(player.getPlayerName()));
    }
    public void testFindPlayerFalse()
    {
        PlayerCollection pC = initialiseCollection();
        Player player = pC.findPlayer("Tim");
        assertTrue(player == null);
    }

    public static Crater[] initialiseBoard()
    {
        Player player1 = new Player("John");
        Player player2 = new Player("Tim");
        player1.setPlayingTurnTo(true);

        Crater[] board = new Crater[16];
        for(int i = 0; i < 16; ++i)
        {
            if (i != 0 && i != 8)
            {
                board[i] = new Crater(false);
            }
            else
            {
                board[i] = new Crater(true);
            }
            if (i > 0 && i < 9)
            {
                board[i].setOwner(player2);
            }
            else
            {
                board[i].setOwner(player1);
            }
        }

        for(int i = 0; i < 16; ++i)
        {
            if (i != 15)
            {
                board[i].setNextCrater(board[i + 1]);
            }
            else
            {
                board[i].setNextCrater(board[0]);
            }
        }

        for(int i = 0; i < 16; ++i)
        {
            if (i != 0 && i != 8)
            {
                board[i].setOppositeCrater(board[16 - i]);
            }
        }

        player1.setStore(board[0]);
        player2.setStore(board[8]);
        return board;
    }

    public void testPlaceAlong()
    {
        Crater[] board = initialiseBoard();
        board[10].setStones(1);
        board[11].setStones(0);
        board[5].setStones(5);
        board[8].setStones(0);
        board[0].setStones(0);

        board[10].placeAlong(board[10].getStones());
        board[10].setStones(0);

        int[] result = {board[10].getStones(), board[11].getStones(), board[5].getStones(), board[0].getStones()};
        int[] expected = {0, 0, 0, 6};

        assertArrayEquals(expected, result);
    }
}