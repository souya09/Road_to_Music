package jp.ac.cm0107.road_to_music;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LastPlayHandler {
    private static final String ACCESS_TOKEN_NAME = "LastPlayData.access_token";
    private static final String ACCESS_TOKEN_TN = "trackName_";
    private static final String ACCESS_TOKEN_AN = "artistName_";
    private static final String ACCESS_TOKEN_URL = "url_";
    private static final String ACCESS_TOKEN_URI = "uri_";
    private static final String ACCESS_TOKEN_DURATION = "durationMs_";
    private static final String ACCESS_TOKEN_SIZE = "listSize";
    public static void setListSize(Context context, int listSize) {
        Context appContext = context.getApplicationContext();
        SharedPreferences sharedPref = getSharedPreferences(appContext);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt(ACCESS_TOKEN_SIZE, listSize);
        editor.apply();
    }
    public static void setLastPlayData
            (Context context, String trackName, String artName,
                String url, String uri, long durationMs, int i) {
        Context appContext = context.getApplicationContext();
        SharedPreferences sharedPref = getSharedPreferences(appContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (i==0) {
            editor.clear().commit();
        }

        editor.putString(ACCESS_TOKEN_TN+i,trackName);
        editor.putString(ACCESS_TOKEN_AN+i,artName);
        editor.putString(ACCESS_TOKEN_URL+i,url);
        editor.putString(ACCESS_TOKEN_URI+i,uri);
        editor.putLong(ACCESS_TOKEN_DURATION+i,durationMs);

        editor.apply();
    }

    public static  Map<String, Object> getLastPlayData(Context context ,int i) {
            Context appContext = context.getApplicationContext();
            SharedPreferences sharedPref = getSharedPreferences(appContext);

            String trackName = sharedPref.getString(ACCESS_TOKEN_TN+i,null);
        String artName = sharedPref.getString(ACCESS_TOKEN_AN+i,null);
        String url = sharedPref.getString(ACCESS_TOKEN_URL+i,null);
        String uri = sharedPref.getString(ACCESS_TOKEN_URI+i,null);
        long durationMs = sharedPref.getLong(ACCESS_TOKEN_DURATION+i,0);

        Map<String, Object> item = new HashMap<>();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs);
        seconds = seconds%60;
        String trackMinSec;
        if (seconds < 10){
            trackMinSec = minutes+":0"+seconds;
        }else {
            trackMinSec = minutes+":"+seconds;
        }

        item.put("TrackName", trackName);
        item.put("ArtistName", artName);
        item.put("imageArt", url);
        item.put("TrackURI", uri);
        item.put("TrackMinSec", trackMinSec);

        return  item;
    }
    public static String[] getTrackURI(Context context, int trackListSize){
        Context appContext = context.getApplicationContext();
        SharedPreferences sharedPref = getSharedPreferences(appContext);
        String uris[] = new String[trackListSize];
        for (int i = 0; i < trackListSize; i++) {
            uris[i] = sharedPref.getString(ACCESS_TOKEN_URI + i, null);
        }

        return uris;
    }
    public static int getListSize(Context context){
        Context appContext = context.getApplicationContext();
        SharedPreferences sharedPref = getSharedPreferences(appContext);
        int listSize = sharedPref.getInt(ACCESS_TOKEN_SIZE,20);
        return listSize;
    }

    private static SharedPreferences getSharedPreferences(Context appContext) {
        return appContext.getSharedPreferences
                        (ACCESS_TOKEN_NAME, Context.MODE_PRIVATE);
    }


}
