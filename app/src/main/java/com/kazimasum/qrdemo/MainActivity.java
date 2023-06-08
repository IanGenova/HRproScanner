package com.kazimasum.qrdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Button scanbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        scanbtn = findViewById(R.id.scanbtn);

        // Set up the action bar toggle for the sidebar menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Handle the click events of the sidebar menu items
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch (id) {
                    case R.id.scanbtn:
                        startActivity(new Intent(getApplicationContext(), scannerView.class));
                        return true;
                    case R.id.nav_home:
                        // Handle Home button click here
                        // Perform the desired action
                        return true;
                    case R.id.nav_gallery:
                        // Handle Gallery button click here
                        // Perform the desired action
                        return true;
                }

                return false;
            }
        });

        scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), scannerView.class));
            }
        });
        try {
            // Load the server's SSL certificate from the raw resources folder
            InputStream inputStream = getResources().openRawResource(R.raw.cert);
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            X509Certificate serverCertificate = (X509Certificate) certificateFactory.generateCertificate(inputStream);

            // Create a KeyStore and add the server's certificate
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("server", serverCertificate);

            // Create a TrustManagerFactory and initialize it with the KeyStore
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            // Create an SSLContext and initialize it with the TrustManagerFactory
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

            // Set the custom SSLContext as the default SSL context
            SSLContext.setDefault(sslContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.scanbtn:
                startActivity(new Intent(getApplicationContext(), scannerView.class));
                return true;
            case R.id.nav_home:
                // Handle Passes Scanner button click here
                // Perform the desired action
                return true;
            case R.id.nav_gallery:
                // Handle Payroll button click here
                // Perform the desired action
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

