package com.dayo.deecloud;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;

import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.stream.Stream;

@SpringBootApplication
public class DeeCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeeCloudApplication.class, args);
    }

}

@Component
@RequiredArgsConstructor
class Producer{
 private final KafkaTemplate<Integer, String> template;

    Faker faker;

    @EventListener(ApplicationStartedEvent.class)

    public void generate() {

        faker = Faker.instance();

        final Flux<Long> interval = Flux.interval(Duration.ofMillis(1_000));

        final Flux<String> quotes = Flux.fromStream(Stream.generate(() -> faker.hobbit().quote()));

        Flux.zip(interval, quotes).map(it -> template.send("hobbit", faker.random().nextInt(42), it.getT2()))
                .blockLast();

    }
}