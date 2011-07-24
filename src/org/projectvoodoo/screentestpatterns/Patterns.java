
package org.projectvoodoo.screentestpatterns;

import android.graphics.Color;
import android.util.Log;

public class Patterns {

    private final String TAG = "ScreenTestPatterns";

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
        if (step < 0)
            step = grayscaleLevels;
        int val = (int) ((float) 255 / grayscaleLevels * step + 0.5);
        color = Color.rgb(val, val, val);
    }

    public void nearBlack() {
        if (step > nearBlackLevels || step < 0)
            step = 0;
        if (step < 0)
            step = nearBlackLevels;
        int val = (int) ((float) 255 / 100 * step + 0.5);
        color = Color.rgb(val, val, val);
    }

    public void nearWhite() {
        if (step > nearWhiteLevels || step < 0)
            step = 0;
        if (step < 0)
            step = nearWhiteLevels;
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

    private void saturations() {
        float[] hsvStart = {
                0, 0, 0
        };
        float[] hsvEnd = {
                0, 0, 0
        };
        int stepOffset = 0;
        int range = saturationLevels + 1;

        if (step >= 0 && step < range) {

            Color.colorToHSV(Color.rgb(128, 128, 128), hsvStart);
            Color.colorToHSV(Color.RED, hsvEnd);
            stepOffset = 0;

            Log.i(TAG, "Red satuation");
            // start V: 0.5
            // H : 0
            // 128 128 128
            // 170 111 111
            // 203 92 92
            // 231 68 68
            // 255 0 0

        } else if (step >= range && step < (range * 2)) {

            Color.colorToHSV(Color.rgb(219, 219, 219), hsvStart);
            Color.colorToHSV(Color.GREEN, hsvEnd);

            stepOffset = 1;

            Log.i(TAG, "Green satuation");
            // start V: 0.86
            // H : 120
            // 219 219 219
            // 177 233 177
            // 137 243 137
            // 94 249 94
            // 0 255 0

        } else if (step >= range * 2 && step < (range * 3)) {

            Color.colorToHSV(Color.rgb(78, 78, 78), hsvStart);
            Color.colorToHSV(Color.BLUE, hsvEnd);

            stepOffset = 2;

            Log.i(TAG, "Blue satuation");
            // start V: 0.31
            // H : 240
            // 78 78 78
            // 76 76 100
            // 72 72 127
            // 64 64 168
            // 0 0 255

        } else if (step >= range * 3 && step < (range * 4)) {

            Color.colorToHSV(Color.rgb(245, 245, 245), hsvStart);
            Color.colorToHSV(Color.YELLOW, hsvEnd);

            stepOffset = 3;

            Log.i(TAG, "Yellow satuation");

        } else if (step >= range * 4 && step < (range * 5)) {

            Color.colorToHSV(Color.rgb(227, 227, 227), hsvStart);
            Color.colorToHSV(Color.CYAN, hsvEnd);

            stepOffset = 4;

            Log.i(TAG, "Cyan satuation");

        } else if (step >= range * 5 && step < (range * 6)) {

            Color.colorToHSV(Color.rgb(144, 144, 144), hsvStart);
            Color.colorToHSV(Color.MAGENTA, hsvEnd);

            stepOffset = 5;

            Log.i(TAG, "Magenta satuation");
        }

        Log.i(TAG, "Start\tH: " + hsvStart[0] + " S: " + hsvStart[1] + " V: " + hsvStart[2]);
        Log.i(TAG, "End\tH: " + hsvEnd[0] + " S: " + hsvEnd[1] + " V: " + hsvEnd[2]);

        color = saturationInterpolation(hsvStart, hsvEnd, stepOffset, range);
    }

    private int saturationInterpolation(float[] hsvStart, float[] hsvEnd, int stepOffset, int range) {

        float[] hsv = {
                0, 0, 0
        };

        int saturationStep = (step - (stepOffset * range));
        Log.i(TAG, "Saturation step: " + saturationStep);

        float startFactor = (float) (saturationLevels - saturationStep) / saturationLevels;
        float endFactor = (float) saturationStep / saturationLevels;
        Log.i(TAG, "Saturation factors: start: " + startFactor + " end: " + endFactor);

        // set hue always to hsvEnd
        hsv[0] = hsvEnd[0];
        for (int i = 1; i < 3; i++)
            hsv[i] = hsvStart[i] * startFactor + hsvEnd[i] * endFactor;

        Log.i(TAG, "Blend\tH: " + hsv[0] + " S: " + hsv[1] + " V: " + hsv[2]);

        return Color.HSVToColor(hsv);
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
            case SATURATIONS:
                saturations();
                logColor(color);
                return color;
            default:
                break;
        }

        // should never arrive here, output scary red
        Log.d(TAG, "Invalid type: " + type);
        return Color.rgb(255, 0, 0);
    }

    private void logColor(int color) {
        Log.i(TAG, "Step: " + step);
        Log.i(TAG, "Red: " + Color.red(color)
                + " Green: " + Color.green(color)
                + " Blue: " + Color.blue(color));
    }
}
