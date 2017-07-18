package br.com.wasys.gfin.cheqfast.cliente.service;

import br.com.wasys.gfin.cheqfast.cliente.endpoint.DocumentoEndpoint;
import br.com.wasys.gfin.cheqfast.cliente.endpoint.Endpoint;
import br.com.wasys.gfin.cheqfast.cliente.model.DocumentoModel;
import br.com.wasys.gfin.cheqfast.cliente.model.JustificativaModel;
import br.com.wasys.gfin.cheqfast.cliente.model.ResultModel;
import br.com.wasys.library.service.Service;
import retrofit2.Call;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by pascke on 03/09/16.
 */
public class DocumentoService extends Service {

    public static DocumentoModel obter(Long id) throws Throwable {
        DocumentoEndpoint endpoint = Endpoint.create(DocumentoEndpoint.class);
        Call<DocumentoModel> call = endpoint.obter(id);
        DocumentoModel model = Endpoint.execute(call);
        return model;
    }

    public static ResultModel justificar(JustificativaModel justificativaModel) throws Throwable {
        DocumentoEndpoint endpoint = Endpoint.create(DocumentoEndpoint.class);
        Call<ResultModel> call = endpoint.justificar(justificativaModel);
        ResultModel model = Endpoint.execute(call);
        return model;
    }


    public static class Async {

        public static Observable<DocumentoModel> obter(final Long id) {
            return Observable.create(new Observable.OnSubscribe<DocumentoModel>() {
                @Override
                public void call(Subscriber<? super DocumentoModel> subscriber) {
                    try {
                        DocumentoModel model = DocumentoService.obter(id);
                        subscriber.onNext(model);
                        subscriber.onCompleted();
                    } catch (Throwable e) {
                        subscriber.onError(e);
                    }
                }
            });
        }

        public static Observable<ResultModel> justificar(final JustificativaModel justificativaModel) {
            return Observable.create(new Observable.OnSubscribe<ResultModel>() {
                @Override
                public void call(Subscriber<? super ResultModel> subscriber) {
                    try {
                        ResultModel resultModel = DocumentoService.justificar(justificativaModel);
                        subscriber.onNext(resultModel);
                        subscriber.onCompleted();
                    } catch (Throwable e) {
                        subscriber.onError(e);
                    }
                }
            });
        }
    }
}