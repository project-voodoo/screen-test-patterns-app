/* This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details.
 *
 * Author: François SIMOND aka supercurio
 *
 * Sources:
 * https://github.com/project-voodoo/screen-test-patterns-app
 *
 * Market:
 * https://market.android.com/details?id=org.projectvoodoo.screentestpatterns
 *
 * feedback welcome on twitter/XDA/email whatever
 * contributions are welcome too
 * IRC: Freenode, channel #project-voodoo
 */

package org.projectvoodoo.screentestpatterns;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import org.projectvoodoo.screentestpatterns.PatternGenerator.PatternType;

public class Main extends Activity implements OnClickListener, OnSeekBarChangeListener {

    private static final String TAG = "Voodoo ScreenTestPatterns Main";

    private PatternGenerator patternGenerator;

    private ShapeDrawable mDisplay;
    private View mPatternView;
    private Spinner mGrayscaleLevelsSpinner;
    private Spinner mNearBlackLevelsSpinner;
    private Spinner mNearWhiteLevelsSpinner;
    private Spinner mSaturationLevelsSpinner;
    private Spinner mPatternTypeSpinner;
    private TextView mCurrentPatternInfos;

    private Button mSetGrayscale;
    private Button mSetNearWhite;
    private Button mSetNearBlack;
    private Button mSetColors;
    private Button mSetSaturations;

    private Button mNnext;
    private Button mPrev;

    private Button mBrightness;
    private SeekBar mBrightnessSeek;

    private Boolean mIsTablet = false;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // detect tablet screen size:
        mIsTablet = isTablet();

        // instantiate pattern engine
        patternGenerator = new PatternGenerator();

        // keep screen always on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (!mIsTablet)
            requestWindowFeature(Window.FEATURE_NO_TITLE);

        // select layout first
        if (mIsTablet)
            setContentView(R.layout.tablet);
        else
            setContentView(R.layout.phone);

        // we will display the stuff here
        mPatternView = (View) findViewById(R.id.pattern_display);

        mDisplay = new ShapeDrawable(new RectShape());
        mDisplay.getPaint().setColor(Color.GRAY);
        mPatternView.setBackgroundDrawable(mDisplay);
        mPatternView.setOnClickListener(this);

        if (mIsTablet) {
            // configure spinners
            // For grayscale measurements
            mGrayscaleLevelsSpinner = (Spinner) findViewById(R.id.spinner_grayscale_levels);
            ArrayAdapter<CharSequence> grayscaleAdapter =
                    ArrayAdapter.createFromResource(this,
                            R.array.grayscale_array,
                            android.R.layout.simple_spinner_item);
            grayscaleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mGrayscaleLevelsSpinner.setAdapter(grayscaleAdapter);
            setSpinnerValue(
                    mGrayscaleLevelsSpinner,
                    Integer.parseInt(App.settings.getString(App.KEY_GRAYSCALE_LEVELS,
                            patternGenerator.getSteps(PatternType.GRAYSCALE) + "")));
            mGrayscaleLevelsSpinner.setOnItemSelectedListener(optionsListener);

            // For near black measurements
            mNearBlackLevelsSpinner = (Spinner) findViewById(R.id.spinner_near_black_levels);
            ArrayAdapter<CharSequence> blackLevelsAdapter =
                    ArrayAdapter.createFromResource(this,
                            R.array.near_black_array,
                            android.R.layout.simple_spinner_item);
            blackLevelsAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mNearBlackLevelsSpinner.setAdapter(blackLevelsAdapter);
            setSpinnerValue(
                    mNearBlackLevelsSpinner,
                    Integer.parseInt(App.settings.getString(App.KEY_NEAR_BLACK_LEVELS,
                            patternGenerator.getSteps(PatternType.NEAR_BLACK) + "")));
            mNearBlackLevelsSpinner.setOnItemSelectedListener(optionsListener);

            // For near white measurements
            mNearWhiteLevelsSpinner = (Spinner) findViewById(R.id.spinner_near_white_levels);
            ArrayAdapter<CharSequence> whiteLevelsAdapter =
                    ArrayAdapter.createFromResource(this,
                            R.array.near_white_array,
                            android.R.layout.simple_spinner_item);
            whiteLevelsAdapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item);
            mNearWhiteLevelsSpinner.setAdapter(whiteLevelsAdapter);
            setSpinnerValue(
                    mNearWhiteLevelsSpinner,
                    Integer.parseInt(App.settings.getString(App.KEY_NEAR_WHITE_LEVELS,
                            patternGenerator.getSteps(PatternType.NEAR_WHITE) + "")));
            mNearWhiteLevelsSpinner.setOnItemSelectedListener(optionsListener);

