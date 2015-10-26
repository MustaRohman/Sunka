package toucan.sunka;



import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.EditText;

/**
 * Created by mustarohman on 22/10/2015.
 */

public class MultiplayerDialogFragment extends DialogFragment {



    @Override
    /**
     * Creates and returns an AlertDialog, which is inflated with dialog_multiplayer layout.
     * Sets the functionality of the negative and positive buttons of the dialog.
     */
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_multiplayer, null));
                builder.setPositiveButton(R.string.play, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        Context context = getContext();
                        Intent newIntent = new Intent(context, TwoPlayerLocalActivity.class);
                        context.startActivity(newIntent);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }
}
