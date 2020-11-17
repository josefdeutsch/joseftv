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
import androidx.constraintlayout.widget.ConstraintLayout;

import static com.example.android.tvleanback.ui.AuthenticationActivity.mAuth;

public class VerticalGridActivity extends LeanbackActivity {

    /**
     * Called when the activity is first created.
     */


    private static final String TAG = "VerticalGridActivity";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vertical_grid);
        getWindow().setBackgroundDrawableResource(R.color.default_background);
    }




    @Override
    public void onBackPressed(){
        startActivity(new Intent(this, AuthenticationActivity.class));
        this.finishAfterTransition();
    }
}
