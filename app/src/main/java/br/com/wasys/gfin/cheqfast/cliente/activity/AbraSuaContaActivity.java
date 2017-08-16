package br.com.wasys.gfin.cheqfast.cliente.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import br.com.wasys.gfin.cheqfast.cliente.BuildConfig;
import br.com.wasys.gfin.cheqfast.cliente.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AbraSuaContaActivity extends CheqFastActivity {

    @BindView(R.id.webview) WebView webView;

    private ValueCallback<Uri[]> mFilePathCallback;

    private static final int REQUEST_SELECT_FILE = 100;
    private static final int FILE_CHOOSER_RESULT_CODE = 1;

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
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowContentAccess(true);
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                hideProgress();
//            }
//        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                    mFilePathCallback = null;
                }
                mFilePathCallback = filePathCallback;
                Intent intent = fileChooserParams.createIntent();
                try {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e) {
                    mFilePathCallback = null;
                    Toast.makeText(AbraSuaContaActivity.this, "Não pode abrir o seletor de arquivos", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }
        });
        //showProgress();
        webView.loadUrl(BuildConfig.ABRA_SUA_CONTA_URL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_FILE) {
            if (mFilePathCallback == null) {
                return;
            }
            mFilePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
            mFilePathCallback = null;
        } else {
            Toast.makeText(this, "Falha no upload da imagem", Toast.LENGTH_LONG).show();
        }
    }
}
