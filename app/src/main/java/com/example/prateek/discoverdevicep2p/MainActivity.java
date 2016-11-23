package com.example.prateek.discoverdevicep2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final IntentFilter intentFilter = new IntentFilter();

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    MainActivityFragment mainActivityFragment;

    private WifiP2pManager.PeerListListener peerListListener;

    private ArrayAdapter<WifiP2pDevice> wifip2padapter;

    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    private ListView listview;
    private TextView textview;

    private IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



       /* String[] data = {
                "Mon 6/23 - Sunny - 31/17",
                "Tue 6/24 - Foggy - 21/8",
                "Wed 6/25 - Cloudy - 22/17",
                "Thurs 6/26 - Rainy - 18/11",
                "Fri 6/27 - Foggy - 21/10",
                "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
                "Sun 6/29 - Sunny - 20/7"
        };
        List<String> displist = new ArrayList<String>(Arrays.asList(data));
*/


        //change in Wifi P2P status
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        //change in list of available peers
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        //State of the Wifi P2P connectivity has changed
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        //Device details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);

        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WifiDirectBroadcast(mManager, mChannel, this);

        textview = (TextView) findViewById(R.id.list_device);
        listview = (ListView) findViewById(R.id.peerslist);

        wifip2padapter = new ArrayAdapter<WifiP2pDevice>(this, R.layout.peer_list_item, R.id.list_device, peers);
        listview.setAdapter(wifip2padapter);

        Button button = (Button) findViewById(R.id.discover);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("P2P", "discovering device");
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        Log.v("Success", "Device discovered");
                        setData(true);
                        Log.v("LOG", "SECOND");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.v("p2p", "discovering unsuccesfull");
                        setData(false);
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void setData(boolean isTrue) {
        if (isTrue) {
            Toast.makeText(this, "Divice Found", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Device Cannot Be Found", Toast.LENGTH_SHORT).show();
        }
    }


    BroadcastReceiver discoveryReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {

                @Override
                public void onPeersAvailable(WifiP2pDeviceList peerList) {

                    peers.clear();
                    peers.addAll(peerList.getDeviceList());
                    Log.v("LOG", "Devices List Found" + peerList.getDeviceList());
                    wifip2padapter.notifyDataSetChanged();

                    if (peers.size() == 0) {
                        Log.v("Log", "No device Found");
                    }

                }
            });
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new WifiDirectBroadcast(mManager, mChannel, this);
        registerReceiver(mReceiver, intentFilter);
        registerReceiver(discoveryReciever, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        unregisterReceiver(discoveryReciever);
    }

}
