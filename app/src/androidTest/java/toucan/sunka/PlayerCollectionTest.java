package toucan.sunka;

import org.junit.*;
import static org.junit.Assert.*;

public class PlayerCollectionTest {

    @Test
    public void sortTest()
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
        Player[] sortedList = (Player[]) pC.getAllPlayers().toArray();
        Player[] expected = {p4, p3, p1, p2};
        assertArrayEquals(expected, sortedList);
    }
}
