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

import android.content.ContentValues;
import android.content.Context;
import android.media.Rating;

import androidx.annotation.NonNull;

import android.util.Log;

import com.example.android.tvleanback.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * The VideoDbBuilder is used to grab a JSON file from a server and parse the data
 * to be placed into a local database
 */
public class VideoDbBuilder {
    public static final String TAG_MEDIA = "videos";
    public static final String TAG_GOOGLE_VIDEOS = "googlevideos";
    public static final String TAG_CATEGORY = "category";
    public static final String TAG_STUDIO = "studio";
    public static final String TAG_SOURCES = "sources";
    public static final String TAG_DESCRIPTION = "description";
    public static final String TAG_CARD_THUMB = "card";
    public static final String TAG_BACKGROUND = "background";
    public static final String TAG_TITLE = "title";

    private static final String TAG = "VideoDbBuilder";

    private Context mContext;

    /**
     * Default constructor that can be used for tests
     */
    public VideoDbBuilder() {

    }

    public VideoDbBuilder(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Fetches JSON data representing videos from a server and populates that in a database
     *
     * @param url The location of the video list
     */
    public @NonNull
    List<ContentValues> fetch(String url)
            throws IOException, JSONException {
       JSONObject videoData = getJsonString(url);
       //JSONObject videoData = fetchJSON(url);
        //fetchJSON(url);
        return buildMedia(videoData);
    }
    /**
     * Takes the contents of a JSON object and populates the database
     *
     * @param jsonObj The JSON object of videos
     * @throws JSONException if the JSON object is invalid
     */
    public List<ContentValues> buildMedia(JSONObject jsonObj) throws JSONException {

        JSONArray categoryArray = jsonObj.getJSONArray(TAG_GOOGLE_VIDEOS);
        List<ContentValues> videosToInsert = new ArrayList<>();

        for (int i = 0; i < categoryArray.length(); i++) {
            JSONArray videoArray;

            JSONObject category = categoryArray.getJSONObject(i);
            String categoryName = category.getString(TAG_CATEGORY);
            videoArray = category.getJSONArray(TAG_MEDIA);

            for (int j = 0; j < videoArray.length(); j++) {
                JSONObject video = videoArray.getJSONObject(j);

                // If there are no URLs, skip this video entry.
                JSONArray urls = video.optJSONArray(TAG_SOURCES);
                if (urls == null || urls.length() == 0) {
                    continue;
                }

                String title = video.optString(TAG_TITLE);
                String description = video.optString(TAG_DESCRIPTION);
                String videoUrl = (String) urls.get(0); // Get the first video only.
                String bgImageUrl = video.optString(TAG_BACKGROUND);
                String cardImageUrl = video.optString(TAG_CARD_THUMB);
                String studio = video.optString(TAG_STUDIO);

                ContentValues videoValues = new ContentValues();
                videoValues.put(VideoContract.VideoEntry.COLUMN_CATEGORY, categoryName);
                videoValues.put(VideoContract.VideoEntry.COLUMN_NAME, title);
                videoValues.put(VideoContract.VideoEntry.COLUMN_DESC, description);
                videoValues.put(VideoContract.VideoEntry.COLUMN_VIDEO_URL, videoUrl);
                videoValues.put(VideoContract.VideoEntry.COLUMN_CARD_IMG, cardImageUrl);
                videoValues.put(VideoContract.VideoEntry.COLUMN_BG_IMAGE_URL, bgImageUrl);
                videoValues.put(VideoContract.VideoEntry.COLUMN_STUDIO, studio);

                // Fixed defaults.
                videoValues.put(VideoContract.VideoEntry.COLUMN_CONTENT_TYPE, "video/mp4");
                videoValues.put(VideoContract.VideoEntry.COLUMN_IS_LIVE, false);
                videoValues.put(VideoContract.VideoEntry.COLUMN_AUDIO_CHANNEL_CONFIG, "2.0");
                videoValues.put(VideoContract.VideoEntry.COLUMN_PRODUCTION_YEAR, 2014);
                videoValues.put(VideoContract.VideoEntry.COLUMN_DURATION, 0);
                videoValues.put(VideoContract.VideoEntry.COLUMN_RATING_STYLE,
                        Rating.RATING_5_STARS);
                videoValues.put(VideoContract.VideoEntry.COLUMN_RATING_SCORE, 3.5f);
                if (mContext != null) {
                    videoValues.put(VideoContract.VideoEntry.COLUMN_PURCHASE_PRICE,
                            mContext.getResources().getString(R.string.buy_2));
                    videoValues.put(VideoContract.VideoEntry.COLUMN_RENTAL_PRICE,
                            mContext.getResources().getString(R.string.rent_2));
                    videoValues.put(VideoContract.VideoEntry.COLUMN_ACTION,
                            mContext.getResources().getString(R.string.global_search));
                }

                // TODO: Get these dimensions.
                videoValues.put(VideoContract.VideoEntry.COLUMN_VIDEO_WIDTH, 1280);
                videoValues.put(VideoContract.VideoEntry.COLUMN_VIDEO_HEIGHT, 720);

                videosToInsert.add(videoValues);
            }
        }
        return videosToInsert;
    }

    /**
     * Fetch JSON object from a given URL.
     *
     * @return the JSONObject representation of the response
     * @throws JSONException
     * @throws IOException
     */


    private JSONObject getJsonString(String input) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        return new JSONObject(before2);
    }

