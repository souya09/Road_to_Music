package jp.ac.cm0107.road_to_music;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.ErrorCallback;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AudioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AudioFragment extends Fragment  {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button playBtn;
    private TextView title;
    private TextView artist;
    private ImageView image;

    static final String CLIENT_ID = "304b769601404fb3a87f98ca093f9bf9";
    static final String REDIRECT_URI = "http://22cm0107.main.jp/SpotifySDK-redirection";
    private final ErrorCallback mErrorCallback = this::logError;
    private static SpotifyAppRemote mSpotifyAppRemote;
    Subscription<PlayerState> mPlayerStateSubscription;

    public AudioFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AudioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AudioFragment newInstance(String param1, String param2) {
        AudioFragment fragment = new AudioFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void settingsSp() {
        Log.d(TAG, "settingsSp: ");
        //Spotifyに接続
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();
        Log.d(TAG, "settingsSp: ");
        SpotifyAppRemote.connect(this.getContext(), connectionParams, new Connector.ConnectionListener() {
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;
                Log.d("Fragment", "Connected! Yay! to Fragment!!!!!!!");
                onSubscribedToPlayerState();
                // Now you can start interacting with App Remote
            }

            public void onFailure(Throwable throwable) {
                Log.e("MyActivity", throwable.getMessage(), throwable);

                // Something went wrong when attempting to connect! Handle errors here
            }
        });
        Log.d(TAG, "settingsSp: ");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio, container,false);

        playBtn = (Button)view.findViewById(R.id.fragment_PlayBtn);
        title = (TextView)view.findViewById(R.id.fragment_TrackNameList);
        artist = (TextView)view.findViewById(R.id.fragment_ArtistNameList);
        image = (ImageView) view.findViewById(R.id.fragment_image);
        settingsSp();
        //onSubscribedToPlayerState();
        playBtn.setOnClickListener(new playSet());

        // Inflate the layout for this fragment
        return view;
    }

    private void onSubscribedToPlayerState() {
        Log.d(TAG, "onSubscribedToPlayerState: ");
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

    private final Subscription.EventCallback<PlayerState> mPlayerStateEventCallback =
            new Subscription.EventCallback<PlayerState>() {
                @Override
                public void onEvent(PlayerState playerState) {
                    if (playerState.track != null){
                        title.setText(playerState.track.name);
                        title.setTag(playerState);
                        artist.setText(playerState.track.artist.name);
                        artist.setTag(playerState);

                        mSpotifyAppRemote.getImagesApi()
                                .getImage(playerState.track.imageUri, Image.Dimension.THUMBNAIL)
                                .setResultCallback(bitmap -> {
                                    image.setImageBitmap(bitmap);
                                });
                    }

                }

            };

    private class playSet implements View.OnClickListener {
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
        Toast.makeText(getContext(),"error", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "", throwable);
    }
}