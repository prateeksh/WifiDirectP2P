package com.example.prateek.discoverdevicep2p;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements WifiP2pManager.ConnectionInfoListener {

    private WifiP2pDevice device;
    private WifiP2pInfo info;
    ProgressDialog progressDialog = null;
    View rootView = null;
    private int CHOOSE_FILE_RESULT_CODE = 1;
    public static final String IP_SERVER = "192.168.1.5";
    public static int PORT = 8988;
    private static boolean server_running = false;

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
        rootView.findViewById(R.id.sendData).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
                    }
                }
        );
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

       /* String localIp = Utils.getLocalIPAddress();
        Log.v("LOCAL IP", localIp);
        String client_mac = device.deviceAddress;
        Log.v("MAC", client_mac);
        String clientIp = Utils.getIPFromMac(client_mac);
        Log.v("CLIENT IP", clientIp);*/
        String client_mac = device.deviceAddress;
        Uri uri = data.getData();
        TextView statusText = (TextView) rootView.findViewById(R.id.status_text);
        statusText.setText("Sending: " + uri);
        Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
        serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
        //serviceIntent.putExtra(FileTransferService.EXTRAS_ADDRESS, clientIp);
        if (uri != null){
            serviceIntent.putExtra(FileTransferService.EXTRAS_ADDRESS, client_mac);
        }else{
            serviceIntent.putExtra(FileTransferService.EXTRAS_ADDRESS, IP_SERVER);
        }

        serviceIntent.putExtra(FileTransferService.EXTRAS_PORT, PORT);
        getActivity().startService(serviceIntent);
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info){
        if(progressDialog !=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        this.info = info;
        this.getView().setVisibility(View.VISIBLE);
        TextView textView = (TextView) rootView.findViewById(R.id.group_owner);
        textView.setText(getResources().getString(R.string.group_owner)
        + ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
        :getResources().getString(R.string.no)));

        /*textView = (TextView) rootView.findViewById(R.id.device_info);
        textView.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());
*/
        if (!server_running){
            new ServerAsyncTask(getActivity(), rootView.findViewById(R.id.status_text)).execute();
            server_running = true;
        }
        if (info.groupFormed && info.isGroupOwner) {
            Log.v(MainActivity.TAG, "Group Formed");
        } else if (info.groupFormed) {
            Log.v(MainActivity.TAG, "Group not Formed");
        }

        // hide the connect button
        rootView.findViewById(R.id.connect).setVisibility(View.GONE);
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
        view = (TextView) rootView.findViewById(R.id.status_text);
        view.setText(R.string.empty);
        //rootView.findViewById(R.id.sendData).setVisibility(View.GONE);
        this.getView().setVisibility(View.GONE);
    }

    public static class ServerAsyncTask extends AsyncTask<Void,Void,String>{

        private final Context context;
        private final TextView statusText;

        public ServerAsyncTask(Context context, View statusText){
            this.context = context;
            this.statusText = (TextView) statusText;
        }

        @Override
        protected String doInBackground(Void... params){
            try {
                ServerSocket serverSocket = new ServerSocket(PORT);
                Socket client = serverSocket.accept();
                final File f = new File(Environment.getExternalStorageDirectory() + "/"
                        + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
                        + ".jpg");

                File dirs = new File(f.getParent());
                if (!dirs.exists())
                    dirs.mkdirs();
                f.createNewFile();

                //Log.d(WiFiDirectActivity.TAG, "server: copying files " + f.toString());
                InputStream inputstream = client.getInputStream();
                copyFile(inputstream, new FileOutputStream(f));
                serverSocket.close();
                server_running = false;
                return f.getAbsolutePath();
            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                statusText.setText("File copied - " + result);
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + result), "image/*");
                context.startActivity(intent);
            }
        }
        @Override
        protected void onPreExecute() {
            statusText.setText("Opening a server socket");
        }
    }
    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);

            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            //Log.d(WiFiDirectActivity.TAG, e.toString());
            return false;
        }
        return true;
    }
}
