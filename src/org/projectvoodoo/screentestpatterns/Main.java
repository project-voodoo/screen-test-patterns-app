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

import org.projectvoodoo.screentestpatterns.Patterns.PatternType;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
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
import android.widget.Spinner;
import android.widget.TextView;

public class Main extends Activity implements OnClickListener, OnItemSelectedListener {

    @SuppressWarnings("unused")
    private static final String TAG = "Voodoo ScreenTestPatterns Main";

    private static final String KEY_GRAYSCALE_LEVELS = "grayscale_levels";
    private static final String KEY_NEAR_WHITE_LEVELS = "near_white_levels";
    private static final String KEY_NEAR_BLACK_LEVELS = "near_black_levels";
    private static final String KEY_SATURATION_LEVELS = "saturations_levels";

    private Patterns mPattern;

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

    private Boolean mIsTablet = false;

    SharedPreferences mSettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // detect tablet screen size:
        mIsTablet = isTablet();

        // instantiate pattern engine
        mPattern = new Patterns(this);

        // preference manager
        mSettings = getSharedPreferences(PatternGeneratorOptions.prefName, MODE_PRIVATE);

        // keep screen always on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (!mIsTablet)
            requestWindowFeature(Window.FEATURE_NO_TITLE);

        // select layout first
        if (mIsTablet)
            setContentView(R.layout.main);
        else
            setContentView(R.layout.phone);

        // we will display the stuff here
        mPatternView = (View) findViewById(R.id.pattern_display);
        mDisplay = new ShapeDrawable(new OvalShape());
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
                    Integer.parseInt(mSettings.getString(KEY_GRAYSCALE_LEVELS,
                            mPattern.mGrayscaleLevels + "")));
            mGrayscaleLevelsSpinner.setOnItemSelectedListener(this);

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
                    Integer.parseInt(mSettings.getString(KEY_NEAR_BLACK_LEVELS,
                            mPattern.mNearBlackLevels + "")));
            mNearBlackLevelsSpinner.setOnItemSelectedListener(this);

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
                    Integer.parseInt(mSettings.getString(KEY_NEAR_WHITE_LEVELS,
                            mPattern.mNearWhiteLevels + "")));
            mNearWhiteLevelsSpinner.setOnItemSelectedListener(this);

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
                    Integer.parseInt(mSettings.getString(KEY_SATURATION_LEVELS,
                            mPattern.mSaturationLevels + "")));
            mSaturationLevelsSpinner.setOnItemSelectedListener(this);

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
            mPatternTypeSpinner.setOnItemSelectedListener(this);

        }

        // Informs users of the current pattern displayed
        mCurrentPatternInfos = (TextView) findViewById(R.id.current_pattern_info);

        mPrev = (Button) findViewById(R.id.button_prev);
        mPrev.setOnClickListener(this);

        mNnext = (Button) findViewById(R.id.button_next);
        mNnext.setOnClickListener(this);

        loadPatternGeneratorConfig();
    }

    @Override
    protected void onResume() {

        loadPatternGeneratorConfig();

        super.onResume();
    }

    private void loadPatternGeneratorConfig() {
        // load pattern generator config from preferences
        mPattern.mGrayscaleLevels = Integer.parseInt(mSettings.getString(KEY_GRAYSCALE_LEVELS,
                mPattern.mGrayscaleLevels + ""));
        mPattern.mNearBlackLevels = Integer.parseInt(mSettings.getString(KEY_NEAR_BLACK_LEVELS,
                mPattern.mNearBlackLevels + ""));
        mPattern.mNearWhiteLevels = Integer.parseInt(mSettings.getString(KEY_NEAR_WHITE_LEVELS,
                mPattern.mNearWhiteLevels + ""));
        mPattern.mSaturationLevels = Integer.parseInt(mSettings.getString(KEY_SATURATION_LEVELS,
                mPattern.mSaturationLevels + ""));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_grayscale:
                mPattern.type = PatternType.GRAYSCALE;
                mPattern.mStep = 0;
                break;

            case R.id.button_near_black:
                mPattern.type = PatternType.NEAR_BLACK;
                mPattern.mStep = 0;
                break;

            case R.id.button_near_white:
                mPattern.type = PatternType.NEAR_WHITE;
                mPattern.mStep = 0;
                break;

            case R.id.button_colors:
                mPattern.type = PatternType.COLORS;
                mPattern.mStep = 0;
                break;

            case R.id.button_saturations:
                mPattern.type = PatternType.SATURATIONS;
                mPattern.mStep = 0;
                break;

            case R.id.button_prev:
                mPattern.mStep -= 1;
                break;

            case R.id.button_next:
                mPattern.mStep += 1;
                break;

            case R.id.pattern_display:
                mPattern.mStep += 1;
                break;

        }

        displayPattern();
    }

    private void displayPattern() {

        mDisplay.getPaint().setColor(mPattern.getColor());
        mPatternView.setBackgroundDrawable(mDisplay);
        mPatternView.invalidate();
        showCurrentPatternInfos();
    }

    private void showCurrentPatternInfos() {
        String text = mPattern.type + " ";
        if (mPattern.type == PatternType.GRAYSCALE)
            text += "IRE " +
                    (int) ((float) 100 / mPattern.mGrayscaleLevels * mPattern.mStep) + "\n";
        else
            text += "\n";

        text += "R: " + Color.red(mPattern.mColor);
        text += " G: " + Color.green(mPattern.mColor);
        text += " B: " + Color.blue(mPattern.mColor);
        mCurrentPatternInfos.setText(text);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        try {
            String valueStr = (String) parent.getAdapter().getItem(pos);
            int value = Integer.parseInt(valueStr);

            SharedPreferences.Editor editor = mSettings.edit();

            switch (parent.getId()) {
                case R.id.spinner_grayscale_levels:
                    mPattern.mGrayscaleLevels = value;
                    editor.putString(KEY_GRAYSCALE_LEVELS, valueStr);
                    break;

                case R.id.spinner_near_black_levels:
                    mPattern.mNearBlackLevels = value;
                    editor.putString(KEY_NEAR_BLACK_LEVELS, valueStr);
                    break;

                case R.id.spinner_near_white_levels:
                    mPattern.mNearWhiteLevels = value;
                    editor.putString(KEY_NEAR_WHITE_LEVELS, valueStr);
                    break;

                case R.id.spinner_saturation_levels:
                    mPattern.mSaturationLevels = value;
                    editor.putString(KEY_SATURATION_LEVELS, valueStr);
                    break;
            }

            mPattern.mStep = 0;
            editor.commit();
            displayPattern();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

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

    private boolean isTablet() {
        int layout = getResources().getConfiguration().screenLayout;
        boolean xlarge = ((layout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
        boolean large = ((layout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }
}
