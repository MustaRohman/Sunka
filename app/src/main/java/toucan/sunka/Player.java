package toucan.sunka;

import java.util.Comparator;

public class Player {

    private String playerName;
    private int gamesWon;
    private int gamesLost;
    private Crater store;
    private int playerRank;
    private boolean playingTurn;

    public Player(String pN) {
        playerName = pN;
        gamesWon = 0;
        gamesLost = 0;
        playerRank = -1;
        playingTurn = false;
    }

    public void setGamesWon(int gW) {
        gamesWon = gW;
    }

    public void setPlayingTurnTo(boolean b)
    {
        playingTurn = b;
    }

    public void setGamesLost(int gL) {
        gamesLost = gL;
    }

    public void setPlayerRank(int pR) {
        playerRank = pR;
    }

    public void setStore(Crater crater){
        store = crater;
    }

    public String getPlayerName()
    {
        return playerName;
    }

    public int getNumberOfGamesWon()
    {
        return gamesWon;
    }

    public int getNumberOfGamesLost()
    {
        return gamesLost;
    }

    public int getPlayerRank()
    {
        return playerRank;
    }

    public Crater getStore(){
        return store;
    }

    public boolean isPlayingTurn()
    {
        return playingTurn;
    }

    public static Comparator<Player> PlayerScoreComparator = new Comparator<Player>()
    {
        public int compare(Player p1, Player p2)
        {
            int score1 = p1.getNumberOfGamesWon();
            int score2 = p2.getNumberOfGamesWon();

            return score2 - score1;
        }
    };

    public String toString() {
        return playerName + "\n" +
                playerRank + "\n" +
                gamesWon + "\n" +
                gamesLost;
    }
}