            // For saturation measurements
            mSaturationLevelsSpinner = (Spinner) findViewById(R.id.spinner_saturation_levels);
            ArrayAdapter<CharSequence> saturationLevelsAdapter =
                    ArrayAdapter.createFromResource(this,
                            R.array.saturations_array,
                            android.R.layout.simple_spinner_item);
            saturationLevelsAdapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item);
            mSaturationLevelsSpinner.setAdapter(saturationLevelsAdapter);
            setSpinnerValue(
                    mSaturationLevelsSpinner,
                    Integer.parseInt(App.settings.getString(App.KEY_SATURATION_LEVELS,
                            patternGenerator.getSteps(PatternType.SATURATIONS) + "")));
            mSaturationLevelsSpinner.setOnItemSelectedListener(optionsListener);

            // Buttons
            mSetGrayscale = (Button) findViewById(R.id.button_grayscale);
            mSetGrayscale.setOnClickListener(this);
            mSetNearBlack = (Button) findViewById(R.id.button_near_black);
            mSetNearBlack.setOnClickListener(this);
            mSetNearWhite = (Button) findViewById(R.id.button_near_white);
            mSetNearWhite.setOnClickListener(this);
            mSetColors = (Button) findViewById(R.id.button_colors);
            mSetColors.setOnClickListener(this);
            mSetSaturations = (Button) findViewById(R.id.button_saturations);
            mSetSaturations.setOnClickListener(this);

        } else {

            mPatternTypeSpinner = (Spinner) findViewById(R.id.spinner_pattern_type);
            ArrayAdapter<CharSequence> patternTypesAdapter =
                    ArrayAdapter.createFromResource(this,
                            R.array.pattern_types_array,
                            android.R.layout.simple_spinner_item);
            patternTypesAdapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item);
            mPatternTypeSpinner.setAdapter(patternTypesAdapter);
            mPatternTypeSpinner.setOnItemSelectedListener(patternChoiceListener);
        }

        // Informs users of the current pattern displayed
        mCurrentPatternInfos = (TextView) findViewById(R.id.current_pattern_info);

        mPrev = (Button) findViewById(R.id.button_prev);
        mPrev.setOnClickListener(this);

        mNnext = (Button) findViewById(R.id.button_next);
        mNnext.setOnClickListener(this);

        mBrightness = (Button) findViewById(R.id.button_brightness);
        mBrightness.setOnClickListener(this);
        setBrightness(App.settings.getInt(App.KEY_BRIGHTNESS, 127), false);
        setBrightnessButton();

        loadPatternGeneratorConfig();
    }

    @Override
    protected void onResume() {

        loadPatternGeneratorConfig();

        super.onResume();
    }

    @SuppressLint("NewApi")
    private void loadPatternGeneratorConfig() {

        LinearLayout patternScaler = (LinearLayout) findViewById(R.id.pattern_scaler);
        float scale = (float) Integer.parseInt(
                App.settings.getString(App.KEY_PATTERN_SCALE, "100")) / 100;

        if (Build.VERSION.SDK_INT >= 11) {
            patternScaler.invalidate();
            patternScaler.setScaleX(scale);
            patternScaler.setScaleY(scale);
        }

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_grayscale:
                patternGenerator.setType(PatternType.GRAYSCALE);
                break;

            case R.id.button_near_black:
                patternGenerator.setType(PatternType.NEAR_BLACK);
                break;

            case R.id.button_near_white:
                patternGenerator.setType(PatternType.NEAR_WHITE);
                break;

            case R.id.button_colors:
                patternGenerator.setType(PatternType.COLORS);
                break;

            case R.id.button_saturations:
                patternGenerator.setType(PatternType.SATURATIONS);
                break;

            case R.id.button_prev:
                patternGenerator.prevStep();
                break;

            case R.id.button_next:
                patternGenerator.nextStep();
                break;

            case R.id.pattern_display:
                patternGenerator.nextStep();
                break;

            case R.id.button_brightness:
                showDialog(0);
                return;

        }

        displayPattern();
    }

    @SuppressWarnings("deprecation")
    private void displayPattern() {

        mDisplay.getPaint().setColor(patternGenerator.getColor());
        mPatternView.setBackgroundDrawable(mDisplay);
        mPatternView.invalidate();
        showCurrentPatternInfos();
    }

    private void showCurrentPatternInfos() {
        mCurrentPatternInfos.setText(patternGenerator.showCurrentPatternInfos());
    }

    private void setSpinnerValue(Spinner spinner, int value) {
        int i;
        String item;
        for (i = 0; i < spinner.getAdapter().getCount(); i++) {
            item = (String) spinner.getAdapter().getItem(i);
            try {
                if (Integer.parseInt(item) == value)
                    spinner.setSelection(i);
            } catch (Exception e) {
                // should never happen
                break;
            }
        }
    }

    OnItemSelectedListener optionsListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {

            String valueStr = (String) parent.getAdapter().getItem(pos);
            int value = Integer.parseInt(valueStr);

            switch (parent.getId()) {
                case R.id.spinner_grayscale_levels:
                    patternGenerator.saveSteps(PatternType.GRAYSCALE, value);
                    break;

                case R.id.spinner_near_black_levels:
                    patternGenerator.saveSteps(PatternType.NEAR_BLACK, value);
                    break;

                case R.id.spinner_near_white_levels:
                    patternGenerator.saveSteps(PatternType.NEAR_WHITE, value);
                    break;

                case R.id.spinner_saturation_levels:
                    patternGenerator.saveSteps(PatternType.SATURATIONS, value);
                    break;

            }

            displayPattern();

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };

    OnItemSelectedListener patternChoiceListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {

            switch (pos) {
                case 0:
                    patternGenerator.setType(PatternType.GRAYSCALE);
                    break;

                case 1:
                    patternGenerator.setType(PatternType.COLORS);
                    break;

                case 2:
                    patternGenerator.setType(PatternType.SATURATIONS);
                    break;

                case 3:
                    patternGenerator.setType(PatternType.NEAR_BLACK);
                    break;

                case 4:
                    patternGenerator.setType(PatternType.NEAR_WHITE);
                    break;
            }

            displayPattern();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mIsTablet) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.options, menu);
            return true;
        } else
            return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.pattern_options:
                Intent intent = new Intent(this, PatternGeneratorOptions.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.brightness_dialog);

        mBrightnessSeek = (SeekBar) dialog.findViewById(R.id.brightness_seek);
        mBrightnessSeek.setMax(255);
        mBrightnessSeek.setProgress(App.brightnessValue);
        mBrightnessSeek.setOnSeekBarChangeListener(this);
        dialog.setTitle(R.string.choose_brightness);

        WindowManager.LayoutParams layout = dialog.getWindow().getAttributes();
        layout.dimAmount = 0;
        if (mIsTablet)
            layout.width = 500;
        dialog.getWindow().setAttributes(layout);

        for (int buttonId : App.BRIGHTNESS_BUTTONS)
            ((Button) dialog.findViewById(buttonId)).setOnClickListener(brightnessClickReceiver);

        return dialog;
    }

    private boolean isTablet() {
        int layout = getResources().getConfiguration().screenLayout;
        boolean xlarge = ((layout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((layout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    private void setBrightness(int brightness, boolean record) {

        float brightnessValue = (float) brightness / 255;
        if (brightnessValue == 0)
            brightnessValue = 0.0045f;

        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = brightnessValue;
        getWindow().setAttributes(layout);

        setBrightnessButton();

        App.brightnessValue = brightness;
        if (record)
            App.settings.edit()
                    .putInt(App.KEY_BRIGHTNESS, brightness)
                    .commit();
    }

    private void setBrightnessButton() {
        mBrightness.setText(getText(R.string.button_brightness) + "\n" +
                App.brightnessValue + "/255 - " +
                String.format("%.0f %%", (float) App.brightnessValue / 255 * 100));
    }

    // brightness seekbar
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            Log.i(TAG, "Change brightness to: " + progress);
            setBrightness(progress, false);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        setBrightness(seekBar.getProgress(), true);
    }

    View.OnClickListener brightnessClickReceiver = new OnClickListener() {

        @Override
        public void onClick(View v) {

            if (mBrightnessSeek != null) {
                int value = 100;

                switch (v.getId()) {
                    case R.id.button_bright_0:
                        value = 0;
                        break;

                    case R.id.button_bright_25:
                        value = 64;
                        break;

                    case R.id.button_bright_50:
                        value = 127;
                        break;

                    case R.id.button_bright_65:
                        value = 166;
                        break;

                    case R.id.button_bright_75:
                        value = 191;
                        break;

                    case R.id.button_bright_100:
                        value = 255;
                        break;
                }

                setBrightness(value, true);
                mBrightnessSeek.setProgress(value);
                setBrightnessButton();
            }
        }
    };

}
