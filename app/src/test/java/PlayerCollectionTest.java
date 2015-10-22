import org.junit.Test;
import org.junit.Before;
import toucan.sunka.Player;
import toucan.sunka.PlayerCollection;
import static org.junit.Assert.*;

public class PlayerCollectionTest{

    private PlayerCollection pC;


    @Before
    public void init(){
        pC = new PlayerCollection();
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
    }

    @Test
    public void testFindPlayer(){
        Player player = pC.findPlayer("John");
        Player inexistentPlayer = pC.findPlayer("Tim");
        assertTrue("John".equals(player.getPlayerName()));
        assertTrue( inexistentPlayer == null );
    }
}