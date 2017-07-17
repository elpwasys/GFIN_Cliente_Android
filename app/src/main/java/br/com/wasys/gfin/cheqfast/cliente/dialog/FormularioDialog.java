package br.com.wasys.gfin.cheqfast.cliente.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import br.com.wasys.gfin.cheqfast.cliente.R;
import br.com.wasys.gfin.cheqfast.cliente.model.CampoGrupoModel;
import br.com.wasys.gfin.cheqfast.cliente.widget.AppGroupInputLayout;
import br.com.wasys.library.utils.AndroidUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pascke on 14/07/17.
 */

public class FormularioDialog extends AppCompatDialogFragment {

    @BindView(R.id.layout_fields) LinearLayout mLayoutFields;

    private List<CampoGrupoModel> mGrupos;
    private FormularioListener mFormularioListener;

    public static FormularioDialog newInstance(List<CampoGrupoModel> grupos) {
        FormularioDialog fragment = new FormularioDialog();
        fragment.mGrupos = grupos;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.dialog_formulario, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);
        ButterKnife.bind(this, view);
        AlertDialog dialog = builder.create();
        prepare();
        return dialog;
    }

    @OnClick(R.id.button_fechar)
    public void onFecharClick() {
        dismiss();
    }

    @OnClick(R.id.button_confirmar)
    public void onConfirmarlick() {
        AndroidUtils.hideKeyboard(getContext(), mLayoutFields);
        if (isValid()) {
            int childCount = mLayoutFields.getChildCount();
            if (childCount > 0) {
                ArrayList<CampoGrupoModel> gruposCampos = new ArrayList<>();
                for (int i = 0; i < childCount; i++) {
                    View view = mLayoutFields.getChildAt(i);
                    if (view instanceof AppGroupInputLayout) {
                        AppGroupInputLayout grupoLayout = (AppGroupInputLayout) view;
                        CampoGrupoModel grupoModel = grupoLayout.getValue();
                        gruposCampos.add(grupoModel);
                    }
                }
                if (mFormularioListener != null) {
                    mFormularioListener.onConfirmar(gruposCampos);
                }
            }
            dismiss();
        }
    }

    private boolean isValid() {
        boolean valid = true;
        int childCount = mLayoutFields.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View view = mLayoutFields.getChildAt(i);
                if (view instanceof AppGroupInputLayout) {
                    AppGroupInputLayout grupoLayout = (AppGroupInputLayout) view;
                    if (!grupoLayout.isValid()) {
                        valid = false;
                    }
                }
            }
            if (!valid) {
                Context context = getContext();
                Toast.makeText(context, R.string.msg_form_invalido, Toast.LENGTH_SHORT).show();
            }
        }
        return valid;
    }

    private void prepare() {
        if (CollectionUtils.isNotEmpty(mGrupos)) {
            Context context = getContext();
            for (CampoGrupoModel grupo : mGrupos) {
                AppGroupInputLayout campoGrupoLayout = new AppGroupInputLayout(context);
                campoGrupoLayout.setOrientation(LinearLayout.VERTICAL);
                campoGrupoLayout.setGrupo(grupo);
                mLayoutFields.addView(campoGrupoLayout);
            }
        }
    }

    public void setFormularioListener(FormularioListener formularioListener) {
        mFormularioListener = formularioListener;
    }

    public static interface FormularioListener {
        void onConfirmar(ArrayList<CampoGrupoModel> gruposCampos);
    }
}
