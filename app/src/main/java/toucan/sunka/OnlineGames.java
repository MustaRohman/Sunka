package toucan.sunka;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.List;

public class OnlineGames extends AppCompatActivity {

    public static String SERVER_ADDRESS = "http://10.40.208.74:3000";
    public String REQUEST = "req";
    private Player player;
    final Activity activity = this;
    private String[] serverList = {""};
    final private String REQUEST_KEY = "REQUEST_KEY";
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(SERVER_ADDRESS);
            Log.d("INFO", "Socket connection established!");
        } catch (URISyntaxException e) {
            Log.d("INFO", "Unable to connect!!!");
        }
    }

    private Emitter.Listener parseServerList = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    synchronized (REQUEST_KEY) {
                        Log.d("INFO", "Received data: " + ((String) args[0]));
                        int i = 0, j = 0;
                        String[] servList = new String[30];
                        String server = "";
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
                        ArrayAdapter<String> serverListAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, serverList);
                        ListView serverListView = (ListView) findViewById(R.id.server_list);
                        serverListAdapter.notifyDataSetChanged();
                        serverListView.setAdapter(serverListAdapter);
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_games);
        mSocket.emit(REQUEST, "getServers");
        mSocket.on("serverList", parseServerList);
        mSocket.connect();
        player = new Player("TestPlayer");
        Log.d("INFO", "CONNECTED");
    }

    public void createServer(View view){
        String message = String.format("createSever %s", player.getPlayerName());
        mSocket.emit(REQUEST, message);
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
