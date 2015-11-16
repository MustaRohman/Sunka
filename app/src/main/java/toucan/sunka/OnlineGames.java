package toucan.sunka;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.w3c.dom.CharacterData;

import java.net.URISyntaxException;
import java.util.Objects;

public class OnlineGames extends AppCompatActivity {

    final public static String SERVER_ADDRESS = "http://192.168.0.10:3000";
    final public static String KEY_PLAYER = "KEY_PLAYER";
    final public static String KEY_OPPONENT = "KEY_OPPONENT";
    final public static String KEY_ID = "KEY_ID";
    private Player player;
    protected String REQUEST =  "req";
    protected int serverNumber = 0;
    final Activity activity = this;
    protected String[] servList = new String[30];
    protected boolean dataReceived;
    protected ListView serverListView;
    protected Player opponent = null;
    protected String gameID;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(SERVER_ADDRESS);
            Log.d("INFO", "Socket connection established!");
        } catch (URISyntaxException e) {
            Log.d("INFO", "Unable to connect!!!");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_games);

        player = getIntent().getParcelableExtra(OnlineDialog.ONLINE_PLAYER_ONE);
        ((TextView) findViewById(R.id.player_text_view_mp)).setText(player.getPlayerName());
        final TextView opponentNameView = (TextView) findViewById(R.id.opponent_name);
        serverListView = (ListView) findViewById(R.id.server_list);
        serverListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View child, int position, long id) {
                String opponentString = (((TextView) child).getText().toString());
                updateOpponent(opponentString, opponentNameView);
            }
        });

        setSocketUp();

    }

    private class startGame extends AsyncTask<Object, Void, Void> {
        Intent twoPlayerOnline;
        Boolean dataArrived = false;
        protected void onPreExecute() {
             twoPlayerOnline = new Intent(activity, TwoPlayerOnline.class);
            displayAlert(true);
        }

        protected Void doInBackground(Object... params) {
            while( opponent == null  || !dataArrived ){
                Log.d("BACKEND THREAD", "Opponent not loaded yet. Retrying in .1s...");
                try {
                    Thread.sleep(1000);
                    Log.d("INFO","Opponent not ready. Retrying in .1s...");
                    if (gameID != null) {
                        twoPlayerOnline.putExtra(KEY_ID, gameID);
                        dataArrived = true;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();}
            }
            if (opponent != null){
                Log.d("BACKEND THREAD", "Player connected. Starting game");

            }
            return null;
        }

        protected void onPostExecute(Void result) {
            Log.d("GUI THREAD", "GUI thread ended");
            twoPlayerOnline.putExtra(KEY_PLAYER, player);
            twoPlayerOnline.putExtra(KEY_OPPONENT, opponent);
            Log.d("!!!!",gameID);
            activity.startActivity(twoPlayerOnline);
            displayAlert(false);
        }
    }


    private class populateList extends AsyncTask<Boolean, ArrayAdapter<String>, Void> {
        protected Void doInBackground(Boolean... params) {
            Log.d("BACKEND THREAD", "Backend thread started");
            while (servList[0] == null && !dataReceived) {
                Log.d("BACKEND THREAD", "Server request not received yet. Retrying again in .1s");
                try {
                    Thread.sleep(300);
                    if (servList[0] != null) {
                        int i = 0;
                        String[] serverListString = new String[serverNumber];
                        dataReceived = true;
                        while (servList[i] != null) {
                            serverListString[i] = servList[i];
                            Log.d("BACKEND THREAD", "Found server, name: " + serverListString[i]);
                            i++;
                        }
                        publishProgress(new ArrayAdapter<>(activity, R.layout.list_item, serverListString));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d("BACKEND THREAD", "Backend thread ended");
            return null;
        }

        protected void onProgressUpdate(ArrayAdapter<String>... params){
            Log.d("GUI THREAD", " received adaptor ");
            serverListView.setAdapter(params[0]);
            servList = new String[30];
            Log.d("GUI THREAD", "Gui thread ended");

        }
    }

    public void setSocketUp(){
        mSocket.on("serverList", parseServerList);
        mSocket.on("gameStart", parseOpponentData);
        mSocket.connect();
    }

    private Emitter.Listener parseServerList = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int i = 1, j = 0;
                    String server = "";
                    serverNumber = Character.getNumericValue(((String) args[0]).charAt(0));
                    while (((String) args[0]).charAt(i) != '-') {
                        char currentChar = ((String) args[0]).charAt(i++);
                        if (currentChar != ':')
                            server += currentChar;
                        else {
                            servList[j++] = server;
                            server = "";
                        }
                    }
                    servList[j] = server;
                    Log.d("LISTENER TRIGGERED", "Server List variable set to " + servList[0] + " " + servList[1]);
                }
            });
        }
    };

    private Emitter.Listener parseOpponentData = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String game = ((String) args[0]);
                    int i = 0;
                    Log.d("LISTENER TRIGGERED", "Received message that signals game start, code : " + ((String) args[0]));
                    String firstPlayer = "", secondPlayer = "";
                    String id = "";
                    while (game.charAt(i) != ':')
                        firstPlayer += game.charAt(i++);
                    i++;
                    while (game.charAt(i) != ':')
                        secondPlayer += game.charAt(i++);
                    i++;
                    while (i != game.length())
                        id += game.charAt(i++);
                    setOpponent(firstPlayer, secondPlayer, id);
                    Log.d("LISTENER TRIGGERED", firstPlayer + " " + secondPlayer + " " + gameID);
                }
            });
        }
    };

    public void setOpponent(String firstPlayer, String secondPlayer, String id){
        String owner = this.player.getPlayerName();
        if (firstPlayer.length() != owner.length())
            opponent = new Player(firstPlayer);
        int x=0;
        boolean equals = true;
        while( x != firstPlayer.length() - 1  )
            if (firstPlayer.charAt(x) != owner.charAt(x++))
                equals = false;
        if (equals) opponent = new Player(secondPlayer);
        else opponent = new Player(firstPlayer);
        gameID = id;
    }

    public void playGame(View view) {
        String message = String.format("g%s:%s-", player.getPlayerName(), opponent.getPlayerName());
        mSocket.emit(REQUEST, message);
        new startGame().execute();
    }

    public void displayAlert(boolean display){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage("Waiting for an opponent to connect...");
        if (display) alertDialog.show();
        else alertDialog.dismiss();
    }


    public void refreshServerList(View view){
        dataReceived = false;
        mSocket.emit(REQUEST, "getServers");
        new populateList().execute();
    }

    public void createServer(View view){
        String message = String.format("c%s", player.getPlayerName());
        mSocket.emit(REQUEST, message);
        new startGame().execute();
    }

    public void updateOpponent( String opponentName, TextView view ){
        opponent = new Player(opponentName);
        view.setText("Opponent: " + opponentName);
    }

    @Override
    protected void onPause() {
        super.onPause();
        displayAlert(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_online_games, menu);
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
