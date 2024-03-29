package com.monitoring.kendali.listrik.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.monitoring.kendali.listrik.R;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class PriceDialog extends DialogFragment {
    private static final String TIME = "time";
    private static final String POWER = "wh";
    private static final String PRICE = "price";

    long currentTime = System.currentTimeMillis();

    public static PriceDialog newInstance(long time, String power, String price) {
        PriceDialog dialog = new PriceDialog();

        Bundle args = new Bundle();
        args.putLong(TIME, time);
        args.putString(POWER, power);
        args.putString(PRICE, price);
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

        tvTime.setText("Waktu: " + time + "\nBiaya: " + formatRupiah.format(price));

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
        double power = Double.parseDouble(getArguments().getString(POWER)) / 3600.0 /1000.0;

        return power * Double.parseDouble(getArguments().getString(PRICE)) * seconds;
    }
}
