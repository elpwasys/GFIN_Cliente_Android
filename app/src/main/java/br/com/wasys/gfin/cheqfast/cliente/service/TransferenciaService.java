package br.com.wasys.gfin.cheqfast.cliente.service;

import android.util.Log;

import br.com.wasys.gfin.cheqfast.cliente.endpoint.Endpoint;
import br.com.wasys.gfin.cheqfast.cliente.endpoint.TransferenciaEndpoint;
import br.com.wasys.gfin.cheqfast.cliente.model.TransferenciaModel;
import br.com.wasys.library.service.Service;
import retrofit2.Call;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by pascke on 03/09/16.
 */
public class TransferenciaService extends Service {

    public static TransferenciaModel obter(Long id) throws Throwable {
        TransferenciaEndpoint endpoint = Endpoint.create(TransferenciaEndpoint.class);
        Call<TransferenciaModel> call = endpoint.obter(id);
        TransferenciaModel model = Endpoint.execute(call);
        return model;
    }

    public static class Async {

        public static Observable<TransferenciaModel> obter(final Long id) {
            return Observable.create(new Observable.OnSubscribe<TransferenciaModel>() {
                @Override
                public void call(Subscriber<? super TransferenciaModel> subscriber) {
                    try {
                        TransferenciaModel model = TransferenciaService.obter(id);
                        subscriber.onNext(model);
                        subscriber.onCompleted();
                    } catch (Throwable e) {
                        subscriber.onError(e);
                    }
                }
            });
        }
    }
}