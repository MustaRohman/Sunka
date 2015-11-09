package toucan.sunka;

import android.graphics.Color;
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


public class TwoPlayerLocalActivity extends AppCompatActivity {
    private Crater playerOneStore;
    private Crater playerTwoStore;
    Crater[] craterList = new Crater[16];

    public Player getFirstPlayer() {
        return firstPlayer;
    }

    public Player getSecondPlayer() {
        return secondPlayer;
    }

    private Player firstPlayer;
    private Player secondPlayer;

    private TextView firstPlayerLabel;
    private TextView secondPlayerLabel;
    
    private boolean firstMove = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_player_local);

        firstPlayer = getIntent().getParcelableExtra(MultiplayerDialogFragment.PLAYER_ONE_KEY);
        secondPlayer = getIntent().getParcelableExtra(MultiplayerDialogFragment.PLAYER_TWO_KEY);

        initializeCraters();

        firstPlayerLabel = (TextView) findViewById(R.id.player_one_view);
        firstPlayerLabel.setText(firstPlayer.getPlayerName());
        secondPlayerLabel = (TextView) findViewById(R.id.player_two_view);
        secondPlayerLabel.setText(secondPlayer.getPlayerName());
    }

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
        crater.makeMoveFromHere();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_two_player_local, menu);
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

    public void initializeStores() {
        playerOneStore = (Crater) findViewById(R.id.store_right);
        playerOneStore.initialise(true);
        playerTwoStore = (Crater) findViewById(R.id.store_left);
        playerTwoStore.initialise(true);
        craterList[0] = playerTwoStore;
        craterList[8] = playerOneStore;
    }

    public void initializeCraters(){
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
    public void createGameOverDialog(){
        DialogFragment fragment = new GameOverDialog();
        FragmentManager fm = getSupportFragmentManager();

        Bundle playerInfo = new Bundle();

        int p1Stones = firstPlayer.getStore().getStones();
        int p2Stones = secondPlayer.getStore().getStones();


        //Initialises victorPlayer with the victor of the current game
        if (p1Stones > p2Stones){
            firstPlayer.setGamesWon(firstPlayer.getNumberOfGamesWon() + 1);
        } else {
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


    /**
     * ONCLICK METHOD FOR TESTING PURPOSES
     * @param view
     */
//    public void onClickGameOverTest(View view) {
//
//        firstPlayer.setGamesWon(0);
//        secondPlayer.setGamesWon(8);
//
//        Player testPlayer1 = new Player("John");
//        testPlayer1.setGamesWon(1);
//
//        Player testPlayer2 = new Player("Manny");
//        testPlayer2.setGamesWon(1);
//
//        Player testPlayer3 = new Player("Hazel");
//        testPlayer3.setGamesWon(3);
//
//        MainScreen.collection.addPlayer(testPlayer1);
//        MainScreen.collection.addPlayer(testPlayer2);
//        MainScreen.collection.addPlayer(testPlayer3);
//        MainScreen.collection.sortByGamesWon();
//
//        for (int i = 0; i < MainScreen.collection.size(); i++){
//            Player p = MainScreen.collection.getPlayerAtPosition(i);
//            Log.d("onClickGameOverTest", p.getPlayerName() + " " + p.getPlayerRank() + " " + p.getNumberOfGamesWon());
//        }
//
//        Player bestPlayer = MainScreen.collection.getPlayerAtPosition(0);
//        Log.d("onClickGameOverTest", "Rank 1 is " + bestPlayer.getPlayerName() + " Wins: " + bestPlayer.getNumberOfGamesWon());
//        Log.d("onClickGameOverTest", secondPlayer.getNumberOfGamesWon() + " " + secondPlayer.getPlayerRank());
//        createGameOverDialog();
//    }
    public void turnNotification(Player p){
        if(firstPlayer.equals(p)){
            firstPlayerLabel.setBackgroundColor(Color.GREEN);
            secondPlayerLabel.setBackgroundColor(Color.TRANSPARENT);
        }
        else{
            secondPlayerLabel.setBackgroundColor(Color.GREEN);
            firstPlayerLabel.setBackgroundColor(Color.TRANSPARENT);
        }
    }
}
