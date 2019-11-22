package ru.yandex;

import java.util.concurrent.BlockingQueue;

import io.grpc.stub.StreamObserver;

import ru.yandex.GrpcEcho.EchoResponse;

public class EchoResponseObserver implements StreamObserver<EchoResponse> {
    private final BlockingQueue<EchoResponse> queue;

    EchoResponseObserver(BlockingQueue<EchoResponse> queue) {
        this.queue = queue;
    }

    @Override
    public void onNext(EchoResponse value) {
        queue.offer(value);
    }

    @Override
    public void onError(Throwable t) {
    }

    @Override
    public void onCompleted() {
    }
}
