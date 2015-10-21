package toucan.sunka;

import java.util.ArrayList;

public class PlayerCollection {

    private ArrayList<Player> players;

    public PlayerCollection()
    {
        players = new ArrayList<Player>();
    }

    public void addPlayer(Player p)
    {
        players.add(p);
    }

    public Player findPlayer(String n)
    {
        for(Player player : players)
        {
            if (player.getPlayerName().equals(n))
            {
                return player;
            }
        }

        return null;
    }
}
