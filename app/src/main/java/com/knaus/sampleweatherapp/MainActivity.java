package com.knaus.sampleweatherapp;

import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements com.google.android.gms.location.LocationListener {

    private static final String TAG = "MainActivity";

    private EditText edtSearch;
    private Button btnSearch;
    private Button btnGps;

    private GoogleApiClient mGoogleApiClient = null;
    private LocationRequest mLocationRequest = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");
        edtSearch = (EditText) findViewById(R.id.edit_search);
        btnSearch = (Button) findViewById(R.id.btn_search);
        btnGps = (Button) findViewById(R.id.btn_gps);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLocation(edtSearch.getText().toString());
                // Check if no view has focus:
                View view = getCurrentFocus();
                // hide keyboard if view found
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                // remove focus from edittext
                LinearLayout layout = (LinearLayout) findViewById(R.id.activity_main_layout);
                layout.requestFocus();
            }
        });

        btnGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getGpsLocation();
            }
        });

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((i == EditorInfo.IME_ACTION_DONE) || ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
                    btnSearch.performClick();
                    return true;
                }
                return false;
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, new WeatherFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search_location) {
            showInputDialog();
        }
        return false;
    }

    @Override
    protected void onStop() {
        stopFusedLocation();
        super.onStop();
    }

    /**
     * LocationChangedListener, on response from Google Play Services location API, translate location to a name and run weather forecast
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        String locationName = getNameFromLocation(location);
        if (locationName != null) {
            stopFusedLocation();
            changeLocation(locationName);
        }
    }

    /**
     * Checks if Google play services are supported by this device
     * @return true on success, else false
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability gaa = GoogleApiAvailability.getInstance();
        int resultCode = gaa.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (gaa.isUserResolvableError(resultCode)) {
                gaa.getErrorDialog(this, resultCode, 0);
            } else {
                Toast.makeText(getApplicationContext(), "This device is not supported.", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Start Google play services API for location services
     */
    public void startFusedLocation() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(
                    new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {

                        }
                    }).addOnConnectionFailedListener(
                    new GoogleApiClient.OnConnectionFailedListener() {

                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        }
                    }
            ).build();
        }
        mGoogleApiClient.connect();
    }

    /**
     * Stops Google Play Services API
     */
    public void stopFusedLocation() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Helper function for Google Play Services location API
     * @param listener
     */
    public void registerRequestUpdate(final com.google.android.gms.location.LocationListener listener) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);   //for using GPS module
        mLocationRequest.setInterval(1000);  // check every one second

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, listener);
                } catch (SecurityException se) {
                    Log.e(TAG, se.getMessage());
                } catch (Exception e) {
                    // on non-security error, check if connected to play services and try again
                    Log.e(TAG, e.getMessage());
                    if (!isGoogleApiClientConnected()) {
                        mGoogleApiClient.connect();
                    }
                    registerRequestUpdate(listener);
                }
            }
        }, 1000);
    }

    /**
     * Helper function to check if connected to Google API
     * @return
     */
    private boolean isGoogleApiClientConnected() {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    /**
     * Show input dialog on menu click
     */
    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change city");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeLocation(input.getText().toString());
            }
        });
        builder.show();
    }

    /**
     * Set weather fragment with location information
     * @param location
     */
    private void changeLocation(String location) {
        WeatherFragment wf = (WeatherFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        wf.changeLocation(location);
        // save the location in preferences
        new LocationPreference(this).setLocation(location);
    }

    /**
     * Helper function to start GPS location retrieval
     */
    private void getGpsLocation() {
        if (checkPlayServices()) {
            startFusedLocation();
            registerRequestUpdate(this);
        }
    }

    /**
     * Helper function to get location name from location's latitude/longitude
     * Called after Google location API returns value
     * @param location
     * @return
     */
    public String getNameFromLocation(Location location) {
        String result = null;
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0) {
                Address gpsResult = addresses.get(0);
                Log.d(TAG, gpsResult.toString());
                result = gpsResult.getLocality();
            }
        } catch (Exception e) {
            Log.d(TAG, String.format("Latitude: %f, Longitude: %f, %s", location.getLatitude(), location.getLongitude(), e.getMessage()));
        }
        return result;
    }
}
