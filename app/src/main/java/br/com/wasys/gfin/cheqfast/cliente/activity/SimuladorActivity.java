package br.com.wasys.gfin.cheqfast.cliente.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import br.com.wasys.gfin.cheqfast.cliente.R;

public class SimuladorActivity extends AppCompatActivity {

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, SimuladorActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulador);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
