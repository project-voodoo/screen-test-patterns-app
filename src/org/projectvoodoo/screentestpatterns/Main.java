
package org.projectvoodoo.screentestpatterns;

import org.projectvoodoo.screentestpatterns.Patterns.PatternType;

import android.app.Activity;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class Main extends Activity implements OnClickListener {

    Patterns pattern;

    private ShapeDrawable display;
    private View patternView;
    private Spinner greyscaleLevels;
    private Spinner blackLevels;
    private Spinner whiteLevels;
    private Spinner saturationLevels;
    private TextView currentPatternInfos;

    private Button setGreyscale;
    private Button setNearWhite;
    private Button setNearBlack;
    private Button setColors;
    private Button setSaturations;

    private Button next;
    private Button prev;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // instantiate pattern engine
        pattern = new Patterns();

        // select layout first
        setContentView(R.layout.main);

        // we will display the stuff here
        patternView = (View) findViewById(R.id.pattern_display);
        display = new ShapeDrawable(new OvalShape());
        display.getPaint().setColor(pattern.greyscale(50));
        patternView.setBackgroundDrawable(display);

        // configure spinners
        // For greyscale measurements
        greyscaleLevels = (Spinner) findViewById(R.id.spinner_grayscale_levels);
        ArrayAdapter<CharSequence> greyScaleAdapter = ArrayAdapter.createFromResource(this,
                R.array.greyscale_array, android.R.layout.simple_spinner_item);
        greyScaleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        greyscaleLevels.setAdapter(greyScaleAdapter);

        // For near black measurements
        blackLevels = (Spinner) findViewById(R.id.spinner_black_levels);
        ArrayAdapter<CharSequence> blackLevelsAdapter = ArrayAdapter.createFromResource(this,
                R.array.near_black_array, android.R.layout.simple_spinner_item);
        blackLevelsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        blackLevels.setAdapter(blackLevelsAdapter);

        // For near white measurements
        whiteLevels = (Spinner) findViewById(R.id.spinner_white_levels);
        ArrayAdapter<CharSequence> whiteLevelsAdapter = ArrayAdapter.createFromResource(this,
                R.array.near_white_array, android.R.layout.simple_spinner_item);
        whiteLevelsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        whiteLevels.setAdapter(whiteLevelsAdapter);

        // For saturation measurements
        saturationLevels = (Spinner) findViewById(R.id.spinner_saturation_levels);
        ArrayAdapter<CharSequence> saturationLevelsAdapter = ArrayAdapter.createFromResource(this,
                R.array.near_white_array, android.R.layout.simple_spinner_item);
        saturationLevelsAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        saturationLevels.setAdapter(saturationLevelsAdapter);

        // Informs users of the current pattern displayed
        currentPatternInfos = (TextView) findViewById(R.id.current_pattern_info);

        // Buttons
        setGreyscale = (Button) findViewById(R.id.button_greyscale);
        setGreyscale.setOnClickListener(this);
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

        if (tag.equals("button_greyscale")) {
            pattern.type = PatternType.GREYSCALE;
            displayPattern();

        } else if (tag.equals("button_near_black")) {
            pattern.type = PatternType.NEAR_BLACK;
            displayPattern();

        } else if (tag.equals("button_near_white")) {
            pattern.type = PatternType.NEAR_WHITE;
            displayPattern();

        } else if (tag.equals("button_colors")) {
            pattern.type = PatternType.COLORS;
            displayPattern();

        } else if (tag.equals("button_saturations")) {
            pattern.type = PatternType.SATURATIONS;
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

    }
}
