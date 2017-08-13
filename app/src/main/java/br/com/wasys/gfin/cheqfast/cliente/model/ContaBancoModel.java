package br.com.wasys.gfin.cheqfast.cliente.model;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import br.com.wasys.gfin.cheqfast.cliente.Application;
import br.com.wasys.gfin.cheqfast.cliente.R;
import br.com.wasys.library.model.Model;

/**
 * Created by pascke on 11/08/17.
 */
public class ContaBancoModel extends Model {

    public Status status;
    public String banco;
    public String agencia;
    public String conta;
    public String cpfCnpj;
    public String nomeTitular;

    public Double valor;
    public Double custo;
    public Double valorTransferencia;

    public enum Status {
        ATIVO (R.string.ativo),
        INATIVO (R.string.inativo),
        HISTORICO (R.string.historico);
        public int stringRes;
        Status(@StringRes int stringRes) {
            this.stringRes = stringRes;
        }
    }
}
