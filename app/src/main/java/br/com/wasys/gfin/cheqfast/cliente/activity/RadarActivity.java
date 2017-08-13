package br.com.wasys.gfin.cheqfast.cliente.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import br.com.wasys.gfin.cheqfast.cliente.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RadarActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.switch_modo) Switch mModoSwitch;
    @BindView(R.id.edit_documento) EditText mDocumentoEditText;
    @BindView(R.id.layout_documento) TextInputLayout mDocumentoTextInputLayout;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, RadarActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        prepare();
    }

    private void prepare() {
        mModoSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String hint = getString(R.string.cpf);
        if (isChecked) {
            hint = getString(R.string.cnpj);
        }
        mDocumentoEditText.setHint(hint);
        mDocumentoTextInputLayout.setHint(hint);
    }
}
