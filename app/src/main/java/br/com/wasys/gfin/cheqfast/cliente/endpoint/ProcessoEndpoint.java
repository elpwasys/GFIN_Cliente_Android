package br.com.wasys.gfin.cheqfast.cliente.endpoint;

import br.com.wasys.gfin.cheqfast.cliente.dataset.DataSet;
import br.com.wasys.gfin.cheqfast.cliente.model.ProcessoModel;
import br.com.wasys.gfin.cheqfast.cliente.model.ProcessoRegraModel;
import br.com.wasys.gfin.cheqfast.cliente.model.TransferenciaModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by pascke on 02/08/16.
 */
public interface ProcessoEndpoint {

    @GET("processo/criar")
    Call<ProcessoModel> criar();

    @POST("processo/salvar")
    Call<ProcessoModel> salvar(@Body ProcessoModel processoModel);

    @GET("processo/editar/{id}")
    Call<DataSet<ProcessoModel, ProcessoRegraModel>> editar(@Path("id") Long id);

    @POST("processo/aprovar/{id}")
    Call<DataSet<ProcessoModel, ProcessoRegraModel>> aprovar(@Path("id") Long id, @Body TransferenciaModel transferenciaModel);

    @GET("processo/cancelar/{id}")
    Call<DataSet<ProcessoModel, ProcessoRegraModel>> cancelar(@Path("id") Long id);
}