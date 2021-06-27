package com.drug.store.service;

import com.drug.store.domain.Order;
import com.drug.store.event.OrderCreatedEvent;
import com.drug.store.repository.OrderEventStore;
import com.drug.store.util.OrderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderService {

  private final OrderEventStore repository;

  public void createOrder(long orderId, long userId) {
    repository.addEvent(orderId, new OrderCreatedEvent(orderId, userId));
  }

  public void updateOrder(long orderId, long userId, long drugId) {
    Order order = OrderUtil.recreateOrderState(repository, orderId, drugId);

  }
}
