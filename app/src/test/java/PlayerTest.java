import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

import toucan.sunka.Player;
import toucan.sunka.PlayerCollection;


public class PlayerTest{

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
    public void testSort(){
        Object[] tempArray = pC.getAllPlayers().toArray();
        pC.sortByGamesWon();
        Object[] sortedList = pC.getAllPlayers().toArray();

        Object[] expected = {tempArray[3], tempArray[2], tempArray[0], tempArray[1]};
        assertArrayEquals(expected, sortedList);
    }
}