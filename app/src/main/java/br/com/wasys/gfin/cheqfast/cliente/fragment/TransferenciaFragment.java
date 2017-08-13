package br.com.wasys.gfin.cheqfast.cliente.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import br.com.wasys.gfin.cheqfast.cliente.R;
import br.com.wasys.gfin.cheqfast.cliente.adapter.OnStartDragListener;
import br.com.wasys.gfin.cheqfast.cliente.adapter.RecyclerFavorecidoAdapter;
import br.com.wasys.gfin.cheqfast.cliente.adapter.SimpleItemTouchHelperCallback;
import br.com.wasys.gfin.cheqfast.cliente.dataset.DataSet;
import br.com.wasys.gfin.cheqfast.cliente.dialog.FavorecidoDialog;
import br.com.wasys.gfin.cheqfast.cliente.model.ContaBancoModel;
import br.com.wasys.gfin.cheqfast.cliente.model.FavorecidoModel;
import br.com.wasys.gfin.cheqfast.cliente.model.ProcessoModel;
import br.com.wasys.gfin.cheqfast.cliente.model.ProcessoRegraModel;
import br.com.wasys.gfin.cheqfast.cliente.model.TransferenciaModel;
import br.com.wasys.gfin.cheqfast.cliente.service.ProcessoService;
import br.com.wasys.gfin.cheqfast.cliente.service.TransferenciaService;
import br.com.wasys.library.utils.FieldUtils;
import br.com.wasys.library.utils.FragmentUtils;
import br.com.wasys.library.utils.NumberUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransferenciaFragment extends CheqFastFragment implements OnStartDragListener, FavorecidoDialog.OnFavorecidoListener, RecyclerFavorecidoAdapter.FavorecidoAdapterListener {

    @BindView(R.id.text_nome) TextView mNomeTextView;
    @BindView(R.id.text_banco) TextView mBancoTextView;
    @BindView(R.id.text_valor) TextView mValorTransferenciaTextView;
    @BindView(R.id.text_total) TextView mTotalTextView;
    @BindView(R.id.recyclerview) RecyclerView mRecyclerView;
    @BindView(R.id.text_cpf_cnpj) TextView mCpfCnpjTextView;
    @BindView(R.id.text_msg_custo) TextView mMsgCustoTextView;
    @BindView(R.id.text_agencia_conta) TextView mAgenciaContaTextView;

    private Long mId;
    private TransferenciaModel mTransferencia;
    private ArrayList<FavorecidoModel> mFavoritos;

    private ItemTouchHelper mItemTouchHelper;
    private OnTransferenciaListener mOnTransferenciaListener;
    private RecyclerFavorecidoAdapter mRecyclerFavorecidoAdapter;

    private static final String KEY_ID = TransferenciaFragment.class.getSimpleName() + ".mId";

    public static TransferenciaFragment newInstance(Long id) {
        TransferenciaFragment fragment = new TransferenciaFragment();
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
        mFavoritos = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transferencia, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setTitle(R.string.transferencia);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerFavorecidoAdapter = new RecyclerFavorecidoAdapter(mFavoritos);
        mRecyclerView.setAdapter(mRecyclerFavorecidoAdapter);
        mRecyclerFavorecidoAdapter.setFavorecidoAdapterListener(this);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mRecyclerFavorecidoAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        carregar();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_transferencia, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_add:
                adicionar();
                return true;
        }
        return false;
    }

    @OnClick(R.id.button_confirmar)
    public void onConfirmarClick() {
        if (isValid()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.aprovar)
                    .setMessage(R.string.msg_aprovar_processo)
                    .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            aprovar();
                        }
                    })
                    .setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void aprovar() {
        if (mId != null && mTransferencia != null) {
            TransferenciaModel transferencia = new TransferenciaModel();
            ContaBancoModel principal = new ContaBancoModel();
            principal.banco = mTransferencia.principal.banco;
            principal.agencia = mTransferencia.principal.agencia;
            principal.conta = mTransferencia.principal.conta;
            principal.nomeTitular = mTransferencia.principal.nomeTitular;
            principal.cpfCnpj = mTransferencia.principal.cpfCnpj;
            principal.custo = mTransferencia.principal.custo;
            principal.valor = mTransferencia.principal.valor;
            principal.valorTransferencia = mTransferencia.principal.valorTransferencia;
            transferencia.principal = principal;
            List<FavorecidoModel> favorecidos = mRecyclerFavorecidoAdapter.getDataSet();
            if (CollectionUtils.isNotEmpty(favorecidos)) {
                transferencia.favorecidos = favorecidos;
            }
            startAsyncAprovar(mId, transferencia);
        }
    }

    private void carregar() {
        if (mId != null) {
            startAsyncLoad(mId);
        }
    }

    private void popular() {
        if (mTransferencia != null) {
            // TRANSFERENCIA
            String custo = NumberUtils.format(mTransferencia.custo);
            FieldUtils.setText(mMsgCustoTextView, getString(R.string.msg_custo_transferencia, custo));
            // CONTA PRINCIPAL
            ContaBancoModel principal = mTransferencia.principal;
            FieldUtils.setText(mNomeTextView, principal.nomeTitular);
            FieldUtils.setText(mCpfCnpjTextView, principal.cpfCnpj);
            FieldUtils.setText(mBancoTextView, principal.banco);
            FieldUtils.setText(mAgenciaContaTextView, principal.agencia + "/" + principal.conta);
            calcular();
        }
    }

    private void calcular() {
        Double custo = 0d;
        Double valor = mTransferencia.valor;
        if (CollectionUtils.isNotEmpty(mFavoritos)) {
            for (FavorecidoModel favorecido : mFavoritos) {
                custo += mTransferencia.custo; // CUSTO AUMENTA
                valor -= favorecido.valor; // VALOR DIMINUI
                favorecido.custo = mTransferencia.custo;
                favorecido.valorTransferencia = favorecido.valor - favorecido.custo; // VALOR TRANSFERENCIA = VALOR - CUSTO
            }
        }
        ContaBancoModel principal = mTransferencia.principal;
        principal.valor = valor;
        if (valor > 0) {
            custo += mTransferencia.custo; // CUSTO AUMENTA
            principal.valorTransferencia = valor - mTransferencia.custo;
        } else {
            principal.valorTransferencia = valor;
        }
        FieldUtils.setText(mTotalTextView, mTransferencia.valor - custo);
        FieldUtils.setText(mValorTransferenciaTextView, principal.valorTransferencia);
    }

    public boolean isValid() {
        boolean valid = true;
        Double valor = mTransferencia.valor;
        if (CollectionUtils.isNotEmpty(mFavoritos)) {
            for (FavorecidoModel favorecido : mFavoritos) {
                valor -= favorecido.valor; // VALOR DIMINUI
            }
        }
        Context context = getContext();
        if (valor < 0) {
            valid = false;
            Toast.makeText(context, R.string.msg_erro_transferencia_invalido, Toast.LENGTH_SHORT).show();
        } else if (valor > 0) {
            if ((valor - mTransferencia.custo) <= 0) {
                valid = false;
                Toast.makeText(context, R.string.msg_erro_transferencia_invalido, Toast.LENGTH_SHORT).show();
            }
        }
        return valid;
    }

    private void adicionar() {
        abrir(null);
    }

    private void abrir(FavorecidoModel favorecido) {
        if (mTransferencia != null) {
            FavorecidoDialog dialog = FavorecidoDialog.newInstance(mTransferencia, favorecido);
            dialog.setFavorecidoListener(this);
            dialog.show(getFragmentManager(), dialog.getClass().getSimpleName());
        }
    }

    // Completed Service Methods
    private void startAsyncObterCompleted(TransferenciaModel model) {
        mTransferencia = model;
        popular();
    }

    // Async Service Methods
    private void startAsyncLoad(Long id) {
        showProgress();
        Observable<TransferenciaModel> observable = TransferenciaService.Async.obter(id);
        prepare(observable).subscribe(new Subscriber<TransferenciaModel>() {
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
            public void onNext(TransferenciaModel model) {
                startAsyncObterCompleted(model);
            }
        });
    }

    @Override
    public boolean onSalvar(FavorecidoModel favorecido) {
        boolean contains = false;
        if (mFavoritos.contains(favorecido)) {
            contains = true;
        } else {
            mFavoritos.add(favorecido);
        }
        calcular(); // CALCULA
        if (contains) {
            mRecyclerFavorecidoAdapter.notifyItemChanged(mFavoritos.indexOf(favorecido));
        } else {
            mRecyclerFavorecidoAdapter.notifyItemInserted(mFavoritos.indexOf(favorecido));
        }
        return true;
    }

    @Override
    public void onItemClick(FavorecidoModel model) {
        abrir(model);
    }

    @Override
    public void onItemRemoveClick(FavorecidoModel model) {
        calcular();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    private void onAsyncAprovarCompleted(DataSet<ProcessoModel, ProcessoRegraModel> dataSet) {
        if (mOnTransferenciaListener != null) {
            mOnTransferenciaListener.onAprovar(dataSet);
        }
        FragmentUtils.popBackStackImmediate(getActivity(), getBackStackName());
    }

    private void startAsyncAprovar(Long id, TransferenciaModel model) {
        showProgress();
        Observable<DataSet<ProcessoModel, ProcessoRegraModel>> observable = ProcessoService.Async.aprovar(id, model);
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
                onAsyncAprovarCompleted(dataSet);
            }
        });
    }

    public void setOnTransferenciaListener(OnTransferenciaListener onTransferenciaListener) {
        mOnTransferenciaListener = onTransferenciaListener;
    }

    public static interface OnTransferenciaListener {
        void onAprovar(DataSet<ProcessoModel, ProcessoRegraModel> dataSet);
    }
}
