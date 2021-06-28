package com.drug.store.config;

import com.drug.store.config.properties.KafkaProperties;
import com.drug.store.util.serializer.AvroSerializer;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
@Slf4j
public class KafkaProducerConfig {
  @Bean
  public ProducerFactory<String, Object> producerFactory(KafkaProperties kafkaProperties) {
    log.debug("KAFKA_PROPERTIES: " + kafkaProperties);

    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getServer());
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    props
        .put(ProducerConfig.RETRIES_CONFIG, kafkaProperties.getRetryPolicy().getMaxRetryAttempts());
    props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG,
        kafkaProperties.getRetryPolicy().getRequestTimeout());
    props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG,
        kafkaProperties.getRetryPolicy().getRetryBackoff());

    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, Object> kafkaTemplate(KafkaProperties kafkaProperties) {
    return new KafkaTemplate<>(producerFactory(kafkaProperties));
  }

  @Bean
  public ProducerFactory<String, Object> dlqProducerFactory(KafkaProperties kafkaProperties) {
    log.debug("KAFKA_PROPERTIES: " + kafkaProperties);

    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getServer());
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, Object> dlqKafkaTemplate(KafkaProperties kafkaProperties) {
    return new KafkaTemplate<>(dlqProducerFactory(kafkaProperties));
  }

}
