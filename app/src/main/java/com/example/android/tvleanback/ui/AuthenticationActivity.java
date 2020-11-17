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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.leanback.app.GuidedStepSupportFragment;
import androidx.leanback.widget.GuidanceStylist;
import androidx.leanback.widget.GuidedAction;
import android.text.InputType;
import android.util.Log;
import android.widget.Toast;
import com.example.android.tvleanback.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import static android.content.ContentValues.TAG;

public class AuthenticationActivity extends FragmentActivity {
    private static final int CONTINUE = 2;
    private static final int CONTINUE1 = 3;
    private static final int CONTINUE2 = 4;

    private static final int PASSWORD = 5;
    private static final int EMAIL = 6;

    private static final String TAG = "AuthenticationActivity";


    public static FirebaseAuth mAuth;

    public static String email;
    public static String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        if (null == savedInstanceState) {
            GuidedStepSupportFragment.addAsRoot(this, new FirstStepFragment(), android.R.id.content);
        }
        IntentFilter filter = new IntentFilter("com.yourcompany.testIntent");
        this.registerReceiver(networkReceiver, filter);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        this.unregisterReceiver(networkReceiver);
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
            //getFragmentManager().beginTransaction().add(android.R.id.content, BrowseErrorFragment.class).commit();
           // GuidedActionEditText guidedActionEditText = new GuidedActionEditText(getActivity());

            GuidedAction enterUsername = new GuidedAction.Builder(getContext())
                    .id(EMAIL)
                    .title(getString(R.string.pref_title_username))
                    .descriptionEditable(true)
                    .descriptionInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_CLASS_TEXT)
                    .focusable(true)
                    .build();

            GuidedAction enterPassword = new GuidedAction.Builder(getContext())
                    .id(PASSWORD)
                    .title(getString(R.string.pref_title_password))
                    .descriptionEditable(true)
                    .descriptionInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT)
                    .build();


            GuidedAction login = new GuidedAction.Builder(getContext())
                    .id(CONTINUE)
                    .title(getString(R.string.guidedstep_email))
                    .build();
            GuidedAction google = new GuidedAction.Builder(getContext())
                    .id(CONTINUE1)
                    .title(getString(R.string.guidedstep_google))
                    .build();
            GuidedAction facebook = new GuidedAction.Builder(getContext())
                    .id(CONTINUE2)
                    .title(getString(R.string.guidedstep_facebook))
                    .build();


            actions.add(enterUsername);
            actions.add(enterPassword);
            actions.add(login);
            //  actions.add(google);
            //  actions.add(facebook);


        }


        @Override
        public void onGuidedActionClicked(GuidedAction action) {
            if (action.getId() == CONTINUE) {
                Log.d(TAG, "onGuidedActionClicked: " + action.toString());
                Log.d(TAG, "onGuidedActionClicked: "+email);
                Log.d(TAG, "onGuidedActionClicked: "+password);

                if(email != null && password != null){
                   mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           Toast.makeText(FirstStepFragment.this.getActivity(), "Welcome!", Toast.LENGTH_SHORT).show();
                       }
                   });
               }
                // Assume the user was logged in
                //    Toast.makeText(getActivity(), "Welcome!", Toast.LENGTH_SHORT).show();
                // startActivity(new Intent(getContext(), VerticalGridActivity.class));
                //  getActivity().finishAfterTransition();

            }
            if (action.getId() == EMAIL) {
                email = action.toString();
                Intent intent = new Intent("com.yourcompany.testIntent");
                intent.putExtra("email",email);
                getContext().sendBroadcast(intent);
            }
            if (action.getId() == PASSWORD) {
                password = action.toString();
                Intent intent = new Intent("com.yourcompany.testIntent");
                intent.putExtra("password",password);
                getContext().sendBroadcast(intent);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {

        private final User user = new User();

        @Override
        public void onReceive(final Context context, final Intent intent) {
            String email =  intent.getExtras().getString("email");
            String password =  intent.getExtras().getString("password");

            Log.d(TAG, "onReceive: "+email);
            Log.d(TAG, "onReceive: "+password);

           // user.rememberInput(email,password);


        }

        class User{

            String email;
            String password;

            public void rememberInput(String email,String password){
                this.email = email;
                this.password =  password;

                if(this.email != null && this.password !=null){
                    Log.d(TAG, "rememberInput: "+"sdfsdfsdfsdfsdfsdf");
                }

            }

        }
    };
}
