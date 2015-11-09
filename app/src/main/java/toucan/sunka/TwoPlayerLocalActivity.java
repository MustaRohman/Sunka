package toucan.sunka;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


public class TwoPlayerLocalActivity extends AppCompatActivity {
    private Crater playerOneStore;
    private Crater playerTwoStore;
    Crater[] craterList = new Crater[16];
    private Player firstPlayer;
    private Player secondPlayer;
    private boolean firstMove = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_player_local);

        firstPlayer = getIntent().getParcelableExtra(MultiplayerDialogFragment.PLAYER_ONE_KEY);
        secondPlayer = getIntent().getParcelableExtra(MultiplayerDialogFragment.PLAYER_TWO_KEY);

        initializeCraters();

        TextView firstPlayerLabel = (TextView) findViewById(R.id.player_one_view);
        firstPlayerLabel.setText(firstPlayer.getPlayerName());
        TextView secondPlayerLabel = (TextView) findViewById(R.id.player_two_view);
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
//        for (Crater crater : craterList )
//            crater.setStones(0);
//        craterList[7].setStones(1);
//        craterList[9].setStones(1);
//        craterList[15].setStones(4);
    }
}

/*
Good test for one side empty functionality - All other stones are 0
craterList[7].setStones(1)
craterList[12].setStones(2)
craterList[12].makeMoveFromHere
craterList[14].makeMoveFromHere
craterList[13].makeMoveFromHere
craterList[14].makeMoveFromHere
craterList[15].makeMoveFromHere
craterList[1].makeMoveFromHere SHOULD BE POSSIBLE

One more that checks steal
craterList[6].setStones(1)
craterList[9].setStones(1)
craterList[10].setStones(1)
 */