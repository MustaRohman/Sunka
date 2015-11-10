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

public class OnlineGames extends AppCompatActivity {

    public static String SERVER_ADDRESS = "http://192.168.0.10:3000";
    public String REQUEST =  "req";
    final private Player player = new Player("test");
    final Activity activity = this;
    protected ArrayAdapter<String> serverListAdapter;
    protected String[] servList = new String[30];


    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(SERVER_ADDRESS);
            Log.d("INFO", "Socket connection established!");
        } catch (URISyntaxException e) {
            Log.d("INFO", "Unable to connect!!!");
        }
    }

    private class populateList extends AsyncTask<Object, ArrayAdapter<String>, Void> {
        private ListView serverListView;

        protected void onPreExecute() {
            Log.d("GUI THREAD", "Gui thread started");
            serverListView = (ListView) findViewById(R.id.server_list);
            Log.d("GUI THREAD", "Gui thread ended");
        }

        protected Void doInBackground(Object... params) {
        Log.d("BACKEND THREAD", "Backend thread started");
            while(servList[0] == null){
                Log.d("BACKEND THREAD","Server request not received yet. Retrying again in .1s");
                try {
                    Thread.sleep(300);
                    if (servList[0] != null){
                        Log.d("BACKEND THREAD", "server 1: "+servList[0] + " server 2: " + servList[1]);
                        String[] test = {servList[0], servList[1]};
                        publishProgress(new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, test));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d("BACKEND THREAD", "Backend thread ended");
            return null;
        }

        protected void onProgressUpdate(ArrayAdapter<String>... params){
            Log.d("GUI THREAD"," received adaptor ");
            serverListView.setAdapter(params[0]);
            Log.d("GUI THREAD", "Gui thread ended");

        }
    }

    private Emitter.Listener parseServerList = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int i = 0, j = 0;
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
                    Log.d("LISTENER TRIGGERED", "Server List variable set to " + servList[0] + " " + servList[1]);
                }
            });
        }
    };

    public void setListAdapter(ArrayAdapter<String> serverList){
        this.serverListAdapter = serverList;
    }
//
//    public void waitServerList(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    while (!checkServerList()){
//                        Thread.sleep(100);
//                        Log.d("INFO", "Message timed out. Retrying in 1 second");
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                Log.d("INFO", "Started creating the list, servers: " + serverList[0] + " " + serverList[1]);
//                serverListAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, serverList);
//            }
//        }).start();
//    }


    public final boolean checkServerList() throws InterruptedException {
        return serverListAdapter == null;
    }

    public void refreshServerList(View view){
        mSocket.emit(REQUEST, "getServers");
        new populateList().execute();
//        new populateList().execute();
//        while(serverList == null ){
//            Thread.sleep(100);
//            Log.d("INFO","Server list not found yet, retrying again in 0.1s");
//        }
//        Log.d("INFO","Server list found!");
//        ArrayAdapter<String> serverAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, serverList);
//        ListView serverList = (ListView) findViewById(R.id.server_list);
//        serverList.setAdapter(serverAdapter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_games);
        mSocket.on("serverList", parseServerList);
//        new populateList().execute();
        mSocket.connect();
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
