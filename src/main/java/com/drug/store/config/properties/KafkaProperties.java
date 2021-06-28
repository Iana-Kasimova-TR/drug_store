package com.drug.store.config.properties;

import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka")
@Data
public class KafkaProperties {
    @NotBlank
    private String server;
    @NotNull
    private boolean sslEnabled;
    @NotNull
    private RetryPolicy retryPolicy;
    @NotNull
    private TopicDetails metadata;
    @NotNull
    private TopicDetails indexing;
    @Positive
    private int concurrency;


    @Data
    public static class RetryPolicy {

        @Positive
        private int maxRetryAttempts;
        @Positive
        private int initialInterval;
        @Positive
        private int maxInterval;
        @Positive
        private int multiplier;
        @Positive
        private int requestTimeout;
        @Positive
        private int retryBackoff;

    }

    @Data
    public static class TopicDetails {
        @NotBlank
        private String group;
        @NotBlank
        private String topic;
        private String dlt;
    }

}
