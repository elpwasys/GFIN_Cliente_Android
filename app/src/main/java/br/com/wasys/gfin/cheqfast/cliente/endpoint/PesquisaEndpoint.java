package br.com.wasys.gfin.cheqfast.cliente.endpoint;

import br.com.wasys.gfin.cheqfast.cliente.model.PesquisaModel;
import br.com.wasys.gfin.cheqfast.cliente.paging.ProcessoPagingModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by pascke on 02/08/16.
 */
public interface PesquisaEndpoint {

    @POST("pesquisa/filtrar")
    Call<ProcessoPagingModel> filtrar(@Body PesquisaModel pesquisaModel);
}
