package jp.ac.cm0107.road_to_music;

import static android.content.ContentValues.TAG;
import static com.spotify.sdk.android.auth.LoginActivity.REQUEST_CODE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Recommendations;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity {
    private static final String ACCESS_TOKEN_NAME = "temp_Data";
    private static SpotifyAppRemote mSpotifyAppRemote;
    static final String CLIENT_ID = "304b769601404fb3a87f98ca093f9bf9";
    static final String REDIRECT_URI = "http://22cm0107.main.jp/SpotifySDK-redirection";

    private Button btn_PlayList,btn_NewPlay,btn_test,btn_test2,btn_Adv,btn_Category;
    private Spinner spn_Weather,spn_temperature,spn_situation;
    private Spinner spn_time;
    SpotifyService spotify;
    SpotifyApi api;
    
    Boolean adbBool ;
    Long time_set;
    Float target_tempo;
    Float target_energy;
    Float target_valence;
    Integer count_Category;

    HashMap<String,Object> options_adb;
    boolean[] itemChecked;
    String[] item;
    Integer trackListSize ;
    Integer target_popularity;
    Boolean category_set_Bool;
    FragmentContainerView  fragmentContainerView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final  String[] items = {"acoustic", "afrobeat", "alt-rock", "alternative", "ambient", "anime", "black-metal", "bluegrass", "blues", "bossanova", "brazil", "breakbeat", "british", "cantopop", "chicago-house", "children", "chill", "classical", "club", "comedy", "country", "dance", "dancehall", "death-metal", "deep-house", "detroit-techno", "disco", "disney", "drum-and-bass", "dub", "dubstep", "edm", "electro", "electronic", "emo", "folk", "forro", "french", "funk", "garage", "german", "gospel", "goth", "grindcore", "groove", "grunge", "guitar", "happy", "hard-rock", "hardcore", "hardstyle", "heavy-metal", "hip-hop", "holidays", "honky-tonk", "house", "idm", "indian", "indie", "indie-pop", "industrial", "iranian", "j-dance", "j-idol", "j-pop", "j-rock", "jazz", "k-pop", "kids", "latin", "latino", "malay", "mandopop", "metal", "metal-misc", "metalcore", "minimal-techno", "movies", "mpb", "new-age", "new-release", "opera", "pagode", "party", "philippines-opm", "piano", "pop", "pop-film", "post-dubstep", "power-pop", "progressive-house", "psych-rock", "punk", "punk-rock", "r-n-b", "rainy-day", "reggae", "reggaeton", "road-trip", "rock", "rock-n-roll", "rockabilly", "romance", "sad", "salsa", "samba", "sertanejo", "show-tunes", "singer-songwriter", "ska", "sleep", "songwriter", "soul", "soundtracks", "spanish", "study", "summer", "swedish", "synth-pop", "tango", "techno", "trance", "trip-hop", "turkish", "work-out", "world-music"};
        itemChecked = new boolean[items.length];
        category_set_Bool = false;
        trackListSize = 20;
        count_Category = 0;

        btn_PlayList = findViewById(R.id.btn_list);
        btn_NewPlay = (Button) findViewById(R.id.btn_NewPlay);
        btn_test = (Button) findViewById(R.id.testbtn);
        btn_test2 = findViewById(R.id.testbtn2);
        btn_Adv = findViewById(R.id.btn_Advanced);
        btn_Category = findViewById(R.id.btn_Category);
        spn_Weather = findViewById(R.id.weatherSpnView);
        spn_temperature = findViewById(R.id.temperaturerSpnView);
        spn_situation = findViewById(R.id.situationSpnView);
        spn_time = findViewById(R.id.timeSpnView);
        fragmentContainerView = findViewById(R.id.fragment_Audio);
        fragmentContainerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(TAG, "onTouch: ");
                Intent intent = new Intent(MainActivity.this, AudioPlayerActivity.class);
                startActivityForResult(intent,0  );
                overridePendingTransition(R.anim.in_down,R.anim.out_up);
                return false;
            }
        });


        api = new SpotifyApi();
        adbBool = false;

        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming","playlist-modify-public","playlist-modify-public"});
        AuthorizationRequest request = builder.build();

        AuthorizationClient.openLoginActivity(MainActivity.this,
                REQUEST_CODE,
                request);

        settingsSp();
        btn_PlayList.setOnClickListener(new LastPlayList());
        btn_NewPlay.setOnClickListener(new newPlayList());
        btn_test.setOnClickListener(new test());
        btn_Adv.setOnClickListener(new adv());
        btn_Category.setOnClickListener(new category());

        ArrayAdapter<CharSequence> adapter_W = ArrayAdapter.createFromResource(this,
                R.array.Weather, android.R.layout.simple_spinner_item);
        adapter_W.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> adapter_T = ArrayAdapter.createFromResource(this,
                R.array.temperature, android.R.layout.simple_spinner_item);
        adapter_T.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> adapter_S = ArrayAdapter.createFromResource(this,
                R.array.situation, android.R.layout.simple_spinner_item);
        adapter_S.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> adapter_time = ArrayAdapter.createFromResource(this,
                R.array.time, android.R.layout.simple_spinner_item);
        adapter_time.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spn_Weather.setAdapter(adapter_W);
        spn_temperature.setAdapter(adapter_T);
        spn_situation.setAdapter(adapter_S);
        spn_time.setAdapter(adapter_time);

        spn_Weather.setOnItemSelectedListener(new wather_cl());
        spn_temperature.setOnItemSelectedListener(new temperature_cl());
        spn_situation.setOnItemSelectedListener(new situation_cl());
        spn_time.setOnItemSelectedListener(new time_cl());

        btn_test2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String token =
                        CredentialsHandler.getToken(MainActivity.this);
                if (token != null) {
                    Toast.makeText(MainActivity.this,
                            "トークンあるよ", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this,
                            "トークン切れてるよ", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.d(TAG, "onActivityResult: ");
        

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    Log.d(TAG, "Got token: " + response.getAccessToken());
                    CredentialsHandler.setToken(this, response.getAccessToken(), response.getExpiresIn(), TimeUnit.SECONDS);
                    api.setAccessToken(response.getAccessToken());//アクセストークンをここにいれるpoi？

                    spotify = api.getService();
                    //いけた

                    spotify.getMe(new Callback<UserPrivate>() {
                        @Override
                        public void success(UserPrivate userPrivate, Response response) {
                            Log.d(TAG, "success: ");
                            CredentialsHandler.setUser(MainActivity.this,
                                    userPrivate.id,
                                    userPrivate.display_name,
                                    userPrivate.images.get(0).url);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d(TAG, "failure: ");
                        }
                    });
                    // Handle successful response
                    break;

                // Auth flow returned an error
                case ERROR:
                    Log.d(TAG,"ERROR");
                            // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
        else if (requestCode == 1){
            options_adb = (HashMap<String, Object>)
                    intent.getSerializableExtra("adv_data");
            adbBool = true;
            settingsSp();
            Log.d(TAG, "onActivityResult: ");
        }
        else if (requestCode == RESULT_CANCELED){
            settingsSp();
            spotify_API_Connect();
            Log.d(TAG, "onActivityResult: ");
        }
    }
    private void settingsSp(){

        SpotifyAppRemote.disconnect(mSpotifyAppRemote);

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
    private void spotify_API_Connect(){
        api.setAccessToken(CredentialsHandler.getToken(this));
        spotify = api.getService();
        time_set(spn_time.getSelectedItemPosition());   //  Time
    }




    private class newPlayList implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            if (category_set_Bool) {
                view.setEnabled(false);
                if (!adbBool) {
                    options_set();
                }
                mSpotifyAppRemote.getPlayerApi().setRepeat(0);
                mSpotifyAppRemote.getPlayerApi().setShuffle(false);
                for (int i = 0; i <= LastPlayHandler.getListSize(MainActivity.this); i++) {
                    mSpotifyAppRemote.getPlayerApi().skipNext();
                }
                trackListSize = 0;
                Log.d(TAG, "onClick: ");


                //ここサーチ画面遷移してたよ

//            Log.d(TAG, "onClick:" + CredentialsHandler.getToken(MainActivity.this));
//
//            SpotifyAppRemote.disconnect(mSpotifyAppRemote);
//            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
//            startActivity(intent);

//            spotify.searchTracks("蓮ノ空", new Callback<TracksPager>() {
//                @Override
//                public void success(TracksPager tracksPager, Response response) {
//                    Log.d(TAG, "success: " +tracksPager.tracks.items.get(0).name);
//
//
//                    mSpotifyAppRemote.getPlayerApi().play(tracksPager.tracks.items.get(0).uri);
//                    mSpotifyAppRemote.getPlayerApi()
//                            .subscribeToPlayerState()
//                    .setEventCallback(playerState -> {
//                        final Track track = playerState.track;
//                        if (track != null) {
//                            Log.d("MainActivity", track.name + " by " + track.artist.name);
//                        }
//                    });
//                }
//
//                @Override
//                public void failure(RetrofitError error) {
//
//                }
//            });
                spotify.getRecommendations(options_adb, new Callback<Recommendations>() {
                    @Override
                    public void success(Recommendations recommendations, Response response) {

                        Log.d(TAG, "success: ");


                        for (int i = 0; i < recommendations.tracks.size(); i++) {
                            if (i == 0) {
                                mSpotifyAppRemote.getPlayerApi().play(recommendations.tracks.get(i).uri);
                                time_set = time_set - recommendations.tracks.get(i).duration_ms;
                            } else {
                                if (null == time_set || time_set >= 0) {
                                    mSpotifyAppRemote.getPlayerApi().queue(recommendations.tracks.get(i).uri);
                                    time_set = time_set - recommendations.tracks.get(i).duration_ms;
                                    Log.d(TAG, "success: ");
                                } else {
                                    Log.d(TAG, "time_set:end " + time_set);
                                    break;
                                }
                            }
                            trackListSize++;
                            setPlayData(recommendations.tracks.get(i).name,
                                    recommendations.tracks.get(i).artists.get(0).name,
                                    recommendations.tracks.get(i).album.images.get(2).url,
                                    recommendations.tracks.get(i).uri,
                                    recommendations.tracks.get(i).duration_ms,
                                    i);
                        }
                        setTrackListSize(trackListSize);
                        time_set(spn_time.getSelectedItemPosition());
                        Log.d(TAG, "success: ");
                        view.setEnabled(true);
                        // mSpotifyAppRemote.getPlayerApi().play(recommendations.tracks.get(0).uri);
                        mSpotifyAppRemote.getPlayerApi()
                                .subscribeToPlayerState()
                                .setEventCallback(playerState -> {
                                    final Track track = playerState.track;
                                    if (track != null) {
                                        Log.d("MainActivity", track.name + " by " + track.artist.name);
                                    }
                                });

//                    for (int i = 0; i < recommendations.tracks.size(); i++) {
//                        mSpotifyAppRemote.getPlayerApi().queue(recommendations.tracks.get(0).uri);
//                    }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG, "failure: ");
                    }
                });


            }else {
                Toast.makeText(MainActivity.this,
                        "Categoryを１〜５個選択してください",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setTrackListSize(Integer trackListSize) {
        LastPlayHandler.setListSize(this, trackListSize);
    }

    private void setPlayData(String trackName, String artName, String url, String uri, long duration_ms, int i) {
        LastPlayHandler.setLastPlayData(this, trackName, artName, url,uri,duration_ms,i);
    }
    private class LastPlayList implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, OderListActivity.class);
            intent.putExtra("trackListSize",trackListSize);
            startActivityForResult(intent,0);
        }
    }


    private Map<String, Object> options_set() {
        StringJoiner sj = new StringJoiner(",");
        for (int i = 0; item.length>i; i++) {
            if (itemChecked[i]) {
                sj.add(item[i]);
            }
        }

        options_adb = new HashMap<>();

        options_adb.put("market","JP");
        options_adb.put("limit",100);

        options_adb.put("seed_genres",sj.toString());


        options_adb.put("target_tempo",target_tempo);
        options_adb.put("target_energy",target_energy);
        options_adb.put("target_popularity",target_popularity);
        options_adb.put("target_valence",target_valence);
        return options_adb;
    }

    private class test implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            SpotifyAppRemote.disconnect(mSpotifyAppRemote);
            //Intent intent = new Intent(MainActivity.this, RemotePlayerActivity.class);
            Intent intent = new Intent(MainActivity.this, AudioPlayerActivity.class);
            startActivityForResult(intent,0  );
        }
    }




    private class adv implements View.OnClickListener {
        @Override
        public void onClick(View view) {



            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivityForResult(intent,1);
//            Map<String, Object> options = new HashMap<>();
//            options.put("market","JP");
//
//            options.put("seed_genres","anime");
//            options.put("seed_artists","64tJ2EAv1R6UaZqc4iOCyj");
//
//            options.put("max_tempo","180");
//            options.put("target_tempo","144");
//
//            spotify.getRecommendations(options, new Callback<Recommendations>() {
//                @Override
//                public void success(Recommendations recommendations, Response response) {
//                    Log.d("Recommendations", "success: "+recommendations);
//
//                }
//
//                @Override
//                public void failure(RetrofitError error) {
//                    Log.d("RetrofitError", "failure: " + error);
//                }
//            });

        }
    }

    private class category implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            item  = new String[]{"acoustic", "afrobeat", "alt-rock", "alternative", "ambient", "anime", "black-metal", "bluegrass", "blues", "bossanova", "brazil", "breakbeat", "british", "cantopop", "chicago-house", "children", "chill", "classical", "club", "comedy", "country", "dance", "dancehall", "death-metal", "deep-house", "detroit-techno", "disco", "disney", "drum-and-bass", "dub", "dubstep", "edm", "electro", "electronic", "emo", "folk", "forro", "french", "funk", "garage", "german", "gospel", "goth", "grindcore", "groove", "grunge", "guitar", "happy", "hard-rock", "hardcore", "hardstyle", "heavy-metal", "hip-hop", "holidays", "honky-tonk", "house", "idm", "indian", "indie", "indie-pop", "industrial", "iranian", "j-dance", "j-idol", "j-pop", "j-rock", "jazz", "k-pop", "kids", "latin", "latino", "malay", "mandopop", "metal", "metal-misc", "metalcore", "minimal-techno", "movies", "mpb", "new-age", "new-release", "opera", "pagode", "party", "philippines-opm", "piano", "pop", "pop-film", "post-dubstep", "power-pop", "progressive-house", "psych-rock", "punk", "punk-rock", "r-n-b", "rainy-day", "reggae", "reggaeton", "road-trip", "rock", "rock-n-roll", "rockabilly", "romance", "sad", "salsa", "samba", "sertanejo", "show-tunes", "singer-songwriter", "ska", "sleep", "songwriter", "soul", "soundtracks", "spanish", "study", "summer", "swedish", "synth-pop", "tango", "techno", "trance", "trip-hop", "turkish", "work-out", "world-music"};

            itemChecked = new boolean[item.length];
            count_Category = 0;

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("SelectCategory:")
                    .setMultiChoiceItems(
                            R.array.category,
                            itemChecked,
                            new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int i,
                                                    boolean b) {
                                    itemChecked[i] = b;

                                    if (b) {
                                        count_Category++;
                                    }else{
                                        count_Category--;
                                    }
                                    if (count_Category > 5) {
                                        Toast.makeText(MainActivity.this, "選択件数が多すぎます", Toast.LENGTH_SHORT).show();
                                        category_set_Bool = false;
                                    } else {
                                        Toast.makeText(MainActivity.this, "あと" + (5 - count_Category) + "件まで選択可能です", Toast.LENGTH_SHORT).show();
                                        category_set_Bool = true;
                                    }
                                }
                            })

                    .setPositiveButton("Close",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                }
                            })
                    .show();

        }
    }

    private class wather_cl implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            switch (i) {
                case 0:target_valence = 0.85F;
                    break;
                case 1:target_valence = 0.6F;
                    break;
                case 2:target_valence = 0.4F;
                    break;
                case 3:target_valence = null;
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

        private class temperature_cl implements AdapterView.OnItemSelectedListener {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:target_energy = 0.40F;
                        break;
                    case 1:target_energy = 0.45F;
                        break;
                    case 2:target_energy = 0.60F;
                        break;
                    case 3: target_energy = 0.70F;
                        break;
                    case 4: target_energy = 0.80F;
                        break;
                    case 5: target_energy = null;
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        }

    private class situation_cl implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            switch (i) {
                case 0:
                    target_tempo = 50F;
                    target_popularity = null;
                    break;
                case 1:
                    target_tempo = 80F;
                    target_popularity = null;
                    break;
                case 2:
                    target_tempo = 100F;
                    target_popularity = null;
                    break;
                case 3:
                    target_tempo = null;
                    target_popularity = 70;
                    break;
                case 4:
                    target_tempo = null;
                    target_popularity = null;
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private class time_cl implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            time_set(i);
            Log.d(TAG, "onItemSelected: ");
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private void time_set(int i) {
        switch (i) {
            case 0:time_set =  TimeUnit.MINUTES.toMillis(30);
                break;
            case 1:time_set =  TimeUnit.MINUTES.toMillis(60);
                break;
            case 2:time_set =  TimeUnit.MINUTES.toMillis(90);
                break;
            case 3:time_set =  TimeUnit.MINUTES.toMillis(120);
                break;
            case 4:time_set =  TimeUnit.MINUTES.toMillis(180);
                break;
            case 5:time_set = null;
                break;
        }
        Log.d(TAG, "time_set: ");
    }
}

