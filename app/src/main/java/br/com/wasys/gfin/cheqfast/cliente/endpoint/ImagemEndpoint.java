package br.com.wasys.gfin.cheqfast.cliente.endpoint;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by pascke on 02/08/16.
 */
public interface ImagemEndpoint {

    @GET("{nome}")
    Call<ResponseBody> carregar(@Path("nome") String nome);
}