package br.com.wasys.gfin.cheqfast.cliente.service;

import br.com.wasys.gfin.cheqfast.cliente.endpoint.ClienteEndpoint;
import br.com.wasys.gfin.cheqfast.cliente.endpoint.Endpoint;
import br.com.wasys.gfin.cheqfast.cliente.model.ProcessoModel;
import br.com.wasys.library.service.Service;
import retrofit2.Call;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by pascke on 03/09/16.
 */
public class ClienteService extends Service {

    public static ProcessoModel obter() throws Throwable {
        ClienteEndpoint endpoint = Endpoint.create(ClienteEndpoint.class);
        Call<ProcessoModel> call = endpoint.obter();
        ProcessoModel model = Endpoint.execute(call);
        return model;
    }

    public static class Async {

        public static Observable<ProcessoModel> obter() {
            return Observable.create(new Observable.OnSubscribe<ProcessoModel>() {
                @Override
                public void call(Subscriber<? super ProcessoModel> subscriber) {
                    try {
                        ProcessoModel model = ClienteService.obter();
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