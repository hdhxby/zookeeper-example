package io.github.hdhxby.example.zookeeper.example.feign;

import io.github.hdhxby.example.zookeeper.example.web.rest.WorldResource;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "world", fallback = WorldFallBack.class)
public interface WorldClient extends WorldResource {

    @GetMapping("/api/world")
    @Override
    ResponseEntity<String> world(@RequestParam(value = "name",defaultValue = "world",required = false) String name,@RequestParam(value = "millis",defaultValue = "0",required = false) Long millis);
}
