package br.com.wasys.gfin.cheqfast.cliente.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.todobom.opennotescanner.OpenNoteScannerActivity;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.wasys.gfin.cheqfast.cliente.Permission;
import br.com.wasys.gfin.cheqfast.cliente.R;
import br.com.wasys.gfin.cheqfast.cliente.adapter.OnStartDragListener;
import br.com.wasys.gfin.cheqfast.cliente.adapter.RecyclerImageAdapter;
import br.com.wasys.gfin.cheqfast.cliente.adapter.SimpleItemTouchHelperCallback;
import br.com.wasys.gfin.cheqfast.cliente.dialog.FormularioDialog;
import br.com.wasys.gfin.cheqfast.cliente.model.CampoGrupoModel;
import br.com.wasys.gfin.cheqfast.cliente.model.ImagemModel;
import br.com.wasys.gfin.cheqfast.cliente.model.ProcessoModel;
import br.com.wasys.gfin.cheqfast.cliente.model.UploadModel;
import br.com.wasys.gfin.cheqfast.cliente.service.ImagemService;
import br.com.wasys.gfin.cheqfast.cliente.service.ProcessoService;
import br.com.wasys.library.utils.AndroidUtils;
import br.com.wasys.library.utils.ImageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProcessoNovoFragment extends CheqFastFragment implements RecyclerImageAdapter.ImageAdapterListener, OnStartDragListener, FormularioDialog.FormularioListener {

    @BindView(R.id.text_info) TextView mInfoTextView;
    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;

    @BindView(R.id.button_menu) FloatingActionMenu mMenuButton;
    @BindView(R.id.button_capturar) FloatingActionButton mCapturarButton;
    @BindView(R.id.button_finalizar) FloatingActionButton mFinalizarButton;

    private ProcessoModel mProcesso;

    private Uri mTmpUri;
    private Uri mViewerUri;

    private ItemTouchHelper mItemTouchHelper;
    private LinearLayoutManager mLayoutManager;
    private RecyclerImageAdapter mRecyclerImageAdapter;

    private static final int REQUEST_VIEW = 1;
    private static final int REQUEST_SCAN = 2;

    public static ProcessoNovoFragment newInstance() {
        ProcessoNovoFragment fragment = new ProcessoNovoFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_processo_novo, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        prepare();
        startAsyncObter();
    }

    @Override
    public void onDestroyView() {
        destroyViewUri();
        super.onDestroyView();
    }

    @OnClick(R.id.button_capturar)
    public void onCapturarClick() {
        mMenuButton.close(true);
        requestScanner();
    }

    @OnClick(R.id.button_finalizar)
    public void onFinalizarClick() {
        mMenuButton.close(true);
        List<ImagemModel> dataSet = mRecyclerImageAdapter.getDataSet();
        if (CollectionUtils.isEmpty(dataSet)) {
            Toast.makeText(getContext(), R.string.msg_finalizar_sem_cheque, Toast.LENGTH_SHORT).show();
        } else {
            FormularioDialog dialog = FormularioDialog.newInstance(mProcesso.gruposCampos);
            dialog.setFormularioListener(this);
            dialog.show(getFragmentManager(), dialog.getClass().getSimpleName());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_VIEW: {
                destroyViewUri();
                break;
            }
            case REQUEST_SCAN: {
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
                    try {
                        ImageUtils.rotate(uri, -90);
                    } catch (IOException e) {

                    }
                    ImagemModel model = ImagemModel.from(uri);
                    mTmpUri = null;
                    mRecyclerImageAdapter.add(model);
                    setVisibilities();
                    break;
                } else if (mTmpUri != null) {
                    destroyPath(mTmpUri.getPath());
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
                    startScanner();
                }
            }
        }
    }

    @Override
    public void onItemClick(ImagemModel model) {
        try {
            mViewerUri = ImagemService.createViewUri(model.path, true);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(mViewerUri, "image/*");
            startActivityForResult(intent, REQUEST_VIEW);
        } catch (Exception e) {
            handle(e);
        }
    }

    @Override
    public void onItemRemoveClick(ImagemModel model) {
        destroyPath(model.path);
        setVisibilities();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onConfirmar(ArrayList<CampoGrupoModel> gruposCampos) {
        List<ImagemModel> dataSet = mRecyclerImageAdapter.getDataSet();
        if (CollectionUtils.isNotEmpty(dataSet)) {
            mProcesso.uploads = new ArrayList<>();
            mProcesso.gruposCampos = gruposCampos;
            for (ImagemModel imagemModel : dataSet) {
                mProcesso.uploads.add(new UploadModel(imagemModel.path));
            }
            startAsyncSalvar();
        }
    }

    private void prepare() {
        setTitle(R.string.cheques);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerImageAdapter = new RecyclerImageAdapter(new ArrayList<ImagemModel>());
        mRecyclerView.setAdapter(mRecyclerImageAdapter);
        mRecyclerImageAdapter.setImageAdapterListener(this);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mRecyclerImageAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

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

    private void setVisibilities() {
        int itemCount = mRecyclerImageAdapter.getItemCount();
        if (itemCount > 0) {
            mInfoTextView.setVisibility(View.GONE);
        } else {
            mInfoTextView.setVisibility(View.VISIBLE);
        }
    }

    private void requestScanner() {
        if (Permission.isScanGranted()) {
            startScanner();
        } else {
            FragmentActivity activity = this.getActivity();
            ActivityCompat.requestPermissions(activity, Permission.SCAN, REQUEST_SCAN);
        }
    }

    private void startScanner() {
        if (AndroidUtils.isExternalStorageWritable()) {
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
    }

    // ASYNC COMPLETED METHODS
    private void onAsyncObterCompleted(ProcessoModel model) {
        mProcesso = model;
        mMenuButton.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mMenuButton.open(true);
            }
        }, 200);
    }

    private void onAsyncSalvarCompleted(ProcessoModel model) {
        Toast.makeText(getContext(), R.string.msg_processo_salvo_sucesso, Toast.LENGTH_LONG).show();
        mRecyclerImageAdapter.clear();
        setVisibilities();
        mMenuButton.setVisibility(View.INVISIBLE);
        startAsyncObter();
    }

    // ASYNC METHODS
    private void startAsyncObter() {
        showProgress();
        Observable<ProcessoModel> observable = ProcessoService.Async.obter();
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
                hideProgress();
                onAsyncObterCompleted(model);
            }
        });
    }

    private void startAsyncSalvar() {
        showProgress();
        Observable<ProcessoModel> observable = ProcessoService.Async.salvar(mProcesso);
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
                hideProgress();
                onAsyncSalvarCompleted(model);
            }
        });
    }
}
