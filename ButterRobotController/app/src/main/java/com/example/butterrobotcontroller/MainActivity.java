/*  Michael Hegglin
 *
 *  Class Definition:
 *      This class handles all the responses from user input
 *      (user input referring to the joystick inputs or the manual
 *      vs. the artificial intelligence switch).  It then uses other
 *      classes to handle video streams or send user response data
 */

package com.example.butterrobotcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import static android.view.View.TRANSLATION_X;
import static android.view.View.TRANSLATION_Y;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener
{
    private float dX;                                                                                              //holds the switch value to know if it's manual or not
    private float dY;                                                                                              //holds the switch value to know if it's manual or not

    /*
     *  Definition:
     *      This method shows any toast message that you want
     *      and shows it for a specific duration
     */
    public void showToastMessage(String text, int duration)
    {
        final Toast switch_toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        switch_toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                switch_toast.cancel();
            }
        }, duration);
    }

    /*
     *  Definition:
     *      Create method that sets up the video
     *      and handles all the button pressing in the application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);                                    // forces the app to use landscape mode

        Switch manualSwitch = (Switch) findViewById(R.id.manual_switch);                                            // links the java to the xml for the manual switch
        manualSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked) {
                    showToastMessage("Driver Mode: On", 500);
                } else {
                    showToastMessage("Driver Mode: Off", 500);
                }
            }
        });

        View rightStick = findViewById(R.id.inner_right);                                                           // links the java to the right joystick
        rightStick.setOnTouchListener(this);                                                                        // sets up a listener that triggers when joystick is touched

        View leftStick = findViewById(R.id.inner_left);                                                             // links the java to the left joystick
        leftStick.setOnTouchListener(this);                                                                         // sets up a listener that triggers when joystick is touched
    }

    /*
     *  Definition:
     *      This method takes the x and y and parses them on a scale from 1 - 10
     *      This number is then send to the pi (then the servos) and moving speed
     *      is dependent on the 1 - 10 scale.
     */
    public void sendXY(float x, float y, String type)
    {

    }

    /*
     *  Definition:
     *      This method allows the joy sticks to be moved around inside their circles.
     *      Triggers data to be sent to the raspberry pi to tell the servers to move
     */
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        float radius = 200;

        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:                                                                           //happens when the joystick is pressed
                dX = v.getX() - event.getRawX();
                dY = v.getY() - event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:                                                                           //happens when the joystick is moving
                float x = event.getRawX() + dX;
                float y = event.getRawY() + dY;

                if (Math.pow((x - 219), 2) + Math.pow((y - 219), 2) <= radius*radius ||
                        Math.pow((x - 211), 2) + Math.pow((y - 219), 2) <= radius*radius)
                {
                    v.setX(x);
                    v.setY(y);

                    if (v.getId() == R.id.inner_right) {                                                            // showToastMessage("Right Joy Stick", 500);
                        sendXY(x, y, "move_camera");                                                          // send cmds to the pi to steer the car
                    } else if (v.getId() == R.id.inner_left) {                                                      // showToastMessage("Left Joy Stick", 500);
                        sendXY(x, y, "move_car");                                                             // send the cmds to the pie to move the car
                    }
                }

                //System.out.println("x:" + x);
                //System.out.println("y: "+ y);

                break;

            case MotionEvent.ACTION_UP:                                                                             // happens when the joystick is let go
                PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(TRANSLATION_X, 0);                 // returns to original x position
                PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(TRANSLATION_Y, 0);                 // returns to original y position
                ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(v, pvhX, pvhY);                     //animates the object to move to old position
                animator.setDuration(400);
                animator.start();

                break;
        }

        return false;
    }
}
