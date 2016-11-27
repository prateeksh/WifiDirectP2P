package com.example.prateek.discoverdevicep2p;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prateek on 27-11-2016.
 */

public class DeviceListFragment extends ListFragment implements WifiP2pManager.PeerListListener {

    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    View mView = null;
    private WifiP2pDevice device;
    ProgressDialog progressDialog = null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        this.setListAdapter(new WifiPeerListAdapter(getActivity(), R.layout.device_row, peers));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mView = inflater.inflate(R.layout.peer_list_item, null);
        return mView;
    }

    public WifiP2pDevice getDevice(){
        return device;
    }

    private static String getDeviceStatus(int deviceStatus) {
        Log.d(MainActivity.TAG, "Peer status :" + deviceStatus);
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";

        }
    }

    //Initiate connecting with peers
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        WifiP2pDevice device = (WifiP2pDevice) getListAdapter().getItem(position);
        ((DeviceActionListener) getActivity()).showDetails(device);

    }

    //Adapter
    private class WifiPeerListAdapter extends ArrayAdapter<WifiP2pDevice>{

        private List<WifiP2pDevice> items;

        public WifiPeerListAdapter(Context context, int id, List<WifiP2pDevice> object){
            super(context,id,object);
            items = object;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            View view = convertView;
            if(view == null){
                LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.device_row, null);
            }
            WifiP2pDevice device = items.get(position);
            if(device != null){
                TextView textView = (TextView) view.findViewById(R.id.device_name);
                TextView textView1 = (TextView) view.findViewById(R.id.device_details);
                if(textView != null){
                    textView.setText(device.deviceName);
                }
                if(textView1 != null){
                    textView1.setText(getDeviceStatus(device.status));
                }
            }
            else {
                Log.d(MainActivity.TAG,"No device");
            }
            return view;
        }
    }


    public void updateThisDevice(WifiP2pDevice device){
        this.device = device;
        TextView textView = (TextView) mView.findViewById(R.id.my_name);
        textView.setText(device.deviceName);

        textView = (TextView) mView.findViewById(R.id.my_status);
        textView.setText(getDeviceStatus(device.status));
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList){
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        peers.clear();
        peers.addAll(peerList.getDeviceList());
        ((WifiPeerListAdapter)getListAdapter()).notifyDataSetChanged();
        if(peers.size() == 0){
            Log.v(MainActivity.TAG,"No peer Device");
            return;
        }
    }

    public void clearPeers() {
        peers.clear();
        ((WifiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
    }

    public void onInitiateDiscovery() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel", "finding peers", true,
                true, new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {

                    }
                });
    }

    public interface DeviceActionListener{
        void showDetails(WifiP2pDevice device);
        void cancelDisconnect();
        void connect(WifiP2pConfig config);
        void disconnect();
    }
}
