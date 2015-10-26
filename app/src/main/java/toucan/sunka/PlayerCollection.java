package toucan.sunka;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

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

    public ArrayList<Player> getAllPlayers()
    {
        return players;
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

    public void sortByGamesWon()
    {
        if (!players.isEmpty())
        {
            Collections.sort(players, Player.PlayerScoreComparator);
        }
    }

    public void savePlayerInfoToFile(File directory)
    {
        try {
            File playerDatabase = new File(directory, "GameData\\PlayerDatabase.pd");
            playerDatabase.getParentFile().mkdir();
            playerDatabase.createNewFile();
            FileOutputStream fOut = new FileOutputStream(playerDatabase, true);
            fOut.write(getDataInFileFormat().getBytes());
            fOut.close();
        }
        catch (FileNotFoundException fNFE)
        {
            fNFE.printStackTrace();
        }
        catch (SecurityException sE)
        {
            sE.printStackTrace();
        }
        catch (IOException iOE)
        {
            iOE.printStackTrace();
        }
    }

    public void loadPlayerInfoFromFile(File directory)
    {
        try {
            File playerDatabase = new File(directory, "GameData\\PlayerDatabase.pd");
            playerDatabase.getParentFile().mkdir();
            playerDatabase.createNewFile();
            FileInputStream fIn = new FileInputStream(playerDatabase);

            int character;
            String fileData = "";

            while((character = fIn.read()) != -1)
            {
                fileData += Character.toString((char) character);
            }

            recoverDataFromFileFormat(fileData);
        }
        catch (FileNotFoundException fNFE)
        {
            fNFE.printStackTrace();
        }
        catch (SecurityException sE)
        {
            sE.printStackTrace();
        }
        catch (IOException iOE)
        {
            iOE.printStackTrace();
        }
    }

    public String getDataInFileFormat()
    {
        String data = "";

        for(Player p : players)
        {
            data += p + "\n\n";
        }

        return data;
    }

    public void recoverDataFromFileFormat(String data)
    {
        clearCollection();

        Scanner inPlayer = new Scanner(data);
        inPlayer.useDelimiter("\n\n");
        while(inPlayer.hasNext())
        {
            Scanner inData = new Scanner(inPlayer.next());
            inData.useDelimiter("\n");
            Player tempPlayer = new Player(inData.next());
            tempPlayer.setPlayerRank(Integer.parseInt(inData.next()));
            tempPlayer.setGamesWon(Integer.parseInt(inData.next()));
            tempPlayer.setGamesLost(Integer.parseInt(inData.next()));
            addPlayer(tempPlayer);
        }
    }

    public void clearCollection()
    {
        players.clear();
    }

    public String toString()
    {
        String playerDatabase = "";
        int counter = 1;
        for(Player p: players)
        {
            playerDatabase += "*** Player " + counter + " ***" + "\n" + p + "\n";
            ++counter;
        }

        return playerDatabase;
    }
}
