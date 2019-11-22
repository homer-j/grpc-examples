CXX = g++
CPPFLAGS += `pkg-config --cflags protobuf grpc`
CXXFLAGS += -std=c++11
LDFLAGS += -L/usr/local/lib `pkg-config --libs protobuf grpc++`\
           -lgrpc++_reflection\
           -ldl

PROTOC = protoc
GRPC_CPP_PLUGIN = grpc_cpp_plugin
GRPC_CPP_PLUGIN_PATH ?= `which $(GRPC_CPP_PLUGIN)`

PROTOS_PATH = ../echo-client/src/main/proto

vpath %.proto $(PROTOS_PATH)

echo_server: grpc_echo.pb.o grpc_echo.grpc.pb.o grpc_echo_server.o
	$(CXX) $^ $(LDFLAGS) -o $@

.PRECIOUS: %.pb.cc
%.pb.cc: %.proto
	$(PROTOC) -I $(PROTOS_PATH) --cpp_out=. $<

.PRECIOUS: %.grpc.pb.cc
%.grpc.pb.cc: %.proto
	$(PROTOC) -I $(PROTOS_PATH) --grpc_out=. \
	--plugin=protoc-gen-grpc=$(GRPC_CPP_PLUGIN_PATH) $<

.PHONY: clean
clean:
	rm -f *.o *.pb.cc *.pb.h echo_server
