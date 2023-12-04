package jp.ac.cm0107.road_to_music;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MyDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(requireActivity())
                .setTitle("タイトル")
                .setMessage("ここにメッセージを入力します")
                .setPositiveButton("OK", (dialog, id) -> {
                    // このボタンを押した時の処理を書きます。
                })
                .setNegativeButton("キャンセル", null)
                .setNeutralButton("あとで", null)
                .create();
    }
}
