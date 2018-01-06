package net.kwatts.powtools.view;

import android.app.Activity;
import android.databinding.Observable;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.rey.material.widget.Button;

import net.kwatts.powtools.MainActivity;
import net.kwatts.powtools.R;
import net.kwatts.powtools.model.OWDevice;
import net.kwatts.powtools.util.BluetoothUtil;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class RideModeView {

    CardView rideModeCard;
    final Activity activity;
    final OWDevice owDevice;

    public RideModeView(MainActivity activity, OWDevice owDevice) {

        this.rideModeCard = activity.findViewById(R.id.card_ride_mode);
        this.activity = activity;
        this.owDevice = owDevice;

        CardView.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

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
        final List<Button> buttons = new ArrayList<>();
        for (String rideMode : rideModes) {
            Button button = new Button(activity);
            buttons.add(button);

//            TextViewCompat.setAutoSizeTextTypeWithDefaults(button, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            button.setGravity(Gravity.CENTER);
            button.setMaxLines(1);

            button.setText(rideMode);
            LinearLayout.MarginLayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);

//            textViewParams.setMargins(0, rideModeButtonMargin, rideModeButtonMargin, rideModeButtonMargin);
            radioGroup.addView(button, textViewParams);

            final int positionIdentifier = position;
            button.setOnClickListener(v -> onButtonPressed(owDevice, activity.getBluetoothUtil(), positionIdentifier));

            position++;
        }
        rideModeCard.setPadding(rideModeButtonMargin, rideModeButtonMargin,rideModeButtonMargin,rideModeButtonMargin);
        rideModeCard.addView(radioGroup, params);

        owDevice.isConnected.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                updateEnabledState(buttons);
            }
        });
        updateEnabledState(buttons);


//
//
//        mRideModeToggleButton = this.findViewById(R.id.mstb_multi_ridemodes);
//        if (owDevice.isOneWheelPlus.get()) {
//            mRideModeToggleButton.setElements(getResources().getStringArray(R.array.owplus_ridemode_array));
//        } else {
//            mRideModeToggleButton.setElements(getResources().getStringArray(R.array.ow_ridemode_array));
//        }
//
//        mRideModeToggleButton.setOnValueChangedListener(position -> {
//            if (owDevice.isConnected.get()) {
//                Log.d(TAG, "owDevice.setRideMode button pressed:" + position);
//                if (owDevice.isOneWheelPlus.get()) {
//                    updateLog("Ridemode changed to:" + position + 4);
//                    owDevice.setRideMode(getBluetoothUtil(),position + 4); // ow+ ble value for ridemode 4,5,6,7,8 (delirium)
//                } else {
//                    updateLog("Ridemode changed to:" + position + 1);
//                    owDevice.setRideMode(getBluetoothUtil(),position + 1); // ow uses 1,2,3 (expert)
//                }
//            } else {
//                Toast.makeText(mContext, "Not connected to Device!", Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    public void updateEnabledState(List<Button> buttons) {
        for (Button button : buttons) {
            button.setEnabled(owDevice.isConnected.get());
        }
    }

    public void onButtonPressed(OWDevice mOWDevice, BluetoothUtil bluetoothUtil, int positionIdentifier) {
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
        mOWDevice.setRideMode(bluetoothUtil,rideModeInt);
    }
}
