
package org.projectvoodoo.screentestpatterns;

import org.projectvoodoo.screentestpatterns.Patterns.PatternType;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class Main extends Activity implements OnClickListener, OnItemSelectedListener {

    Patterns pattern;

    private ShapeDrawable display;
    private View patternView;
    private Spinner grayscaleLevelsSpinner;
    private Spinner nearBlackLevelsSpinner;
    private Spinner nearWhiteLevelsSpinner;
    private Spinner saturationLevelsSpinner;
    private TextView currentPatternInfos;

    private Button setGrayscale;
    private Button setNearWhite;
    private Button setNearBlack;
    private Button setColors;
    private Button setSaturations;

    private Button next;
    private Button prev;

    SharedPreferences settings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // instantiate pattern engine
        pattern = new Patterns(this);

        // preference manager
        settings = getSharedPreferences("hcfr", MODE_PRIVATE);

        // select layout first
        setContentView(R.layout.main);

        // we will display the stuff here
        patternView = (View) findViewById(R.id.pattern_display);
        display = new ShapeDrawable(new OvalShape());
        display.getPaint().setColor(Color.GRAY);
        patternView.setBackgroundDrawable(display);

        // configure spinners
        // For grayscale measurements
        grayscaleLevelsSpinner = (Spinner) findViewById(R.id.spinner_grayscale_levels);
        ArrayAdapter<CharSequence> grayscaleAdapter = ArrayAdapter.createFromResource(this,
                R.array.grayscale_array, android.R.layout.simple_spinner_item);
        grayscaleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        grayscaleLevelsSpinner.setAdapter(grayscaleAdapter);
        setSpinnerValue(
                grayscaleLevelsSpinner,
                settings.getInt("grayscale_levels", pattern.grayscaleLevels));
        grayscaleLevelsSpinner.setOnItemSelectedListener(this);

        // For near black measurements
        nearBlackLevelsSpinner = (Spinner) findViewById(R.id.spinner_near_black_levels);
        ArrayAdapter<CharSequence> blackLevelsAdapter = ArrayAdapter.createFromResource(this,
                R.array.near_black_array, android.R.layout.simple_spinner_item);
        blackLevelsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nearBlackLevelsSpinner.setAdapter(blackLevelsAdapter);
        setSpinnerValue(
                nearBlackLevelsSpinner,
                settings.getInt("near_black_levels", pattern.nearBlackLevels));
        nearBlackLevelsSpinner.setOnItemSelectedListener(this);

        // For near white measurements
        nearWhiteLevelsSpinner = (Spinner) findViewById(R.id.spinner_near_white_levels);
        ArrayAdapter<CharSequence> whiteLevelsAdapter = ArrayAdapter.createFromResource(this,
                R.array.near_white_array, android.R.layout.simple_spinner_item);
        whiteLevelsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nearWhiteLevelsSpinner.setAdapter(whiteLevelsAdapter);
        setSpinnerValue(
                nearWhiteLevelsSpinner,
                settings.getInt("near_white_levels", pattern.nearWhiteLevels));
        nearWhiteLevelsSpinner.setOnItemSelectedListener(this);

        // For saturation measurements
        saturationLevelsSpinner = (Spinner) findViewById(R.id.spinner_saturation_levels);
        ArrayAdapter<CharSequence> saturationLevelsAdapter = ArrayAdapter.createFromResource(this,
                R.array.saturations_array, android.R.layout.simple_spinner_item);
        saturationLevelsAdapter.setDropDownViewResource(
                        android.R.layout.simple_spinner_dropdown_item);
        saturationLevelsSpinner.setAdapter(saturationLevelsAdapter);
        setSpinnerValue(
                saturationLevelsSpinner,
                settings.getInt("saturation_levels", pattern.saturationLevels));
        saturationLevelsSpinner.setOnItemSelectedListener(this);

        // Informs users of the current pattern displayed
        currentPatternInfos = (TextView) findViewById(R.id.current_pattern_info);

        // Buttons
        setGrayscale = (Button) findViewById(R.id.button_grayscale);
        setGrayscale.setOnClickListener(this);
        setNearBlack = (Button) findViewById(R.id.button_near_black);
        setNearBlack.setOnClickListener(this);
        setNearWhite = (Button) findViewById(R.id.button_near_white);
        setNearWhite.setOnClickListener(this);
        setColors = (Button) findViewById(R.id.button_colors);
        setColors.setOnClickListener(this);
        setSaturations = (Button) findViewById(R.id.button_saturations);
        setSaturations.setOnClickListener(this);

        prev = (Button) findViewById(R.id.button_prev);
        prev.setOnClickListener(this);

        next = (Button) findViewById(R.id.button_next);
        next.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        String tag = v.getTag() + "";

        Log.d("ScreenTestPatterns", "Button pressed: " + tag);

        if (tag.equals("grayscale")) {
            pattern.type = PatternType.GRAYSCALE;
            pattern.step = 0;
            displayPattern();

        } else if (tag.equals("near_black")) {
            pattern.type = PatternType.NEAR_BLACK;
            pattern.step = 0;
            displayPattern();

        } else if (tag.equals("near_white")) {
            pattern.type = PatternType.NEAR_WHITE;
            pattern.step = 0;
            displayPattern();

        } else if (tag.equals("colors")) {
            pattern.type = PatternType.COLORS;
            pattern.step = 0;
            displayPattern();

        } else if (tag.equals("saturations")) {
            pattern.type = PatternType.SATURATIONS;
            pattern.step = 0;
            displayPattern();

        } else if (tag.equals("prev")) {
            pattern.step -= 1;
            displayPattern();

        } else if (tag.equals("next")) {
            pattern.step += 1;
            displayPattern();
        }
    }

    private void displayPattern() {

        display.getPaint().setColor(pattern.getColor());
        patternView.setBackgroundDrawable(display);
        showCurrentPatternInfos();

    }

    private void showCurrentPatternInfos() {
        String text = pattern.type + " ";
        if (pattern.type == PatternType.GRAYSCALE)
            text += "IRE " + (int) ((float) 100 / pattern.grayscaleLevels * pattern.step) + "\n";
        else
            text += "\n";
        text += "R: " + Color.red(pattern.color);
        text += " G: " + Color.green(pattern.color);
        text += " B: " + Color.blue(pattern.color);
        currentPatternInfos.setText(text);
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
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        try {
            int value = Integer.parseInt((String) arg0.getAdapter().getItem(arg2));
            String tag = arg0.getTag() + "";
            Log.d("ScreenTestPatterns", tag + " item: " + value);

            // now save the preference
            SharedPreferences.Editor editor = settings.edit();
            String name = tag + "_levels";

            if (tag.equals("grayscale")) {
                pattern.grayscaleLevels = value;
                pattern.step = 0;
                displayPattern();
                editor.putInt(name, value);

            } else if (tag.equals("near_black")) {
                pattern.nearBlackLevels = value;
                pattern.step = 0;
                displayPattern();
                editor.putInt(name, value);

            } else if (tag.equals("near_white")) {
                pattern.nearWhiteLevels = value;
                pattern.step = 0;
                displayPattern();
                editor.putInt(name, value);

            } else if (tag.equals("saturations")) {
                pattern.saturationLevels = value;
                pattern.step = 0;
                displayPattern();
                editor.putInt(name, value);
            }
            editor.commit();

        } catch (Exception e) {
            Log.d("ScreenTestPatterns", "Error: Invalid item selection");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }
}
