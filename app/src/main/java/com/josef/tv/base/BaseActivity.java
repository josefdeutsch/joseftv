package com.josef.tv.base;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import com.josef.tv.tvleanback.R;
import com.josef.tv.ui.TVDialogActivity;

public abstract class BaseActivity extends FragmentActivity {

    public static final int DIALOG_NETWORK_REQUEST_CODE = 0002;
    private static final String TAG = "BaseActivity";
    @NonNull
    private ConnectivityManager connectivityManager;
    @NonNull
    private ConnectivityManager.NetworkCallback networkCallback;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectivityManager = getConnectivityManager();
        networkCallback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(Network network) {
                Log.d(TAG, "onAvailable: "+network.toString());
            }

            @Override
            public void onLost(Network network) {
                startErrorActivity();
            }

            @Override
            public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                Log.d(TAG, "onCapabilitiesChanged: "+ networkCapabilities.toString());
            }

            @Override
            public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
            }

        };

        connectivityManager.registerDefaultNetworkCallback(networkCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == DIALOG_NETWORK_REQUEST_CODE && resultCode == RESULT_OK) {
                recreate();
            }
            if (requestCode == DIALOG_NETWORK_REQUEST_CODE && resultCode == RESULT_CANCELED) {
                startErrorActivity();
            }
        } catch (Exception ex) {
            Toast.makeText(this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void startErrorActivity() {
        Intent intent = new Intent(this, TVDialogActivity.class);
        intent.putExtra(TVDialogActivity.ARG_TITLE_RES, R.string.dialog_title);
        intent.putExtra(TVDialogActivity.ARG_ICON_RES, R.drawable.jlogo);
        intent.putExtra(TVDialogActivity.ARG_NEGATIVE_RES, R.string.dialog_no);
        intent.putExtra(TVDialogActivity.ARG_POSITIVE_RES, R.string.dialog_yes);
        intent.putExtra(TVDialogActivity.ARG_DESC_RES, R.string.dialog_desc);
        startActivityForResult(intent, DIALOG_NETWORK_REQUEST_CODE);
    }

    @NonNull
    public Network getActiveNetwork() {
        ConnectivityManager connectivityManager = this.getSystemService(ConnectivityManager.class);
        return connectivityManager.getActiveNetwork();
    }

    @NonNull
    public NetworkCapabilities getNetworkCapabilities(Network currentNetwork) {
        ConnectivityManager connectivityManager = this.getSystemService(ConnectivityManager.class);
        return connectivityManager.getNetworkCapabilities(currentNetwork);
    }

    @NonNull
    public LinkProperties getLinkProperties(Network currentNetwork) {
        ConnectivityManager connectivityManager = this.getSystemService(ConnectivityManager.class);
        return connectivityManager.getLinkProperties(currentNetwork);
    }

    @NonNull
    public ConnectivityManager getConnectivityManager() {
        return this.getSystemService(ConnectivityManager.class);
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
