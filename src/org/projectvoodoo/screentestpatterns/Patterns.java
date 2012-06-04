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

    private static final String TAG = "Voodoo ScreenTestPatterns Patterns";
    private Context mContext;

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
    public int mStep = 0;
    public int mGrayscaleLevels = 10;
    public int mNearBlackLevels = 4;
    public int mNearWhiteLevels = 4;
    public int mSaturationLevels = 4;

    public int mColor;

    Patterns(Context c) {
        mContext = c;
    }

    public void grayscale() {
        if (mStep > mGrayscaleLevels)
            mStep = 0;
        if (mStep < 0)
            mStep = mGrayscaleLevels;
        int val = (int) ((float) 255 / mGrayscaleLevels * mStep + 0.5);
        mColor = Color.rgb(val, val, val);
    }

    public void nearBlack() {
        if (mStep > mNearBlackLevels || mStep < 0)
            mStep = 0;
        if (mStep < 0)
            mStep = mNearBlackLevels;
        int val = (int) ((float) 255 / 100 * mStep + 0.5);
        mColor = Color.rgb(val, val, val);
    }

    public void nearWhite() {
        if (mStep > mNearWhiteLevels || mStep < 0)
            mStep = 0;
        if (mStep < 0)
            mStep = mNearWhiteLevels;
        int val = (int) ((float) 255 * (100 - mNearWhiteLevels + mStep) / 100);
        mColor = Color.rgb(val, val, val);
    }

    private void colors() {
        switch (mStep) {
            case 0:
                mColor = Color.RED;
                return;
            case 1:
                mColor = Color.GREEN;
                return;
            case 2:
                mColor = Color.BLUE;
                return;
            case 3:
                mColor = Color.YELLOW;
                return;
            case 4:
                mColor = Color.CYAN;
                return;
            case 5:
                mColor = Color.MAGENTA;
                return;
            case 6:
                mColor = Color.WHITE;
                return;
            default:
                mStep = 0;
                colors();
                return;
        }
    }

    private void saturations() {

        int range = mSaturationLevels + 1;

        // allow looping via Prev button
        if (mStep < 0)
            mStep = (range * 6) - 1;

        int skip = SATURATION_TABLE_LENGTH / mSaturationLevels;
        int offset = (mStep % range) * skip;

        if (mStep >= 0 && mStep < range) {

            Log.i(TAG, "Red satuation");
            mColor = getColorFromSaturationTable(R.array.saturation_table_red, offset);

        } else if (mStep < (range * 2)) {

            Log.i(TAG, "Green satuation");
            mColor = getColorFromSaturationTable(R.array.saturation_table_green, offset);

        } else if (mStep < (range * 3)) {

            Log.i(TAG, "Blue satuation");
            mColor = getColorFromSaturationTable(R.array.saturation_table_blue, offset);

        } else if (mStep < (range * 4)) {

            Log.i(TAG, "Yellow satuation");
            mColor = getColorFromSaturationTable(R.array.saturation_table_yellow, offset);

        } else if (mStep < (range * 5)) {

            Log.i(TAG, "Cyan satuation");
            mColor = getColorFromSaturationTable(R.array.saturation_table_cyan, offset);

        } else if (mStep < (range * 6)) {

            Log.i(TAG, "Magenta satuation");
            mColor = getColorFromSaturationTable(R.array.saturation_table_magenta, offset);
        } else {
            mStep = 0;
            saturations();
        }
        Log.i(TAG, "Offset: " + offset);
    }

    private int getColorFromSaturationTable(int resId, int offset) {
        Resources res = mContext.getResources();
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
                logColor(mColor);
                return mColor;

            case COLORS:
                colors();
                logColor(mColor);
                return mColor;

            case NEAR_BLACK:
                nearBlack();
                logColor(mColor);
                return mColor;

            case NEAR_WHITE:
                nearWhite();
                logColor(mColor);
                return mColor;

            case SATURATIONS:
                saturations();
                logColor(mColor);
                return mColor;

            default:
                break;
        }

        // should never arrive here, output scary red
        Log.d(TAG, "Invalid type: " + type);
        return Color.rgb(255, 0, 0);
    }

    private void logColor(int color) {
        Log.i(TAG, "Step: " + mStep);
        Log.i(TAG, "Red: " + Color.red(color)
                + " Green: " + Color.green(color)
                + " Blue: " + Color.blue(color));
    }
}
