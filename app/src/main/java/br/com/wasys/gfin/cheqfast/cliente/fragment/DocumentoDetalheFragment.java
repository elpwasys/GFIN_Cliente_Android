package br.com.wasys.gfin.cheqfast.cliente.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.todobom.opennotescanner.OpenNoteScannerActivity;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import br.com.wasys.gfin.cheqfast.cliente.Permission;
import br.com.wasys.gfin.cheqfast.cliente.R;
import br.com.wasys.gfin.cheqfast.cliente.dialog.DigitalizacaoDialog;
import br.com.wasys.gfin.cheqfast.cliente.dialog.PendenciaDialog;
import br.com.wasys.gfin.cheqfast.cliente.model.ChequeModel;
import br.com.wasys.gfin.cheqfast.cliente.model.DigitalizacaoModel;
import br.com.wasys.gfin.cheqfast.cliente.model.DocumentoModel;
import br.com.wasys.gfin.cheqfast.cliente.model.ImagemModel;
import br.com.wasys.gfin.cheqfast.cliente.model.JustificativaModel;
import br.com.wasys.gfin.cheqfast.cliente.model.ResultModel;
import br.com.wasys.gfin.cheqfast.cliente.model.UploadModel;
import br.com.wasys.gfin.cheqfast.cliente.service.DigitalizacaoService;
import br.com.wasys.gfin.cheqfast.cliente.service.DocumentoService;
import br.com.wasys.gfin.cheqfast.cliente.service.ImagemService;
import br.com.wasys.library.utils.AndroidUtils;
import br.com.wasys.library.utils.FieldUtils;
import br.com.wasys.library.utils.FragmentUtils;
import br.com.wasys.library.utils.ImageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;

import static br.com.wasys.gfin.cheqfast.cliente.background.DigitalizacaoService.startDigitalizacaoService;

/**
 * A simple {@link Fragment} subclass.
 */
public class DocumentoDetalheFragment extends CheqFastFragment {

    @BindView(R.id.progress) ProgressBar mProgressBar;
    @BindView(R.id.text_data) TextView mDataTextView;
    @BindView(R.id.text_nome) TextView mNomeTextView;
    @BindView(R.id.text_status) TextView mStatusTextView;
    @BindView(R.id.image_status) ImageView mStatusImageView;
    @BindView(R.id.text_documento) TextView mDocumentoTextView;

    @BindView(R.id.view_cheque_atual) View mChequeAtualView;
    @BindView(R.id.view_cheque_outro) View mChequeOutroView;
    @BindView(R.id.image_cheque_atual) ImageView mChequeAtualImageView;
    @BindView(R.id.image_cheque_outro) ImageView mChequeOutroImageView;

    @BindView(R.id.view_pendencia) View mViewPendencia;
    @BindView(R.id.text_observacao) TextView mObservacaoTextView;
    @BindView(R.id.text_irregularidade) TextView mIrregularidadeTextView;

    @BindView(R.id.button_info) ImageButton mInfoButton;
    @BindView(R.id.fab_menu) FloatingActionMenu mFloatingActionMenu;
    @BindView(R.id.fab_substituir) FloatingActionButton mSubstituirFloatingActionButton;
    @BindView(R.id.fab_scanner) com.github.clans.fab.FloatingActionButton mScannerFloatingActionButton;
    @BindView(R.id.fab_justificar) com.github.clans.fab.FloatingActionButton mJustificarFloatingActionButton;

    private Long mId;
    private Uri mTmpUri;
    private Uri mViewerUri;
    private ImagemModel mImagem;
    private DocumentoModel mDocumento;
    private DigitalizacaoModel mDigitalizacao;

    private static final int REQUEST_VIEW = 1;
    private static final int REQUEST_SCAN = 2;

    private static final String KEY_ID = DocumentoDetalheFragment.class.getSimpleName() + ".mId";

