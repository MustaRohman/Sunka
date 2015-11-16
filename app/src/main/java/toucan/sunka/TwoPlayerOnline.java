package toucan.sunka;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
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
    public ImageView stoneImage;
    static public Socket mSocket;
    {
        try {
            mSocket = IO.socket(OnlineGames.SERVER_ADDRESS);
            Log.d("INFO", "Socket connection established!");
        } catch (URISyntaxException e) {
            Log.d("INFO", "Unable to connect!!!");
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
        firstPlayer.setTextView(firstPlayerLabel);
        TextView secondPlayerLabel = (TextView) findViewById(R.id.online_player_two_view);
        secondPlayerLabel.setText(secondPlayer.getPlayerName().toString());
        secondPlayer.setTextView(secondPlayerLabel);
        setSocketUp();
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
            stoneImage = (ImageView) findViewById(R.id.online_store_imageView_p2);
            Crater crater = correspondingCrater(params[0]);
            Crater.updateCraterImage(crater, 0);
            moveAnimation(crater.getNextCrater(), crater.getStones(), crater.getActivePlayer(), stoneImage);
            crater.makeMoveFromHere();
        }
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
                        stoneImage = (ImageView) findViewById(R.id.online_store_imageView_p2);
                        Crater crater = correspondingCrater(freeMove);
                        Crater.updateCraterImage(crater, 0);
                        moveAnimation(crater.getNextCrater(), crater.getStones(), crater.getActivePlayer(), stoneImage);
                        crater.makeMoveFromHere();
                    }
                    else opponentMove = Integer.parseInt(((String) args[0]).charAt(0) + "");
                    Log.d("LISTENER", "Received opponent move: " + opponentMove);
                }
            });
        }
    };

    public Crater correspondingCrater(int id){
        Crater currentCrater = firstPlayer.getStore();
        while (id-- != 0)
            currentCrater = currentCrater.getNextCrater();
        return currentCrater;
    }

    public void setSocketUp(){
        mSocket.on(firstPlayer.getPlayerName(), parseMove);
    }

    public void onCraterClick(View view){
        Crater crater = (Crater) view;
        if(crater.getStones() != 0 ) {
            if (firstMove) {
                if (crater.getOwner() == firstPlayer) {
                    firstPlayer.highlightText();
                    firstPlayer.setPlayingTurnTo(true);
                    secondPlayer.setPlayingTurnTo(false);
                } else {
                    firstPlayer.setPlayingTurnTo(false);
                    secondPlayer.highlightText();
                    secondPlayer.setPlayingTurnTo(true);
                }
                firstMove = false;
            }
            stoneImage = crater.getActivePlayer().equals(firstPlayer) ?
                    ((ImageView) findViewById(R.id.online_store_imageView_p1)) :
                    ((ImageView) findViewById(R.id.online_store_imageView_p2));
            Crater.updateCraterImage(crater, 0);
            moveAnimation(crater.getNextCrater(), crater.getStones(), crater.getActivePlayer(), stoneImage);
            String type = "n";
            Boolean free = false;
            if (crater.getsFreeMove()) free = true;
            crater.makeMoveFromHere();
            if (crater.checkSide(secondPlayer) || free) type = "f";
            if (type == "f" && crater.checkSide(firstPlayer)) {
                type = "n"; // game over
                createGameOverDialog();
            }
            if (type != "f")
                new makeOpponentMove().execute();
            mSocket.emit("game", gameID + ":" + crater.getOwner().getPlayerName() +
                    ":" + crater.getPositionOnBoard() + type);
        }
    }

    private void moveAnimation(final Crater crater, final int count, final Player player, final ImageView stoneImage){
        stoneImage.setVisibility(View.INVISIBLE);
        Log.d("moveAnimation", "stoneImage has been set as Invisible");
        if (count > 0) {

            stoneImage.setVisibility(View.VISIBLE);
            Log.d("moveAnimation", "stoneImage has been set as Visible");

            int moveXCenter = (getLeftInParent(crater) - getLeftInParent(stoneImage)) +
                    (crater.getRight() - crater.getLeft()) / 4;
            int moveY = getTopInParent(crater) - getTopInParent(stoneImage) +
                    (crater.getBottom() - crater.getTop()) / 4;

            TranslateAnimation move = new TranslateAnimation(0, moveXCenter,
                    0, moveY);
            move.setDuration(500);
            move.setFillAfter(false);
            move.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    stoneImage.setVisibility(View.INVISIBLE);
                    Log.d("moveAnimation", "stoneImage has been set as Invisible");

                    Crater nextCrater;

                    //Checks if next crater is opponent's store
                    nextCrater = crater.getNextCrater().equals(player.getStore().getOppositeCrater()) ?
                            crater.getNextCrater().getNextCrater() :
                            crater.getNextCrater();
                    moveAnimation(nextCrater, count - 1, player, stoneImage);
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            stoneImage.startAnimation(move);
        }
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
            craterList[i].setGravity(Gravity.BOTTOM);
        }
        for (int i = 9; i < 16; i++) {
            craterList[i].setOppositeCrater(craterList[16 - i]);
            craterList[i].setOwner(secondPlayer);
            craterList[i].setGravity(Gravity.TOP);
        }
    }

    private int getLeftInParent(View view) {
        if (view.getParent() == view.getRootView())
            return view.getLeft();
        else
            return view.getLeft() + getLeftInParent((View) view.getParent());
    }

    private int getTopInParent(View view) {
        if (view.getParent() == view.getRootView())
            return view.getTop();
        else
            return view.getTop() + getTopInParent((View) view.getParent());
    }

   public void createGameOverDialog(){
        DialogFragment fragment = new GameOverDialog();
        FragmentManager fm = getSupportFragmentManager();

        Bundle playerInfo = new Bundle();

        int p1Stones = firstPlayer.getStore().getStones();
        int p2Stones = secondPlayer.getStore().getStones();


        //Initialises victorPlayer with the victor of the current game
        if (p1Stones > p2Stones){
            firstPlayer.setGamesWon(firstPlayer.getNumberOfGamesWon() + 1);
        } else if (p2Stones > p1Stones){
            Log.d("createGameOverDialog", String.valueOf(secondPlayer.getNumberOfGamesWon()));
            secondPlayer.setGamesWon(secondPlayer.getNumberOfGamesWon() + 1);
            Log.d("createGameOverDialog", String.valueOf(secondPlayer.getNumberOfGamesWon()));
        }

        MainScreen.collection.sortByGamesWon();
        Log.d("createGameOverDialog", String.valueOf(secondPlayer.getPlayerRank()));


        playerInfo.putString(MultiplayerDialogFragment.PLAYER_ONE_KEY, firstPlayer.getPlayerName());
        playerInfo.putString(MultiplayerDialogFragment.PLAYER_TWO_KEY, secondPlayer.getPlayerName());
        playerInfo.putString(GameOverDialog.PLAYER_ONE_STONES, String.valueOf(firstPlayer.getStore().getStones()));
        playerInfo.putString(GameOverDialog.PLAYER_TWO_STONES, String.valueOf(secondPlayer.getStore().getStones()));
        playerInfo.putString(GameOverDialog.PLAYER_ONE_WINS, String.valueOf(firstPlayer.getNumberOfGamesWon()));
        playerInfo.putString(GameOverDialog.PLAYER_TWO_WINS, String.valueOf(secondPlayer.getNumberOfGamesWon()));

        fragment.setArguments(playerInfo);
        fragment.show(fm,"gameOverDialog");
    }
    public Player getFirstPlayer() {
        return firstPlayer;
    }

    public Player getSecondPlayer() {
        return secondPlayer;
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
