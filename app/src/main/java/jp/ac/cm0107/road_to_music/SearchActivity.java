package jp.ac.cm0107.road_to_music;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchActivity extends AppCompatActivity {
    public static SpotifyAppRemote mSpotifyAppRemote;
    static final String CLIENT_ID = "304b769601404fb3a87f98ca093f9bf9";
    static final String REDIRECT_URI = "http://22cm0107.main.jp/SpotifySDK-redirection";
    SpotifyService spotify;
    SpotifyApi api;
    SearchView searchView;
    ListView list;
    Intent backIntent;
    private OnBackPressedCallback mBackButtonCallback;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mBackButtonCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // バックボタンが押された際の処理?
                Log.d(TAG, "handleOnBackPressed: ");
                backIntent = new Intent();
                setResult(RESULT_CANCELED, backIntent);

                finish();
            }
        };
                    getOnBackPressedDispatcher().
                    addCallback(this,mBackButtonCallback);


        searchView = findViewById(R.id.search);
        list = findViewById(R.id.list);

        api = new SpotifyApi();

        SpotifyAppRemote.disconnect(mSpotifyAppRemote);

        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();
        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");
                        api.setAccessToken(CredentialsHandler.getToken(SearchActivity.this));
                        spotify = api.getService();



                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);


                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchStr) {
                //検索ボタン押したときに反映
                spotify.searchTracks(searchStr, new Callback<TracksPager>() {
                    @Override
                    public void success(TracksPager tracksPager, Response response) {
                        ArrayList<Map<String, Object>> listData = new ArrayList<>();
                        for (int i = 0; i < tracksPager.tracks.items.size(); i++) {
                            Log.d("TrackName",
                                    "success: "+ tracksPager.tracks.items.get(i).name);
                            Map<String, Object> item = new HashMap<>();
                            long trackMS = tracksPager.tracks.items.get(i).duration_ms;
                            long minutes = TimeUnit.MILLISECONDS.toMinutes(trackMS);
                            long seconds = TimeUnit.MILLISECONDS.toSeconds(trackMS);
                            seconds = seconds%60;
                            String trackMinSec;
                            if (seconds < 10){
                                trackMinSec = minutes+":0"+seconds;
                            }else {
                                trackMinSec = minutes+":"+seconds;
                            }

                            item.put("TrackMinSec", trackMinSec);
                            item.put("TrackName", tracksPager.tracks.items.get(i).name);
                            item.put("ArtistName", tracksPager.tracks.items.get(i).artists.get(0).name);
                            item.put("imageArt", tracksPager.tracks.items.get(i).album.images.get(2).url);
                            item.put("TrackURI", tracksPager.tracks.items.get(i).uri);

                            listData.add(item);
                        }//取り出し終わり
                        String[] from_template = {};
                        int[] to_template = {};
                        ImageListAdapter ilAdapter = new ImageListAdapter(
                                SearchActivity.this,
                                listData,
                                R.layout.list_item,
                                from_template,
                                to_template
                        );
                        list.setAdapter(ilAdapter);
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                String urlTest = listData.get(i).get("TrackURI").toString();
                                Log.d("listData", "onItemClick: "+listData.get(i).get("TrackURI"));

                                mSpotifyAppRemote.getPlayerApi().play(urlTest);
                            }
                        });
                    }
                    @Override
                    public void failure(RetrofitError error) {
                        Log.d("Error", "failure: "+ error);
                    }
                });

                return false;
            }


            @Override
            public boolean onQueryTextChange(String s) {
                //動的に検索結果反映
                return false;
            }
        });
    }
//    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        super.onActivityResult(requestCode, resultCode, intent);
//
//        // Check if result comes from the correct activity
//        if (requestCode == REQUEST_CODE) {
//            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);
//
//            switch (response.getType()) {
//                // Response was successful and contains auth token
//                case TOKEN:
//                    Log.d(TAG, "Got token: " + response.getAccessToken());
//                    CredentialsHandler.setToken(this, response.getAccessToken(), response.getExpiresIn(), TimeUnit.SECONDS);
//                    api.setAccessToken(response.getAccessToken());//アクセストークンをここにいれるpoi？
//
//                    spotify = api.getService();
//                    //いけた
//
//                    // Handle successful response
//                    break;
//
//                // Auth flow returned an error
//                case ERROR:
//                    Log.d(TAG,"ERROR");
//                    // Handle error response
//                    break;
//
//                // Most likely auth flow was cancelled
//                default:
//                    // Handle other cases
//            }
//        }
//    }
}