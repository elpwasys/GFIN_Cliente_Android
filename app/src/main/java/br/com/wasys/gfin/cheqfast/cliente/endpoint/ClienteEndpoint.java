package br.com.wasys.gfin.cheqfast.cliente.endpoint;

import br.com.wasys.gfin.cheqfast.cliente.model.ProcessoModel;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by pascke on 02/08/16.
 */
public interface ClienteEndpoint {

    @GET("cliente/obter")
    Call<ProcessoModel> obter();
}