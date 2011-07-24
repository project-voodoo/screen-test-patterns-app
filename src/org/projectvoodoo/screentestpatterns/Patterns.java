
package org.projectvoodoo.screentestpatterns;

import android.graphics.Color;
import android.util.Log;

public class Patterns {

    // Measurement modes, with default to grayscale
    public enum PatternType {
        GRAYSCALE,
        NEAR_BLACK,
        NEAR_WHITE,
        COLORS,
        SATURATIONS
    }

    public PatternType type = PatternType.GRAYSCALE;
    public int step = 0;
    public int grayscaleLevels = 10;
    public int nearBlackLevels = 4;
    public int nearWhiteLevels = 4;
    public int saturationLevels = 4;

    public int color;

    public int grayscale() {
        int val = (int) ((float) 255 / (float) grayscaleLevels * (float) step + 0.5);
        int color = Color.rgb(val, val, val);
        logColor(color);
        return color;
    }

    public int getColor() {
        switch (type) {
            case GRAYSCALE:
                color = grayscale();
                return color;
            default:
                break;
        }

        // should never arrive here, output scary red
        Log.d("ScreenTestPatterns", "Invalid type: " + type);
        return Color.rgb(255, 0, 0);
    }

    private void logColor(int color) {
        Log.i("ScreenTestPatterns", "Red: " + Color.red(color)
                + " Green: " + Color.green(color)
                + " Blue: " + Color.blue(color));
    }
}
