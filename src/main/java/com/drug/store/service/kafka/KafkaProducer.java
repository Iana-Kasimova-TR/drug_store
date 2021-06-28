package com.drug.store.service.kafka;

import com.drug.store.domain.Order;
import com.drug.store.service.command.OrderCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {
  @Value("${kafka.indexing.topic}")
  private String topic;

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void sendMessage(OrderCommand command, Order order) {
      log.info("Start sending message to payment service {} and operation {}");
      kafkaTemplate.send(topic, command.execute(order));
  }
}
