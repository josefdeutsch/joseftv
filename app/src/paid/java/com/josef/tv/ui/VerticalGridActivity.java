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

package com.josef.tv.ui;

import android.content.Intent;
import android.os.Bundle;

import com.josef.tv.tvleanback.R;

public class VerticalGridActivity extends LeanbackActivity {

    /**
     * Called when the activity is first created.
     */
    public static final int REQUEST_CODE = 1;

    private static final String TAG = "VerticalGridActivity";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vertical_grid);
        getWindow().setBackgroundDrawableResource(R.color.default_background);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, AuthenticationActivity.class);
        startActivityForResult(intent , REQUEST_CODE);
        this.finishAfterTransition();
    }
}
