package br.com.wasys.gfin.cheqfast.cliente.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;

import br.com.wasys.gfin.cheqfast.cliente.R;
import br.com.wasys.library.utils.FieldUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.edit_login) EditText mLoginEditText;
    @BindView(R.id.edit_senha) EditText mSenhaEditText;

    @BindView(R.id.layout_login) TextInputLayout mLoginTextInputLayout;
    @BindView(R.id.layout_senha) TextInputLayout mSenhaTextInputLayout;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_recuperar)
    public void onRecoveryClick() {
        Intent intent = RecoveryActivity.newIntent(this);
        startActivity(intent);
    }

    @OnClick(R.id.button_entrar)
    public void onEntrarClick() {
        boolean valid = isValid();
        if (valid) {
            startAsyncAutenticar();
        }
    }

    private boolean isValid() {
        boolean valid = true;
        // LOGIN
        String login = FieldUtils.getValue(mLoginEditText);
        if (StringUtils.isBlank(login)) {
            valid = false;
            mLoginTextInputLayout.setError(getString(R.string.msg_required_field, getString(R.string.email)));
        } else {
            mLoginTextInputLayout.setErrorEnabled(false);
        }
        // SENHA
        String senha = FieldUtils.getValue(mSenhaEditText);
        if (StringUtils.isBlank(senha)) {
            valid = false;
            mSenhaTextInputLayout.setError(getString(R.string.msg_required_field, getString(R.string.senha)));
        } else {
            mSenhaTextInputLayout.setErrorEnabled(false);
        }
        return valid;
    }

    private void startAsyncAutenticar() {
        Intent intent = MainActivity.newIntent(this);
        startActivity(intent);
        finish();
    }
}
