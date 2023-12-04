package jp.ac.cm0107.road_to_music;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.spotify.android.appremote.api.SpotifyAppRemote;

public class MainActivity extends AppCompatActivity {
    private static SpotifyAppRemote mSpotifyAppRemote;
    private static final String CLIENT_ID = "304b769601404fb3a87f98ca093f9bf9";
    private static final String REDIRECT_URI = "http://22cm0107.main.jp/SpotifySDK-redirection";

    Button mBtn_Advanced = findViewById(R.id.btn_Advanced);
    Button mBtn_Category = findViewById(R.id.btn_Category);
    Button mBtn_NewPlay = findViewById(R.id.btn_NewPlay);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




    }
}