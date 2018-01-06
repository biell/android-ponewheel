package net.kwatts.powtools.view;

import android.databinding.Observable;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.rey.material.widget.Button;

import net.kwatts.powtools.MainActivity;
import net.kwatts.powtools.R;
import net.kwatts.powtools.model.OWDevice;
import net.kwatts.powtools.util.BluetoothUtil;

import java.util.ArrayList;
import java.util.List;

public class RideModeView {

    CardView rideModeCard;
    final MainActivity activity;
    final OWDevice owDevice;
    final List<Button> buttons = new ArrayList<>();


    public RideModeView(MainActivity activity, OWDevice owDevice) {

        this.rideModeCard = activity.findViewById(R.id.card_ride_mode);
        this.activity = activity;
        this.owDevice = owDevice;


        LinearLayout radioGroup = new LinearLayout(activity);
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);

        String[] rideModes;
        if (owDevice.isOneWheelPlus.get()) {
            rideModes = activity.getResources().getStringArray(R.array.owplus_ridemode_array);
        } else {
            rideModes = activity.getResources().getStringArray(R.array.ow_ridemode_array);
        }
        int rideModeButtonMargin = activity.getResources().getDimensionPixelSize(R.dimen.ride_mode_button_margin);

        int position = 0;
        for (String rideMode : rideModes) {
            Button button = new Button(activity);
            buttons.add(button);

            button.setGravity(Gravity.CENTER);
            button.setMaxLines(1);

            button.setText(rideMode);
            LinearLayout.MarginLayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

//            textViewParams.setMargins(0, rideModeButtonMargin, rideModeButtonMargin, rideModeButtonMargin);
            radioGroup.addView(button, textViewParams);

            final int positionIdentifier = position;
            button.setOnClickListener(v -> onButtonPressed(owDevice, activity.getBluetoothUtil(), positionIdentifier, button));

            position++;
        }
        rideModeCard.setPadding(rideModeButtonMargin, rideModeButtonMargin,rideModeButtonMargin,rideModeButtonMargin);


        HorizontalScrollView scrollView = new HorizontalScrollView(activity);
        scrollView.addView(radioGroup);

        CardView.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rideModeCard.addView(scrollView, params);

        owDevice.isConnected.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                updateEnabledState(buttons);
            }
        });
        updateEnabledState(buttons);
        for (Button button1 : buttons) {
            button1.setBackgroundResource(android.R.drawable.btn_default);
        }
    }

    public void updateEnabledState(List<Button> buttons) {
        for (Button button : buttons) {
            button.setEnabled(owDevice.isConnected.get());
        }
    }

    public void onButtonPressed(OWDevice mOWDevice, BluetoothUtil bluetoothUtil, int positionIdentifier, Button button) {
        int rideModeInt;
        if (mOWDevice.isOneWheelPlus.get()) {
            rideModeInt = positionIdentifier + 4; // ow+ ble value for rideMode 4,5,6,7,8 (delirium)
            if (rideModeInt < 4 || rideModeInt > 8) {
                throw new IllegalStateException("Unknown rideModeInt for ow+: " + rideModeInt);
            }
        } else {
            rideModeInt = positionIdentifier + 1; // ow uses 1,2,3 (expert)
            if (rideModeInt < 1 || rideModeInt > 3) {
                throw new IllegalStateException("Unknown rideModeInt for ow: " + rideModeInt);
            }
        }
        activity.updateLog("Ridemode changed to:" + rideModeInt);
        mOWDevice.setRideMode(bluetoothUtil,rideModeInt);
        button.setSelected(true);
        for (Button button1 : buttons) {
            button1.setBackgroundResource(android.R.drawable.btn_default);
        }

        button.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
    }
}
