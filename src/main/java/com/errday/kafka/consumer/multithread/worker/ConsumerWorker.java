package com.errday.kafka.consumer.multithread.worker;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsumerWorker implements Runnable {

    private String recordValue;

    public ConsumerWorker(String recordValue) {
        this.recordValue = recordValue;
    }

    @Override
    public void run() {
        log.info("thread: {}    record:{}", Thread.currentThread().getName(), recordValue);
    }
}
