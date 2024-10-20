package com.omer.microservices.apigateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ApiGatewayConfiguration {

    /**
     *
     * Yapılandırılan Rotalar (Routes):
     * İlk route - /get:
     *
     * /get yoluna gelen istekler http://httpbin.org:80 adresine yönlendirilir.
     * İstek gönderilirken iki ek filtre uygulanır:
     * addRequestHeader("My-Header", "MY-URI"): İsteğe özel bir başlık (My-Header) eklenir.
     * addRequestParameter("Param", "MyValue"): İsteğe özel bir parametre (Param) eklenir.
     * İkinci route - /currency-exchange/**:
     *
     * /currency-exchange/** ile eşleşen tüm istekler, "lb://currency-exchange" URI'sine yönlendirilir.
     * lb:// ifadesi, Load Balancer (Yük dengeleyici) kullanıldığını gösterir. Yani istekler, currency-exchange adlı microservice'e yönlendirilir ve yük dengelenir.
     * Üçüncü route - /currency-conversion/**:
     *
     * /currency-conversion/** ile eşleşen istekler, "lb://currency-conversion" servisine yönlendirilir.
     * Bu da, yük dengeleyici üzerinden currency-conversion microservice'ine istek yönlendirilir.
     * Dördüncü route - /currency-conversion-feign/**:
     *
     * /currency-conversion-feign/** yolundaki istekler, yine "lb://currency-conversion" microservice'ine yönlendirilir. Bu rota, Feign Client kullanan istekler için tanımlanmış olabilir.
     * Beşinci route - /currency-conversion-new/**:
     *
     * Bu rota, /currency-conversion-new/** ile eşleşen istekleri, URI'deki bir bölümü (path segmentini) yeniden yazarak /currency-conversion-feign/** yoluna yönlendirir.
     * rewritePath filtresi, /currency-conversion-new/{segment} desenindeki segmenti, /currency-conversion-feign/{segment} olarak değiştirir. Böylece, istek dinamik olarak farklı bir path'e yeniden yönlendirilir.
     *
     */
    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(p -> p.path("/get")
                .filters(f -> f.addRequestHeader("My-Header", "MY-URI")
                    .addRequestParameter("Param", "MyValue"))
                .uri("http://httpbin.org:80"))
            .route(p -> p.path("/currency-exchange/**")
                .uri("lb://currency-exchange"))
            .route(p -> p.path("/currency-conversion/**")
                .uri("lb://currency-conversion"))
            .route(p -> p.path("/currency-conversion-feign/**")
                .uri("lb://currency-conversion"))
            .route(p -> p.path("/currency-conversion-new/**")
                 .filters(f -> f.rewritePath(
                     "/currency-conversion-new/(?<segment>.*)",
                     "/currency-conversion-feign/${segment}"))
                .uri("lb://currency-conversion"))
            .build();
    }
}
