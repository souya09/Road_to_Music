package jp.ac.cm0107.road_to_music;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

public class MainActivity extends AppCompatActivity {
    private static SpotifyAppRemote mSpotifyAppRemote;
    static final String CLIENT_ID = "304b769601404fb3a87f98ca093f9bf9";
    static final String REDIRECT_URI = "http://22cm0107.main.jp/SpotifySDK-redirection";

    private Button btn_NewPlay;
    private Button btn_test;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_NewPlay = (Button) findViewById(R.id.btn_NewPlay);
        btn_test = (Button) findViewById(R.id.testbtn);

        //Spotifyに接続
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

                        // Now you can start interacting with App Remote
                        connected();

                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });

        btn_NewPlay.setOnClickListener(new newPlayList());
        btn_test.setOnClickListener(new test());
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    private void connected() {
        //接続成功時実行
        Log.d("Conect", "Connected! Yay!");
    }

    private class newPlayList implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // Play a playlist
                mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:6wB8g69q7vwUj0fFC4c2D2");

            // Subscribe to PlayerState
            mSpotifyAppRemote.getPlayerApi()
                    .subscribeToPlayerState()
                    .setEventCallback(playerState -> {
                        final Track track = playerState.track;
                        if (track != null) {
                            Log.d("MainActivity", track.name + " by " + track.artist.name);
                        }
                    });
        }
    }

    private class test implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            SpotifyAppRemote.disconnect(mSpotifyAppRemote);
            Intent intent = new Intent(MainActivity.this, AudioPlayerActivity.class);
            startActivity(intent);
        }
    }

}
