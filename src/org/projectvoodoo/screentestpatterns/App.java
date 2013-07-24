
package org.projectvoodoo.screentestpatterns;

import android.app.Application;
import android.content.SharedPreferences;

public class App extends Application {

    public static final String KEY_GRAYSCALE_LEVELS = "grayscale_levels";
    public static final String KEY_NEAR_WHITE_LEVELS = "near_white_levels";
    public static final String KEY_NEAR_BLACK_LEVELS = "near_black_levels";
    public static final String KEY_SATURATION_LEVELS = "saturations_levels";
    public static final String KEY_BRIGHTNESS = "brightness";
    public static final String KEY_PATTERN_SCALE = "pattern_scale";

    public static final int[] BRIGHTNESS_BUTTONS = {
            R.id.button_bright_0,
            R.id.button_bright_25,
            R.id.button_bright_50,
            R.id.button_bright_65,
            R.id.button_bright_75,
            R.id.button_bright_100,
    };

    public static SharedPreferences settings;
    public static int brightnessValue;
    public static boolean reloadGenerator = false;

    @Override
    public void onCreate() {

        settings = getSharedPreferences(PatternGeneratorOptions.prefName, MODE_PRIVATE);
        super.onCreate();
    }

}
