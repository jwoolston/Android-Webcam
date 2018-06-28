package com.jwoolston.usb.webcam.app;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import java.util.Arrays;
import org.greenrobot.eventbus.EventBus;
import timber.log.Timber;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class FormatPickerDialog extends DialogFragment {

    public static final String ARGUMENT_VALUES = FormatPickerDialog.class.getCanonicalName() + ".ARGUMENT_VALUES";
    public static final String ARGUMENT_INDICES = FormatPickerDialog.class.getCanonicalName() + ".ARGUMENT_INDICES";

    public static final String TAG          = "FormatPickerDialog";

    private NumberPicker numberPicker;
    private String[] values;
    private int[] indices;
    private int currentIndex = 0;

    public FormatPickerDialog() {};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle args = getArguments();
        if (args == null) {
            throw new IllegalStateException("An arguments bundle must be provided.");
        }
        values = args.getStringArray(ARGUMENT_VALUES);
        indices = args.getIntArray(ARGUMENT_INDICES);
        Timber.d("Displayed values: %s", Arrays.toString(values));
    }

    @Override
    public void onStart() {
        super.onStart();
        setCancelable(false);

        numberPicker = (NumberPicker) getDialog().findViewById(R.id.number_picker);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPicker.setDisplayedValues(values);
        numberPicker.setMinValue(getMinimumValue());
        numberPicker.setMaxValue(getMaximumValue());
        numberPicker.setValue(getInitialValue());
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(new OnValueChangeListener() {
            @Override public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                currentIndex = newVal;
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext()).setTitle("Available Formats")
                .setMessage("Please pick from the available video formats.")
                .setView(R.layout.dialog_format_picker)
                .setPositiveButton(android.R.string.ok, new OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        EventBus.getDefault().post(new VideoFormatSelected(indices[currentIndex]));
                    }
                })
                .create();
    }


    protected int getInitialValue() {
        return currentIndex;
    }

    protected int getMinimumValue() {
        return 0;
    }

    protected int getMaximumValue() {
        return values.length - 1;
    }
}
