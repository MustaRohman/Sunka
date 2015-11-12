package toucan.sunka;

import android.content.DialogInterface;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by M on 11/11/2015.
 */
public class WaitingDialog extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        TextView textView = new TextView(getContext());
        textView.setText("                                " +
                "Waiting for other player...");
        builder.setView(textView);
        return builder.create();
    }
}