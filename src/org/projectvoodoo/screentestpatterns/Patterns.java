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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;

public class Patterns {

    private Context context;
    private static final String TAG = "Voodoo ScreenTestPatterns Patterns";

    // Measurement modes, with default to grayscale
    public static enum PatternType {
        GRAYSCALE,
        NEAR_BLACK,
        NEAR_WHITE,
        COLORS,
        SATURATIONS
    }

    private static final int SATURATION_TABLE_LENGTH = 16;

    public PatternType type = PatternType.GRAYSCALE;
    public int step = 0;
    public int grayscaleLevels = 10;
    public int nearBlackLevels = 4;
    public int nearWhiteLevels = 4;
    public int saturationLevels = 4;

    public int color;

    Patterns(Context c) {
        context = c;
    }

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

        int range = saturationLevels + 1;

        // allow looping via Prev button
        if (step < 0)
            step = (range * 6) - 1;

        int skip = SATURATION_TABLE_LENGTH / saturationLevels;
        int offset = (step % range) * skip;

        if (step >= 0 && step < range) {

            Log.i(TAG, "Red satuation");
            color = getColorFromSaturationTable(R.array.saturation_table_red, offset);

        } else if (step < (range * 2)) {

            Log.i(TAG, "Green satuation");
            color = getColorFromSaturationTable(R.array.saturation_table_green, offset);

        } else if (step < (range * 3)) {

            Log.i(TAG, "Blue satuation");
            color = getColorFromSaturationTable(R.array.saturation_table_blue, offset);

        } else if (step < (range * 4)) {

            Log.i(TAG, "Yellow satuation");
            color = getColorFromSaturationTable(R.array.saturation_table_yellow, offset);

        } else if (step < (range * 5)) {

            Log.i(TAG, "Cyan satuation");
            color = getColorFromSaturationTable(R.array.saturation_table_cyan, offset);

        } else if (step < (range * 6)) {

            Log.i(TAG, "Magenta satuation");
            color = getColorFromSaturationTable(R.array.saturation_table_magenta, offset);
        } else {
            step = 0;
            saturations();
        }
        Log.i(TAG, "Offset: " + offset);
    }

    private int getColorFromSaturationTable(int resId, int offset) {
        Resources res = context.getResources();
        String[] components = res.getStringArray(resId);

        String[] valuesStrings = components[offset].split(" ");
        int i;
        int[] values = new int[3];
        for (i = 0; i < 3; i++)
            values[i] = Integer.parseInt(valuesStrings[i]);

        return Color.rgb(values[0], values[1], values[2]);
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
