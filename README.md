# android-rotate-trigger

Rotation on/off toggle for android

## lolwat?

This is an *on/off application,* which toggles auto-rotation and then immediately quits without ever showing an interface. This makes it a suitable target for e.g. [Fingerprint Gestures](https://play.google.com/store/apps/details?id=com.superthomaslab.fingerprintgestures&hl=en)

*There's no message.* (there's supposed to be but \*cough\*) Just try rotating your phone to test if it's active.

There are, of course, other applications already on Google Play which toggle auto-rotation and then quit, but all the ones I tried seemed to just be wrappers around this:

```java
// Toggle Auto-rotate
int enabled = Settings.System.getInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION);
Settings.System.putInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION, 1 - enabled);
```

which will toggle between Auto and a single "natural" orientation, usually Portrait.

This one, on the other hand, wraps the following:

```java
// Enable auto-rotation
Settings.System.putInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION, 1);

//---------------------
// Lock to the current orientation
int rotation = windowManager.getDefaultDisplay().getRotation();
Settings.System.putInt(contentResolver, Settings.System.USER_ROTATION, rotation);
Settings.System.putInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION, 0);
```

Which *locks the current orientation* when auto-rotate is disabled.  (there were a couple of apps I found which work this way, but both lived in the notification area and couldn't be triggered from Fingerprint Gestures).

## License

WTFPL 2.0, _at your own risk._

(what I mean is, I bestow upon you all rights that I am capable of bestowing upon you.
 I haven't thoroughly checked the files created by android-studio to make sure there isn't
 anything with copyleft license bits hiding in there)
