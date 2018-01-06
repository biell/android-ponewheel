package net.kwatts.powtools.view;

import android.databinding.Observable;
import android.support.v7.widget.CardView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import net.kwatts.powtools.MainActivity;
import net.kwatts.powtools.R;
import net.kwatts.powtools.model.OWDevice;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;


public class RideModeView {

    CardView rideModeCard;
    final MainActivity activity;
    final OWDevice owDevice;

    public RideModeView(MainActivity activity, OWDevice owDevice) {

        this.rideModeCard = activity.findViewById(R.id.card_ride_mode);
        this.activity = activity;
        this.owDevice = owDevice;

        CardView.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        String[] rideModes;
        if (owDevice.isOneWheelPlus.get()) {
            rideModes = activity.getResources().getStringArray(R.array.owplus_ridemode_array);
        } else {
            rideModes = activity.getResources().getStringArray(R.array.ow_ridemode_array);
        }
        int rideModeButtonMargin = activity.getResources().getDimensionPixelSize(R.dimen.ride_mode_button_margin);

        MultiStateToggleButton multiStateToggleButton = new MultiStateToggleButton(activity);
        multiStateToggleButton.setElements(rideModes);

//        int position = 0;
//        final List<Button> buttons = new ArrayList<>();
//        for (String rideMode : rideModes) {
//            Button button = new Button(activity);
//            buttons.add(button);
//
//            button.setGravity(Gravity.CENTER);
//            button.setMaxLines(1);
//
//            button.setText(rideMode);
//            LinearLayout.MarginLayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
//
//            radioGroup.addView(button, textViewParams);
//
//            final int positionIdentifier = position;
//            button.setOnClickListener(v -> onButtonPressed(owDevice, activity.getBluetoothUtil(), positionIdentifier));
//
//            position++;
//        }
        rideModeCard.setPadding(rideModeButtonMargin, rideModeButtonMargin,rideModeButtonMargin,rideModeButtonMargin);
        rideModeCard.addView(multiStateToggleButton, params);

        owDevice.isConnected.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                updateEnabledState(multiStateToggleButton);
            }
        });
        updateEnabledState(multiStateToggleButton);

        multiStateToggleButton.setOnValueChangedListener(value -> {
            onButtonPressed(owDevice, multiStateToggleButton);
        });

    }

    public void updateEnabledState(MultiStateToggleButton multiStateToggleButton) {
        multiStateToggleButton.setEnabled(owDevice.isConnected.get());
    }

    public void onButtonPressed(OWDevice mOWDevice, MultiStateToggleButton multiStateToggleButton) {
        int positionIdentifier = multiStateToggleButton.getValue();
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
        mOWDevice.setRideMode(activity.getBluetoothUtil(),rideModeInt);
    }
}
