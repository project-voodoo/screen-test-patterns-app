
package org.projectvoodoo.screentestpatterns;

import android.app.Activity;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class Main extends Activity {

    private ShapeDrawable display;
    private View patternView;
    private Spinner greyscaleLevels;
    private Spinner blackLevels;
    private Spinner whiteLevels;
    private Spinner saturationLevels;
    private TextView currentPatternInfos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // select layout first
        setContentView(R.layout.main);

        display = new ShapeDrawable(new OvalShape());
        display.getPaint().setColor(0xFF0000FF);

        patternView = (View) findViewById(R.id.pattern_display);
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

    }

}
