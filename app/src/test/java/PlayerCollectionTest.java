import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;

import java.io.File;

import toucan.sunka.Player;
import toucan.sunka.PlayerCollection;
import static org.junit.Assert.*;

public class PlayerCollectionTest {

    private PlayerCollection pC;
    private File startLocation;

    @Before
    public void init() {
        pC = PlayerTest.initializePlayers();
        startLocation = new File(System.getProperty("user.dir"));
    }

    @After
    public void clearUp() {
        try {
            File playerDatabase = new File(System.getProperty("user.dir") + "\\GameData\\PlayerDatabase.pd");
            playerDatabase.delete(); // First the file
            playerDatabase = playerDatabase.getParentFile();
            playerDatabase.delete(); // Second the empty directory
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFindPlayer() {
        Player player = pC.findPlayer("John");
        Player inexistentPlayer = pC.findPlayer("Tim");
        assertTrue("John".equals(player.getPlayerName()));
        assertTrue( inexistentPlayer == null );
    }

    @Test
    public void testFileStoring1() {
        String expected = pC.toString();

        doFileWritingAndReading();
        String result = pC.toString();

        assertTrue(expected.equals(result));
    }

    public void doFileWritingAndReading() {
        pC.savePlayerInfoToFile(startLocation);
        pC.loadPlayerInfoFromFile(startLocation);
    }

    @Test
    public void testFileStoring2() {
        Player newP1 = new Player("Kate");
        Player newP2 = new Player("Lynn");

        pC.addPlayer(newP1);
        pC.addPlayer(newP2);

        String expected = pC.toString();

        doFileWritingAndReading();
        String result = pC.toString();

        assertTrue(expected.equals(result));
    }
}