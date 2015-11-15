package toucan.sunka;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    private boolean firstMove = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_player_ai);

        firstPlayer = getIntent().getParcelableExtra(MultiplayerDialogFragment.PLAYER_ONE_KEY);
        Player tempPlayer = getIntent().getParcelableExtra(MultiplayerDialogFragment.PLAYER_TWO_KEY);
        aiPlayer = new SimpleAI(tempPlayer);

        initializeCraters();

        firstPlayerLabel = (TextView) findViewById(R.id.player_one_view);
        firstPlayerLabel.setText(firstPlayer.getPlayerName());
        secondPlayerLabel = (TextView) findViewById(R.id.player_two_view);
        secondPlayerLabel.setText(aiPlayer.getPlayerName());
    }

    private class makeAIMove extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params){
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
            return null;
        }

        protected void onPostExecute(Void result) {

            aiPlayer.getBestCrater().makeMoveFromHere();
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
        if (crater.getsFreeMove()) wait = true;
        crater.makeMoveFromHere();
        if (crater.checkSide(aiPlayer)) wait = true;
        if (wait && crater.checkSide(firstPlayer));
             // gameover
        if (!wait)
            new makeAIMove().execute();
    }

    public void initializeStores() {
        playerOneStore = (Crater) findViewById(R.id.store_right);
        playerOneStore.initialise(true);
        aiTwoStore = (Crater) findViewById(R.id.store_left);
        aiTwoStore.initialise(true);
        craterList[0] = aiTwoStore;
        aiPlayer.setStoreIndex(0);
        craterList[8] = playerOneStore;
    }

    public void initializeCraters() {
        initializeStores();
        LinearLayout topRow = (LinearLayout) findViewById(R.id.top_row);
        LinearLayout bottomRow = (LinearLayout) findViewById(R.id.bottom_row);
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
        }
        for (int i = 9; i < 16; i++) {
            craterList[i].setOppositeCrater(craterList[16 - i]);
            craterList[i].setOwner(aiPlayer);
        }
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

    public Player getFirstPlayer() {
        return firstPlayer;
    }
    public Player getAiPlayer() { return aiPlayer; }

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
