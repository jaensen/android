package apollong.dspace;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import android.os.IBinder;
import android.provider.Settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.view.View;
import android.webkit.WebChromeClient;

import android.webkit.*;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebViewClient;
import android.widget.Button;

public class DSpace extends Activity {

    WebView _webView;
    WebSettings _webSettings;
    LocationService _locationService;
    String _url;
    Intent _startLocationServiceIntent;
    ServiceConnection _serviceConnection;
    Button _startButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        launch("file:///android_asset/www/index.html");
    }

    private void launch(String url) {
        _url = url;

        /**
         * webview with custom client for
         * url handling and options for various android versions
         */
        _webView = new WebView(DSpace.this);
        _webSettings = _webView.getSettings();
        _webView.setWebChromeClient(new WebChromeClient() {
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                // could ask the user but no.
                callback.invoke(origin, true, false);
            }
        });
        // attach client interaction, like url changes
        _webView.setWebViewClient(new CustomWebViewClient());

        // performance
        _webSettings.setJavaScriptEnabled(true);
        _webSettings.setDomStorageEnabled(true);
        _webSettings.setLightTouchEnabled(true);
        _webSettings.setRenderPriority(RenderPriority.HIGH);
        _webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        // ui
        _webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        _webView.setHapticFeedbackEnabled(true);   // does nothing yet
        _webView.requestFocus(View.FOCUS_DOWN);

        _startLocationServiceIntent = new Intent(this, LocationService.class);
        _serviceConnection = new ServiceConnection() {

            public void onServiceConnected(ComponentName className,
                                           IBinder binder) {
                LocationService.LocationServiceBinder serviceBinder = (LocationService.LocationServiceBinder) binder;
                LocationService serviceInstance = serviceBinder.getService();

                try {
                    serviceInstance.start();
                } catch (GpsNotReadyException ex) {
                    showSettingsAlert();
                }

                _startButton.setVisibility(View.GONE);

                _webView.addJavascriptInterface(new JsApi(serviceInstance), "HostLocationService");
                _webView.loadUrl(_url);
            }

            public void onServiceDisconnected(ComponentName className) {

            }
        };

        _startButton = (Button)findViewById(R.id.startGuiButton);
        _startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bindService(_startLocationServiceIntent, _serviceConnection, BIND_AUTO_CREATE);
            }
        });

        bindService(_startLocationServiceIntent, _serviceConnection, BIND_AUTO_CREATE);

        setContentView(_webView);
    }

    public void showSettingsAlert() {
        _startButton.setVisibility(View.VISIBLE);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("GPS required");
        alertDialog.setMessage("GPS is not enabled. Do you want to enable it?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}

