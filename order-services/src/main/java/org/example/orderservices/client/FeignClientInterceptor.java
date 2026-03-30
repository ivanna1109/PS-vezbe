package org.example.orderservices.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

//Presretac zahteva koji se salju Feign kanalom
@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        template.header("X-API-KEY", "studentska-tajna-2026");
    }
}