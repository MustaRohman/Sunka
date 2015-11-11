package toucan.sunka;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by mustarohman on 22/10/2015.
 */

public class MultiplayerDialogFragment extends DialogFragment {
    public static final String PLAYER_ONE_KEY = "PLAYER_ONE_KEY";
    public static final String PLAYER_TWO_KEY = "PLAYER_TWO_KEY";

    /**
     * Creates and returns an AlertDialog, which is inflated with dialog_multiplayer layout.
     * Sets the functionality of the negative and positive buttons of the dialog.
     */
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_multiplayer, null));

        builder.setPositiveButton(R.string.play, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditText player1Txt = (EditText) ((AlertDialog) dialog).findViewById(R.id.player_one_name);
                EditText player2Txt = (EditText) ((AlertDialog) dialog).findViewById(R.id.player_two_name);

                String player1Name = player1Txt.getText().toString();
                String player2Name = player2Txt.getText().toString();

                Player player1 = initiatePlayer(player1Name);
                Player player2 = initiatePlayer(player2Name);

                Context context = getContext();
                Intent twoPlayerGame = new Intent(context, TwoPlayerLocalActivity.class);
                twoPlayerGame.putExtra(PLAYER_ONE_KEY, player1);
                twoPlayerGame.putExtra(PLAYER_TWO_KEY, player2);
                context.startActivity(twoPlayerGame);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) { }});
        return builder.create();
    }

    public Player initiatePlayer(String name){
            Player p = MainScreen.collection.findPlayer(name);

            if(p == null) {
                p = new Player(name);
                MainScreen.collection.addPlayer(p);
            }

        return p;
    }

}
