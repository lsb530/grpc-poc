package org.boki.grpcpoc

import SampleServiceGrpc
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import test

@SpringBootApplication
class GrpcPocApplication

fun main(args: Array<String>) {
    val sampleServiceGrpc: SampleServiceGrpc
    val test = test {
        inputA = 123456
        inputB = "hello"
    }
    runApplication<GrpcPocApplication>(*args)
}
