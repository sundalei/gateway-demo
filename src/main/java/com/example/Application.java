package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@EnableConfigurationProperties(UriConfiguration.class)
@SpringBootApplication
@RestController
public class Application {

  private static final Logger LOG = LoggerFactory.getLogger(Application.class);

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public RouteLocator myRoutes(RouteLocatorBuilder builder, UriConfiguration uriConfiguration) {
    String httpUri = uriConfiguration.getHttpbin();
    LOG.debug("httpUri: {}", httpUri);

    return builder
        .routes()
        .route(
            predicateSpec ->
                predicateSpec
                    .path("/get")
                    .filters(filterSpec -> filterSpec.addRequestHeader("Hello", "World"))
                    .uri(httpUri))
        .route(
            predicateSpec ->
                predicateSpec
                    .host("*.circuitbreaker.com")
                    .filters(
                        filterSpec ->
                            filterSpec.circuitBreaker(
                                config ->
                                    config.setName("mycmd").setFallbackUri("forward:/fallback")))
                    .uri(httpUri))
        .build();
  }

  @RequestMapping("/fallback")
  public Mono<String> fallback() {
    return Mono.just("fallback");
  }
}
