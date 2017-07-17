package br.com.wasys.gfin.cheqfast.cliente.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.wasys.gfin.cheqfast.cliente.R;
import br.com.wasys.gfin.cheqfast.cliente.model.ProcessoModel;
import br.com.wasys.library.adapter.ListAdapter;
import br.com.wasys.library.utils.FieldUtils;

/**
 * Created by pascke on 28/04/16.
 */
public class ProcessoAdapter extends ListAdapter<ProcessoModel> {

    public ProcessoAdapter(Context context, List<ProcessoModel> rows) {
        super(context, rows);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        }
        else {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.list_item_processo, null);
            holder.idTextView = (TextView) view.findViewById(R.id.text_view_id);
            holder.dataTextView = (TextView) view.findViewById(R.id.text_view_data);
            holder.statusTextView = (TextView) view.findViewById(R.id.text_view_status);
            holder.statusImageView = (ImageView) view.findViewById(R.id.image_view_status);
            view.setTag(holder);
        }
        ProcessoModel processo = mRows.get(position);
        holder.statusTextView.setText(processo.status.stringRes);
        holder.statusImageView.setImageResource(processo.status.drawableRes);
        FieldUtils.setText(holder.idTextView, processo.id);
        FieldUtils.setText(holder.dataTextView, processo.dataCriacao);
        return view;
    }

    static class ViewHolder {
        ImageView statusImageView;
        TextView idTextView;
        TextView dataTextView;
        TextView statusTextView;
    }
}