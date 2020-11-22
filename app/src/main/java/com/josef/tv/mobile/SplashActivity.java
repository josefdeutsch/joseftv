/*
 * Copyright 2019 Google LLC
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
package com.josef.tv.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.josef.tv.tvleanback.R;
import com.josef.tv.ui.AuthenticationActivity;
import com.josef.tv.ui.OnboardingActivity;

import static com.josef.tv.ui.VerticalGridActivity.REQUEST_CODE;

public class SplashActivity extends Activity {

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final static int RC_ONBOARD = 1;
    public final static int RC_ONAUTH = 2;

    private static SplashActivity splashActivity;

    private static final String TAG = "SplashActivity";

    public static SplashActivity getSplashActivity() {
        return splashActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getSplashActivity().startActivityForResult(
                        new Intent(getSplashActivity(),OnboardingActivity.class),RC_ONBOARD
                );
            }
        }, 3000);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == RC_ONBOARD && resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: ");
                getSplashActivity().startActivityForResult(
                        new Intent(getSplashActivity(),AuthenticationActivity.class),RC_ONAUTH
                );
                getSplashActivity().finishAfterTransition();
            }
            if (requestCode == RC_ONAUTH && resultCode == RESULT_OK) {
                getSplashActivity().finishAfterTransition();
            }

        } catch (Exception ex) {
            Toast.makeText(this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        splashActivity = this;
    }

    @Override
    public void onStop() {
        super.onStop();
        splashActivity = this;
    }
}
