package toucan.sunka;

import android.graphics.Color;
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

public class OnePlayerAI extends AppCompatActivity {

    private Crater playerOneStore;
    private Crater aiTwoStore;
    Crater[] craterList = new Crater[16];
    protected Player firstPlayer;
    private SimpleAI aiPlayer;
    private TextView firstPlayerLabel;
    private TextView secondPlayerLabel;
    private ImageView stoneImage;
    private boolean firstMove = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_player_ai);

        firstPlayer = getIntent().getParcelableExtra(MultiplayerDialogFragment.PLAYER_ONE_KEY);
        Player tempPlayer = getIntent().getParcelableExtra(MultiplayerDialogFragment.PLAYER_TWO_KEY);
        aiPlayer = new SimpleAI(tempPlayer);

        initializeCraters();

        firstPlayerLabel = (TextView) findViewById(R.id.vsai_player_one_view);
        firstPlayer.setTextView(firstPlayerLabel);
        firstPlayerLabel.setText(firstPlayer.getPlayerName());
        secondPlayerLabel = (TextView) findViewById(R.id.vsai_player_two_view);
        aiPlayer.setTextView(secondPlayerLabel);
        secondPlayerLabel.setText(aiPlayer.getPlayerName());
    }

    private class makeAIMove extends AsyncTask<Integer, Boolean, Void> {
        protected int[] lastMove = new int[2];
        protected boolean finished = false;
        protected Void doInBackground(Integer... params){
            try {
                Thread.sleep(2100);
                Log.d("INFO","Waiting for human player to finish");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while( !aiPlayer.isPlayingTurn() ){
                try {
                    Thread.sleep(1000);
                    Log.d("BACKEND THREAD","AI not currently playing. Retrying in 1s");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            aiPlayer.generateSevenStates();
            aiPlayer.generateBestMove();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress();
            Log.d("NORMAL TURN", "NORMAL TURN!");
            while (aiPlayer.getsFreeMoveWith(lastMove[0], lastMove[1], 8)){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d("EXTRA TURN", "GOT EXTRA TURN");
                    aiPlayer.generateSevenStates();
                    aiPlayer.generateBestMove();
                    finished = false;
                    publishProgress();
            }
            return null;
        }

        protected void onProgressUpdate(Boolean... result) {
            Crater crater = aiPlayer.getBestCrater();
            stoneImage = crater.getActivePlayer().equals(firstPlayer) ?
                    ((ImageView) findViewById(R.id.vsai_store_imageView_p1)) :
                    ((ImageView) findViewById(R.id.vsai_store_imageView_p2));
            moveAnimation(crater.getNextCrater(), crater.getStones(), crater.getActivePlayer(), stoneImage);
            updateLatestMove(crater);
            crater.makeMoveFromHere();
            finished = true;
        }

        protected void updateLatestMove(Crater crater){
            int [] ret = {crater.getPositionOnBoard(), crater.getStones()};
            lastMove = ret;
        }

    }

    public void onCraterClick(View view) {
        Crater crater = (Crater) view;
        if (firstMove) {
            if (crater.getOwner() == firstPlayer) {
                firstPlayer.setPlayingTurnTo(true);
                aiPlayer.setPlayingTurnTo(false);
            }
            else {
                firstPlayer.setPlayingTurnTo(false);
                aiPlayer.setPlayingTurnTo(true);
            }
            firstMove = false;
        }
        boolean wait = false;
        stoneImage = crater.getActivePlayer().equals(firstPlayer) ?
                ((ImageView) findViewById(R.id.vsai_store_imageView_p1)) :
                ((ImageView) findViewById(R.id.vsai_store_imageView_p2));
        if (crater.getsFreeMove()) wait = true;
        int pos = crater.getPositionOnBoard(), stn = crater.getStones();
        moveAnimation(crater.getNextCrater(), crater.getStones(), crater.getActivePlayer(), stoneImage);
        crater.makeMoveFromHere();
        if (crater.checkSide(aiPlayer)) wait = true;
        if (wait && crater.checkSide(firstPlayer))
             createGameOverDialog();
        if (!wait)
            new makeAIMove().execute(pos,stn);
    }

    private void moveAnimation(final Crater crater, final int count, final Player player, final ImageView stoneImage){
        stoneImage.setVisibility(View.INVISIBLE);
        if (count > 0) {
            stoneImage.setVisibility(View.VISIBLE);
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
                    Crater nextCrater;
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

    public void initializeStores() {
        playerOneStore = (Crater) findViewById(R.id.vsai_store_right);
        playerOneStore.initialise(true);
        aiTwoStore = (Crater) findViewById(R.id.vsai_store_left);
        aiTwoStore.initialise(true);
        craterList[0] = aiTwoStore;
        aiPlayer.setStoreIndex(0);
        craterList[8] = playerOneStore;
    }

    public void initializeCraters() {
        initializeStores();
        LinearLayout topRow = (LinearLayout) findViewById(R.id.vsai_top_row);
        LinearLayout bottomRow = (LinearLayout) findViewById(R.id.vsai_bottom_row);
        int j = 15;

        for (int i = 1; i < bottomRow.getChildCount() + 1; i++ ) {
            Crater currentCrater = (Crater) bottomRow.getChildAt(i-1);
            craterList[i] = currentCrater;
        }
        for (int i = 0; i < topRow.getChildCount(); i++){
            Crater currentCrater = (Crater) topRow.getChildAt(i);
            craterList[j--] = currentCrater;
        }
        craterList[15].setNextCrater(craterList[0]);
        for (int i = 0; i < 15; i++ )
            craterList[i].setNextCrater(craterList[i+1]);

        craterList[0].setOppositeCrater(craterList[8]);
        craterList[8].setOppositeCrater(craterList[0]);
        craterList[0].setOwner(aiPlayer);
        aiPlayer.setStore(craterList[0]);
        craterList[8].setOwner(firstPlayer);
        firstPlayer.setStore(craterList[8]);
        for (int i = 1; i < 8; i++) {
            craterList[i].setOppositeCrater(craterList[16 - i]);
            craterList[i].setOwner(firstPlayer);
            craterList[i].setGravity(Gravity.BOTTOM);
        }
        for (int i = 9; i < 16; i++) {
            craterList[i].setOppositeCrater(craterList[16 - i]);
            craterList[i].setOwner(aiPlayer);
            craterList[i].setGravity(Gravity.TOP);
        }
        craterList[7].setStones(12);
        craterList[4].setStones(1);
        craterList[5].setStones(0);
        aiPlayer.setButtonChoices();
    }

    public void createGameOverDialog() {
        DialogFragment fragment = new GameOverDialog();
        FragmentManager fm = getSupportFragmentManager();

        Bundle playerInfo = new Bundle();

        int p1Stones = firstPlayer.getStore().getStones();
        int p2Stones = aiPlayer.getStore().getStones();

        //Initialises victorPlayer with the victor of the current game
        if (p1Stones > p2Stones) {
            firstPlayer.setGamesWon(firstPlayer.getNumberOfGamesWon() + 1);
        } else {
            Log.d("createGameOverDialog", String.valueOf(aiPlayer.getNumberOfGamesWon()));
            aiPlayer.setGamesWon(aiPlayer.getNumberOfGamesWon() + 1);
            Log.d("createGameOverDialog", String.valueOf(aiPlayer.getNumberOfGamesWon()));
        }

        MainScreen.collection.sortByGamesWon();

        Log.d("createGameOverDialog", String.valueOf(aiPlayer.getPlayerRank()));
        playerInfo.putString(MultiplayerDialogFragment.PLAYER_ONE_KEY, firstPlayer.getPlayerName());
        playerInfo.putString(MultiplayerDialogFragment.PLAYER_TWO_KEY, aiPlayer.getPlayerName());
        playerInfo.putString(GameOverDialog.PLAYER_ONE_STONES, String.valueOf(firstPlayer.getStore().getStones()));
        playerInfo.putString(GameOverDialog.PLAYER_TWO_STONES, String.valueOf(aiPlayer.getStore().getStones()));
        playerInfo.putString(GameOverDialog.PLAYER_ONE_WINS, String.valueOf(firstPlayer.getNumberOfGamesWon()));
        playerInfo.putString(GameOverDialog.PLAYER_TWO_WINS, String.valueOf(aiPlayer.getNumberOfGamesWon()));

        fragment.setArguments(playerInfo);
        fragment.show(fm,"gameOverDialog");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_one_player_ai, menu);
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
