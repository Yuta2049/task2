package com.exam.task2.service;

import com.exam.task2.client.Client;
import com.exam.task2.model.Event;
import com.exam.task2.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class HandlerService implements Handler {

    private final Executor executor = Executors.newCachedThreadPool();

    @Autowired
    private Client client;

    public HandlerService(Client client) {
        this.client = client;
    }

    @Override
    public Duration timeout() {
        return null;
    }

    @Override
    public void performOperation() {
        Flux<Event> eventFlux = Flux.create(fluxSink -> client.readData());
        eventFlux.doOnNext(event -> {
                            List<CompletableFuture<Result>> cfs = event.recipients().stream()
                                    .map(address -> CompletableFuture.supplyAsync(() -> client.sendData(address, event.payload()), executor)
                                            .thenApply(result -> {
                                                if (result.equals(Result.REJECTED)) {
                                                    try {
                                                        Thread.sleep(timeout().toMillis());
                                                    } catch (InterruptedException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                    return client.sendData(address, event.payload());
                                                } else {
                                                    return result;
                                                }
                                            }))
                                    .toList();

                            cfs.stream().map(CompletableFuture::join).collect(Collectors.toList());
                        }
                )
                .subscribe();
    }
}
