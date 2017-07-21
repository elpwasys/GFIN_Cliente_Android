package br.com.wasys.gfin.cheqfast.cliente.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;

import br.com.wasys.gfin.cheqfast.cliente.R;
import br.com.wasys.gfin.cheqfast.cliente.dataset.DataSet;
import br.com.wasys.gfin.cheqfast.cliente.dialog.DigitalizacaoDialog;
import br.com.wasys.gfin.cheqfast.cliente.model.CampoGrupoModel;
import br.com.wasys.gfin.cheqfast.cliente.model.ChequeModel;
import br.com.wasys.gfin.cheqfast.cliente.model.DigitalizacaoModel;
import br.com.wasys.gfin.cheqfast.cliente.model.DocumentoModel;
import br.com.wasys.gfin.cheqfast.cliente.model.ProcessoModel;
import br.com.wasys.gfin.cheqfast.cliente.model.ProcessoRegraModel;
import br.com.wasys.gfin.cheqfast.cliente.service.DigitalizacaoService;
import br.com.wasys.gfin.cheqfast.cliente.service.ProcessoService;
import br.com.wasys.gfin.cheqfast.cliente.widget.AppGroupReadonlyInputLayout;
import br.com.wasys.library.utils.FieldUtils;
import br.com.wasys.library.utils.FragmentUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;

