package com.kazimasum.qrdemo;

import android.Manifest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class scannerView extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    ZXingScannerView scannerView;
    MediaPlayer mediaPlayer;
    MediaPlayer mediaPlayer2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        mediaPlayer = MediaPlayer.create(this, R.raw.beep);
        mediaPlayer2 = MediaPlayer.create(this,R.raw.beep3);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        scannerView.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    public void handleResult(Result rawResult) {

        String scannedData = "{id: "+rawResult.getText()+"}";

        // Process the scanned data
        // For example, you can extract individual data fields from the scanned result
        try {
                Boolean checkFirst = false;
                JSONObject getNewJson = new JSONObject();
                try {
                    JSONObject checkJson = new JSONObject(scannedData);
                    checkFirst = true;
                } catch (JSONException e){
                    scannedData = "{'id': '',branchCode: ''}";
                    Toast.makeText(scannerView.this, "Unrecognized QR Code", Toast.LENGTH_SHORT).show();

                }
                System.out.println(checkFirst);

             JSONObject jsonObject = new JSONObject(scannedData);
             String idNum = jsonObject.getString("id");


            // Use the extracted data as needed
            // For example, display it in TextViews or perform further actions

            // Communicate with the server API using the scanned data
            // Make HTTP requests, send the data, and handle the response

            // Here's an example of making a POST request using the OkHttp library


            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Create an SSLContext that uses the trust manager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();

            MediaType mediaType = MediaType.parse("application/json");

            JSONObject requestObject = new JSONObject();
            requestObject.put("secretKey", "ygbcmishom");
            requestObject.put("key", "447d58c32ffde62c5382d3e5af4e6db49029654a6146d12ad9a72df5c22edc8be3117a0127a58537b3e151dc324eb98bba32ae48637d1a26f882e0dce154771d");
            requestObject.put("roles", "[5150]");
            requestObject.put("id", idNum);



            RequestBody body = RequestBody.create(mediaType, requestObject.toString());

            String secretKey = "ygbcmishom";
            System.out.println("ok");
            Request request = new Request.Builder()
                    .url("https://10.10.32.81:3500/HRProAuthenticateApi/GetEmployeeDataApi/")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    // Handle request failure
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            System.out.println(jsonObject);
                            String idNum = jsonObject.getString("idNum");
                            String name = jsonObject.getString("fullName");
                            String designation = jsonObject.getString("actualDesig");
                            String scantext = jsonObject.getString("status");
                            String branch = jsonObject.getString("branchCode");
                            String emplStatus = jsonObject.getString("emplStatus");
                            updateUI(idNum, name, designation, scantext, branch, emplStatus);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        // Handle unsuccessful response from the server
                        final int statusCode = response.code();
                        final String errorMessage = response.message();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Display an error message or perform any other appropriate action
                                Toast.makeText(scannerView.this, "Error: " + statusCode + " " + errorMessage, Toast.LENGTH_SHORT).show();
                                Toast.makeText(scannerView.this, "Unrecognized QR Code", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            // Handle JSON parsing error if necessary
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    private void updateUI(String idNum, String name, String designation, String scantext, String branch, String emplStatus) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_result);
                TextView idNumTextView = findViewById(R.id.idnum);
                TextView nameTextView = findViewById(R.id.name);
                TextView designationTextView = findViewById(R.id.position);
                TextView statusTextView = findViewById(R.id.scantext);
                TextView branchTextView = findViewById(R.id.branch);
                TextView employmentTextView = findViewById(R.id.emplstatus);
                if (scantext.equalsIgnoreCase("active")) {
                    mediaPlayer.start();
                    statusTextView.setTextColor(ContextCompat.getColor(scannerView.this, R.color.activeColor));
                } else if (scantext.equalsIgnoreCase("inactive")) {
                    mediaPlayer2.start();
                    statusTextView.setTextColor(ContextCompat.getColor(scannerView.this, R.color.inactiveColor));
                } else {
                    statusTextView.setTextColor(ContextCompat.getColor(scannerView.this, android.R.color.black));
                }

                idNumTextView.setText(idNum);
                nameTextView.setText(name);
                designationTextView.setText(designation);
                statusTextView.setText(scantext);
                branchTextView.setText(branch);
                employmentTextView.setText(emplStatus);

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }
}