package ru.yandex;

import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import ru.yandex.GrpcEcho.EchoRequest;
import ru.yandex.GrpcEcho.EchoResponse;

public class EchoClient {

    private static final String HOST = "localhost";
    private static final int PORT = 50051;

    public static void main(String[] args) {
        System.out.println("Echo client. Type \"exit\" to exit.");

        if (args.length > 0 && "streaming".equals(args[0])) {
            System.out.println("Running echo client in Bidi streaming mode");
            runBidiStreamingClient();
        } else {
            System.out.println("Running echo client in Unary call mode");
            runUnaryCallsClient();
        }
    }

    private static void runUnaryCallsClient() {
        var client =  EchoServiceGrpc.newBlockingStub(createChannel(HOST, PORT));

        readInput(line -> {
            var request = createRequest(line);
            try {
                var echoResponse = client.getEcho(request);
                System.out.println(echoResponse);
            } catch (StatusRuntimeException e) {
                System.out.println("Error: " + e.getStatus() + ". " + e.getMessage());
            }
        });
    }

    private static void runBidiStreamingClient() {
        var client =  EchoServiceGrpc.newStub(createChannel(HOST, PORT));

        BlockingQueue<EchoResponse> responseQueue = new ArrayBlockingQueue<>(1);
        EchoResponseObserver responseObserver = new EchoResponseObserver(responseQueue);
        StreamObserver<EchoRequest> requestObserver = client.startEchoStream(responseObserver);

        readInput(line -> {
            EchoRequest request = createRequest(line);

            requestObserver.onNext(request);

            try {
                EchoResponse response = responseQueue.take();
                System.out.println(response.getMessage());
            } catch (InterruptedException e) {
                requestObserver.onError(e);
                throw new RuntimeException(e);
            }
        });

        requestObserver.onCompleted();
    }

    private static EchoRequest createRequest(String message) {
        return EchoRequest.newBuilder()
                .setMessage(message)
                .build();
    }

    private static void readInput(Consumer<String> lineConsumer) {
        Scanner console = new Scanner(System.in);
        String nextLine;
        while ((nextLine = console.nextLine()) != null) {
            if ("exit".equals(nextLine)) {
                break;
            }

            lineConsumer.accept(nextLine);
        }
    }

    private static Channel createChannel(String host, int port) {
        return ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext() // turn of TLS for demo purposes
                .build();
    }

}
