package br.com.wasys.gfin.cheqfast.cliente.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.wasys.gfin.cheqfast.cliente.R;
import br.com.wasys.gfin.cheqfast.cliente.model.ContaBancoModel;
import br.com.wasys.gfin.cheqfast.cliente.model.FavorecidoModel;
import br.com.wasys.library.utils.AndroidUtils;
import br.com.wasys.library.utils.FieldUtils;
import br.com.wasys.library.utils.ImageUtils;
import butterknife.ButterKnife;

/**
 * Created by pascke on 13/07/17.
 */

public class RecyclerFavorecidoAdapter extends RecyclerView.Adapter<RecyclerFavorecidoAdapter.ViewHolder> implements View.OnClickListener, ItemTouchHelperAdapter {

    private List<FavorecidoModel> mDataSet;
    private OnStartDragListener mDragStartListener;
    private FavorecidoAdapterListener mFavorecidoAdapterListener;

    public RecyclerFavorecidoAdapter(List<FavorecidoModel> mDataSet) {
        this.mDataSet = mDataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_item_favorecido, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.mNomeTextView = ButterKnife.findById(view, R.id.text_nome);
        viewHolder.mCpfCnpjTextView = ButterKnife.findById(view, R.id.text_cpf_cnpj);
        viewHolder.mBancoTextView = ButterKnife.findById(view, R.id.text_banco);
        viewHolder.mAgenciaContaTextView = ButterKnife.findById(view, R.id.text_agencia_conta);
        viewHolder.mValorTransferenciaTextView = ButterKnife.findById(view, R.id.text_valor);
        viewHolder.mView.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        FavorecidoModel model = mDataSet.get(position);
        holder.model = model;
        holder.mView.setTag(holder);
        FieldUtils.setText(holder.mNomeTextView, model.nomeTitular);
        FieldUtils.setText(holder.mCpfCnpjTextView, model.cpfCnpj);
        FieldUtils.setText(holder.mBancoTextView, model.banco);
        FieldUtils.setText(holder.mAgenciaContaTextView, model.agencia + "/" + model.conta);
        FieldUtils.setText(holder.mValorTransferenciaTextView, model.valorTransferencia);
        if (mDragStartListener != null) {
            holder.mView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        mDragStartListener.onStartDrag(holder);
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return CollectionUtils.size(mDataSet);
    }

    @Override
    public void onClick(View view) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder != null) {
            FavorecidoModel model = holder.model;
            if (view == holder.mView) {
                if (mFavorecidoAdapterListener != null) {
                    mFavorecidoAdapterListener.onItemClick(model);
                }
            }
        }
    }

    @Override
    public void onItemDismiss(int position) {
        FavorecidoModel model = mDataSet.remove(position);
        notifyItemRemoved(position);
        if (mFavorecidoAdapterListener != null) {
            mFavorecidoAdapterListener.onItemRemoveClick(model);
        }
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mDataSet, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public void clear() {
        if (mDataSet != null) {
            mDataSet.clear();
            notifyDataSetChanged();
        }
    }

    public boolean contains(FavorecidoModel model) {
        if (mDataSet != null) {
            return mDataSet.contains(model);
        }
        return false;
    }

    public void add(FavorecidoModel model) {
        if (mDataSet == null) {
            mDataSet = new ArrayList<>();
        }
        mDataSet.add(model);
        notifyItemInserted(mDataSet.indexOf(model));
    }

    public List<FavorecidoModel> getDataSet() {
        return mDataSet;
    }

    public void setFavorecidoAdapterListener(FavorecidoAdapterListener favorecidoAdapterListener) {
        mFavorecidoAdapterListener = favorecidoAdapterListener;
    }

    public static interface FavorecidoAdapterListener {
        void onItemClick(FavorecidoModel model);
        void onItemRemoveClick(FavorecidoModel model);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        private FavorecidoModel model;

        private View mView;
        private TextView mNomeTextView;
        private TextView mBancoTextView;
        private TextView mCpfCnpjTextView;
        private TextView mAgenciaContaTextView;
        private TextView mValorTransferenciaTextView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
        }
        @Override
        public void onItemClear() {
            Resources resources = mView.getResources();
            Drawable drawable = ResourcesCompat.getDrawable(resources, R.drawable.bg_recycler_item, null);
            mView.setBackground(drawable);
        }
        @Override
        public void onItemSelected() {
            Resources resources = mView.getResources();
            Drawable drawable = ResourcesCompat.getDrawable(resources, R.drawable.bg_recycler_item_selected, null);
            mView.setBackground(drawable);
        }
    }
}
