package toucan.sunka;

public class Player {

    private String playerName;
    private int gamesWon;
    private int gamesLost;
    private Crater store;
    private int playerRank;

    public Player(String pN)
    {
        playerName = pN;
        gamesWon = 0;
        gamesLost = 0;
        playerRank = 0;
    }

    public void setGamesWon(int gW)
    {
        gamesWon = gW;
    }

    public void setGamesLost(int gL)
    {
        gamesLost = gL;
    }

    public void setPlayerRank(int pR)
    {
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
}
