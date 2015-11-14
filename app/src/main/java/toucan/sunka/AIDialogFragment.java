package toucan.sunka;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;

/**
 * Created by mustarohman on 13/11/2015.
 */
public class AIDialogFragment extends DialogFragment {

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
        builder.setView(inflater.inflate(R.layout.dialog_ai, null));

        builder.setPositiveButton(R.string.play, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditText player1Txt = (EditText) ((AlertDialog) dialog).findViewById(R.id.player_one_name);

                String player1Name = player1Txt.getText().toString();
                String player2Name = "AI Player";

                Player player1 = initiatePlayer(player1Name);
                Player player2 = initiatePlayer(player2Name);

                Context context = getContext();
                Intent singlePlayerGame = new Intent(context, OnePlayerAI.class);
                singlePlayerGame.putExtra(PLAYER_ONE_KEY, player1);
                singlePlayerGame.putExtra(PLAYER_TWO_KEY, player2);
                context.startActivity(singlePlayerGame);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) { }});
        return builder.create();
    }

    public Player initiatePlayer(String name){
        Player p;
        if ((p = MainScreen.collection.findPlayer(name)) == null) {
            p = new Player(name);
            MainScreen.collection.addPlayer(p);
        }
        return p;
    }
}
