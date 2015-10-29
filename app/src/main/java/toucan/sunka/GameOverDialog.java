package toucan.sunka;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by mustarohman on 28/10/2015.
 */
public class GameOverDialog extends DialogFragment {

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

        table = (TableLayout) layoutView.findViewById(R.id.leaderboard_table);
        addRow("2nd", "Player1", "0023");
        addRow("3nd", "Player2", "0020");

        builder.setView(layoutView);
        thisActivity = (TwoPlayerLocalActivity) this.getActivity();
        builder.setPositiveButton(R.string.play_again, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Player playerOne = thisActivity.getFirstPlayer();
                Player playerTwo = thisActivity.getSecondPlayer();
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
        TextView playerOneText = (TextView) layoutView.findViewById(R.id.player_one_name_leaderboard);
        playerOneText.setText(playerBundle.getString(PLAYER_ONE_KEY));

        TextView playerTwoText = (TextView) layoutView.findViewById(R.id.player_two_name_leaderboard);
        playerTwoText.setText(playerBundle.getString(PLAYER_TWO_KEY));


//        Will also set the wins and stones for each player
    }

    /**
     * Takes in the player's rank name and score and creates and adds a row in the table
     * representing the player info
     */
    private void addRow(String rank, String name, String score){

        View rowView = inflater.inflate(R.layout.leaderboard_row, null);
        TextView rankText = (TextView) rowView.findViewById(R.id.player_rank);
        rankText.setText(rank);

        TextView nameText = (TextView) rowView.findViewById(R.id.player_name);
        nameText.setText(name);

        TextView scoreText = (TextView) rowView.findViewById(R.id.player_score);
        scoreText.setText(rank);

        table.addView(rowView);


    }


}
