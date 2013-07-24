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

import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.HashMap;

public class PatternGenerator {

    private static final String TAG = "Voodoo ScreenTestPatterns Patterns";

    // Measurement modes, with default to grayscale
    public static enum PatternType {
        GRAYSCALE(App.KEY_GRAYSCALE_LEVELS),
        COLORS(null),
        NEAR_BLACK(App.KEY_NEAR_BLACK_LEVELS),
        NEAR_WHITE(App.KEY_NEAR_WHITE_LEVELS),
        SATURATIONS(App.KEY_SATURATION_LEVELS),

        ;
        String prefKey;

        PatternType(String key) {
            this.prefKey = key;
        }
    }

    private static final int SATURATION_TABLE_LENGTH = 16;

    private PatternType mType = PatternType.GRAYSCALE;
    private int mStep = 0;

    private HashMap<PatternType, Integer> mSteps = new HashMap<PatternGenerator.PatternType, Integer>();

    public int mColor;

    PatternGenerator() {
        loadPrefs();
    }

    public PatternType switchType(boolean forward) {

        Log.i(TAG, "Old Pattern type: " + mType);

        int value = forward ? 1 : -1;
        int index = ((mType.ordinal() + PatternType.values().length + value) % PatternType.values().length);
        mType = PatternType.values()[index];

        Log.i(TAG, "New Pattern type: " + mType);

        mStep = 0;

        return mType;
    }

    public int getColor() {
        switch (mType) {
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
                throw new IllegalArgumentException();
        }
    }

    public int getStep() {
        return mStep;
    }

    public void nextStep() {
        mStep = (mStep + 1) % (getStepsInternal());
    }

    public void prevStep() {
        mStep = (mStep + getStepsInternal() - 1) % (getStepsInternal());
    }

    public PatternType getType() {
        return mType;
    }

    public void setType(PatternType newType) {
        mType = newType;
        mStep = 0;
    }

    public int getSteps(PatternType type) {
        return mSteps.get(type);
    }

    public void saveSteps(PatternType type, int steps) {
        SharedPreferences.Editor editor = App.settings.edit();

        mSteps.put(type, steps);
        editor.putString(type.prefKey, steps + "");
        editor.commit();
    }

    public String showCurrentPatternInfos() {
        String text = mType + " ";
        if (mType == PatternType.GRAYSCALE
                || mType == PatternType.NEAR_BLACK
                || mType == PatternType.NEAR_WHITE) {
            text += "IRE ";

            switch (mType) {
                case GRAYSCALE:
                    text += new DecimalFormat("#.##")
                            .format(
                            ((float) 100 / mSteps.get(PatternType.GRAYSCALE) * mStep));
                    break;

                case NEAR_BLACK:
                    text += mStep;
                    break;

                case NEAR_WHITE:
                    text += 100 - mSteps.get(PatternType.NEAR_WHITE) + mStep;
                    break;

                default:
                    break;
            }
        }

        text += "\nR: " + Color.red(mColor);
        text += " G: " + Color.green(mColor);
        text += " B: " + Color.blue(mColor);

        return text;
    }

    private void loadPrefs() {
        // load pattern generator config from preferences, or set defaults from
        // HCFR default settings
        setSteps(PatternType.GRAYSCALE, 10);
        setSteps(PatternType.NEAR_BLACK, 4);
        setSteps(PatternType.NEAR_WHITE, 4);
        setSteps(PatternType.SATURATIONS, 4);

        // specific: hardcoded for "all colors"
        mSteps.put(PatternType.COLORS, 6);
    }

    private void setSteps(PatternType type, int defaultValue) {
        mSteps.put(type, Utils.getStringPrefAsInt(type.prefKey, defaultValue));
    }

    private int getStepsInternal() {
        int multiplier = mType == PatternType.SATURATIONS ? 6 : 1;
        return (mSteps.get(mType) + 1) * multiplier;
    }

    private void grayscale() {
        int val = (int) ((float) 255 / mSteps.get(PatternType.GRAYSCALE) * mStep + 0.5);
        mColor = Color.rgb(val, val, val);
    }

    private void nearBlack() {
        int val = (int) ((float) 255 / 100 * mStep + 0.5);
        mColor = Color.rgb(val, val, val);
    }

    private void nearWhite() {
        int val = (int) ((float) 255 * (100 - mSteps.get(PatternType.NEAR_WHITE) + mStep) / 100);
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

        int range = mSteps.get(PatternType.SATURATIONS) + 1;

        int skip = SATURATION_TABLE_LENGTH / mSteps.get(PatternType.SATURATIONS);
        int offset = (mStep % range) * skip;

        if (mStep >= 0 && mStep < range) {
            Log.i(TAG, "Red satuation");
            mColor = getColorFromSaturationTable(SaturationTables.RED, offset);

        } else if (mStep < (range * 2)) {
            Log.i(TAG, "Green satuation");
            mColor = getColorFromSaturationTable(SaturationTables.GREEN, offset);

        } else if (mStep < (range * 3)) {
            Log.i(TAG, "Blue satuation");
            mColor = getColorFromSaturationTable(SaturationTables.BLUE, offset);

        } else if (mStep < (range * 4)) {
            Log.i(TAG, "Yellow satuation");
            mColor = getColorFromSaturationTable(SaturationTables.YELLOW, offset);

        } else if (mStep < (range * 5)) {
            Log.i(TAG, "Cyan satuation");
            mColor = getColorFromSaturationTable(SaturationTables.CYAN, offset);

        } else if (mStep < (range * 6)) {
            Log.i(TAG, "Magenta satuation");
            mColor = getColorFromSaturationTable(SaturationTables.MAGENTA, offset);

        }

        Log.i(TAG, "Offset: " + offset);
    }

    private int getColorFromSaturationTable(int[] table, int offset) {
        return Color.rgb(
                table[offset * 3],
                table[offset * 3 + 1],
                table[offset * 3 + 2]);
    }

    private void logColor(int color) {
        Log.i(TAG, "Pattern Type: " + mType + " step: " + mStep + "/" + (getStepsInternal() - 1));
        Log.i(TAG, "Red: " + Color.red(color)
                + " Green: " + Color.green(color)
                + " Blue: " + Color.blue(color));
    }

}
