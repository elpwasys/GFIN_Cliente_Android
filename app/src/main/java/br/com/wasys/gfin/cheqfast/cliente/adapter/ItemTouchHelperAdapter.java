package br.com.wasys.gfin.cheqfast.cliente.adapter;

/**
 * Created by pascke on 14/07/17.
 */

public interface ItemTouchHelperAdapter {
    void onItemDismiss(int position);
    boolean onItemMove(int fromPosition, int toPosition);
}
