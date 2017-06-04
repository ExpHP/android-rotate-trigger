package com.github.exphp.rotationonoff;

/**
 * Created by lampam on 6/4/17.
 */

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Simplified interface for dealing with rotation settings.
 *
 * Automagically deals with explicit requests for the WRITE_SETTINGS permission in M and above.
 */
public class RotationManager {
    private Context context;
    private ContentResolver resolver;

    class UnexpectedValue extends Exception {
    }

    class PermissionError extends Exception {
    }

    RotationManager(Context context) {
        this.context = context;
        this.resolver = context.getContentResolver();
    }

    // FIXME not only should this class probably not be doing IO, but it doesn't even work!
    // (I don't think a BroadcastReceiver can toast?)
    private void toast(String msg, int duration) {
        Toast.makeText(this.context, msg, duration);
    }

    // Toggle between accelerometer-based auto-rotation and locked orientation.
    public boolean toggle() {
        boolean isCurrentlyAuto;
        try {
            isCurrentlyAuto = this.raw_getAutoRotation();

        } catch (UnexpectedValue e) {
            isCurrentlyAuto = true; // meh.

        } catch (Settings.SettingNotFoundException e) {
            // FIXME show what setting could not be found?
            toast("Could not set orientation: Device not supported!", Toast.LENGTH_SHORT);
            return false;
        }

        if (isCurrentlyAuto) {
            if (!lockCurrent()) {
                return false;
            }
            toast("Locked current orientation.", Toast.LENGTH_SHORT);
        } else {
            if (!autoRotate()) {
                return false;
            }
            toast("Enabled auto-rotation.", Toast.LENGTH_SHORT);
        }

        try {
            assert isCurrentlyAuto != this.raw_getAutoRotation();
        } catch (UnexpectedValue e) {
            throw new AssertionError(e);
        } catch (Settings.SettingNotFoundException e) {
            throw new AssertionError(e);
        }

        return true;
    }

    // Enable accelerometer-based auto-rotation.
    public boolean autoRotate() {
        try {
            this.raw_putAutoRotate(true);
        } catch (PermissionError e) {
            this.toast("Could not set orientation: Insufficient privileges!", Toast.LENGTH_SHORT);
            return false;
        }
        return true;
    }

    // Lock the orientation to whatever direction cardinal direction it currently faces.
    public boolean lockCurrent() {
        try {
            // once auto-rotate is disabled, android will fall back to USER_ROTATION.
            // We should set it now to the current orientation.
            this.raw_putUserRotation(this.raw_getDisplayOrientation());
            this.raw_putAutoRotate(false);
        } catch (PermissionError e) {
            this.toast("Could not set orientation: Insufficient privileges!", Toast.LENGTH_SHORT);
            return false;
        }
        return true;
    }


    //--------------------------------------
    // raw functionality that exposes edge cases explicitly through 'throws'

    private int raw_getDisplayOrientation() {
        return ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay()
                .getRotation();
    }

    private boolean raw_getAutoRotation() throws Settings.SettingNotFoundException, UnexpectedValue {
        switch (this.raw_getInt(Settings.System.ACCELEROMETER_ROTATION)) {
            case 0: { return false; }
            case 1: { return true; }
            default: { throw new UnexpectedValue(); }
        }
    }

    private int raw_getUserRotation() throws Settings.SettingNotFoundException {
        return this.raw_getInt(Settings.System.USER_ROTATION);
    }

    private void raw_putAutoRotate(boolean enabled) throws PermissionError {
        this.raw_putInt(Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);
    }

    private void raw_putUserRotation(int rotation) throws PermissionError {
        this.raw_putInt(Settings.System.USER_ROTATION, rotation);
    }


    private void raw_putInt(String key, int value) throws PermissionError {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(this.context)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + this.context.getPackageName()));
            this.context.startActivity(intent);
        }

        try {
            Settings.System.putInt(this.resolver, key, value);
        } catch (java.lang.SecurityException e) {
            throw new PermissionError();
        }
    }

    private int raw_getInt(String key) throws Settings.SettingNotFoundException {
        return Settings.System.getInt(this.resolver, key);
    }
}
