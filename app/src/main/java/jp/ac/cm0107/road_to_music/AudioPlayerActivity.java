package jp.ac.cm0107.road_to_music;

import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.ErrorCallback;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Capabilities;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.PlayerContext;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import java.util.Locale;

public class AudioPlayerActivity extends AppCompatActivity {
    public static SpotifyAppRemote mSpotifyAppRemote;
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private TextView songTitleText;
    private ImageView albumArtImage;
    private ImageButton playPauseButton;
    private ImageButton nextButton;
    private ImageButton prevButton;
    private Button connectBtn;
    private Button disconnectBtn;
    private SeekBar mSeekBar;
    TrackProgressBar mTrackProgressBar;
    private final ErrorCallback mErrorCallback = this::logError;
    Subscription<PlayerState> mPlayerStateSubscription;
    Subscription<PlayerContext> mPlayerContextSubscription;
    Subscription<Capabilities> mCapabilitiesSubscription;

    private void showDialog(String title, String message) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).create().show();
    }
    public void showCurrentPlayerContext(View view) {
        if (view.getTag() != null) {
            showDialog("PlayerContext", gson.toJson(view.getTag()));
        }
    }

    public void showCurrentPlayerState(View view) {
        if (view.getTag() != null) {
            showDialog("PlayerState", gson.toJson(view.getTag()));
        }
    }

    private final Subscription.EventCallback<PlayerState> mPlayerStateEventCallback =
            new Subscription.EventCallback<PlayerState>() {
                @Override
                public void onEvent(PlayerState playerState) {
                    if (playerState.track != null){
//                        songTitleText.findViewById(R.id.songTitleTextView);
                        songTitleText.setText(
//                        Log.d("playerState log", "onEvent: "+
                        String.format(
                                Locale.US, "%s\n%s", playerState.track.name, playerState.track.artist.name));
                        songTitleText.setTag(playerState);
//                        Log.d("playerState log", "onEvent: "+playerState);
                    }
                    Log.d(TAG, "onEvent: "+playerState.track );
                    if (playerState.track != null) {
                        if (playerState.playbackSpeed > 0) {
                            mTrackProgressBar.unpause();
                        } else {
                            mTrackProgressBar.pause();
                        }
                        // Get image from track
                        mSpotifyAppRemote
                                .getImagesApi()
                                .getImage(playerState.track.imageUri, Image.Dimension.LARGE)
                                .setResultCallback(
                                        bitmap -> {
                                            albumArtImage.setImageBitmap(bitmap);
                                        });
                        // Invalidate seekbar length and position
                        Log.d(TAG, "onEvent: "+playerState.track.duration);
                        mSeekBar.setMax((int) playerState.track.duration);
                        mTrackProgressBar.setDuration(playerState.track.duration);
                        mTrackProgressBar.update(playerState.playbackPosition);

                    }
                    mSeekBar.setEnabled(true);
                }
            };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        playPauseButton = (ImageButton) findViewById(R.id.playPauseButton);
        playPauseButton.setOnClickListener(new PlayPause());
        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new nextBtnClick());
        prevButton = findViewById(R.id.prevButton);
        prevButton.setOnClickListener(new prevBtnClick());
        songTitleText = findViewById(R.id.songTitleTextView);
        albumArtImage = findViewById(R.id.albumArtImageView);
        connectBtn = findViewById(R.id.connect_button);
        disconnectBtn = findViewById(R.id.disconnect_button);





        mSeekBar = (SeekBar)findViewById(R.id.seekBar);
        mSeekBar.setEnabled(false);
        mSeekBar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        mSeekBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        mTrackProgressBar = new TrackProgressBar(mSeekBar);
        connect(false);
    }

    public void connection(View view) {
        connect(false);
    }
    public void disconnection(View view) {
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    private void connect(boolean showAuthView){
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);

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
                        onSubscribedToPlayerStateButtonClicked(null);
                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);


                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }
    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);

    }

    public void onSubscribedToPlayerStateButtonClicked(View view) {

        if (mPlayerStateSubscription != null && !mPlayerStateSubscription.isCanceled()) {
            mPlayerStateSubscription.cancel();
            mPlayerStateSubscription = null;
        }


        mPlayerStateSubscription =
                (Subscription<PlayerState>)
                        mSpotifyAppRemote
                                .getPlayerApi()
                                .subscribeToPlayerState()
                                .setEventCallback(mPlayerStateEventCallback);


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
    private void logMessage(String msg) {
        logMessage(msg);
    }

    //戻る
    private class prevBtnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            mSpotifyAppRemote
                    .getPlayerApi()
                    .skipPrevious()
                    .setErrorCallback(mErrorCallback);
        }
    }

    //進む
    private class nextBtnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            mSpotifyAppRemote
                    .getPlayerApi()
                    .skipNext()

                    .setErrorCallback(mErrorCallback);

        }
    }


}