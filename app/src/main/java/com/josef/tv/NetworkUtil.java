package com.josef.tv;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class NetworkUtil extends AsyncTask<Void, Boolean, Boolean> {

    private OnAuthConnecting onAuthConnecting;

    public interface OnAuthConnecting {
        void isConnected(boolean aboolen);
    }

    public NetworkUtil(OnAuthConnecting onAuthConnecting) {
        this.onAuthConnecting = onAuthConnecting;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        return pingGoogle();
    }

    protected void onPostExecute(Boolean result) {
        onAuthConnecting.isConnected(result);
    }

    public boolean pingGoogle() {
        boolean aboolean = false;
        try {
            URL url = new URL("https://www.google.com");
            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
            urlc.setRequestProperty("User-Agent", "Android Application:");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(3000); // mTimeout is in seconds
            urlc.connect();
            aboolean = urlc.getResponseCode() == 200;
            urlc.disconnect();
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return aboolean;
    }
}





