package com.example.prateek.discoverdevicep2p;

import android.app.ProgressDialog;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {

    private WifiP2pDevice device;
    private WifiP2pInfo info;
    ProgressDialog progressDialog = null;
    View rootView = null;

    public DetailFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       rootView = inflater.inflate(R.layout.connect_detail, null);
        rootView.findViewById(R.id.connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                if(progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                        "Connecting to :" + device.deviceAddress, true, true);
                ((DeviceListFragment.DeviceActionListener)getActivity()).connect(config);
            }
        });

        rootView.findViewById(R.id.disconnect).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((DeviceListFragment.DeviceActionListener)getActivity()).disconnect();
                    }
                }
        );
        return rootView;
    }

    public void showDetails(WifiP2pDevice device){
        this.device = device;
        this.getView().setVisibility(View.VISIBLE);
        TextView textView = (TextView) rootView.findViewById(R.id.address);
        textView.setText(device.deviceAddress);

        textView = (TextView) rootView.findViewById(R.id.info);
        textView.setText(device.toString());
    }

    public void resetViews(){
        rootView.findViewById(R.id.connect).setVisibility(View.VISIBLE);
        TextView view = (TextView) rootView.findViewById(R.id.address);
        view.setText(R.string.empty);
        view = (TextView) rootView.findViewById(R.id.info);
        view.setText(R.string.empty);
        this.getView().setVisibility(View.GONE);
    }

}
