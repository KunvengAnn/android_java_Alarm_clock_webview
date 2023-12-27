package com.example.clock_alarm_webview_app;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private WebView webView;

    private DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar tool_bar;

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //drawer tool bar
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        tool_bar = findViewById(R.id.toolbar);
        //tool bar
        setSupportActionBar(tool_bar);

        //navigation drawer menu

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, tool_bar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();


        //web view
        webView = findViewById(R.id.id_web_view);
        // config web view
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        // Load html
        String htmlFileName = "alarm_clock/index.html";
        String baseUrl = "file:///android_asset/";
        String htmlPath = baseUrl + htmlFileName;
        webView.loadUrl(htmlPath);

        // Create an instance of the WebAppInterface and bind it to the WebView
        WebAppInterface webAppInterface = new WebAppInterface(this); // 'this' refers to the current activity context
        webView.addJavascriptInterface(webAppInterface, "Android");

        //call it to check local storage
        webAppInterface.checkLocalStorage();


    }

    //for when click drawer
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        String baseUrl = "file:///android_asset/";

        if (menuItem.getItemId() == R.id.nav_Info_dev) {
            String htmlFileName = "alarm_clock/about_developer.html";
            String htmlPath = baseUrl + htmlFileName;
            webView.loadUrl(htmlPath);
        } else if (menuItem.getItemId() == R.id.nav_empty) {
            Toast.makeText(this, "No data to see", Toast.LENGTH_LONG).show();
        }

        // Close the drawer after item selection
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }



    // onBackPressed method
    @Override
    public void onBackPressed() {
        //webView Back Pressed for web view back
        // drawerLayout Back Pressed for drawer back
        if (webView != null && webView.canGoBack() || drawerLayout.isDrawerOpen(GravityCompat.START)) {
            webView.goBack();

            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}

