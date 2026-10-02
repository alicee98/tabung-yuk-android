package id.tabungyuk.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public final class MainActivity extends Activity {
    private static final String HOME = "https://tabung-yuk.vercel.app/";
    private WebView web;
    private ProgressBar progress;
    private LinearLayout errorPanel;
    private boolean failed;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(45, 37, 101));
        getWindow().setNavigationBarColor(Color.rgb(45, 37, 101));
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.rgb(45, 37, 101));
        root.setOnApplyWindowInsetsListener((v, insets) -> {
            v.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                    insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
            return insets.consumeSystemWindowInsets();
        });
        web = new WebView(this);
        root.addView(web, new FrameLayout.LayoutParams(-1, -1));
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setSupportZoom(false);
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(web, false);

        errorPanel = new LinearLayout(this);
        errorPanel.setOrientation(LinearLayout.VERTICAL);
        errorPanel.setGravity(Gravity.CENTER);
        errorPanel.setPadding(dp(28), dp(28), dp(28), dp(28));
        errorPanel.setBackgroundColor(Color.WHITE);
        TextView title = new TextView(this);
        title.setText("Halaman belum dapat dibuka");
        title.setTextSize(22);
        title.setTextColor(Color.rgb(45, 37, 101));
        title.setGravity(Gravity.CENTER);
        errorPanel.addView(title);
        TextView message = new TextView(this);
        message.setText("Periksa koneksi internet, lalu coba lagi.");
        message.setTextSize(16);
        message.setGravity(Gravity.CENTER);
        message.setPadding(0, dp(14), 0, dp(22));
        errorPanel.addView(message);
        Button retry = new Button(this);
        retry.setText("Coba lagi");
        retry.setOnClickListener(v -> { failed = false; errorPanel.setVisibility(View.GONE); web.loadUrl(HOME); });
        errorPanel.addView(retry);
        errorPanel.setVisibility(View.GONE);
        root.addView(errorPanel, new FrameLayout.LayoutParams(-1, -1));

        progress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progress.setProgressTintList(android.content.res.ColorStateList.valueOf(Color.rgb(183, 207, 80)));
        root.addView(progress, new FrameLayout.LayoutParams(-1, dp(3), Gravity.TOP));
        setContentView(root);
        web.setWebChromeClient(new WebChromeClient() {
            @Override public void onProgressChanged(WebView view, int value) {
                progress.setProgress(value);
                progress.setVisibility(value == 100 || failed ? View.GONE : View.VISIBLE);
            }
        });
        web.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return route(request.getUrl());
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return route(Uri.parse(url));
            }
            @Override public void onPageStarted(WebView view, String url, android.graphics.Bitmap icon) {
                failed = false;
                errorPanel.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
            }
            @Override public void onPageFinished(WebView view, String url) {
                progress.setVisibility(View.GONE);
                CookieManager.getInstance().flush();
            }
            @Override public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (request.isForMainFrame()) showError();
            }
            @Override public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse response) {
                if (request.isForMainFrame() && response.getStatusCode() >= 400) showError();
            }
        });
        if (state == null || web.restoreState(state) == null) web.loadUrl(HOME);
    }
    private boolean route(Uri uri) {
        if ("https".equalsIgnoreCase(uri.getScheme()) && "tabung-yuk.vercel.app".equalsIgnoreCase(uri.getHost())) return false;
        if ("https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme()) || "mailto".equalsIgnoreCase(uri.getScheme()) || "tel".equalsIgnoreCase(uri.getScheme())) {
            try { startActivity(new Intent(Intent.ACTION_VIEW, uri)); }
            catch (android.content.ActivityNotFoundException e) { Toast.makeText(this, "Tidak ada aplikasi untuk membuka tautan ini.", Toast.LENGTH_SHORT).show(); }
        }
        return true;
    }
    private void showError() {
        failed = true;
        progress.setVisibility(View.GONE);
        errorPanel.setVisibility(View.VISIBLE);
    }
    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
    @Override public void onBackPressed() {
        if (web.canGoBack()) { errorPanel.setVisibility(View.GONE); web.goBack(); }
        else super.onBackPressed();
    }
    @Override protected void onSaveInstanceState(Bundle out) { web.saveState(out); super.onSaveInstanceState(out); }
    @Override protected void onPause() { web.onPause(); CookieManager.getInstance().flush(); super.onPause(); }
    @Override protected void onResume() { super.onResume(); if (web != null) web.onResume(); }
    @Override protected void onDestroy() { ((android.view.ViewGroup) web.getParent()).removeView(web); web.destroy(); super.onDestroy(); }
}
