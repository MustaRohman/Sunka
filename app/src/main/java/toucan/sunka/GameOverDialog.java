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

/**
 * Created by mustarohman on 28/10/2015.
 */
public class GameOverDialog extends DialogFragment {
    private TwoPlayerLocalActivity thisActivity;
    public static final String PLAYER_ONE_KEY = "PLAYER_ONE_KEY";
    public static final String PLAYER_TWO_KEY = "PLAYER_TWO_KEY";
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_game_over, null));
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
}
