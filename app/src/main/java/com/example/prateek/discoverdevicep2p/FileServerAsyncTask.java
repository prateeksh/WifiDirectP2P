package com.example.prateek.discoverdevicep2p;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Prateek on 22-02-2017.
 */

public class FileServerAsyncTask extends AsyncTask<Void,Void,String> {

    private Context mContext;
    private TextView textView;

    public FileServerAsyncTask(Context context, View statusText){
        this.mContext = context;
        this.textView = (TextView) statusText;
    }

    @Override
    protected String doInBackground(Void... params){
        try{
            ServerSocket serverSocket = new ServerSocket(8888);
            Socket client = serverSocket.accept();

            final File file = new File(Environment.getExternalStorageDirectory() + "/"
            + mContext.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
            + ".jpg");

            File dirs = new File(file.getParent());
            if (!dirs.exists())
                dirs.mkdirs();
            file.createNewFile();
            InputStream inputstream = client.getInputStream();

            serverSocket.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
                Log.e(MainActivity.TAG, e.getMessage());
                return null;
        }

    }
}

