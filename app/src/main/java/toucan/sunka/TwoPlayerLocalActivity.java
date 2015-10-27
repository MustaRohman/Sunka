package toucan.sunka;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Iterator;

public class TwoPlayerLocalActivity extends AppCompatActivity {
    private Crater playerOneStore;
    private Crater playerTwoStore;
    Crater[] craterList = new Crater[16];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_player_local);
        initialisingCraters();
    }

    public void onCraterClick(View view){
        Log.d("test", "i'm here");
        Crater crater = (Crater) view;
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

    public void initialisingCraters(){
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
        for (int i = 1; i < 7; i++) {
            craterList[i].setOppositeCrater(craterList[16 - i]);
        }
        for (int i = 9; i < 15; i++)
            craterList[i].setOppositeCrater(craterList[16-i]);
    }

}
