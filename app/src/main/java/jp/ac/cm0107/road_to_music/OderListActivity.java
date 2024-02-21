package jp.ac.cm0107.road_to_music;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class OderListActivity extends AppCompatActivity {
    public static SpotifyAppRemote mSpotifyAppRemote;
    static final String CLIENT_ID = "304b769601404fb3a87f98ca093f9bf9";
    static final String REDIRECT_URI = "http://22cm0107.main.jp/SpotifySDK-redirection";
    SpotifyService spotify;
    SpotifyApi api;
    ListView list;
    Button btnListSave;
    Intent intent;
    private Integer trackListSize;
    HashMap<String, Object> options ;
    HashMap<String, Object> playlist_uri;
    String user_id;
    EditText edt_PlayListName;
    private OnBackPressedCallback mBackButtonCallback;
    Intent backIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oder_list);
        edt_PlayListName = findViewById(R.id.editTextText);
        btnListSave = findViewById(R.id.lastListButton);
        btnListSave.setOnClickListener(new saveOnClick());

        user_id = CredentialsHandler.getUserId(OderListActivity.this);

        list = (ListView) findViewById(R.id.list_Oder);

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


        spotifyConnect();

        intent = getIntent();
        trackListSize = LastPlayHandler.getListSize(this);

        ArrayList<Map<String, Object>> listData = new ArrayList<>();
        for (int i = 0; i < trackListSize; i++) {
            listData.add(LastPlayHandler.getLastPlayData(this,i));
        }
        Log.d(TAG, "onCreate: ");
        String[] from_template = {};
        int[] to_template = {};
        ImageListAdapter ilAdapter = new ImageListAdapter(
                this,
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

    private void spotifyConnect() {

        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        api = new SpotifyApi();

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
                        api.setAccessToken(CredentialsHandler.getToken(OderListActivity.this));
                        spotify = api.getService();


                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }

                });
    }

    private class saveOnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String str_playListName = edt_PlayListName.getText().toString();
            options = new HashMap<>();
            options.put("name",str_playListName);
            options.put("description","test");
            //options.put("public",false);

            spotify.createPlaylist(user_id, options, new Callback<Playlist>() {
                @Override
                public void success(Playlist playlist, Response response) {
                    Log.d(TAG, "success: ");
                    playlist_uri =  new HashMap<>();
                    playlist_uri.put("uris", LastPlayHandler.getTrackURI(OderListActivity.this,trackListSize));
                    spotify.addTracksToPlaylist(user_id, playlist.id, null, playlist_uri, new Callback<Pager<PlaylistTrack>>() {
                        @Override
                        public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                            Log.d(TAG, "success: ");
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d(TAG, "failure: ");
                        }
                    });

                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d(TAG, "failure: ");
                }
            });

        }
    }
}