package com.exam.task2.client;

import com.exam.task2.model.Result;
import com.exam.task2.model.Address;
import com.exam.task2.model.Event;
import com.exam.task2.model.Payload;

public interface Client {
    //блокирующий метод для чтения данных
    Event readData();

    //блокирующий метод отправки данных
    Result sendData(Address dest, Payload payload);
}
