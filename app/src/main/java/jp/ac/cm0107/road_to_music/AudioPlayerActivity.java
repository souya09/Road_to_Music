package jp.ac.cm0107.road_to_music;

import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.ErrorCallback;
import com.spotify.protocol.types.Track;

public class AudioPlayerActivity extends AppCompatActivity {
    public static SpotifyAppRemote mSpotifyAppRemote;
    private ImageButton playPauseButton;
    private SeekBar mSeekBar;
    TrackProgressBar mTrackProgressBar;
    private final ErrorCallback mErrorCallback = this::logError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        playPauseButton = (ImageButton) findViewById(R.id.playPauseButton);
        playPauseButton.setOnClickListener(new PlayPause());

        mSeekBar = (SeekBar)findViewById(R.id.seekBar);

        mSeekBar.setEnabled(false);
        mSeekBar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        mSeekBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        mTrackProgressBar = new TrackProgressBar(mSeekBar);

        ConnectionParams connectionParams =
                new ConnectionParams.Builder("304b769601404fb3a87f98ca093f9bf9")
                        .setRedirectUri("http://22cm0107.main.jp/SpotifySDK-redirection")
                        .showAuthView(true)
                        .build();
        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");

                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });

    }

    //プログレスバー
    private class TrackProgressBar {



        private static final int LOOP_DURATION = 500;
        private final SeekBar mSeekBar;
        private final Handler mHandler;

        private final SeekBar.OnSeekBarChangeListener mSeekBarChangeListener =
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mSpotifyAppRemote
                                .getPlayerApi()
                                .seekTo(seekBar.getProgress())
                                .setErrorCallback(mErrorCallback);
                    }
                };

        private final Runnable mSeekRunnable =
                new Runnable() {
                    @Override
                    public void run() {
                        int progress = mSeekBar.getProgress();
                        mSeekBar.setProgress(progress + LOOP_DURATION);
                        mHandler.postDelayed(mSeekRunnable, LOOP_DURATION);
                    }
                };

        private TrackProgressBar(SeekBar seekBar) {
            mSeekBar = seekBar;
            mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
            mHandler = new Handler();
        }

        private void setDuration(long duration) {
            mSeekBar.setMax((int) duration);
        }

        private void update(long progress) {
            mSeekBar.setProgress((int) progress);
        }

        private void pause() {
            mHandler.removeCallbacks(mSeekRunnable);
        }

        private void unpause() {
            mHandler.removeCallbacks(mSeekRunnable);
            mHandler.postDelayed(mSeekRunnable, LOOP_DURATION);
        }
    }



    private class PlayPause implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            mSpotifyAppRemote
                    .getPlayerApi()
                    .getPlayerState()
                    .setResultCallback(
                            playerState -> {
                                final Track track = playerState.track;
                                if (track != null) {
                                    Log.d("MainActivity", track.name + " by " + track.artist.name);
                                }
                                if (playerState.isPaused) {
                                    mSpotifyAppRemote
                                            .getPlayerApi()
                                            .resume();

                                } else {
                                    mSpotifyAppRemote
                                            .getPlayerApi()
                                            .pause();

                                }
                            });
        }
    }
    private void logError(Throwable throwable) {
        Toast.makeText(this,"error", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "", throwable);
    }
}