    public static DocumentoDetalheFragment newInstance(Long id) {
        DocumentoDetalheFragment fragment = new DocumentoDetalheFragment();
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
        return inflater.inflate(R.layout.fragment_documento_detalhe, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        prepare();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_VIEW: {
                destroyViewUri();
                break;
            }case REQUEST_SCAN: {
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
                    try {
                        ImageUtils.rotate(uri, -90);
                    } catch (IOException e) {

                    }
                    createReplaceUri(uri);
                    break;
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean granted = true;
        if (ArrayUtils.isNotEmpty(grantResults)) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    granted = false;
                    break;
                }
            }
        }
        if (granted) {
            switch (requestCode) {
                case REQUEST_SCAN: {
                    openScanner();
                }
            }
        }
    }

    @OnClick(R.id.fab_scanner)
    public void onScannerClick() {
        mFloatingActionMenu.close(true);
        requestScanner();
    }

    @OnClick(R.id.fab_substituir)
    public void onSubstituirClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.substituir)
                .setMessage(R.string.msg_substituir_cheque)
                .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        substituir();
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

    @OnClick(R.id.fab_justificar)
    public void onJustificarClick() {
        mFloatingActionMenu.close(true);
        PendenciaDialog dialog = PendenciaDialog.newInstance(mDocumento, new PendenciaDialog.OnPendenciaDialogListener() {
            @Override
            public void onJustificar(String justificativa) {
                JustificativaModel justificativaModel = new JustificativaModel();
                justificativaModel.id = mDocumento.id;
                justificativaModel.texto = justificativa;
                startAsyncJustificar(justificativaModel);
            }
        });
        FragmentManager manager = getFragmentManager();
        dialog.show(manager, dialog.getClass().getSimpleName());
    }

    @OnClick(R.id.button_info)
    public void onInfoClick() {
        if (mId != null) {
            startAsyncDigitalizacao(mId);
        }
    }

    @OnClick({R.id.image_cheque_atual, R.id.image_cheque_outro})
    public void onChequeAtualClick(View view) {
        String path = null;
        if (view == mChequeAtualImageView) {
            path = mImagem.path;
        } else if (view == mChequeOutroImageView) {
            path = mTmpUri.getPath();
        }
        if (StringUtils.isNotBlank(path)) {
            try {
                mViewerUri = ImagemService.createViewUri(path, true);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(mViewerUri, "image/*");
                startActivityForResult(intent, REQUEST_VIEW);
            } catch (Throwable e) {
                handle(e);
            }
        }
    }

    private void substituir() {
        if (mTmpUri != null) {
            UploadModel upload = new UploadModel(mTmpUri.getPath());
            startAsyncSalvar(upload);
        }
    }

    private void createReplaceUri(Uri uri) {
        mTmpUri = uri;
        Context context = getContext();
        int screenWidth = AndroidUtils.getWidthPixels(context);
        try {
            Bitmap bitmap = ImageUtils.resize(mTmpUri, screenWidth);
            mChequeOutroImageView.setImageBitmap(bitmap);
            mChequeOutroImageView.setVisibility(View.VISIBLE);
            mChequeOutroView.setVisibility(View.VISIBLE);
            mSubstituirFloatingActionButton.setVisibility(View.VISIBLE);
        } catch (IOException e) {

        }
    }

    private void requestScanner() {
        if (Permission.isScanGranted()) {
            openScanner();
        } else {
            FragmentActivity activity = this.getActivity();
            ActivityCompat.requestPermissions(activity, Permission.SCAN, REQUEST_SCAN);
        }
    }

    private void openScanner() {
        Context context = getBaseContext();
        try {
            mTmpUri = ImagemService.createTmpUri(true);
            Intent intent = new Intent(context, OpenNoteScannerActivity.class);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mTmpUri);
            startActivityForResult(intent, REQUEST_SCAN);
        } catch (Exception e) {
            handle(e);
        }
    }

    private void destroyViewUri() {
        if (mViewerUri != null) {
            String path = mViewerUri.getPath();
            destroyPath(path);
        }
    }

    private void destroyPath(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    private void prepare() {
        setTitle(R.string.cheque);
        if (mId != null) {
            startAsyncObter(mId);
        }
        mFloatingActionMenu.setClosedOnTouchOutside(true);
    }

    private void populate() {

        mViewPendencia.setVisibility(View.GONE);
        mFloatingActionMenu.setVisibility(View.GONE);
        mScannerFloatingActionButton.setEnabled(false);
        mJustificarFloatingActionButton.setEnabled(false);

        if (mDocumento != null) {
            mInfoButton.setVisibility(View.VISIBLE);
            DocumentoModel.Status status = mDocumento.status;
            mStatusImageView.setImageResource(status.drawableRes);
            FieldUtils.setText(mDataTextView, mDocumento.dataDigitalizacao);
            FieldUtils.setText(mNomeTextView, mDocumento.nome);
            FieldUtils.setText(mStatusTextView, getString(status.stringRes));
            ChequeModel cheque = mDocumento.cheque;
            if (cheque != null) {
                FieldUtils.setText(mDocumentoTextView, cheque.cpfCnpj);
            }
            List<ImagemModel> imagens = mDocumento.imagens;
            if (CollectionUtils.isNotEmpty(imagens)) {
                int last = imagens.size() - 1;
                ImagemModel imagemModel = imagens.get(last);
                startAsyncLoad(imagemModel.caminho);
            }
            if (mDocumento.justificavel || mDocumento.digitalizavel) {
                mFloatingActionMenu.setVisibility(View.VISIBLE);
                mScannerFloatingActionButton.setEnabled(mDocumento.digitalizavel);
                mJustificarFloatingActionButton.setEnabled(mDocumento.justificavel);
            }

            if (DocumentoModel.Status.PENDENTE.equals(status)) {
                FieldUtils.setText(mObservacaoTextView, mDocumento.pendenciaObservacao);
                FieldUtils.setText(mIrregularidadeTextView, mDocumento.irregularidadeNome);
                mViewPendencia.setVisibility(View.VISIBLE);
            }
        }
    }

    private void reenviar() {
        String referencia = String.valueOf(mId);
        DigitalizacaoModel.Tipo tipo = DigitalizacaoModel.Tipo.DOCUMENTO;
        startDigitalizacaoService(getContext(), tipo, referencia);
        Toast.makeText(getContext(), R.string.msg_documento_reenviado, Toast.LENGTH_LONG).show();
    }

    private void onAsyncSalvarCompleted(DigitalizacaoModel model) {
        if (model != null) {
            Context context = getContext();
            startDigitalizacaoService(context, model.tipo, model.referencia);
            Toast.makeText(getContext(), R.string.msg_documento_enviado_sucesso, Toast.LENGTH_LONG).show();
            FragmentUtils.popBackStackImmediate(getActivity(), getBackStackName());
        }
    }

    private void onAsyncJustificarCompleted(ResultModel model) {
        Toast.makeText(getContext(), R.string.msg_justificativa_sucesso, Toast.LENGTH_LONG).show();
        FragmentUtils.popBackStackImmediate(getActivity(), getBackStackName());
    }

    private void onAsyncLoadCompleted(ImagemModel imagemModel) {
        mImagem = imagemModel;
        if (mImagem != null) {
            Uri uri = Uri.parse(mImagem.path);
            Context context = getContext();
            int width = AndroidUtils.getWidthPixels(context);
            try {
                Bitmap bitmap = ImageUtils.resize(uri, width);
                mChequeAtualImageView.setImageBitmap(bitmap);
                mChequeAtualView.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onAsyncObterCompleted(DocumentoModel documentoModel) {
        mDocumento = documentoModel;
        populate();
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
                        reenviar();
                    }
                }
            });
            FragmentManager fragmentManager = getFragmentManager();
            dialog.show(fragmentManager, dialog.getClass().getSimpleName());
        }
    }

    private void startAsyncSalvar(UploadModel upload) {
        if (mId != null) {
            String referencia = String.valueOf(mId);
            List<UploadModel> uploads = Arrays.asList(upload);
            Observable<DigitalizacaoModel> observable = DigitalizacaoService.Async.criar(referencia, DigitalizacaoModel.Tipo.DOCUMENTO, uploads);
            prepare(observable).subscribe(new Subscriber<DigitalizacaoModel>() {
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
                public void onNext(DigitalizacaoModel model) {
                    hideProgress();
                    onAsyncSalvarCompleted(model);
                }
            });
        }
    }

    private void startAsyncObter(Long id) {
        showProgress();
        Observable<DocumentoModel> observable = DocumentoService.Async.obter(id);
        prepare(observable).subscribe(new Subscriber<DocumentoModel>() {
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
            public void onNext(DocumentoModel documentoModel) {
                hideProgress();
                onAsyncObterCompleted(documentoModel);
            }
        });
    }

    private void startAsyncLoad(String caminho) {
        if (StringUtils.isNotBlank(caminho)) {
            mChequeAtualView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            Observable<ImagemModel> observable = ImagemService.Async.carregar(caminho);
            prepare(observable).subscribe(new Subscriber<ImagemModel>() {
                @Override
                public void onCompleted() {
                    mProgressBar.setVisibility(View.GONE);
                }
                @Override
                public void onError(Throwable e) {
                    mProgressBar.setVisibility(View.GONE);
                    handle(e);
                }
                @Override
                public void onNext(ImagemModel model) {
                    mProgressBar.setVisibility(View.GONE);
                    onAsyncLoadCompleted(model);
                }
            });
        }
    }

    private void startAsyncDigitalizacao(Long id) {
        String referencia = String.valueOf(id);
        Observable<DigitalizacaoModel> observable = DigitalizacaoService.Async.getBy(referencia, DigitalizacaoModel.Tipo.DOCUMENTO);
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

    private void startAsyncJustificar(JustificativaModel justificativaModel) {
        showProgress();
        Observable<ResultModel> observable = DocumentoService.Async.justificar(justificativaModel);
        prepare(observable).subscribe(new Subscriber<ResultModel>() {
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
            public void onNext(ResultModel model) {
                hideProgress();
                onAsyncJustificarCompleted(model);
            }
        });
    }
}