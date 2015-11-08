package toucan.sunka;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainScreen extends FragmentActivity{

    Button multiPlayerLocal;
    Button multiPlayer;

    public static PlayerCollection collection; // needs to be saved in phone memory


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        initButtons();
        collection = new PlayerCollection();
    }

    public void initButtons(){
        multiPlayerLocal = (Button) findViewById(R.id.two_player_local);
        multiPlayerLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMultiplayerDialog();
            }
        });

        multiPlayer = (Button) findViewById (R.id.two_player_online);
        final FragmentActivity activity = this;
        multiPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent OnlineGames = new Intent(activity, OnlineGames.class);
                activity.startActivity(OnlineGames);
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
     */
    public void createMultiplayerDialog() {

        DialogFragment fragment = new MultiplayerDialogFragment();
        FragmentManager fm = getSupportFragmentManager();
        fragment.show(fm,"multiplayerDialog");

    }
}