    String statics = "{\"googlevideos\":[{\"category\":\"Google+\",\"videos\":[{\"description\":\"Jon introduces Instant Upload with a few thoughts on how we remember the things that matter. Check out some ways we've been rethinking real-life sharing for the web at plus.google.com\",\"sources\":[\"https://storage.googleapis.com/android-tv/Sample%20videos/Google%2B/Google%2B_%20Instant%20Upload.mp4\"],\"card\":\"https://storage.googleapis.com/android-tv/Sample%20videos/Google%2B/Google%2B_%20Instant%20Upload/card.jpg\",\"background\":\"https://storage.googleapis.com/android-tv/Sample%20videos/Google%2B/Google%2B_%20Instant%20Upload/bg.jpg\",\"title\":\"Instant Upload\",\"studio\":\"Google+\"}]}]}";

    String before = "{\"googlevideos\":[{\"category\":\"Google+\",\"videos\":[{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0001.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00010621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00010621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0002.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00020621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00020621.png\",\"title\":\"material\",\"studio\":\"Google+\"}]}]}";

    String before2 = "{\"googlevideos\":[{\"category\":\"Google+\",\"videos\":[{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0001.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00010621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00010621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0002.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00020621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00020621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0003.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00030621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00030621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0004.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00040621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00040621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0005.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00050621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00050621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0006.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00060621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00060621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0007.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00070621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00070621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0008.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00080621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00080621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0009.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00090621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00090621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0010.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00100621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00100621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0011.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00110621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00110621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0012.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00120621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00120621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0013.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00130621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00130621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0014.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00140621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00140621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0015.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00150621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00150621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0016.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00160621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00160621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0017.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00170621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00170621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0018.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00180621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00180621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0019.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00190621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00190621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0020.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00200621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00200621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0021.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00210621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00210621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0022.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00220621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00220621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0023.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00230621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00230621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0024.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00240621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00240621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0025.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00250621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00250621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0026.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00260621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00260621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0027.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00270621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00270621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0028.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00280621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00280621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0029.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00290621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00290621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0030.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00300621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00300621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0031.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00310621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00310621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0032.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00320621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00320621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0033.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00330621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00330621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0034.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00340621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00340621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0035.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00350621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00350621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0036.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00360621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00360621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0037.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00370621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00370621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0038.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00380621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00380621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0039.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00390621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00390621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0040.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00400621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00400621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0041.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00410621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00410621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0042.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00420621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00420621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0043.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00430621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00430621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0044.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00440621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00440621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0045.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00450621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00450621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0046.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00460621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00460621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0047.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00470621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00470621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0048.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00480621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00480621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0049.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00490621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00490621.png\",\"title\":\"material\",\"studio\":\"Google+\"},{\"description\":\"LoremIpsum...\",\"sources\":[\"http://joseph3d.com/wp-content/uploads/2019/06/g0050.mp4\"],\"card\":\"http://joseph3d.com/wp-content/uploads/2019/06/00500621.png\",\"background\":\"http://joseph3d.com/wp-content/uploads/2019/06/00500621.png\",\"title\":\"material\",\"studio\":\"Google+\"}]}]}";



    private JSONObject fetchJSON(String urlString) throws JSONException, IOException {
        /**BufferedReader reader = null;
        java.net.URL url = new java.net.URL(urlString);
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        try {
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),
                    "utf-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();
            return new JSONObject(json);
        } finally {
            urlConnection.disconnect();
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "JSON feed closed", e);
                }
            }
        }**/

        BufferedReader reader = null;
        java.net.URL url = new java.net.URL(urlString);
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        try {
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),
                    "utf-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();
            //Log.d(TAG, "fetchJSON: "+json);
            JSONObject jsonObject = new JSONObject(before);
            Log.d(TAG, "fetchJSON: "+jsonObject.toString());
            return jsonObject;

        } finally {
            urlConnection.disconnect();
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "JSON feed closed", e);
                }
            }
        }
    }
}
