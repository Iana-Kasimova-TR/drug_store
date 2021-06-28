package com.drug.store.config;

import com.drug.store.config.properties.KafkaProperties;
import com.drug.store.exception.NotRetryableException;
import com.drug.store.message.PaymentMessage;
import com.drug.store.util.deserializer.AvroDeserializer;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.util.backoff.ExponentialBackOff;

@EnableKafka
@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
@Slf4j
public class KafkaConsumerCofig {

  public ConsumerFactory<String, PaymentMessage> consumerFactory(KafkaProperties kafkaProperties) {
    log.debug("KAFKA_PROPERTIES: " + kafkaProperties);

    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getServer());
    props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getMetadata().getGroup());
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AvroDeserializer.class);

    return new DefaultKafkaConsumerFactory<String, PaymentMessage>(
        props,
        new StringDeserializer(),
        new ErrorHandlingDeserializer<>(new AvroDeserializer<>(PaymentMessage.class)));
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, PaymentMessage> kafkaListenerContainerFactory(
      KafkaProperties kafkaProperties, SeekToCurrentErrorHandler seekToCurrentErrorHandler) {
    ConcurrentKafkaListenerContainerFactory<String, PaymentMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();

    factory.setConcurrency(kafkaProperties.getConcurrency());

    factory.setConsumerFactory(consumerFactory(kafkaProperties));
    factory.setAckDiscarded(true);
    factory.setErrorHandler(seekToCurrentErrorHandler);
    return factory;
  }

  @Bean
  public SeekToCurrentErrorHandler errorHandler(KafkaProperties kafkaProperties,
      @Qualifier("dlqKafkaTemplate") KafkaOperations<String, Object> template) {
    ExponentialBackOff backOff =
        new ExponentialBackOff(
            kafkaProperties.getRetryPolicy().getInitialInterval(),
            kafkaProperties.getRetryPolicy().getMultiplier());
    backOff.setMaxElapsedTime(kafkaProperties.getRetryPolicy().getMaxInterval());
    DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template,
        (r, e) -> new TopicPartition(kafkaProperties.getMetadata().getDlt(), r.partition()));
    SeekToCurrentErrorHandler handler =
        new SeekToCurrentErrorHandler(
            recoverer,
            backOff);
    handler.addNotRetryableExceptions(NotRetryableException.class);
    return handler;
  }


}
