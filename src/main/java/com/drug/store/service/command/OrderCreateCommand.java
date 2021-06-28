package com.drug.store.service.command;

import com.drug.store.domain.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderCreateCommand implements OrderCommand{

  @Override
  public String execute(Order order) {
    //todo:transform Order in message
  }
}
