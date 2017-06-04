package com.github.exphp.rotationonoff;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        Shortcuts require an activity, but we don't want a window.

        This is achieved by:

         - setting a NoDisplay theme
         - setting an empty taskAffinity, so that new windows aren't associated with
           anything else that might be open.
         - calling 'finish' in 'onCreate', and NOT calling 'setContentView'.
         */

        //setContentView(R.layout.activity_main);
        this.toggleRotation();
        this.finish();
    }

    public void onButtonClick_toggle(View view) {
        this.toggleRotation();
    }

    void toggleRotation() {
        new RotationManager(this.getApplicationContext()).toggle();
        //sendBroadcast(new Intent().setAction("com.github.exphp.rotationonoff.TOGGLE"));
    }
}
