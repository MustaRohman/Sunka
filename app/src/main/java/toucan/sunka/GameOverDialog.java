package toucan.sunka;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

/**
 * Created by mustarohman on 28/10/2015.
 */
public class GameOverDialog extends DialogFragment {

    public static final String PLAYER_ONE_WINS = "PLAYER_ONE_WINS";
    public static final String PLAYER_TWO_WINS = "PLAYER_TWO_WINS";

    public static final String PLAYER_ONE_STONES = "PLAYER_ONE_STONES";
    public static final String PLAYER_TWO_STONES = "PLAYER_TWO_STONES";

    private Player playerOne;
    private Player playerTwo;
    Player victorPlayer;
    Player loserPlayer;
    private TableLayout table;
    private TwoPlayerLocalActivity thisActivity;
    private LayoutInflater inflater;
    private Bundle playerBundle;
    private View layoutView;
    public static final String PLAYER_ONE_KEY = "PLAYER_ONE_KEY";
    public static final String PLAYER_TWO_KEY = "PLAYER_TWO_KEY";


    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        inflater = getActivity().getLayoutInflater();
        layoutView = inflater.inflate(R.layout.dialog_game_over, null);

        initialisePlayerInfo();

        initialiseLeaderboard();

        builder.setView(layoutView);
        thisActivity = (TwoPlayerLocalActivity) this.getActivity();
        builder.setPositiveButton(R.string.play_again, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                playerOne = thisActivity.getFirstPlayer();
                playerTwo = thisActivity.getSecondPlayer();
                Context context = getContext();
                Intent newTwoPlayerGame = new Intent(context, TwoPlayerLocalActivity.class);
                newTwoPlayerGame.putExtra(PLAYER_ONE_KEY, playerOne);
                newTwoPlayerGame.putExtra(PLAYER_TWO_KEY, playerTwo);
                context.startActivity(newTwoPlayerGame);
            }
        });
        builder.setNegativeButton(R.string.main_menu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Context context = getContext();
                Intent backToMainMenu = new Intent(context, MainScreen.class);
                context.startActivity(backToMainMenu);
            }
        });
        return builder.create();

    }


    private void initialisePlayerInfo(){

        playerBundle = getArguments();

        int p1Stones = Integer.parseInt(playerBundle.getString(PLAYER_ONE_STONES));
        int p2Stones = Integer.parseInt(playerBundle.getString(PLAYER_TWO_STONES));

        //Initialises victorPlayer with the victor of the current game
        if (p1Stones > p2Stones){
            victorPlayer = MainScreen.collection.findPlayer(playerBundle.getString(PLAYER_ONE_KEY));
            loserPlayer = MainScreen.collection.findPlayer(playerBundle.getString(PLAYER_TWO_KEY));
        } else {
            victorPlayer = MainScreen.collection.findPlayer(playerBundle.getString(PLAYER_TWO_KEY));
            loserPlayer = MainScreen.collection.findPlayer(playerBundle.getString(PLAYER_ONE_KEY));
        }

        //Updates the victor's wins and resorts the collection
        victorPlayer.setGamesWon(victorPlayer.getNumberOfGamesWon() + 1);
        loserPlayer.setGamesLost(loserPlayer.getNumberOfGamesLost() + 1);
        MainScreen.collection.sortByGamesWon();

        //Sets win message
        TextView winTitle = (TextView) layoutView.findViewById(R.id.win_title);
        winTitle.setText(victorPlayer.getPlayerName() + " Has Won!");

        //Sets player names
        TextView p1NameText = (TextView) layoutView.findViewById(R.id.player_one_name_leaderboard);
        p1NameText.setText(playerBundle.getString(PLAYER_ONE_KEY));

        TextView p2NameText = (TextView) layoutView.findViewById(R.id.player_two_name_leaderboard);
        p2NameText.setText(playerBundle.getString(PLAYER_TWO_KEY));

        //Sets player wins
        TextView p1WinsText = (TextView) layoutView.findViewById(R.id.player_one_wins);
        p1WinsText.setText(playerBundle.getString(PLAYER_ONE_WINS));

        TextView p2WinsText = (TextView) layoutView.findViewById(R.id.player_two_wins);
        p2WinsText.setText(playerBundle.getString(PLAYER_TWO_WINS));

        //Sets player stones
        TextView p1StonesText = (TextView) layoutView.findViewById(R.id.player_one_stones);
        p1StonesText.setText(playerBundle.getString(PLAYER_ONE_STONES));

        TextView p2StonesText = (TextView) layoutView.findViewById(R.id.player_two_stones);
        p2StonesText.setText(playerBundle.getString(PLAYER_TWO_STONES));


    }

    private void initialiseLeaderboard(){

        table = (TableLayout) layoutView.findViewById(R.id.leaderboard_table);


        Player player1st = MainScreen.collection.getPlayerAtPosition(0);
        Player player2nd = MainScreen.collection.getPlayerAtPosition(1);
        addRow(player1st.getPlayerRank(), player1st.getPlayerName(),
                player1st.getNumberOfGamesWon(),player1st.getNumberOfGamesLost(), table, inflater);
        addRow(player2nd.getPlayerRank(), player2nd.getPlayerName(),
                player2nd.getNumberOfGamesWon(),player2nd.getNumberOfGamesLost() ,table, inflater);
        Player player3rd;
        try{
            player3rd = MainScreen.collection.getPlayerAtPosition(2);
            addRow(player3rd.getPlayerRank(), player3rd.getPlayerName(),
                    player3rd.getNumberOfGamesWon(),player3rd.getNumberOfGamesLost(), table, inflater);}
        catch(Exception e){
            player3rd = null;
        }

        Log.d("GameOverDialog", String.valueOf(victorPlayer.getNumberOfGamesWon()));

        if (victorPlayer.getPlayerRank() > 3){

            addBlankRow(table, inflater);


        }

    }

    /**
     * Takes in the player's rank, name and score, TableLayout and LayoutInflater and adds a row in the
     * provided TableLayout
     */
    public static void addRow(Integer rank, String name, Integer score,Integer losses, TableLayout table, LayoutInflater inflater){

        View rowView = inflater.inflate(R.layout.leaderboard_row, null);
        TextView rankText = (TextView) rowView.findViewById(R.id.player_rank);
        rankText.setText(String.valueOf(rank));

        TextView nameText = (TextView) rowView.findViewById(R.id.player_name);
        nameText.setText(name);

        TextView scoreText = (TextView) rowView.findViewById(R.id.player_score);
        scoreText.setText(String.valueOf(score));

        TextView lossesText = (TextView) rowView.findViewById(R.id.player_losses);
        lossesText.setText(String.valueOf(losses));

        table.addView(rowView);


    }

    public static void addBlankRow(TableLayout table, LayoutInflater inflater){

        View rowView = inflater.inflate(R.layout.leaderboard_row, null);

        TextView rankText = (TextView) rowView.findViewById(R.id.player_rank);
        rankText.setText("------");

        TextView nameText = (TextView) rowView.findViewById(R.id.player_name);
        nameText.setText("---------");

        TextView scoreText = (TextView) rowView.findViewById(R.id.player_score);
        scoreText.setText(String.valueOf("------"));

        table.addView(rowView);


    }


}
