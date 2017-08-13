package br.com.wasys.gfin.cheqfast.cliente.endpoint;

import br.com.wasys.gfin.cheqfast.cliente.model.ContaBancoModel;
import br.com.wasys.gfin.cheqfast.cliente.model.TransferenciaModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by pascke on 02/08/16.
 */
public interface TransferenciaEndpoint {

    @GET("transferencia/obter/{id}")
    Call<TransferenciaModel> obter(@Path("id") Long id);
}