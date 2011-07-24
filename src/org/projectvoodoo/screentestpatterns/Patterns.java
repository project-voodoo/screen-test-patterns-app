
package org.projectvoodoo.screentestpatterns;

import android.graphics.Color;
import android.util.Log;

public class Patterns {

    // Measurement modes, with default to greyscale
    public enum PatternType {
        GREYSCALE,
        NEAR_BLACK,
        NEAR_WHITE,
        COLORS,
        SATURATIONS
    }

    public PatternType type = PatternType.GREYSCALE;
    public int step = 0;

    public int greyscale(Integer percentage) {
        int val = 255 * percentage / 100;
        int color = Color.rgb(val, val, val);
        logColor(color);
        return color;
    }

    public int getColor() {
        switch (type) {
            case GREYSCALE:
                return greyscale(step * 10);
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
