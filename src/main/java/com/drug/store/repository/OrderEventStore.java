package com.drug.store.repository;

import com.drug.store.event.Event;
import com.drug.store.event.OrderCreatedEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

//TODO: we need add kafka instead Map?
@Component
public class OrderEventStore {
  private Map<Long, List<Event>> store = new HashMap<>();

  public List<Event> getEvents(long orderId){
    return store.computeIfAbsent(orderId, id  -> new ArrayList<>());
  }

  public void addEvent(long orderId, OrderCreatedEvent orderCreatedEvent) {
    List<Event> events = store.computeIfAbsent(orderId, id -> new ArrayList<>());
    events.add(orderCreatedEvent);
  }
}
