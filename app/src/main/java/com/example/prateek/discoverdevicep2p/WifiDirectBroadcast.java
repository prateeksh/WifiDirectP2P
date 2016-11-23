package com.example.prateek.discoverdevicep2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

/**
 * Created by Prateek on 20-11-2016.
 */

public class WifiDirectBroadcast extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mAcitivity;
    WifiP2pManager.PeerListListener myPeerListListener;

    public WifiDirectBroadcast(WifiP2pManager manager, WifiP2pManager.Channel channel,
                               MainActivity activity){
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mAcitivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent){
        String action = intent.getAction();

        if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){

            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                Log.v("LOG","Enabled");
            }else{
                Log.v("LOG","Disabled");
            }

        }else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            Log.v("LOG","PEERS CHANGED");
            if(mManager != null){
                mManager.requestPeers(mChannel, myPeerListListener);
                Log.v("LOG", "Peers changed") ;
            }

        }else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){
            //Code to be added

        }else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){
           /* MainActivityFragment fragment = (MainActivityFragment) mAcitivity.getFragmentManager()
                    .findFragmentById(R.id.);
*/
        }
    }

}
