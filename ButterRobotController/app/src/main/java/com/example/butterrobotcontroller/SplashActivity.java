/*  Michael Hegglin
 *
 *  Class Definition:
 *      This class handles the Splash screen
 *      for the application.  It waits for the main
 *      application to load, then loads it
 */

package com.example.butterrobotcontroller;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity
{
    /*
     *  Definition:
     *      This method uses the windownBackground theme to draw a
     *      background for the splash screen, while the real app is
     *      busy loading.  Loads real app when ready
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
