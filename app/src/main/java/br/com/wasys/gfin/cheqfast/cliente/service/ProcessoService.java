package br.com.wasys.gfin.cheqfast.cliente.service;

import android.content.Context;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

import br.com.wasys.gfin.cheqfast.cliente.Application;
import br.com.wasys.gfin.cheqfast.cliente.endpoint.Endpoint;
import br.com.wasys.gfin.cheqfast.cliente.endpoint.ProcessoEndpoint;
import br.com.wasys.gfin.cheqfast.cliente.model.DigitalizacaoModel;
import br.com.wasys.gfin.cheqfast.cliente.model.ProcessoModel;
import br.com.wasys.gfin.cheqfast.cliente.model.UploadModel;
import br.com.wasys.library.service.Service;
import retrofit2.Call;
import rx.Observable;
import rx.Subscriber;

import static br.com.wasys.gfin.cheqfast.cliente.background.DigitalizacaoService.startDigitalizacaoService;

/**
 * Created by pascke on 03/09/16.
 */
public class ProcessoService extends Service {

    public static ProcessoModel obter() throws Throwable {
        ProcessoEndpoint endpoint = Endpoint.create(ProcessoEndpoint.class);
        Call<ProcessoModel> call = endpoint.criar();
        ProcessoModel model = Endpoint.execute(call);
        return model;
    }

    public static ProcessoModel salvar(ProcessoModel processoModel) throws Throwable {
        ProcessoEndpoint endpoint = Endpoint.create(ProcessoEndpoint.class);
        Call<ProcessoModel> call = endpoint.salvar(processoModel);
        ProcessoModel result = Endpoint.execute(call);
        List<UploadModel> models = processoModel.uploads;
        if (CollectionUtils.isNotEmpty(models)) {
            String referencia = String.valueOf(result.id);
            DigitalizacaoModel.Tipo tipo = DigitalizacaoModel.Tipo.TIPIFICACAO;
            DigitalizacaoService.criar(referencia, tipo, models);
            // INICIA A DIGITALIZACAO EM BACKGROUND
            Context context = Application.getContext();
            startDigitalizacaoService(context, tipo, referencia);
        }
        return result;
    }

    public static class Async {

        public static Observable<ProcessoModel> obter() {
            return Observable.create(new Observable.OnSubscribe<ProcessoModel>() {
                @Override
                public void call(Subscriber<? super ProcessoModel> subscriber) {
                    try {
                        ProcessoModel model = ProcessoService.obter();
                        subscriber.onNext(model);
                        subscriber.onCompleted();
                    } catch (Throwable e) {
                        subscriber.onError(e);
                    }
                }
            });
        }

        public static Observable<ProcessoModel> salvar(final ProcessoModel processoModel) {
            return Observable.create(new Observable.OnSubscribe<ProcessoModel>() {
                @Override
                public void call(Subscriber<? super ProcessoModel> subscriber) {
                    try {
                        ProcessoModel model = ProcessoService.salvar(processoModel);
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