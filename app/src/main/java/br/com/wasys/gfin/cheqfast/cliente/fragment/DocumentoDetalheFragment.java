package br.com.wasys.gfin.cheqfast.cliente.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import br.com.wasys.gfin.cheqfast.cliente.R;
import br.com.wasys.gfin.cheqfast.cliente.model.DigitalizacaoModel;
import br.com.wasys.gfin.cheqfast.cliente.model.DocumentoModel;
import br.com.wasys.gfin.cheqfast.cliente.model.ImagemModel;
import br.com.wasys.gfin.cheqfast.cliente.service.DocumentoService;
import br.com.wasys.gfin.cheqfast.cliente.service.ImagemService;
import br.com.wasys.library.utils.AndroidUtils;
import br.com.wasys.library.utils.FieldUtils;
import br.com.wasys.library.utils.ImageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;

/**
 * A simple {@link Fragment} subclass.
 */
public class DocumentoDetalheFragment extends CheqFastFragment {

    @BindView(R.id.view_cheque) View mChequeView;
    @BindView(R.id.progress) ProgressBar mProgressBar;
    @BindView(R.id.text_data) TextView mDataTextView;
    @BindView(R.id.text_nome) TextView mNomeTextView;
    @BindView(R.id.text_status) TextView mStatusTextView;
    @BindView(R.id.image_status) ImageView mStatusImageView;
    @BindView(R.id.image_cheque) ImageView mChequeImageView;
    @BindView(R.id.text_documento) TextView mDocumentoTextView;

    private Long mId;
    private Uri mViewerUri;
    private ImagemModel mImagem;
    private DocumentoModel mDocumento;
    private DigitalizacaoModel mDigitalizacao;

    private static final int REQUEST_VIEW = 1;
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
            }
        }
    }

    @OnClick(R.id.image_cheque)
    public void onImageClick() {
        try {
            mViewerUri = ImagemService.createViewUri(mImagem.path, true);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(mViewerUri, "image/*");
            startActivityForResult(intent, REQUEST_VIEW);
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
    }

    private void update() {
        if (mDocumento != null) {
            mStatusImageView.setImageResource(mDocumento.status.drawableRes);
            FieldUtils.setText(mDataTextView, mDocumento.dataDigitalizacao);
            FieldUtils.setText(mNomeTextView, mDocumento.nome);
            FieldUtils.setText(mStatusTextView, getString(mDocumento.status.stringRes));
            FieldUtils.setText(mDocumentoTextView, "000.000.000-00"); // PEDIR PARA O FELIPE
            List<ImagemModel> imagens = mDocumento.imagens;
            if (CollectionUtils.isNotEmpty(imagens)) {
                int last = imagens.size() - 1;
                ImagemModel imagemModel = imagens.get(last);
                startAsyncLoad(imagemModel.caminho);
            }
        }
    }

    private void onAsyncLoadCompleted(ImagemModel imagemModel) {
        mImagem = imagemModel;
        if (mImagem != null) {
            Uri uri = Uri.parse(mImagem.path);
            Context context = getContext();
            int width = AndroidUtils.getWidthPixels(context);
            try {
                Bitmap bitmap = ImageUtils.resize(uri, width);
                mChequeImageView.setImageBitmap(bitmap);
                mChequeView.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onAsyncObterCompleted(DocumentoModel documentoModel) {
        mDocumento = documentoModel;
        update();
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
            mChequeView.setVisibility(View.GONE);
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
}
