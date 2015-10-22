package toucan.sunka;

import android.test.ActivityInstrumentationTestCase2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
}