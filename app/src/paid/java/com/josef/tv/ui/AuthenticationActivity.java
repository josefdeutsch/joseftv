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

package com.josef.tv.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.leanback.app.GuidedStepSupportFragment;
import androidx.leanback.widget.GuidanceStylist;
import androidx.leanback.widget.GuidedAction;

import android.text.InputType;
import android.util.Log;
import android.widget.Toast;

import com.josef.tv.OnNetWorkTask;
import com.josef.tv.tvleanback.R;
import com.josef.tv.data.FetchVideoService;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static com.josef.tv.Utils.hideProgressbar;
import static com.josef.tv.Utils.showProgressbar;
import static com.josef.tv.mobile.SplashActivity.RC_ONAUTH;
import static com.josef.tv.ui.VerticalGridActivity.REQUEST_CODE;

public class AuthenticationActivity extends FragmentActivity {

    private static final int PASSWORD = 5;
    private static final int EMAIL = 6;
    private static final int CONTINUE = 2;


    private static final int DIALOG_REQUEST_CODE = 23;

    private static final String TAG = "AuthenticationActivity";

    public static FirebaseAuth mAuth;
    private static FirebaseAuth.AuthStateListener mAuthListener;

    private final IntentFilter auth_tokens = new IntentFilter("com.josef.tv.filter");

    private static AuthenticationActivity authenticationActivity;

    public static AuthenticationActivity getAuthenticationActivity() {
        return authenticationActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
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
                        // add null check if user has not added anything...
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

            } else if (intent.getAction().equals("com.josef.tv.auth.password")) {

                SharedPreferences prefs = context.getSharedPreferences("com.josef.tv.prefs.main", Context.MODE_PRIVATE);
                prefs.edit().putString("com.josef.tv.auth.password", intent.getExtras().getString("com.josef.tv.auth.password.key")).apply();

            } else if (intent.getAction().equals("com.josef.tv.auth.onCompleteListener")) {

                SharedPreferences prefs = context.getSharedPreferences("com.josef.tv.prefs.main", Context.MODE_PRIVATE);
                prefs.edit().putString("com.josef.tv.auth.onCompleteListener", intent.getExtras().getString("com.josef.tv.auth.onCompleteListener.key")).apply();

            } else if (intent.getAction().equals("com.josef.tv.auth.onFailureListener")) {

                SharedPreferences prefs = context.getSharedPreferences("com.josef.tv.prefs.main", Context.MODE_PRIVATE);
                prefs.edit().putString("com.josef.tv.auth.onFailureListener", intent.getExtras().getString("com.josef.tv.auth.onFailureListener.key")).apply();
                Toast.makeText(context, intent.getExtras().getString("com.josef.tv.auth.onFailureListener.key"), Toast.LENGTH_SHORT).show();

            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        authenticationActivity = this;
        mAuth.addAuthStateListener(mAuthListener);
        this.registerReceiver(networkReceiver, auth_tokens);
    }

    @Override
    public void onStop() {
        super.onStop();
        authenticationActivity = this;
        mAuth.removeAuthStateListener(mAuthListener);
        this.unregisterReceiver(networkReceiver);
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
                new OnNetWorkTask(aboolen -> {
                    if (!aboolen) {
                        Toast.makeText(getContext(), "Network not available !", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String email = getContext().getSharedPreferences("com.josef.tv.prefs.main", Context.MODE_PRIVATE).getString("com.josef.tv.auth.email", "default");
                    String password = getContext().getSharedPreferences("com.josef.tv.prefs.main", Context.MODE_PRIVATE).getString("com.josef.tv.auth.password", "default");

                    if (email.equals("default") && password.equals("default")) return;

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            Intent intent = new Intent("com.josef.tv.auth.onCompleteListener");
                            intent.putExtra("com.josef.tv.auth.onCompleteListener.key", "Firebase Authentication is successful :" + task.isSuccessful());
                            getContext().sendBroadcast(intent);
                            getContext().startActivity(new Intent(getContext(), VerticalGridActivity.class));
                            getActivity().finishAfterTransition();

                        } else if (!task.isSuccessful()) {
                            Log.d(TAG, "onComplete: " + "not successful");
                        }

                    }).addOnFailureListener(e -> {

                        Intent intent = new Intent("com.josef.tv.auth.onFailureListener");
                        intent.putExtra("com.josef.tv.auth.onFailureListener.key", e.getMessage());
                        getContext().sendBroadcast(intent);

                    });
                }).execute();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == DIALOG_REQUEST_CODE && resultCode == RESULT_OK) {
                Intent returnIntent = new Intent();
                getAuthenticationActivity().setResult(Activity.RESULT_OK, returnIntent);
                getAuthenticationActivity().finish();
            }
        } catch (Exception ex) {
            Toast.makeText(this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, TVDialogActivity.class);
        intent.putExtra(TVDialogActivity.ARG_TITLE_RES, R.string.dialog_title);
        intent.putExtra(TVDialogActivity.ARG_ICON_RES, R.drawable.jlogo);
        intent.putExtra(TVDialogActivity.ARG_NEGATIVE_RES, R.string.dialog_no);
        intent.putExtra(TVDialogActivity.ARG_POSITIVE_RES, R.string.dialog_yes);
        intent.putExtra(TVDialogActivity.ARG_DESC_RES, R.string.dialog_desc);
        startActivityForResult(intent, DIALOG_REQUEST_CODE);
    }
}
