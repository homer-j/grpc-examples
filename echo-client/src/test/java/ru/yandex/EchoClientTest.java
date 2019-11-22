package ru.yandex;

import java.io.IOException;

import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;

import ru.yandex.GrpcEcho.EchoRequest;
import ru.yandex.GrpcEcho.EchoResponse;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class EchoClientTest {

    private EchoServiceGrpc.EchoServiceImplBase serviceMock = mock(EchoServiceGrpc.EchoServiceImplBase.class);

    @Test
    void testEchoClient() throws IOException {
        doAnswer(EchoClientTest::getEchoMock).when(serviceMock).getEcho(any(), any());

        var server = InProcessServerBuilder
                .forName(InProcessServerBuilder.generateName())
                .directExecutor()
                .addService(serviceMock)
                .build();

        try {
            server.start();



        } finally {
            server.shutdown();
        }

    }

    private static Void getEchoMock(InvocationOnMock inv) {
        EchoRequest request = inv.getArgumentAt(0, EchoRequest.class);
        StreamObserver<EchoResponse> observer = inv.getArgumentAt(1, StreamObserver.class);

        EchoResponse mockResponse = EchoResponse.newBuilder()
                .setMessage("Echo: " + request.getMessage())
                .build();
        observer.onNext(mockResponse);
        observer.onCompleted();
        return null;
    }

}
