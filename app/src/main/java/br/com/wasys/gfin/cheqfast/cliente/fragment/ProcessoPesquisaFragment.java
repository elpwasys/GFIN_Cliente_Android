package br.com.wasys.gfin.cheqfast.cliente.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

import br.com.wasys.gfin.cheqfast.cliente.R;
import br.com.wasys.gfin.cheqfast.cliente.adapter.ProcessoAdapter;
import br.com.wasys.gfin.cheqfast.cliente.dialog.FiltroDialog;
import br.com.wasys.gfin.cheqfast.cliente.model.FiltroModel;
import br.com.wasys.gfin.cheqfast.cliente.model.PesquisaModel;
import br.com.wasys.gfin.cheqfast.cliente.model.ProcessoModel;
import br.com.wasys.gfin.cheqfast.cliente.paging.ProcessoPagingModel;
import br.com.wasys.gfin.cheqfast.cliente.service.PesquisaService;
import br.com.wasys.gfin.cheqfast.cliente.widget.PagingBarLayout;
import br.com.wasys.library.adapter.ListAdapter;
import br.com.wasys.library.utils.FragmentUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProcessoPesquisaFragment extends CheqFastFragment implements AdapterView.OnItemClickListener, PagingBarLayout.Callback, FiltroDialog.FiltroDialogListener {

    @BindView(R.id.textview) TextView mTextView;
    @BindView(R.id.listview) ListView mListView;
    @BindView(R.id.pagingbar) PagingBarLayout mPagingBarLayout;
    @BindView(R.id.constraint) ConstraintLayout mConstraintLayout;

    private ProcessoModel.Status mStatus;
    private PesquisaModel mPesquisaModel;
    private ProcessoPagingModel mPagingModel;
    private ListAdapter<ProcessoModel> mListAdapter;

    private static final String KEY_STATUS = ProcessoPesquisaFragment.class.getName() + ".mStatus";

    public static ProcessoPesquisaFragment newInstance() {
        ProcessoPesquisaFragment fragment = new ProcessoPesquisaFragment();
        return fragment;
    }


    public static ProcessoPesquisaFragment newInstance(ProcessoModel.Status status) {
        ProcessoPesquisaFragment fragment = new ProcessoPesquisaFragment();
        if (status != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(KEY_STATUS, status);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(KEY_STATUS)) {
                mStatus = (ProcessoModel.Status) bundle.get(KEY_STATUS);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_processo_pesquisa, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        prepare();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_processo_pesquisa, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_refresh:
                startAsyncFiltrar();
                return true;
            case R.id.action_search:
                onOpenSearchClick();
                return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        startAsyncFiltrar();
    }

    @OnClick(R.id.textview)
    public void onTextClick() {
        FragmentActivity activity = getActivity();
        ProcessoNovoFragment fragment = ProcessoNovoFragment.newInstance();
        FragmentUtils.popAllBackStackImmediate(activity);
        FragmentUtils.replace(activity, R.id.content_main, fragment, fragment.getBackStackName());
    }

    @Override
    public void onNextClick(int page) {
        mPesquisaModel.page = page;
        startAsyncFiltrar();
    }

    @Override
    public void onPreviousClick(int page) {
        mPesquisaModel.page = page;
        startAsyncFiltrar();
    }

    @Override
    public void onFiltrar(FiltroModel filtroModel) {
        mPesquisaModel.page = 0;
        mPesquisaModel.filtro = filtroModel;
        startAsyncFiltrar();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Adapter adapter = parent.getAdapter();
        ProcessoModel processo = (ProcessoModel) adapter.getItem(position);
        ProcessoDetalheFragment fragment = ProcessoDetalheFragment.newInstance(processo.id);
        FragmentUtils.replace(getActivity(), R.id.content_main, fragment, fragment.getBackStackName());
    }

    private void prepare() {
        setTitle(R.string.pesquisa);
        mListAdapter = new ProcessoAdapter(getBaseContext(), null);
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mListAdapter);
        mPesquisaModel = new PesquisaModel();
        mPesquisaModel.page = 0;
        if (mStatus != null) {
            mPesquisaModel.filtro = new FiltroModel();
            mPesquisaModel.filtro.status = mStatus;
            setTitle(mStatus.stringRes);
        }
        mPagingBarLayout.setCallback(this);
    }

    private void atualizar() {
        List<ProcessoModel> records = mPagingModel.getRecords();
        mListAdapter.setRows(records);
        mPagingBarLayout.setPagingModel(mPagingModel);
        if (CollectionUtils.isNotEmpty(records)) {
            mTextView.setVisibility(View.GONE);
            mConstraintLayout.setVisibility(View.VISIBLE);
        } else {
            mTextView.setVisibility(View.VISIBLE);
            mConstraintLayout.setVisibility(View.GONE);
        }
    }

    private void onOpenSearchClick() {
        FiltroDialog dialog = FiltroDialog.newInstance(mPesquisaModel.filtro);
        dialog.setListener(this);
        if (mStatus != null) {
            dialog.setStatusVisibility(View.GONE);
        }
        FragmentManager manager = getFragmentManager();
        dialog.show(manager,  dialog.getClass().getSimpleName());
    }

    // ASYNC COMPLETED METHOD
    private void asyncFiltrarCompleted(ProcessoPagingModel pagingModel) {
        mPagingModel = pagingModel;
        atualizar();
    }

    // ASYNC METHOD
    private void startAsyncFiltrar() {
        showProgress();
        Observable<ProcessoPagingModel> observable = PesquisaService.Async.filtrar(mPesquisaModel);
        prepare(observable).subscribe(new Subscriber<ProcessoPagingModel>() {
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
            public void onNext(ProcessoPagingModel pagingModel) {
                hideProgress();
                asyncFiltrarCompleted(pagingModel);
            }
        });
    }
}