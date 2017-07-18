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
import android.widget.ImageView;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.wasys.gfin.cheqfast.cliente.R;
import br.com.wasys.gfin.cheqfast.cliente.model.ImagemModel;
import br.com.wasys.library.utils.AndroidUtils;
import br.com.wasys.library.utils.ImageUtils;
import butterknife.ButterKnife;

/**
 * Created by pascke on 13/07/17.
 */

public class RecyclerImageAdapter extends RecyclerView.Adapter<RecyclerImageAdapter.ViewHolder> implements View.OnClickListener, ItemTouchHelperAdapter {

    private List<ImagemModel> mDataSet;
    private OnStartDragListener mDragStartListener;
    private ImageAdapterListener mImageAdapterListener;

    public RecyclerImageAdapter(List<ImagemModel> mDataSet) {
        this.mDataSet = mDataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_item_cheque, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.mImageView = ButterKnife.findById(view, R.id.image_cheque_atual);
        viewHolder.mView.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ImagemModel imagem = mDataSet.get(position);
        Context context = holder.mView.getContext();
        holder.mPosition = position;
        holder.mView.setTag(holder);
        Uri uri = Uri.parse(imagem.path);
        int screenWidth = AndroidUtils.getWidthPixels(context);
        try {
            Bitmap bitmap = ImageUtils.resize(uri, screenWidth);
            holder.mImageView.setImageBitmap(bitmap);
        } catch (IOException e) {

        }
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
            ImagemModel model = mDataSet.get(holder.mPosition);
            if (view == holder.mView) {
                if (mImageAdapterListener != null) {
                    mImageAdapterListener.onItemClick(model);
                }
            }
        }
    }

    @Override
    public void onItemDismiss(int position) {
        ImagemModel model = mDataSet.remove(position);
        notifyItemRemoved(position);
        if (mImageAdapterListener != null) {
            mImageAdapterListener.onItemRemoveClick(model);
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

    public void add(ImagemModel model) {
        if (mDataSet == null) {
            mDataSet = new ArrayList<>();
        }
        mDataSet.add(model);
        notifyItemInserted(mDataSet.indexOf(model));
    }

    public List<ImagemModel> getDataSet() {
        return mDataSet;
    }

    public void setImageAdapterListener(ImageAdapterListener imageAdapterListener) {
        mImageAdapterListener = imageAdapterListener;
    }

    public static interface ImageAdapterListener {
        void onItemClick(ImagemModel model);
        void onItemRemoveClick(ImagemModel model);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        private int mPosition;
        private View mView;
        private ImageView mImageView;
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
