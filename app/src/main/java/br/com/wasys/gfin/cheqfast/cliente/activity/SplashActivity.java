package br.com.wasys.gfin.cheqfast.cliente.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import br.com.wasys.gfin.cheqfast.cliente.Dispositivo;
import br.com.wasys.gfin.cheqfast.cliente.R;

public class SplashActivity extends AppCompatActivity {

    private Handler mHandler = new Handler();

    public static Intent newIntent(Context context) {
        return new Intent(context, SplashActivity.class);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                start();
            }
        }, 2000);
    }

    private void start() {
        Intent intent;
        Dispositivo dispositivo = Dispositivo.current();
        if (dispositivo != null) {
            intent = MainActivity.newIntent(this);
        } else {
            intent = LoginActivity.newIntent(this);
        }
        startActivity(intent);
        finish();
    }
}
