package toucan.sunka;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class TwoPlayerLocalActivity extends AppCompatActivity {

    private Player player1;
    private Player player2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_player_local);

        player1 = (Player) getIntent().getParcelableExtra(MultiplayerDialogFragment.PLAYER_ONE_KEY);
        player2 = (Player) getIntent().getParcelableExtra(MultiplayerDialogFragment.PLAYER_TWO_KEY);

        TextView player1label = (TextView) findViewById(R.id.player_one_view);
        player1label.setText(player1.getPlayerName());
        TextView player2label = (TextView) findViewById(R.id.player_two_view);
        player2label.setText(player2.getPlayerName());

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
}