import static br.com.wasys.gfin.cheqfast.cliente.background.DigitalizacaoService.startDigitalizacaoService;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProcessoDetalheFragment extends CheqFastFragment implements View.OnClickListener {

    @BindView(R.id.view_root) View mViewRoot;
    @BindView(R.id.text_id) TextView mIdTextView;
    @BindView(R.id.text_taxa) TextView mTaxaTextView;
    @BindView(R.id.text_data) TextView mDataTextView;
    @BindView(R.id.text_status) TextView mStatusTextView;
    @BindView(R.id.text_coleta) TextView mColetaTextView;
    @BindView(R.id.image_status) ImageView mStatusImageView;
    @BindView(R.id.layout_fields) LinearLayout mLayoutFields;
    @BindView(R.id.layout_documentos) LinearLayout mLayoutDocumentos;
    @BindView(R.id.text_valor_liberado) TextView mValorLiberadoTextView;

    @BindView(R.id.button_info) ImageButton mInfoButton;
    @BindView(R.id.fab_menu) FloatingActionMenu mFloatingActionMenu;
    @BindView(R.id.button_aprovar) FloatingActionButton mAprovarFloatingActionButton;
    @BindView(R.id.button_cancelar) FloatingActionButton mCancelarFloatingActionButton;

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
        prepare();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mId != null) {
            startAsyncEditarById(mId);
        }
    }

    @Override
    public void onClick(View view) {
        Object object = view.getTag();
        if (object instanceof DocumentoModel) {
            DocumentoModel documento = (DocumentoModel) object;
            DocumentoDetalheFragment fragment = DocumentoDetalheFragment.newInstance(documento.id);
            FragmentUtils.replace(getActivity(), R.id.content_main, fragment, fragment.getBackStackName());
        }
    }

    @OnClick(R.id.button_info)
    public void onInfoClick() {
        if (mId != null) {
            startAsyncDigitalizacaoBy(mId);
        }
    }

    @OnClick(R.id.button_aprovar)
    public void onAprovarClick() {
        mFloatingActionMenu.close(true);
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

    @OnClick(R.id.button_cancelar)
    public void onCancelarClick() {
        mFloatingActionMenu.close(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.cancelar)
                .setMessage(R.string.msg_cancelar_processo)
                .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelar();
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

    private void prepare() {
        setTitle(R.string.detalhe);
        setRootViewVisibility(View.GONE);
        mFloatingActionMenu.setClosedOnTouchOutside(true);
    }

    private void aprovar() {
        if (mId != null) {
            startAsyncAprovar(mId);
        }
    }

    private void cancelar() {
        if (mId != null) {
            startAsyncCancelar(mId);
        }
    }

    private void setRootViewVisibility(int visibility) {
        mViewRoot.setVisibility(visibility);
    }

    private void populate() {

        mLayoutFields.removeAllViews();
        mLayoutDocumentos.removeAllViews();

        mFloatingActionMenu.setVisibility(View.GONE);
        mAprovarFloatingActionButton.setEnabled(false);
        mCancelarFloatingActionButton.setEnabled(false);

        if (mProcesso != null) {

            Context context = getContext();

            mStatusImageView.setImageResource(mProcesso.status.drawableRes);

            FieldUtils.setText(mIdTextView, mProcesso.id);
            FieldUtils.setText(mStatusTextView, getString(mProcesso.status.stringRes));
            FieldUtils.setText(mDataTextView, mProcesso.dataCriacao, "dd/MM/yyyy HH:mm");

            if (mProcesso.coleta != null) {
                FieldUtils.setText(mColetaTextView, getString(mProcesso.coleta.stringRes));
            }

            FieldUtils.setText(mTaxaTextView, mProcesso.taxa);
            FieldUtils.setText(mValorLiberadoTextView, mProcesso.valorLiberado);

            ArrayList<CampoGrupoModel> gruposCampos = mProcesso.gruposCampos;
            if (CollectionUtils.isNotEmpty(gruposCampos)) {
                for (CampoGrupoModel grupo : gruposCampos) {
                    AppGroupReadonlyInputLayout campoGrupoLayout = new AppGroupReadonlyInputLayout(context);
                    campoGrupoLayout.setOrientation(LinearLayout.VERTICAL);
                    campoGrupoLayout.setGrupo(grupo);
                    mLayoutFields.addView(campoGrupoLayout);
                }
            }

            ArrayList<DocumentoModel> documentos = mProcesso.documentos;
            if (CollectionUtils.isNotEmpty(documentos)) {
                LayoutInflater inflater = LayoutInflater.from(context);
                for (DocumentoModel documento : documentos) {
                    View view = inflater.inflate(R.layout.list_item_documento, null);
                    DocumentoModel.Status status = documento.status;
                    ImageView statusImageView = ButterKnife.findById(view, R.id.image_status);
                    TextView dataTextView = ButterKnife.findById(view, R.id.text_data);
                    TextView nomeTextView = ButterKnife.findById(view, R.id.text_nome);
                    TextView statusTextView = ButterKnife.findById(view, R.id.text_status);
                    TextView documentoTextView = ButterKnife.findById(view, R.id.text_documento);
                    statusImageView.setImageResource(status.drawableRes);
                    FieldUtils.setText(dataTextView, documento.dataDigitalizacao);
                    FieldUtils.setText(nomeTextView, documento.nome);
                    FieldUtils.setText(statusTextView, getString(status.stringRes));
                    ChequeModel cheque = documento.cheque;
                    if (cheque != null) {
                        FieldUtils.setText(documentoTextView, cheque.cpfCnpj);
                    }
                    if (DocumentoModel.Status.PENDENTE.equals(status)) {
                        View viewPendencia = ButterKnife.findById(view, R.id.view_pendencia);
                        TextView observacaoTextView = ButterKnife.findById(view, R.id.text_observacao);
                        TextView irregularidadeTextView = ButterKnife.findById(view, R.id.text_irregularidade);
                        FieldUtils.setText(observacaoTextView, documento.pendenciaObservacao);
                        FieldUtils.setText(irregularidadeTextView, documento.irregularidadeNome);
                        viewPendencia.setVisibility(View.VISIBLE);
                    }
                    view.setTag(documento);
                    view.setOnClickListener(this);
                    mLayoutDocumentos.addView(view);
                }
            }

            setRootViewVisibility(View.VISIBLE);

            mInfoButton.setVisibility(View.GONE);
            ProcessoModel.Status status = mProcesso.status;
            if (!ProcessoModel.Status.CANCELADO.equals(status)) {
                mInfoButton.setVisibility(View.VISIBLE);
                startAsyncCheckErrorById(mProcesso.id);
            }

            if (mRegra != null) {
                mAprovarFloatingActionButton.setEnabled(mRegra.podeAprovar);
                mCancelarFloatingActionButton.setEnabled(mRegra.podeCancelar);
                if (mRegra.podeAprovar || mRegra.podeCancelar) {
                    mFloatingActionMenu.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void digitalizar() {
        String referencia = String.valueOf(mId);
        DigitalizacaoModel.Tipo tipo = DigitalizacaoModel.Tipo.TIPIFICACAO;
        startDigitalizacaoService(getContext(), tipo, referencia);
        Toast.makeText(getContext(), R.string.msg_processo_enviado, Toast.LENGTH_LONG).show();
    }

    /**
     *
     * COMPLETED METHODS ASYNCHRONOUS HANDLERS
     *
     */
    private void onAsyncEditarCompleted(DataSet<ProcessoModel, ProcessoRegraModel> dataSet) {

        mRegra = dataSet.meta;
        mProcesso = dataSet.data;

        populate();
    }

    private void onAsyncCheckErrorCompleted(Boolean exists) {
        if (BooleanUtils.isTrue(exists)) {
            String text = getString(R.string.msg_erro_digitalizacao);
            Snackbar snackbar = makeSnackbar(text, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(R.string.abrir, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startAsyncDigitalizacaoBy(mId);
                }
            });
            snackbar.show();
        }
    }

    private void onAsyncDigitalizacaoCompleted(DigitalizacaoModel model) {
        mDigitalizacao = model;
        if (mDigitalizacao == null) {
            Toast.makeText(getContext(), R.string.msg_sem_info_digitalizacao, Toast.LENGTH_SHORT).show();
        } else {
            DigitalizacaoDialog dialog = DigitalizacaoDialog.newInstance(mDigitalizacao, new DigitalizacaoDialog.OnUplodErrorListener() {
                @Override
                public void onReenviar(boolean answer) {
                    if (answer) {
                        digitalizar();
                    }
                }
            });
            FragmentManager fragmentManager = getFragmentManager();
            dialog.show(fragmentManager, dialog.getClass().getSimpleName());
        }
    }

    /**
     *
     * ASYNCHRONOUS METHODS
     *
     */
    private void startAsyncEditarById(Long id) {
        showProgress();
        setRootViewVisibility(View.GONE);
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

    private void startAsyncCheckErrorById(Long id) {
        String referencia = String.valueOf(id);
        Observable<Boolean> observable = DigitalizacaoService.Async.existsBy(referencia, DigitalizacaoModel.Tipo.TIPIFICACAO, DigitalizacaoModel.Status.ERRO);
        prepare(observable).subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
                handle(e);
            }
            @Override
            public void onNext(Boolean exists) {
                onAsyncCheckErrorCompleted(exists);
            }
        });
    }

    private void startAsyncDigitalizacaoBy(Long id) {
        String referencia = String.valueOf(id);
        Observable<DigitalizacaoModel> observable = DigitalizacaoService.Async.getBy(referencia, DigitalizacaoModel.Tipo.TIPIFICACAO);
        prepare(observable).subscribe(new Subscriber<DigitalizacaoModel>() {
            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
                handle(e);
            }
            @Override
            public void onNext(DigitalizacaoModel model) {
                onAsyncDigitalizacaoCompleted(model);
            }
        });
    }

    private void startAsyncAprovar(Long id) {
        showProgress();
        setRootViewVisibility(View.GONE);
        Observable<DataSet<ProcessoModel, ProcessoRegraModel>> observable = ProcessoService.Async.aprovar(id);
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

    private void startAsyncCancelar(Long id) {
        showProgress();
        setRootViewVisibility(View.GONE);
        Observable<DataSet<ProcessoModel, ProcessoRegraModel>> observable = ProcessoService.Async.cancelar(id);
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
