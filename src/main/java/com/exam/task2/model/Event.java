package com.exam.task2.model;

import java.util.List;

public record Event(List<Address> recipients, Payload payload) {}
