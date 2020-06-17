/*
 * Copyright (c) 2014 The Android Open Source Project
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

import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.example.android.tvleanback.R;
import com.example.android.tvleanback.net.LoginActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseUser;

/*
 * VerticalGridActivity that loads VerticalGridFragment
 */

public class VerticalGridActivity extends LoginActivity implements  View.OnClickListener {
    /**
     * Called when the activity is first created.
     */
    private FrameLayout mVerticalGridLayout;
    private SignInButton mSignInButton;
    private String userId;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.vertical_grid);
        getWindow().setBackgroundDrawableResource(R.drawable.grid_bg);

        mSignInButton = findViewById(R.id.sign_in_button);
        mSignInButton.setSize(SignInButton.SIZE_WIDE);
        mSignInButton.setOnClickListener(this);

        GoogleSignInOptions gso = setupGoogleSignInOptions();
        buildGoogleApiClient(gso);
        setupFirebaseAuth();

        mVerticalGridLayout = (FrameLayout)findViewById(R.id.vertical_grid_fragment);
        mVerticalGridLayout.setVisibility(LinearLayout.GONE);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.vertical_grid_fragment, new VerticalGridFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                updateUI(null);
                Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void updateUI(FirebaseUser user) {
        if (user != null) {
            userId=user.getUid();
            mSignInButton.setVisibility(LinearLayout.GONE);
            mVerticalGridLayout.setVisibility(LinearLayout.VISIBLE);
        }
    }
}
