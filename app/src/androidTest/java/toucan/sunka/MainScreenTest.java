package toucan.sunka;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;

import org.junit.Test;

import java.security.KeyException;

import static org.junit.Assert.*;

/**
 * Created by andrei on 19/10/15.
 */
public class MainScreenTest extends ActivityInstrumentationTestCase2<MainScreen> {

    MainScreen mainScreen;

    public MainScreenTest() {
        super(MainScreen.class);
        mainScreen = getActivity();
    }

    //
    @Test
    public void testScreenExists() {
        assertNotNull(mainScreen);
    }

    // Function test template
    public void testRandomMethod() throws Exception {
        // Calling a method from main screen
        int randomNumber = mainScreen.randomMethod();

        // Pressing a button
        Button singlePlayer = (Button) mainScreen.findViewById(R.id.singlePlayer);
        TouchUtils.clickView(this, singlePlayer);

        //Focusing a edit text
        final EditText editText = null;

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                editText.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("String");
    }
}