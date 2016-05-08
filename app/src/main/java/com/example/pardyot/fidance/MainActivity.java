package com.example.pardyot.fidance;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * The starting class form where the game gets started.
 */
public class MainActivity extends AppCompatActivity {
    ImageView help;
    ImageView soundOn;
    ImageView soundOff;
    boolean sound = true;

    private View mDecorView;

    SharedPreferences mPrefs;

    /**
     * The main function responsible for laying out all the layout on the screen.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDecorView = getWindow().getDecorView();
        mPrefs = getSharedPreferences("app", MODE_PRIVATE);



        help = (ImageView) findViewById(R.id.help);
        soundOn = (ImageView) findViewById(R.id.sound_on);
        soundOff = (ImageView) findViewById(R.id.sound_off);
        flipSound();
        openHelpDialog();

        String soundString = mPrefs.getString("sound", null);
        if(soundString!=null){
            sound=false;
            soundOn.setVisibility(View.GONE);
            soundOff.setVisibility(View.VISIBLE);
        } else{
            sound = true;
            soundOn.setVisibility(View.VISIBLE);
            soundOff.setVisibility(View.GONE);
        }
    }

    /**
     * This open ups the help Dialog when the user clicks on the help image below.
     */
    private void openHelpDialog() {
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setTitle("Some Rules and Regulations");
                alertDialogBuilder.setMessage("- This is a two player game, at first the game starts with a random tile highlighted and player1 has to press any of his finger on the tile failing which he would loose.\n" +
                        " - When player1 presses immediately the next tile pops up with a different color for player2.\n" +
                        " - Every player has 5 seconds to press their highlighted tiles failing which they loose.\n" +
                        " - During the game no player is allowed to release any of the previously pressed tile, otherwise that player will loose.\n" +
                        " - If during the course of the game, player moves any of his pressed finger out of the tile area then also he looses.\n" +
                        " - If by chance both the player succeed in pressing all the present tiles then the distance of the press from the center of the button is calculated and added for each player. The player with larger value of this distance looses in the end.");

                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }


    /**
     * On clicking the sound image the sound is flipped from on to off and vice versa
     */
    private void flipSound() {
        soundOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundOff.setVisibility(View.VISIBLE);
                soundOn.setVisibility(View.GONE);
                sound = false;

                SharedPreferences.Editor edit = mPrefs.edit();
                edit.putString("sound", "false");
                edit.commit();

            }
        });
        soundOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundOn.setVisibility(View.VISIBLE);
                soundOff.setVisibility(View.GONE);
                sound = true;

                SharedPreferences.Editor edit = mPrefs.edit();
                edit.clear();
                edit.commit();
            }
        });
    }

    /**
     * The function which is mentioned in its xml part which sets the click Listener to the buttons.
     * @param view The button which is been pressed.
     */
    public void fun(View view) {
        int id = view.getId();
        Intent openGame = new Intent(MainActivity.this, GameActivity.class);
        openGame.putExtra("sound", sound);
        switch (id){
            case R.id.two:
                openGame.putExtra("grid", 2);
                break;
            case R.id.three:
                openGame.putExtra("grid", 3);
                break;
            case R.id.four:
                openGame.putExtra("grid", 4);
                break;
            case R.id.five:
                openGame.putExtra("grid", 5);
                break;
        }
        startActivity(openGame);
    }

    /**
     * Responsible for fully immersive nature of this activity.
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mDecorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }
}
