package toucan.sunka;

import android.app.Activity;
import android.app.AlertDialog;
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

import java.net.URISyntaxException;

public class OnlineGames extends AppCompatActivity {

    public static String SERVER_ADDRESS = "http://192.168.0.10:3000";
    public String REQUEST =  "req";
    final private Player player = new Player("test");
    final Activity activity = this;
    protected ArrayAdapter<String> serverListAdapter;
    protected int serverNumber = 0;
    protected String[] servList = new String[30];
    protected boolean dataReceived;
    protected ListView serverListView;
    protected Player opponent;


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


    private class populateList extends AsyncTask<Boolean, ArrayAdapter<String>, Void> {
        protected Void doInBackground(Boolean... params) {
            Log.d("BACKEND THREAD", "Backend thread started");
            if (((Boolean) params[0])) {
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
                            publishProgress(new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, serverListString));
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {

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
        public void call(Object... args) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    };

    public void playGame(View view) {
        String message = String.format("g%s:%s-", player.getPlayerName(), opponent.getPlayerName());
        mSocket.emit(REQUEST, message);
    }

    public void displayAlert(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage("Waiting for "+ opponent.getPlayerName() + " to connect...");
        alertDialog.show();
    }


    public void refreshServerList(View view){
        dataReceived = false;
        mSocket.emit(REQUEST, "getServers");
        new populateList().execute(true);
    }

    public void createServer(View view){
        String message = String.format("c%s", player.getPlayerName());
        mSocket.emit(REQUEST, message);
    }

    public void updateOpponent( String opponentName, TextView view ){
        opponent = new Player(opponentName);
        view.setText("Opponent: " + opponentName);
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
