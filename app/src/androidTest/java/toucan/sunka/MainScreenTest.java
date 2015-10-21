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

    public void testSort()
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
        pC.sortByGamesWon();
        Object[] sortedList = pC.getAllPlayers().toArray();
        Player[] expected = {p4, p3, p1, p2};
        assertArrayEquals(expected, sortedList);
    }
}