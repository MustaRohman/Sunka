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
 * Created by mustarohman on 14/11/2015.
 */
public class OnlineDialog extends DialogFragment {

    public static final String PLAYER_ONE_KEY = "PLAYER_ONE_KEY";

    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_online, null));

        builder.setPositiveButton(R.string.play, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditText player1Txt = (EditText) ((AlertDialog) dialog).findViewById(R.id.player_one_name);
                String player1Name = player1Txt.getText().toString();
                Player player1 = MainScreen.collection.findPlayer(player1Name);

                if (player1 == null){
                    player1 = new Player(player1Name);
                    MainScreen.collection.addPlayer(player1);
                }

                Context context = getContext();
                Intent onlineGame = new Intent(context, OnlineGames.class);
                onlineGame.putExtra(PLAYER_ONE_KEY, new Player(player1Name));
                context.startActivity(onlineGame);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        return builder.create();
    }

}
