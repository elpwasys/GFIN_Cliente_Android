package br.com.wasys.gfin.cheqfast.cliente.dialog;


import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import br.com.wasys.gfin.cheqfast.cliente.R;
import br.com.wasys.gfin.cheqfast.cliente.model.FavorecidoModel;
import br.com.wasys.gfin.cheqfast.cliente.model.TransferenciaModel;
import br.com.wasys.library.text.Mask;
import br.com.wasys.library.text.MoneyTextWatcher;
import br.com.wasys.library.utils.FieldUtils;
import br.com.wasys.library.utils.NumberUtils;
import br.com.wasys.library.utils.ValidatorUtils;
import br.com.wasys.library.widget.AppSpinner;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavorecidoDialog extends AppCompatDialogFragment implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.edit_valor) EditText mValorEditText;
    @BindView(R.id.edit_conta) EditText mContaEditText;
    @BindView(R.id.edit_agencia) EditText mAgenciaEditText;
    @BindView(R.id.edit_titular) EditText mTitularEditText;
    @BindView(R.id.edit_cpf_cnpj) EditText mCpfCnpjEditText;
    @BindView(R.id.text_cpf_cnpj) TextView mCpfCnpjTextView;
    @BindView(R.id.spinner_banco) AppSpinner mBancoSpinner;
    @BindView(R.id.switch_documento) Switch mDocumentoSwitch;

    @BindView(R.id.layout_valor) TextInputLayout mValorTextInputLayout;
    @BindView(R.id.layout_conta) TextInputLayout mContaTextInputLayout;
    @BindView(R.id.layout_banco) TextInputLayout mBancoTextInputLayout;
    @BindView(R.id.layout_agencia) TextInputLayout mAgenciaTextInputLayout;
    @BindView(R.id.layout_titular) TextInputLayout mTitularTextInputLayout;
    @BindView(R.id.layout_cpf_cnpj) TextInputLayout mCpfCnpjTextInputLayout;

    private FavorecidoModel mFavorecido;
    private TransferenciaModel mTransferencia;

    private TextWatcher mTextWatcher;
    private OnFavorecidoListener mFavorecidoListener;

    public static FavorecidoDialog newInstance(TransferenciaModel transferencia, FavorecidoModel favorecido) {
        FavorecidoDialog fragment = new FavorecidoDialog();
        fragment.mFavorecido = favorecido;
        fragment.mTransferencia = transferencia;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.dialog_favorecido, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);
        ButterKnife.bind(this, view);
        mDocumentoSwitch.setOnCheckedChangeListener(this);
        new MoneyTextWatcher(mValorEditText);
        AlertDialog dialog = builder.create();
        populate();
        return dialog;
    }

    @OnClick(R.id.button_fechar)
    public void onFecharClick() {
        dismiss();
    }

    @OnClick(R.id.button_salvar)
    public void onSalvarClick() {
        if (isValid()) {
            mFavorecido.banco = mBancoSpinner.getValue();
            mFavorecido.agencia = FieldUtils.getValue(mAgenciaEditText);
            mFavorecido.conta = FieldUtils.getValue(mContaEditText);
            mFavorecido.nomeTitular = FieldUtils.getValue(mTitularEditText);
            mFavorecido.cpfCnpj = FieldUtils.getValue(mCpfCnpjEditText);
            mFavorecido.valor = FieldUtils.getValue(Double.class, mValorEditText);
            boolean dismiss = true;
            if (mFavorecidoListener != null) {
                dismiss = mFavorecidoListener.onSalvar(mFavorecido);
            }
            if (dismiss) {
                dismiss();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mCpfCnpjEditText.setText(null);
        mCpfCnpjEditText.removeTextChangedListener(mTextWatcher);
        if (!isChecked) {
            mTextWatcher = Mask.insert("###.###.###-##", mCpfCnpjEditText);
            mCpfCnpjTextView.setText(R.string.trocar_cnpj);
            mCpfCnpjTextInputLayout.setHint(getString(R.string.cpf));
        } else {
            mTextWatcher = Mask.insert("##.###.###/####-##", mCpfCnpjEditText);
            mCpfCnpjTextView.setText(R.string.trocar_cpf);
            mCpfCnpjTextInputLayout.setHint(getString(R.string.cnpj));
        }
        mCpfCnpjTextInputLayout.setError(null);
        mCpfCnpjTextInputLayout.setErrorEnabled(false);
    }

    private void populate() {
        if (mFavorecido == null) {
            mFavorecido = new FavorecidoModel();
        }
        List<String> bancos = mTransferencia.bancos;
        if (CollectionUtils.isNotEmpty(bancos)) {
            List<AppSpinner.Selectable> selectables = new ArrayList<>(bancos.size());
            for (String banco : bancos) {
                selectables.add(new AppSpinner.Selectable(banco, banco));
            }
            mBancoSpinner.setOptions(selectables);
        }
        mTextWatcher = Mask.insert("###.###.###-##", mCpfCnpjEditText);
        if (ValidatorUtils.isCnpj(mFavorecido.cpfCnpj)) {
            mDocumentoSwitch.setChecked(true);
        }
        mBancoSpinner.setValue(mFavorecido.banco);
        FieldUtils.setText(mAgenciaEditText, mFavorecido.agencia);
        FieldUtils.setText(mContaEditText, mFavorecido.conta);
        FieldUtils.setText(mTitularEditText, mFavorecido.nomeTitular);
        FieldUtils.setText(mCpfCnpjEditText, mFavorecido.cpfCnpj);
        FieldUtils.setText(mValorEditText, mFavorecido.valor);
    }

    private boolean isValid() {
        boolean valid = true;
        // BANCO
        String banco = mBancoSpinner.getValue();
        mBancoTextInputLayout.setError(null);
        mBancoTextInputLayout.setErrorEnabled(false);
        if (StringUtils.isBlank(banco)) {
            valid = false;
            mBancoTextInputLayout.setEnabled(true);
            mBancoTextInputLayout.setError(getString(R.string.msg_required_field, getString(R.string.banco)));
        }
        // AGENCIA
        String agencia = FieldUtils.getValue(mAgenciaEditText);
        mAgenciaTextInputLayout.setError(null);
        mAgenciaTextInputLayout.setErrorEnabled(false);
        if (StringUtils.isBlank(agencia)) {
            valid = false;
            mAgenciaTextInputLayout.setEnabled(true);
            mAgenciaTextInputLayout.setError(getString(R.string.msg_required_field, getString(R.string.agencia)));
        }
        // CONTA
        String conta = FieldUtils.getValue(mContaEditText);
        mContaTextInputLayout.setError(null);
        mContaTextInputLayout.setErrorEnabled(false);
        if (StringUtils.isBlank(conta)) {
            valid = false;
            mContaTextInputLayout.setEnabled(true);
            mContaTextInputLayout.setError(getString(R.string.msg_required_field, getString(R.string.conta)));
        }
        // TITULAR
        String titular = FieldUtils.getValue(mTitularEditText);
        mTitularTextInputLayout.setError(null);
        mTitularTextInputLayout.setErrorEnabled(false);
        if (StringUtils.isBlank(titular)) {
            valid = false;
            mTitularTextInputLayout.setEnabled(true);
            mTitularTextInputLayout.setError(getString(R.string.msg_required_field, getString(R.string.titular)));
        }
        // CPF/CNPJ
        String cpfCnpj = FieldUtils.getValue(mCpfCnpjEditText);
        mCpfCnpjTextInputLayout.setError(null);
        mCpfCnpjTextInputLayout.setErrorEnabled(false);
        if (StringUtils.isBlank(cpfCnpj)) {
            valid = false;
            mCpfCnpjTextInputLayout.setError(getString(R.string.msg_required_field, getString(mDocumentoSwitch.isChecked() ? R.string.cnpj : R.string.cpf)));
        } else {
            if (mDocumentoSwitch.isChecked()) {
                if (!ValidatorUtils.isValidCnpj(cpfCnpj)) {
                    valid = false;
                    mCpfCnpjTextInputLayout.setEnabled(true);
                    mCpfCnpjTextInputLayout.setError(getString(R.string.msg_invalid_field, getString(R.string.cnpj)));
                }
            } else if (!ValidatorUtils.isValidCpf(cpfCnpj)) {
                valid = false;
                mCpfCnpjTextInputLayout.setEnabled(true);
                mCpfCnpjTextInputLayout.setError(getString(R.string.msg_invalid_field, getString(R.string.cpf)));
            }
        }
        // VALOR
        Double valor = FieldUtils.getValue(Double.class, mValorEditText);
        mValorTextInputLayout.setError(null);
        mValorTextInputLayout.setErrorEnabled(false);
        if (valor == null) {
            valid = false;
            mValorTextInputLayout.setEnabled(true);
            mValorTextInputLayout.setError(getString(R.string.msg_required_field, getString(R.string.valor)));
        } else if (valor <= 0) {
            valid = false;
            mValorTextInputLayout.setEnabled(true);
            mValorTextInputLayout.setError(getString(R.string.msg_invalid_field, getString(R.string.valor)));
        } else if (valor <= mTransferencia.custo) {
            valid = false;
            mValorTextInputLayout.setEnabled(true);
            String custo = NumberUtils.format(mTransferencia.custo);
            mValorTextInputLayout.setError(getString(R.string.msg_erro_valor_maior_custo, custo));
        }
        return valid;
    }

    public void setFavorecidoListener(OnFavorecidoListener favorecidoListener) {
        mFavorecidoListener = favorecidoListener;
    }

    public static interface OnFavorecidoListener {
        boolean onSalvar(FavorecidoModel favorecido);
    }
}