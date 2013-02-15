package apollong.dspace;

/**
 * import some defaults
 * for network and webview
 */
import android.app.Activity;
import android.os.Bundle;

import android.util.Log;
import java.util.Enumeration;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.webkit.WebChromeClient;

import android.webkit.*;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebViewClient;

public class DSpace extends Activity {
  private final DSpace self = this;
  private WebView webView;
  private WebSettings webSettings;

  @Override
  public void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    launch( "file:///android_asset/www/index.html" );
  };
  private void launch(String url) {
    /**
     * webview with custom client for
     * url handling and options for various android versions
     */
    webView = new WebView(DSpace.this);
    webSettings = webView.getSettings( );
    webView.setWebChromeClient(new WebChromeClient() {
      public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        // could ask the user but no.
        callback.invoke(origin, true, false);
      }
    });
    // attach client interaction, like url changes
    webView.setWebViewClient(new CustomWebViewClient());

    // performance
    webSettings.setJavaScriptEnabled( true );
    webSettings.setDomStorageEnabled( true );
    webSettings.setLightTouchEnabled( true );
    webSettings.setRenderPriority( RenderPriority.HIGH );
    webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

    // ui
    webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    webView.setHapticFeedbackEnabled( true );   // does nothing yet
    webView.requestFocus( View.FOCUS_DOWN );

    setContentView(webView);
    webView.loadUrl(url);
  };
  private class CustomWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
  };
}

