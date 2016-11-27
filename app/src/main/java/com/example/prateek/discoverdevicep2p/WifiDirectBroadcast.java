package com.example.prateek.discoverdevicep2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
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
                mAcitivity.setIsWifiP2pEnabled(true);
            }else{
                mAcitivity.setIsWifiP2pEnabled(false);

                Log.v("LOG","Disabled");
            }

        }else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            Log.v("LOG","Peers Found");
            if(mManager != null){
                mManager.requestPeers(mChannel, (WifiP2pManager.PeerListListener)mAcitivity.getFragmentManager()
                .findFragmentById(R.id.fragment_list));
                Log.v("LOG", "Peers changed") ;
            }

        }else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){

            if(mManager == null){
                return;
            }
            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if(networkInfo.isConnected()){
                DetailFragment fragment = (DetailFragment) mAcitivity
                        .getSupportFragmentManager().findFragmentById(R.id.fragment_detail);

            }
            else
            {
                mAcitivity.resetData();
            }
        }else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){
            DeviceListFragment fragment = (DeviceListFragment) mAcitivity.getFragmentManager()
                    .findFragmentById(R.id.fragment_list);
            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE
            ));
        }
    }

}
