package br.com.wasys.gfin.cheqfast.cliente.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import br.com.wasys.gfin.cheqfast.cliente.BuildConfig;
import br.com.wasys.gfin.cheqfast.cliente.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AbraSuaContaActivity extends CheqFastActivity {

    @BindView(R.id.webview) WebView webView;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, AbraSuaContaActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abra_sua_conta);
        ButterKnife.bind(this);
        this.carregar();
    }

    private void carregar() {
        setTitle(R.string.abra_sua_conta);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                hideProgress();
            }
        });
        showProgress();
        webView.loadUrl(BuildConfig.ABRA_SUA_CONTA_URL);
    }
}
