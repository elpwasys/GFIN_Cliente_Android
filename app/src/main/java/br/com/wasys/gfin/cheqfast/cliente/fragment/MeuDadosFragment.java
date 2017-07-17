package br.com.wasys.gfin.cheqfast.cliente.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;

import br.com.wasys.gfin.cheqfast.cliente.R;
import br.com.wasys.gfin.cheqfast.cliente.model.CampoGrupoModel;
import br.com.wasys.gfin.cheqfast.cliente.model.ProcessoModel;
import br.com.wasys.gfin.cheqfast.cliente.service.ClienteService;
import br.com.wasys.gfin.cheqfast.cliente.widget.AppGroupReadonlyInputLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;


/**
 * A simple {@link Fragment} subclass.
 */
public class MeuDadosFragment extends CheqFastFragment {

    @BindView(R.id.layout_fields) LinearLayout mLayoutFields;

    private ProcessoModel mProcesso;

    public static MeuDadosFragment newInstance() {
        MeuDadosFragment fragment = new MeuDadosFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meu_dados, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        prepare();
        startAsyncData();
    }

    private void prepare() {
        setTitle(R.string.meus_dados);
    }

    private void onSyncDataCompleted(ProcessoModel model) {
        mProcesso = model;
        ArrayList<CampoGrupoModel> gruposCampos = mProcesso.gruposCampos;
        if (CollectionUtils.isNotEmpty(gruposCampos)) {
            Context context = getContext();
            for (CampoGrupoModel grupo : gruposCampos) {
                AppGroupReadonlyInputLayout campoGrupoLayout = new AppGroupReadonlyInputLayout(context);
                campoGrupoLayout.setOrientation(LinearLayout.VERTICAL);
                campoGrupoLayout.setGrupo(grupo);
                mLayoutFields.addView(campoGrupoLayout);
            }
        }
    }

    private void startAsyncData() {
        showProgress();
        Observable<ProcessoModel> observable = ClienteService.Async.obter();
        prepare(observable).subscribe(new Subscriber<ProcessoModel>() {
            @Override
            public void onCompleted() {
                hideProgress();
            }
            @Override
            public void onError(Throwable e) {
                hideProgress();
                handle(e);
            }
            @Override
            public void onNext(ProcessoModel model) {
                onSyncDataCompleted(model);
            }
        });
    }
}
