package io.github.hdhxby.example.zookeeper.example.web.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


public interface WorldResource {

    @GetMapping("/api/world")
    ResponseEntity<String> world(@RequestParam(value = "name",defaultValue = "world",required = false) String name,@RequestParam(value = "millis",defaultValue = "0",required = false) Long millis);
}
