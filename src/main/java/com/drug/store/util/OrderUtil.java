package com.drug.store.util;

import com.drug.store.domain.Drug;
import com.drug.store.domain.Order;
import com.drug.store.event.Event;
import com.drug.store.event.OrderAddDrugEvent;
import com.drug.store.event.OrderConfirmedEvent;
import com.drug.store.event.OrderCreatedEvent;
import com.drug.store.repository.OrderEventStore;
import java.util.ArrayList;
import java.util.List;

public class OrderUtil {

  public static Order recreateOrderState(OrderEventStore repository, long orderId, long userId) {
    Order order = null;
    List<Event> events = repository.getEvents(orderId);
    for (Event event : events) {
      if (event instanceof OrderCreatedEvent) {
        OrderCreatedEvent createdEvent = (OrderCreatedEvent) event;
        order = Order.builder()
            .orderId(orderId)
            .userId(userId)
            .drugs(new ArrayList<>())
            .build();
      }
      if (event instanceof OrderAddDrugEvent) {
        OrderAddDrugEvent updatedEvent = (OrderAddDrugEvent) event;
        Drug drug = new Drug();
        if (order != null) {
          order.getDrugs().add(drug);
        }
      }
      if (event instanceof OrderConfirmedEvent) {
        OrderCreatedEvent createdEvent = (OrderCreatedEvent) event;
        if (order != null) {
          order.setConfirmed(true);
        }
      }
    }
    return order;
  }
}
