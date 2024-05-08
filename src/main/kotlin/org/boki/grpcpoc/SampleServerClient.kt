package org.boki.grpcpoc

import HelloRequest
import HelloResponse
import SampleServiceGrpc
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver

object SampleServerClient {
    private val channel: ManagedChannel = ManagedChannelBuilder
        .forAddress("localhost", 50051)
        .usePlaintext()
        .build()

    private val stub: SampleServiceGrpc.SampleServiceStub = SampleServiceGrpc.newStub(channel)

    fun singleRequestSingleResponse(request: HelloRequest): HelloResponse {
        val responseObserver = object : StreamObserver<HelloResponse> {
            var response: HelloResponse? = null

            override fun onNext(value: HelloResponse?) {
                response = value
            }

            override fun onError(t: Throwable) {
                println("Error occurred: ${t.message}")
            }

            override fun onCompleted() {
                println("Response received: ${response?.world}")
            }
        }

        stub.singleRequestSingleResponse(request, responseObserver)

        // Blocking call, waits for the response
        return responseObserver.response ?: throw IllegalStateException("No response received")
    }
}

