
package org.projectvoodoo.screentestpatterns;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class FullScreenUi extends Activity implements OnSeekBarChangeListener, OnTouchListener {

    private static final String TAG = "Voodoo Test";

    private GestureDetector mDetector;

    private PatternGenerator mPattern;

    private SeekBar mBrightnessSeek;

    private View mPatternBox;
    private View mInverseBoxLeft;
    private View mInverseBoxRight;

    private ShapeDrawable mPatternBoxDrawable;
    private ShapeDrawable mInversePatternBoxDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fullscreen);

        setupViews();

        // keep screen always on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // set Lights Out
        setLightsOut();

        // define brightness
        Utils.setBrightness(this, App.settings.getInt(App.KEY_BRIGHTNESS, 127), false);

        // instantiate pattern engine
        mPattern = new PatternGenerator();
        updatePattern();

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {

        if (App.reloadGenerator) {
            mPattern = new PatternGenerator();
            App.reloadGenerator = false;
        }

        super.onResume();
    }

    private void setupViews() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.layoutMain);
        layout.setOnTouchListener(this);

        mDetector = new GestureDetector(this, new MyGestureListener());

        mPatternBox = (View) findViewById(R.id.pattern);
        mInverseBoxLeft = (View) findViewById(R.id.inverseBoxLeft);
        mInverseBoxRight = (View) findViewById(R.id.inverseBoxRight);

        mPatternBoxDrawable = new ShapeDrawable(new RectShape());
        mInversePatternBoxDrawable = new ShapeDrawable(new RectShape());
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setLightsOut() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            findViewById(R.id.layoutMain).setSystemUiVisibility(View.STATUS_BAR_HIDDEN);

    }

    @Override
    public void onBackPressed() {
        mPattern.prevStep();
        updatePattern();
    }

    @SuppressWarnings("deprecation")
    private void updatePattern() {

        int color = mPattern.getColor();

        mPatternBoxDrawable.getPaint().setColor(color);
        mInversePatternBoxDrawable.getPaint().setColor(
                Color.rgb(
                        255 - Color.red(color),
                        255 - Color.green(color),
                        255 - Color.blue(color)));

        mPatternBox.setBackgroundDrawable(mPatternBoxDrawable);
        mInverseBoxLeft.setBackgroundDrawable(mInversePatternBoxDrawable);
        mInverseBoxRight.setBackgroundDrawable(mInversePatternBoxDrawable);

        mPatternBox.invalidate();
        mInverseBoxLeft.invalidate();
        mInverseBoxRight.invalidate();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_fullscreen, menu);
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.pattern_options:
                Intent intent = new Intent(this, PatternGeneratorOptions.class);
                startActivity(intent);
                return true;

            case R.id.brightness_options:
                showDialog(0);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.brightness_dialog);

        mBrightnessSeek = (SeekBar) dialog.findViewById(R.id.brightness_seek);
        mBrightnessSeek.setMax(255);
        mBrightnessSeek.setProgress(App.brightnessValue);
        mBrightnessSeek.setOnSeekBarChangeListener(this);
        dialog.setTitle(R.string.choose_brightness);

        WindowManager.LayoutParams layout = dialog.getWindow().getAttributes();
        layout.dimAmount = 0;
        dialog.getWindow().setAttributes(layout);

        for (int buttonId : App.BRIGHTNESS_BUTTONS)
            ((Button) dialog.findViewById(buttonId)).setOnClickListener(brightnessClickReceiver);

        return dialog;
    }

    // brightness seekbar
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            Log.i(TAG, "Change brightness to: " + progress);
            Utils.setBrightness(this, progress, false);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Utils.setBrightness(this, seekBar.getProgress(), true);
    }

    View.OnClickListener brightnessClickReceiver = new OnClickListener() {

        @Override
        public void onClick(View v) {

            if (mBrightnessSeek != null) {
                int value = 100;

                switch (v.getId()) {
                    case R.id.button_bright_0:
                        value = 0;
                        break;

                    case R.id.button_bright_25:
                        value = 64;
                        break;

                    case R.id.button_bright_50:
                        value = 127;
                        break;

                    case R.id.button_bright_65:
                        value = 166;
                        break;

                    case R.id.button_bright_75:
                        value = 191;
                        break;

                    case R.id.button_bright_100:
                        value = 255;
                        break;
                }

                Utils.setBrightness(FullScreenUi.this, value, true);
                mBrightnessSeek.setProgress(value);
            }
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            FullScreenUi.this.openOptionsMenu();
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            mPattern.nextStep();
            updatePattern();

            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            final int velocityXmin = 500;
            final int velocityYMax = 2000;

            Log.i(TAG, "fling: x=" + velocityX + " y=" + velocityY);

            if (velocityX < -velocityXmin &&
                    velocityY > -velocityYMax && velocityY < velocityYMax) {
                mPattern.switchType(true);
                updatePattern();
                return true;
            }

            if (velocityX > velocityXmin &&
                    velocityY > -velocityYMax && velocityY < velocityYMax) {
                mPattern.switchType(false);
                updatePattern();
                return true;
            }

            return false;
        }
    }

}
