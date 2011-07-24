
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

    public void grayscale() {
        if (step > grayscaleLevels)
            step = 0;
        int val = (int) ((float) 255 / grayscaleLevels * step + 0.5);
        color = Color.rgb(val, val, val);
    }

    public void nearBlack() {
        if (step > nearBlackLevels)
            step = 0;
        int val = (int) ((float) 255 / 100 * step + 0.5);
        color = Color.rgb(val, val, val);
    }

    public void nearWhite() {
        if (step > nearWhiteLevels)
            step = 0;
        int val = (int) ((float) 255 * (100 - nearWhiteLevels + step) / 100);
        color = Color.rgb(val, val, val);
    }

    private void colors() {
        switch (step) {
            case 0:
                color = Color.RED;
                return;
            case 1:
                color = Color.GREEN;
                return;
            case 2:
                color = Color.BLUE;
                return;
            case 3:
                color = Color.YELLOW;
                return;
            case 4:
                color = Color.CYAN;
                return;
            case 5:
                color = Color.MAGENTA;
                return;
            case 6:
                color = Color.WHITE;
                return;
            default:
                step = 0;
                colors();
                return;
        }
    }

    public int getColor() {
        switch (type) {
            case GRAYSCALE:
                grayscale();
                logColor(color);
                return color;
            case COLORS:
                colors();
                logColor(color);
                return color;
            case NEAR_BLACK:
                nearBlack();
                logColor(color);
                return color;
            case NEAR_WHITE:
                nearWhite();
                logColor(color);
                return color;
            default:
                break;
        }

        // should never arrive here, output scary red
        Log.d("ScreenTestPatterns", "Invalid type: " + type);
        return Color.rgb(255, 0, 0);
    }

    private void logColor(int color) {
        Log.i("ScreenTestPatterns", "Step: " + step);
        Log.i("ScreenTestPatterns", "Red: " + Color.red(color)
                + " Green: " + Color.green(color)
                + " Blue: " + Color.blue(color));
    }
}
