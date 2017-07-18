package br.com.wasys.gfin.cheqfast.cliente.endpoint;

import br.com.wasys.gfin.cheqfast.cliente.model.DocumentoModel;
import br.com.wasys.gfin.cheqfast.cliente.model.JustificativaModel;
import br.com.wasys.gfin.cheqfast.cliente.model.ResultModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by pascke on 02/08/16.
 */
public interface DocumentoEndpoint {

    @GET("documento/obter/{id}")
    Call<DocumentoModel> obter(@Path("id") Long id);

    @POST("documento/justificar")
    Call<ResultModel> justificar(@Body JustificativaModel justificativaModel);
}