package br.com.wasys.gfin.cheqfast.cliente.endpoint;

import br.com.wasys.gfin.cheqfast.cliente.model.CredencialModel;
import br.com.wasys.gfin.cheqfast.cliente.model.DispositivoModel;
import br.com.wasys.gfin.cheqfast.cliente.model.RecoveryModel;
import br.com.wasys.gfin.cheqfast.cliente.model.ResultModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by pascke on 02/08/16.
 */
public interface DispositivoEndpoint {

    @POST("dispositivo/recuperar")
    Call<ResultModel> recuperar(@Body RecoveryModel recoveryModel);

    @POST("dispositivo/autenticar")
    Call<DispositivoModel> autenticar(@Body CredencialModel credencialModel);
}
