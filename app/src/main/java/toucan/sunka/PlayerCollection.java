package toucan.sunka;

import java.util.ArrayList;
import java.util.Collections;

public class PlayerCollection {

    private ArrayList<Player> players;

    public PlayerCollection() {
        players = new ArrayList<Player>();
    }

    public void addPlayer(Player p) {
        players.add(p);
    }

    public ArrayList<Player> getAllPlayers() {
        return players;
    }

    public Player findPlayer(String n) {
        for(Player player : players)
            if (player.getPlayerName().equals(n))
                return player;
        return null;
    }

    public void sortByGamesWon() {
        if (!players.isEmpty())
            Collections.sort(players, Player.PlayerScoreComparator);
    }
}
