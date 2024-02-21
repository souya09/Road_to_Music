package jp.ac.cm0107.road_to_music;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.slider.RangeSlider;

import java.util.HashMap;
import java.util.List;

public class SettingActivity extends AppCompatActivity {
    Switch sw;
    RangeSlider rs_1,rs_2,rs_3,rs_4,rs_5,rs_6,rs_7,rs_8;
    public static List<Integer> popularity;//有名度
    public static List<Float> tempo;//BPM
    public static List<Float> valence; //明るさ
    public static List<Float> energy;//エネルギッシュ
    public static List<Float> acousticness;//アコースティック
    public static List<Float> danceabillty;//踊りやすさ
    public static List<Float> instrumentalness;//インストっぽさ
    public static List<Float> liveness;//ライブっぽさ
    Button saveBtn;
    Boolean adb_ON_OFF;
    private Integer min_popularity;
    private Integer max_popularity;
    private OnBackPressedCallback mBackButtonCallback;
    private Intent backIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
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


        sw = findViewById(R.id.switch_Advanced);
        rs_1 = findViewById(R.id.Rs_1);
        rs_2 = findViewById(R.id.Rs_2);
        rs_3 = findViewById(R.id.Rs_3);
        rs_4 = findViewById(R.id.Rs_4);
        rs_5 = findViewById(R.id.Rs_5);
        rs_6 = findViewById(R.id.Rs_6);
        rs_7 = findViewById(R.id.Rs_7);
        rs_8 = findViewById(R.id.Rs_8);
        saveBtn = findViewById(R.id.save_Set_btn);

        min_popularity = (int) rs_1.getValueFrom();
        max_popularity = (int) rs_1.getValueTo();
        tempo = rs_2.getValues();
        valence = rs_3.getValues();
        energy = rs_4.getValues();
        acousticness = rs_5.getValues();
        danceabillty = rs_6.getValues();
        instrumentalness = rs_7.getValues();
        liveness = rs_8.getValues();

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                adb_ON_OFF = b;

            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                if (adb_ON_OFF){

                    HashMap<String, Object> options = new HashMap<>();
                    options.put("market","JP");

                    //TODO ここどうしよう...
                    options.put("seed_genres","anime");

                    options.put("min_popularity",min_popularity);
                    options.put("max_popularity",max_popularity);
                    options.put("min_tempo",tempo.get(0));
                    options.put("max_tempo",tempo.get(1));
                    options.put("min_valance",valence.get(0));
                    options.put("max_valance",valence.get(1));
                    options.put("min_energy",energy.get(0));
                    options.put("max_energy",energy.get(1));
                    options.put("min_acousticness",acousticness.get(0));
                    options.put("max_acousticness",acousticness.get(1));
                    options.put("min_danceabillty",danceabillty.get(0));
                    options.put("max_danceabillty",danceabillty.get(1));
                    options.put("min_instrumentalness",instrumentalness.get(0));
                    options.put("max_instrumentalness",instrumentalness.get(1));
                    options.put("min_liveness",liveness.get(0));
                    options.put("max_liveness",liveness.get(1));


                    intent.putExtra("adv_data", (HashMap<String, Object>) options);
//                    intent.putIntegerArrayListExtra("adb_list",(ArrayList) adbList);
                    setResult(99,intent);
                    finish();
                }else {
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }
            }
        });

        rs_1.addOnSliderTouchListener(new PopularityRS());
        rs_2.addOnSliderTouchListener(new TempoRS());
        rs_3.addOnSliderTouchListener(new ValenceRS());
        rs_4.addOnSliderTouchListener(new EnergyRS());
        rs_5.addOnSliderTouchListener(new AcousticnessRS());
        rs_6.addOnSliderTouchListener(new DanceabilltyRS());
        rs_7.addOnSliderTouchListener(new InstrumentalnessRS());
        rs_8.addOnSliderTouchListener(new LivenessRS());



    }

//==================ここからRangeSliderの設定========================//
    private class PopularityRS implements RangeSlider.OnSliderTouchListener {
        @Override
        public void onStartTrackingTouch(@NonNull RangeSlider slider) {

        }

        @Override
        public void onStopTrackingTouch(@NonNull RangeSlider slider) {
            min_popularity = (int) slider.getValueFrom();
            max_popularity = (int) slider.getValueTo();
        }
    }

    private class TempoRS implements RangeSlider.OnSliderTouchListener {
        @Override
        public void onStartTrackingTouch(@NonNull RangeSlider slider) {

        }

        @Override
        public void onStopTrackingTouch(@NonNull RangeSlider slider) {
            tempo = slider.getValues();
        }
    }

    private class EnergyRS implements RangeSlider.OnSliderTouchListener {
        @Override
        public void onStartTrackingTouch(@NonNull RangeSlider slider) {

        }

        @Override
        public void onStopTrackingTouch(@NonNull RangeSlider slider) {
            energy = slider.getValues();
        }
    }

    private class AcousticnessRS implements RangeSlider.OnSliderTouchListener {
        @Override
        public void onStartTrackingTouch(@NonNull RangeSlider slider) {

        }

        @Override
        public void onStopTrackingTouch(@NonNull RangeSlider slider) {
            acousticness = slider.getValues();
        }
    }

    private class DanceabilltyRS implements RangeSlider.OnSliderTouchListener {
        @Override
        public void onStartTrackingTouch(@NonNull RangeSlider slider) {

        }

        @Override
        public void onStopTrackingTouch(@NonNull RangeSlider slider) {
            danceabillty = slider.getValues();
        }
    }

    private class InstrumentalnessRS implements RangeSlider.OnSliderTouchListener {
        @Override
        public void onStartTrackingTouch(@NonNull RangeSlider slider) {

        }

        @Override
        public void onStopTrackingTouch(@NonNull RangeSlider slider) {
            instrumentalness = slider.getValues();
        }
    }

    private class LivenessRS implements RangeSlider.OnSliderTouchListener {
        @Override
        public void onStartTrackingTouch(@NonNull RangeSlider slider) {

        }

        @Override
        public void onStopTrackingTouch(@NonNull RangeSlider slider) {
            liveness = slider.getValues();
        }
    }

    private class ValenceRS implements RangeSlider.OnSliderTouchListener {
        @Override
        public void onStartTrackingTouch(@NonNull RangeSlider slider) {

        }

        @Override
        public void onStopTrackingTouch(@NonNull RangeSlider slider) {
            valence = slider.getValues();
        }
    }
    //=====================ここまで========================//



}