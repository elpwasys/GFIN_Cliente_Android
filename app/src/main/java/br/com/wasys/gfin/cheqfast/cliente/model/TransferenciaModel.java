package br.com.wasys.gfin.cheqfast.cliente.model;

import java.util.List;

/**
 * Created by pascke on 11/08/17.
 */

public class TransferenciaModel {

    public Double valor;
    public Double custo;

    public ProcessoModel processo;
    public ContaBancoModel principal;

    public List<String> bancos;
    public List<FavorecidoModel> favorecidos;
}
