package toucan.sunka;

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

/*
Things to do in this class:
- Redo store images (Resize stones)
 */

public class TwoPlayerLocalActivity extends AppCompatActivity {
    private Crater playerOneStore;
    private Crater playerTwoStore;
    Crater[] craterList = new Crater[16];
    private Player firstPlayer;
    private Player secondPlayer;
    private boolean firstMove = true;
    public ImageView stoneImage;

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

        Crater storeTemp = firstPlayer.getStore();
        Log.d("onCraterClick", "width: " + storeTemp.getWidth() +  " height: " + storeTemp.getHeight());

        Crater.updateCraterImage(crater, 0);

        moveAnimation(crater.getNextCrater(), crater.getStones(), crater.getActivePlayer());

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
            craterList[i].setGravity(Gravity.BOTTOM);
        }
        for (int i = 9; i < 16; i++) {
            craterList[i].setOppositeCrater(craterList[16 - i]);
            craterList[i].setOwner(secondPlayer);
            craterList[i].setGravity(Gravity.TOP);

        }


    }

    private void moveAnimation(final Crater crater, final int count, final Player player){

        if (crater.getActivePlayer().equals(firstPlayer)){
            stoneImage = (ImageView) findViewById(R.id.store_imageView_p1);
        } else {
            stoneImage = (ImageView) findViewById(R.id.store_imageView_p2);
        }

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
                    if (crater.getNextCrater().equals(player.getStore().getOppositeCrater())) {
                        nextCrater = crater.getNextCrater().getNextCrater();
                    } else {
                        nextCrater = crater.getNextCrater();
                    }

                    if (crater.isStore()) {
                        Crater.updateStoreImage(crater, crater.getStones());
                    } else {
                        Crater.updateCraterImage(crater, crater.getStones());
                    }

                    moveAnimation(nextCrater, count - 1, player);

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









}
