package io.github.hdhxby.example.zookeeper.web.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


public interface HelloResource {

    @GetMapping("/api/hello")
    ResponseEntity<String> hello(@RequestParam(value = "name",defaultValue = "world",required = false) String name);
}
