package com.gaw.gaw0013;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Button[][] buttons = new Button[3][3];

    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;

    //true = player X; false = player O
    private boolean playerTurn = true;
    private int roundCount = 0, gameCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPref.edit();

        gameCount = mPref.getInt("gameCount", 0);

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(this);
            }
        }
    }



    @Override
    public void onClick(View v) {
        if(!((Button) v).getText().toString().equals(""))
            return;

        if(playerTurn)
            ((Button) v).setText("X");
        else
            ((Button) v).setText("O");

        roundCount++;

        if(winCheck()){
            if(playerTurn) {
                Toast.makeText(this, "Hráč X vyhrál!", Toast.LENGTH_LONG).show();
                waitDelay(3000);
            }
            else {
                Toast.makeText(this, "Hráč O vyhrál!", Toast.LENGTH_LONG).show();
                waitDelay(3000);
            }
        } else if(roundCount == 9) {
            Toast.makeText(this, "REMÍZA!", Toast.LENGTH_LONG).show();
            waitDelay(3000);
        }
        else {
            playerTurn = !playerTurn;

            TextView turnInfo = (TextView) findViewById(R.id.textPlayerTurn);

            if(playerTurn)
                turnInfo.setText("X");
            else
                turnInfo.setText("O");
        }
    }

    public void waitDelay(int time){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mEditor.putInt("gameCount", gameCount + 1);
                mEditor.commit();
                startActivity(new Intent(GameActivity.this, GameActivity.class));
            }
        },time);
    }

    private boolean winCheck(){
        String[][] field = new String[3][3];
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                field[i][j] = buttons[i][j].getText().toString();
            }
        }
        //řádky
        for(int i = 0; i < 3; i++) {
            if (field[i][0].equals(field[i][1]) &&
                    field[i][0].equals(field[i][2]) &&
                    !field[i][0].equals(""))
                return true;
        }
        //sloupce
        for(int i = 0; i < 3; i++){
            if(field[0][i].equals(field[1][i]) &&
                    field[0][i].equals(field[2][i]) &&
                    !field[0][i].equals(""))
                return true;
        }
        //1. diagonála
        if(field[0][0].equals(field[1][1]) &&
                field[0][0].equals(field[2][2]) &&
                !field[0][0].equals(""))
            return true;
        //2. diagonála
        if(field[0][2].equals(field[1][1]) &&
                field[0][2].equals(field[2][0]) &&
                !field[0][2].equals(""))
            return true;
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("roundCount", roundCount);
        outState.putBoolean("playerTurn", playerTurn);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        roundCount = savedInstanceState.getInt("roundCount");
        playerTurn = savedInstanceState.getBoolean("playerTurn");
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_back) {
            startActivity(new Intent(GameActivity.this, MainActivity.class));

        } else if (id == R.id.nav_restart) {
            startActivity(new Intent(GameActivity.this, GameActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
