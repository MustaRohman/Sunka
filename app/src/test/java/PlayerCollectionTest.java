import org.junit.Test;
import org.junit.Before;
import toucan.sunka.Player;
import toucan.sunka.PlayerCollection;
import static org.junit.Assert.*;

public class PlayerCollectionTest{

    private PlayerCollection pC;



    @Before
    public void init(){
        pC = PlayerTest.initializePlayers();
    }

    @Test
    public void testFindPlayer(){
        Player player = pC.findPlayer("John");
        Player inexistentPlayer = pC.findPlayer("Tim");
        assertTrue("John".equals(player.getPlayerName()));
        assertTrue( inexistentPlayer == null );
    }
}