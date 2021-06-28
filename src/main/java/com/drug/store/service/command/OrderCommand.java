package com.drug.store.service.command;

import com.drug.store.domain.Order;

@FunctionalInterface
public interface OrderCommand {

  String execute(Order order);
}
