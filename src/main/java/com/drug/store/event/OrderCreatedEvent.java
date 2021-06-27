package com.drug.store.event;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrderCreatedEvent extends Event{
    private final long orderId;
    private final long userId;
}
