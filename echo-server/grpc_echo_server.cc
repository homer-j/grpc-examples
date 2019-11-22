#include <iostream>
#include <memory>
#include <string>

#include <grpcpp/grpcpp.h>

#include "grpc_echo.grpc.pb.h"

using grpc::Server;
using grpc::ServerBuilder;
using grpc::ServerContext;
using grpc::ServerReaderWriter;
using grpc::Status;
using ru::yandex::EchoRequest;
using ru::yandex::EchoResponse;
using ru::yandex::EchoService;

class EchoServiceImpl final : public EchoService::Service {
    Status getEcho(ServerContext *context, const EchoRequest *request, EchoResponse *response) override {
        response->set_message("Echo: " + request->message());
        return Status::OK;
    }

    Status startEchoStream(ServerContext *context,
                           ServerReaderWriter<EchoResponse, EchoRequest> *stream) override {
        EchoRequest request;
        while (stream->Read(&request)) {
            EchoResponse response;
            response.set_message("Echo: " + request.message());
            stream->Write(response);
        }

        return Status::OK;
    }
};

int main(int argc, char **argv) {
    std::string server_address("0.0.0.0:50051");
    EchoServiceImpl service;

    ServerBuilder builder;

    builder.AddListeningPort(server_address, grpc::InsecureServerCredentials());
    builder.RegisterService(&service);

    std::unique_ptr<Server> server(builder.BuildAndStart());

    std::cout << "Echo server is listening on " << server_address << std::endl;

    server->Wait();

    return 0;
}