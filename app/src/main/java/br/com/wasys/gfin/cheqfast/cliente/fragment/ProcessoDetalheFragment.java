package br.com.wasys.gfin.cheqfast.cliente.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.wasys.gfin.cheqfast.cliente.R;
import br.com.wasys.gfin.cheqfast.cliente.dataset.DataSet;
import br.com.wasys.gfin.cheqfast.cliente.model.DigitalizacaoModel;
import br.com.wasys.gfin.cheqfast.cliente.model.ProcessoModel;
import br.com.wasys.gfin.cheqfast.cliente.model.ProcessoRegraModel;
import br.com.wasys.gfin.cheqfast.cliente.service.ProcessoService;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProcessoDetalheFragment extends CheqFastFragment {

    private Long mId;
    private ProcessoModel mProcesso;
    private ProcessoRegraModel mRegra;
    private DigitalizacaoModel mDigitalizacao;

    private static final String KEY_ID = ProcessoPesquisaFragment.class.getSimpleName() + ".mId";

    public static ProcessoDetalheFragment newInstance(Long id) {
        ProcessoDetalheFragment fragment = new ProcessoDetalheFragment();
        if (id != null) {
            Bundle bundle = new Bundle();
            bundle.putLong(KEY_ID, id);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(KEY_ID)) {
                mId = bundle.getLong(KEY_ID);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_processo_detalhe, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        if (mId != null) {
            startAsyncEditarById(mId);
        }
    }

    /**
     *
     * COMPLETED METHODS ASYNCHRONOUS HANDLERS
     *
     */
    private void onAsyncEditarCompleted(DataSet<ProcessoModel, ProcessoRegraModel> dataSet) {

        mRegra = dataSet.meta;
        mProcesso = dataSet.data;


    }

    /**
     *
     * ASYNCHRONOUS METHODS
     *
     */
    private void startAsyncEditarById(Long id) {
        showProgress();
        Observable<DataSet<ProcessoModel, ProcessoRegraModel>> observable = ProcessoService.Async.editar(id);
        prepare(observable).subscribe(new Subscriber<DataSet<ProcessoModel, ProcessoRegraModel>>() {
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
            public void onNext(DataSet<ProcessoModel, ProcessoRegraModel> dataSet) {
                hideProgress();
                onAsyncEditarCompleted(dataSet);
            }
        });
    }
}
