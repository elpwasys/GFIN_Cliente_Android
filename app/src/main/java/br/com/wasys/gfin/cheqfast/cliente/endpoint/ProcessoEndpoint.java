package br.com.wasys.gfin.cheqfast.cliente.endpoint;

import br.com.wasys.gfin.cheqfast.cliente.model.ProcessoModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by pascke on 02/08/16.
 */
public interface ProcessoEndpoint {

    @GET("processo/criar")
    Call<ProcessoModel> criar();

    @POST("processo/salvar")
    Call<ProcessoModel> salvar(@Body ProcessoModel processoModel);
}