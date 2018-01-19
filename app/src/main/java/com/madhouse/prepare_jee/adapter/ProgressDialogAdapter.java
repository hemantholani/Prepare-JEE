package com.madhouse.prepare_jee.adapter;

import android.app.Dialog;
import android.content.Context;

import com.madhouse.prepare_jee.R;


/**
 * Created by HEMANT on 19-01-2018.
 */

public class ProgressDialogAdapter {
    private Context context;
    private Dialog dialog;

    public ProgressDialogAdapter(Context context) {
        this.context = context;
        dialog = new Dialog(context);

    }

    public void showDialog() {
        dialog.setContentView(R.layout.progress_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

    public void hideDialog() {
        dialog.dismiss();
    }
}
