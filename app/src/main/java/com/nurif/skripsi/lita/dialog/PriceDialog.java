package com.nurif.skripsi.lita.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nurif.skripsi.lita.R;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class PriceDialog extends DialogFragment {
    private static final String TIME = "time";
    private static final String WH = "wh";

    long currentTime = System.currentTimeMillis();

    public static PriceDialog newInstance(long time, String wh) {
        PriceDialog dialog = new PriceDialog();

        Bundle args = new Bundle();
        args.putLong(TIME, time);
        args.putString(WH, wh);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_price, new LinearLayout(getActivity()), false);

        TextView tvTime = (TextView) view.findViewById(R.id.tv_time);

        String time = setIntervalTime(getArguments().getLong(TIME));
        double price = setConversion(currentTime - getArguments().getLong(TIME));

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        tvTime.setText("Waktu " + time + "\nBiaya " + formatRupiah.format(price));

        Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setContentView(view);

        return builder;
    }

    private String setIntervalTime(long time) {
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(new Date(currentTime - time));
    }

    private double setConversion(long intervalTime) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(intervalTime);
        double power = Double.parseDouble(getArguments().getString(WH)) / 1000.0;

        return power * 1467 * seconds;
    }
}
