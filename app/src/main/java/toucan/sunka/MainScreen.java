package toucan.sunka;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainScreen extends FragmentActivity {

    Button multiPlayerLocal;
    Button singlePlayerLocal;
    Button multiPlayer;
    Button statistics;
    Button exit;

    public static PlayerCollection collection; // needs to be saved in phone memory

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        initButtons();
    }

    public void initButtons(){
        multiPlayerLocal = (Button) findViewById(R.id.two_player_local);
        multiPlayerLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMultiplayerDialog();
            }
        });
        if (collection == null) collection = new PlayerCollection();
        collection.loadPlayerInfoFromFile(getApplicationContext().getFilesDir());
        Log.d("Collection state", collection.toString());
        if (collection == null) collection = new PlayerCollection();
        statistics = (Button) findViewById(R.id.statistics);
        statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createStatisticsDialog();
            }
        });
        exit = (Button) findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        singlePlayerLocal = (Button) findViewById(R.id.single_player);
        singlePlayerLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAIDialog();
            }
        });

        multiPlayer = (Button) findViewById (R.id.two_player_online);
        final FragmentActivity activity = this;
        multiPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                DialogFragment fragment = new OnlineDialog();
                FragmentManager fm = getSupportFragmentManager();
                fragment.show(fm, "onlineDialog");
            }
        });

    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
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

    /**
     * Creates an instance of the MultiplayerDialogFragment and displays it on screen.
     * The user can use the dialog to enter her name and open a two player game on
     * the local device.
     */
    public void createMultiplayerDialog() {
        DialogFragment fragment = new MultiplayerDialogFragment();
        FragmentManager fm = getSupportFragmentManager();
        fragment.show(fm,"multiplayerDialog");
    }

    /**
     * Creates an instance of the AIDialogFragment and displays it on screen.
     * The user can use the dialog to enter her name and to open a single player
     * game on the local device which puts her up against an AI player.
     */
    public void createAIDialog(){
        DialogFragment fragment = new AIDialogFragment();
        FragmentManager fm = getSupportFragmentManager();
        fragment.show(fm,"aiDialog");
    }

    public void onStop() {
        super.onStop();
        collection.savePlayerInfoToFile(getApplicationContext().getFilesDir());
        Log.d("Reached", "onStop on MainScreen");
    }

    /**
     * Creates an instance of the StatisticsDialogFragment and displays it on screen.
     */
    public void createStatisticsDialog() {
        StatisticsDialog fragment = new StatisticsDialog();
        FragmentManager fm = getSupportFragmentManager();
        fragment.show(fm,"statisticsDialog");
    }

    public static Player fetchPlayer(String name) {
        Player p;
        if((p = MainScreen.collection.findPlayer(name)) == null) {
            p = new Player(name);
            MainScreen.collection.addPlayer(p);
        }
        return p;
    }
}
