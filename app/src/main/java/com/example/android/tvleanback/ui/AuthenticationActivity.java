/*
 * Copyright (c) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.tvleanback.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.leanback.app.GuidedStepSupportFragment;
import androidx.leanback.widget.GuidanceStylist;
import androidx.leanback.widget.GuidedAction;

import android.text.InputType;
import android.util.Log;
import android.widget.Toast;

import com.example.android.tvleanback.NetworkUtil;
import com.example.android.tvleanback.R;
import com.example.android.tvleanback.data.FetchVideoService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.example.android.tvleanback.Utils.hideProgressbar;
import static com.example.android.tvleanback.Utils.showProgressbar;
import static com.example.android.tvleanback.ui.VerticalGridActivity.REQUEST_CODE;

public class AuthenticationActivity extends FragmentActivity {

    private static final int PASSWORD = 5;
    private static final int EMAIL = 6;
    private static final int CONTINUE = 2;

    private static final String TAG = "AuthenticationActivity";

    public static FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;
    private final IntentFilter auth_tokens = new IntentFilter("com.josef.tv.filter");
    private final IntentFilter connectivity_filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        // boolean booleans = isNetworkAvailable(this);
        // Log.d(TAG, "onCreate: "+booleans);
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        mAuthListener = firebaseAuth -> {

            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                showProgressbar(AuthenticationActivity.this);
                FirebaseDatabase database = FirebaseDatabase.getInstance();//
                DatabaseReference myRef = database.getReference("users").child(user.getUid());
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.child("png").getValue().toString();
                        Intent serviceIntent = new Intent(getApplicationContext(), FetchVideoService.class);
                        serviceIntent.putExtra("data", value);
                        getApplication().startService(serviceIntent);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        hideProgressbar();
                    }
                });
            } else {
                hideProgressbar();
            }
        };
        if (null == savedInstanceState) {
            GuidedStepSupportFragment.addAsRoot(this, new FirstStepFragment(), android.R.id.content);
        }


        auth_tokens.addAction("com.josef.tv.auth.email");
        auth_tokens.addAction("com.josef.tv.auth.password");
        auth_tokens.addAction("com.josef.tv.auth.onCompleteListener");
        auth_tokens.addAction("com.josef.tv.auth.onFailureListener");
    }

    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, final Intent intent) {

            if (intent.getAction().equals("com.josef.tv.auth.email")) {

                SharedPreferences prefs = context.getSharedPreferences("com.josef.tv.prefs.main", Context.MODE_PRIVATE);
                prefs.edit().putString("com.josef.tv.auth.email", intent.getExtras().getString("com.josef.tv.auth.email.key")).apply();
                String email = context.getSharedPreferences("com.josef.tv.prefs.main", Context.MODE_PRIVATE).getString("com.josef.tv.auth.email", "default");
                Toast.makeText(context, email, Toast.LENGTH_SHORT).show();

            } else if (intent.getAction().equals("com.josef.tv.auth.password")) {

                SharedPreferences prefs = context.getSharedPreferences("com.josef.tv.prefs.main", Context.MODE_PRIVATE);
                prefs.edit().putString("com.josef.tv.auth.password", intent.getExtras().getString("com.josef.tv.auth.password.key")).apply();
                String password = context.getSharedPreferences("com.josef.tv.prefs.main", Context.MODE_PRIVATE).getString("com.josef.tv.auth.password", "default");
                Toast.makeText(context, password, Toast.LENGTH_SHORT).show();

            } else if (intent.getAction().equals("com.josef.tv.auth.onCompleteListener")) {

                SharedPreferences prefs = context.getSharedPreferences("com.josef.tv.prefs.main", Context.MODE_PRIVATE);
                prefs.edit().putString("com.josef.tv.auth.onCompleteListener", intent.getExtras().getString("com.josef.tv.auth.onCompleteListener.key")).apply();
                Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show();

            } else if (intent.getAction().equals("com.josef.tv.auth.onFailureListener")) {

                SharedPreferences prefs = context.getSharedPreferences("com.josef.tv.prefs.main", Context.MODE_PRIVATE);
                prefs.edit().putString("com.josef.tv.auth.onFailureListener", intent.getExtras().getString("com.josef.tv.auth.onFailureListener.key")).apply();
                Toast.makeText(context, intent.getExtras().getString("com.josef.tv.auth.onFailureListener.key"), Toast.LENGTH_SHORT).show();

            }

        }
    };
    private final BroadcastReceiver connectivityReceiver = new BroadcastReceiver() {

        private static final String TAG = "Broadcastreceiver";
        private ConnectivityManager connectivityManager;

        @Override
        public void onReceive(final Context context, final Intent intent) {

            // int status = NetworkUtil.getConnectivityStatusString(context);
            if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                //   Log.d(TAG, "onReceive: ");
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        Log.d(TAG, "doInBackground: " + isNetworkAvailable(context));

                       // isNetworkAvailable(context);
                        return null;
                    }
                }.execute();
                // Log.d(TAG, "onReceive: "+String.valueOf(booleans));
            }
        }

        public boolean isConnectedToTheNetwork(Context context, ConnectivityManager connectivityManager) {
            if (connectivityManager == null)
                connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
        }

        public boolean isNetworkAvailable(Context context) {
            boolean success = false;
            try {
                URL url = new URL("https://www.google.com");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                connection.connect();
                success = connection.getResponseCode() == 200;
            } catch (IOException e) {
              //  e.printStackTrace();
            }
            return success;
        }

    };


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        this.registerReceiver(networkReceiver, auth_tokens);
        this.registerReceiver(connectivityReceiver, connectivity_filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
        this.unregisterReceiver(networkReceiver);
        this.unregisterReceiver(connectivityReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        SharedPreferences prefs = this.getSharedPreferences("com.josef.tv.prefs.main", Context.MODE_PRIVATE);
        prefs.edit().putString("com.josef.tv.auth.email", "default").apply();
        prefs.edit().putString("com.josef.tv.auth.password", "default").apply();
        prefs.edit().putString("com.josef.tv.auth.onCompleteListener", "default").apply();
        prefs.edit().putString("com.josef.tv.auth.onFailureListener", "default").apply();

        hideProgressbar();

    }

    public static class FirstStepFragment extends GuidedStepSupportFragment {

        @Override
        public int onProvideTheme() {
            return R.style.Theme_Example_Leanback_GuidedStep_First;
        }

        @Override
        @NonNull
        public GuidanceStylist.Guidance onCreateGuidance(@NonNull Bundle savedInstanceState) {
            String title = getString(R.string.pref_title_screen_signin);
            String description = getString(R.string.pref_title_login_description);
            Drawable icon = getActivity().getDrawable(R.drawable.ic_webdesignsvg_02);
            return new GuidanceStylist.Guidance(title, description, "", icon);
        }

        @Override
        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {


            GuidedAction enterUsername = new GuidedAction.Builder(getContext())
                    .id(EMAIL)
                    .title(getString(R.string.pref_title_username))
                    .descriptionEditable(true)
                    .descriptionInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS | InputType.TYPE_CLASS_TEXT)
                    .focusable(true)
                    .build();

            GuidedAction enterPassword = new GuidedAction.Builder(getContext())
                    .id(PASSWORD)
                    .title(getString(R.string.pref_title_password))
                    .descriptionEditable(true)
                    .descriptionInputType(InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD | InputType.TYPE_CLASS_TEXT)
                    .build();


            GuidedAction login = new GuidedAction.Builder(getContext())
                    .id(CONTINUE)
                    .title(getString(R.string.guidedstep_login))
                    .build();

            actions.add(enterUsername);
            actions.add(enterPassword);
            actions.add(login);

        }


        @Override
        public void onGuidedActionClicked(GuidedAction action) {
            if (action.getId() == CONTINUE) {


                String email = getContext().getSharedPreferences("com.josef.tv.prefs.main", Context.MODE_PRIVATE).getString("com.josef.tv.auth.email", "default");
                String password = getContext().getSharedPreferences("com.josef.tv.prefs.main", Context.MODE_PRIVATE).getString("com.josef.tv.auth.password", "default");

                if (email == "default" && password == "default") return;

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Intent intent = new Intent("com.josef.tv.auth.onCompleteListener");
                            intent.putExtra("com.josef.tv.auth.onCompleteListener.key", "Firebase Authentication is successful :" + task.isSuccessful());
                            getContext().sendBroadcast(intent);
                            getContext().startActivity(new Intent(getContext(), VerticalGridActivity.class));
                            getActivity().finishAfterTransition();
                        } else if (!task.isSuccessful()) {
                            Log.d(TAG, "onComplete: " + "not successful");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Intent intent = new Intent("com.josef.tv.auth.onFailureListener");
                        intent.putExtra("com.josef.tv.auth.onFailureListener.key", e.getMessage());
                        getContext().sendBroadcast(intent);
                    }
                });


            }
            if (action.getId() == EMAIL) {
                String email = getString(action.toString().replaceAll("\\s+", ""), 8);
                Intent intent = new Intent("com.josef.tv.auth.email");
                intent.putExtra("com.josef.tv.auth.email.key", email);
                getContext().sendBroadcast(intent);
            }
            if (action.getId() == PASSWORD) {
                String password = getString(action.toString().replaceAll("\\s+", ""), 8);
                Intent intent = new Intent("com.josef.tv.auth.password");
                intent.putExtra("com.josef.tv.auth.password.key", password);
                getContext().sendBroadcast(intent);
            }
        }

        public String getString(String s, int num) {
            return s.substring(num);
        }
    }

    public static class AlertStepFragment extends GuidedStepSupportFragment {

        @Override
        public int onProvideTheme() {
            return R.style.Theme_Example_Leanback_GuidedStep_First;
        }

        @Override
        @NonNull
        public GuidanceStylist.Guidance onCreateGuidance(@NonNull Bundle savedInstanceState) {
            String title = getString(R.string.pref_title_screen_signin);
            String description = getString(R.string.pref_title_login_description);
            Drawable icon = getActivity().getDrawable(R.drawable.ic_webdesignsvg_02);
            return new GuidanceStylist.Guidance(title, description, "", icon);
        }

        @Override
        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {

            GuidedAction alert = new GuidedAction.Builder(getContext())
                    .id(9)
                    .title(getString(R.string.alert_message))
                    .descriptionEditable(true)
                    .descriptionInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS | InputType.TYPE_CLASS_TEXT)
                    .focusable(true)
                    .build();

            actions.add(alert);
        }

        @Override
        public void onGuidedActionClicked(GuidedAction action) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            }
        } catch (Exception ex) {
            Toast.makeText(this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }
    }

}
