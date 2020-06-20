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

package com.example.android.tvleanback.data;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;
import com.example.android.tvleanback.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * FetchVideoService is responsible for fetching the videos from the Internet and inserting the
 * results into a local SQLite database.
 */

public class FetchVideoService extends IntentService {
    private static final String TAG = "FetchVideoService";
    String inputs = "{\"googlevideos\":[{\"category\":\"Google+\",\"videos\":[{\"description\":\"Lorem Ipsum...\",\"sources\":\"[http://joseph3d.com/wp-content/uploads/2019/06/g0001.mp4]\",\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00010621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00010621.png\",\"title\":\"material :gold, sculpture0 :abstract\",\"studio\":\"Google+\"}]}]}";

    /**
     * Creates an IntentService with a default name for the worker thread.
     */

    public FetchVideoService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {

        String data = workIntent.getStringExtra("data");

        Log.d(TAG, "onHandleIntent: "+data);

        VideoDbBuilder builder = new VideoDbBuilder(getApplicationContext());

        try {
            List<ContentValues> contentValuesList =
                   builder.fetch(data);
                  //builder.fetch(inputs);

            ContentValues[] downloadedVideoContentValues =
                    contentValuesList.toArray(new ContentValues[contentValuesList.size()]);

            getApplicationContext().getContentResolver().bulkInsert(VideoContract.VideoEntry.CONTENT_URI,
                    downloadedVideoContentValues);
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error occurred in downloading videos");
            e.printStackTrace();
        }
    }

}
