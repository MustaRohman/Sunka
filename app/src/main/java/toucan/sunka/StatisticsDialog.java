package toucan.sunka;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by M on 11/10/2015.
 */
public class StatisticsDialog extends DialogFragment {

    private TableLayout table;
    private LayoutInflater inflater;
    private View layoutView;

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        inflater = getActivity().getLayoutInflater();
        layoutView = inflater.inflate(R.layout.dialog_statistics,null);
        createTable();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());



        builder.setView(layoutView);
        builder.setNegativeButton("Main Menu", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) { }});
        return builder.create();
    }

    private void createTable() {
        initialiseLeaderBoard();
        table = (TableLayout) layoutView.findViewById(R.id.leaderboard_table);
        for(int i = 1;i < MainScreen.collection.size()+1;i++){
            Player player = MainScreen.collection.getPlayerAtPosition(i-1);
            addRowWithLosses(i, player.getPlayerName(),player.getNumberOfGamesWon(), player.getNumberOfGamesLost(), table, inflater );
        }
    }


    public void initialiseLeaderBoard(){
        /*Player mert = new Player("Mert");
        mert.setGamesWon(10);
        Player konstantin = new Player("Konstantin");
        konstantin.setGamesWon(5);
        Player andrei = new Player("Andrei");
        andrei.setGamesWon(7);
        Player musta = new Player("Musta");
        musta.setGamesWon(8);
        MainScreen.collection.addPlayer(mert);
        MainScreen.collection.addPlayer(konstantin);
        MainScreen.collection.addPlayer(andrei);
        MainScreen.collection.addPlayer(musta);*/
        ArrayList<Player> players = MainScreen.collection.getAllPlayers();
        Log.d("Size =", String.valueOf(players.size()));
        for(Player p : players){
            Log.d("Player X=",p.toString());
        }
        MainScreen.collection.sortByGamesWon();
    }
    public static void addRowWithLosses(Integer rank, String name, Integer score,Integer losses, TableLayout table, LayoutInflater inflater){

        View rowView = inflater.inflate(R.layout.statistics_row,null);
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

}
