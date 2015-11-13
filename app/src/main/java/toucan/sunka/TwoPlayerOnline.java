package toucan.sunka;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class TwoPlayerOnline extends AppCompatActivity {
    private Crater playerOneStore;
    private Crater playerTwoStore;
    Crater[] craterList = new Crater[16];
    final Activity activity = this;
    private Player firstPlayer;
    private Player secondPlayer;
    private boolean firstMove = true;
    private String gameID;
    protected int opponentMove = 0, freeMove = 0;
    static public Socket mSocket;
    {
        try {
            mSocket = IO.socket(OnlineGames.SERVER_ADDRESS);
            Log.d("INFO", "Socket connection established!");
        } catch (URISyntaxException e) {
            Log.d("INFO", "Unable to connect!!!");
        }
    }

    private class makeOpponentMove extends AsyncTask<Void, Integer, Void> {

        protected Void doInBackground(Void... params) {
            try {
                Log.d("BACKEND THREAD", "WAITING 2.1s FOR GUI CHANGES");
                Thread.sleep(2100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (opponentMove == 0) {
                try {
                    Log.d("BACKEND THREAD", "Waiting 1.7s for answer");
                    Thread.sleep(1700);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (opponentMove != 0) {
                publishProgress(opponentMove);
                Log.d("BACKEND", "Published progress: " + opponentMove);
                opponentMove = 0;
            }
            return null;
        }

        protected void onProgressUpdate(Integer... params){
            correspondingCrater(params[0]).makeMoveFromHere();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_player_online);

        firstPlayer = getIntent().getParcelableExtra(OnlineGames.KEY_PLAYER);
        secondPlayer = getIntent().getParcelableExtra(OnlineGames.KEY_OPPONENT);
        gameID = getIntent().getStringExtra(OnlineGames.KEY_ID);
        initializeCraters();

        TextView firstPlayerLabel = (TextView) findViewById(R.id.online_player_one_view);
        firstPlayerLabel.setText(firstPlayer.getPlayerName());
        TextView secondPlayerLabel = (TextView) findViewById(R.id.online_player_two_view);
        secondPlayerLabel.setText(secondPlayer.getPlayerName().toString());
        setSocketUp();
    }

    public Crater correspondingCrater(int id){
        Crater currentCrater = firstPlayer.getStore();
        while (id-- != 0)
            currentCrater = currentCrater.getNextCrater();
        return currentCrater;
    }

    public void setSocketUp(){
        mSocket.on(firstPlayer.getPlayerName(), parseMove);
    }

    private Emitter.Listener parseMove = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (firstMove) {
                        new makeOpponentMove().execute();
                        firstPlayer.setPlayingTurnTo(false);
                        secondPlayer.setPlayingTurnTo(true);
                        firstMove = false;
                    }

                    if (((String) args[0]).charAt(1) == 'f') {
                        freeMove = Integer.parseInt(((String) args[0]).charAt(0) + "");
                        correspondingCrater(freeMove).makeMoveFromHere();
                    }
                    else opponentMove = Integer.parseInt(((String) args[0]).charAt(0) + "");
                    Log.d("LISTENER", "Received opponent move: " + opponentMove);
                }
            });
        }
    };

    public void onCraterClick(View view){
        Crater crater = (Crater) view;
        if (firstMove){
            if (crater.getOwner() == firstPlayer) {
                firstPlayer.setPlayingTurnTo(true);
                secondPlayer.setPlayingTurnTo(false);
            }
            else {
                firstPlayer.setPlayingTurnTo(false);
                secondPlayer.setPlayingTurnTo(true);
            }
            firstMove = false;
        }
        String type = "n";
        if (crater.getsFreeMove() || secondPlayer.isIdle()) type = "f";
        crater.makeMoveFromHere();
        if (type != "f")
            new makeOpponentMove().execute();
        mSocket.emit("game", gameID + ":" + crater.getOwner().getPlayerName() +
                ":" + crater.getPositionOnBoard() + type);
    }



    public void initializeStores() {
        playerOneStore = (Crater) findViewById(R.id.online_store_right);
        playerOneStore.initialise(true);
        playerTwoStore = (Crater) findViewById(R.id.online_store_left);
        playerTwoStore.initialise(true);
        craterList[0] = playerTwoStore;
        craterList[8] = playerOneStore;
    }

    public void initializeCraters() {
        initializeStores();
        LinearLayout topRow = (LinearLayout) findViewById(R.id.online_top_row);
        LinearLayout bottomRow = (LinearLayout) findViewById(R.id.online_bottom_row);
        int j = 15;

        for (int i = 1; i < bottomRow.getChildCount() + 1; i++) {
            Crater currentCrater = (Crater) bottomRow.getChildAt(i - 1);
            craterList[i] = currentCrater;
        }
        for (int i = 0; i < topRow.getChildCount(); i++) {
            Crater currentCrater = (Crater) topRow.getChildAt(i);
            craterList[j--] = currentCrater;
        }
        craterList[15].setNextCrater(craterList[0]);
        for (int i = 0; i < 15; i++)
            craterList[i].setNextCrater(craterList[i + 1]);

        craterList[0].setOppositeCrater(craterList[8]);
        craterList[8].setOppositeCrater(craterList[0]);
        craterList[0].setOwner(secondPlayer);
        secondPlayer.setStore(craterList[0]);
        craterList[8].setOwner(firstPlayer);
        firstPlayer.setStore(craterList[8]);
        for (int i = 1; i < 8; i++) {
            craterList[i].setOppositeCrater(craterList[16 - i]);
            craterList[i].setOwner(firstPlayer);
        }
        for (int i = 9; i < 16; i++) {
            craterList[i].setOppositeCrater(craterList[16 - i]);
            craterList[i].setOwner(secondPlayer);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_two_player_online, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
