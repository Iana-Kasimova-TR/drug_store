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
    //todo: add event  in database and send message via OrderCreatedCommand to PaymentService
    repository.addEvent(orderId, new OrderCreatedEvent(orderId, userId));
  }

  public void updateOrder(long orderId, long userId, long drugId) {
    //todo: add event  in database and send message via OrderUpdatedCommand
    Order order = OrderUtil.recreateOrderState(repository, orderId, drugId);
  }

  public void confirmOrder(long orderId, long userId, long drugId) {
    //todo: add event  in database and send message via OrderConfirmedCommand to delivery service
    Order order = OrderUtil.recreateOrderState(repository, orderId, drugId);
  }
}
