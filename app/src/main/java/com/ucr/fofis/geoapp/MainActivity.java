package com.ucr.fofis.geoapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ucr.fofis.dataaccess.database.Ruta;
import com.ucr.fofis.geoapp.Fragment.HomeFragment;

/**
 * Actividad principal del App, controla menú de navegacíon, Diálogo de recomendaciones y audio introductorio
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Class currentFragmentType;
    FragmentManager fragmentManager;
    private HomeFragment homeFragment;
    public MediaPlayer introMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //setear la vista/layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //para controlar la navegacion del menu y para poder cambiar de fragmento
        homeFragment = new HomeFragment();
        fragmentManager = getSupportFragmentManager();
        currentFragmentType = homeFragment.getClass();
        setFragment(homeFragment);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        SharedPreferences prefs = this.getSharedPreferences("ibx", Context.MODE_PRIVATE);
        if(prefs.contains("firsttime")){
        }
        else{
            prefs.edit().putString("firsttime", "val").apply();
            //autoplay Intro Sound si es la primera vez que se entra al app
            autoplayIntro();
        }
    }


    //Menu lateral
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            currentFragmentType = HomeFragment.class;
            super.onBackPressed();
            setTitle("Isla Bolanhos Experience PAs");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * navegacion del menu, define lo que se hace para cada item del menu
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.inicio) {
            if (!(currentFragmentType == HomeFragment.class)) {
                setFragment(homeFragment);
                currentFragmentType = homeFragment.getClass();
                setTitle(Ruta.TITULO);
            }
        } else if (id == R.id.nav_recomendaciones) {
            this.showRecommentdationDialog();
        } else if (id == R.id.nav_audio) {
            autoplayIntro();
        } else if (id == R.id.web) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(Ruta.WEB_PAGE_URL));
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     *     setea el fragment seleccionado
     * @param fragment
     */
    private void setFragment(Fragment fragment) {
        if (fragmentManager.getBackStackEntryCount() == 1 && fragment.getClass() == HomeFragment.class) {
            fragmentManager.popBackStack();
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, fragment);
        if (fragmentManager.getBackStackEntryCount() == 0 && fragment.getClass() != HomeFragment.class) {
            fragmentTransaction.addToBackStack(fragment.getTag());
        }
        fragmentTransaction.commit();
    }

    /**
     * Carga el diálogo de recomendaciones cargadas desde Datos.RECOMENDACIONES
     */
    public void showRecommentdationDialog() {
        RecommendationDialog rd = new RecommendationDialog();
        rd.show(getSupportFragmentManager(), "\r\n  \r\n \r\n");
    }

    /**
     * Reproduce audio introductorio
     */
    private void autoplayIntro() {

        AudioManager manager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        if(!(manager.isMusicActive()))
        {
            introMediaPlayer = new MediaPlayer();
            introMediaPlayer = MediaPlayer.create(this, com.ucr.fofis.dataaccess.R.raw.intro);
            introMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            introMediaPlayer.setLooping(false);
            introMediaPlayer.start();
        }
    }
}
