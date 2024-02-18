package com.exam.task2.service;

import java.time.Duration;

public interface Handler {
    Duration timeout();

    void performOperation();
}
