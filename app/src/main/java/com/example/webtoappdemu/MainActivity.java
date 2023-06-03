package com.example.webtoappdemu;



import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    WebView webView;
    LinearLayout layNonet;
    ProgressBar progressBar;
    SwipeRefreshLayout layRoot;
    String USER_AGENT_ = "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19";
    SwipeRefreshLayout refresh;
    Button tryagain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        layRoot = findViewById(R.id.layRoot);
        layNonet = findViewById(R.id.layNonet);
        webView = findViewById(R.id.webview);
        refresh = findViewById(R.id.layRoot);
        tryagain = findViewById(R.id.tryagain);
        progressBar = findViewById(R.id.progressBar);

        checkConnection();


        webView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        webView.setFitsSystemWindows(false); // your preferences
        webView.setVerticalScrollBarEnabled(false); // your preferences
        //webView.setPadding(15,15,15,15); // your preferences
        webView.getSettings().setUserAgentString(USER_AGENT_);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        //webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.setWebViewClient(new HelloWebViewClient());
        webView.setWebChromeClient(new ChromeClient() {
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });

        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl(getString(R.string.website_link));


        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(false);
                webView.reload();
            }
        });

        tryagain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkConnection();
            }
        });

    }//=====================================================End oncreate

    //================================================= defining custom class (HelloWebViewClient)
    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            if (url != null && (url.startsWith("whatsapp://") || url.startsWith("tel") || url.startsWith("https://m.me/109012968398097")) ||
                    url.startsWith("mailto:") || url.startsWith("https://www.youtube.com/")|| url.startsWith("https://www.facebook.com/")) {
                webView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                webView.reload();


                return true;
            }
            else {
                return false;
            }

        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            checkConnection();
            super.onReceivedError(view, request, error);
        }
    }
    //================================================

    //================================================= defining custom class (ChromeClient)
    private class ChromeClient extends WebChromeClient {
        // Defining some variables
        private View mCustomView;
        private CustomViewCallback mCustomViewCallback;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progressBar.setProgress(newProgress);

            if(newProgress >= 100){
                progressBar.setVisibility(View.GONE);

            }else{
                progressBar.setVisibility(View.VISIBLE);
            }
            super.onProgressChanged(view, newProgress);

            String wurl = view.getUrl();
            //Default Copy Link
            Pattern pattern = Pattern.compile("https://chinaonlinebd.com/product/", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(wurl);
            boolean matchFound = matcher.find();
            if(matchFound) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("demo", wurl);
                clipboard.setPrimaryClip(clip);

            }

        }

        public Bitmap getDefaultVideoPoster()
        {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView()
        {
            ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, CustomViewCallback paramCustomViewCallback)
        {
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

    }

    //================================================

    @Override
    public void onBackPressed() {
        if (!webView.canGoBack()) {
            MaterialAlertDialogBuilder builder  = new MaterialAlertDialogBuilder(MainActivity.this, R.style.AlertDialogTheme);
            builder.setIcon(R.drawable.exit);
            builder.setTitle(R.string.app_name);
            builder.setMessage("   Do you want to Exit ?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    finish();
                }

            });
            builder.setNegativeButton("No", null);
            builder.show();


        }

        else{
            webView.goBack();
        }

    }
    //================================================================

    //=================================================== Gallery Permission





    //==================================================================

    //===================================================Internet Check
    public void checkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if(wifi.isConnected()){
            webView.reload();
            layRoot.setVisibility(View.VISIBLE);
            layNonet.setVisibility(View.GONE);
        }

        else if (mobileNetwork.isConnected()){
            webView.reload();
            layRoot.setVisibility(View.VISIBLE);
            layNonet.setVisibility(View.GONE);
        }
        else{
            layRoot.setVisibility(View.GONE);
            layNonet.setVisibility(View.VISIBLE);
        }
    }




}