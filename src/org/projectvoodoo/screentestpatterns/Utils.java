
package org.projectvoodoo.screentestpatterns;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

public class Utils {

    public static void setBrightness(Activity activity, int brightness, boolean record) {

        float brightnessValue = (float) brightness / 255;
        if (brightnessValue == 0)
            brightnessValue = 0.0045f;

        Window window = activity.getWindow();

        WindowManager.LayoutParams layout = window.getAttributes();
        layout.screenBrightness = brightnessValue;
        window.setAttributes(layout);

        App.brightnessValue = brightness;
        if (record)
            App.settings.edit()
                    .putInt(App.KEY_BRIGHTNESS, brightness)
                    .commit();
    }

    public static int getStringPrefAsInt(String key, int defaultValue) {
        try {

            if (!App.settings.contains(key))
                App.settings.edit().
                        putString(key, defaultValue + "")
                        .commit();

            return Integer.parseInt(
                    App.settings.getString(
                            key,
                            defaultValue + ""));
        } catch (Exception e) {
            return defaultValue;
        }
    }

}
