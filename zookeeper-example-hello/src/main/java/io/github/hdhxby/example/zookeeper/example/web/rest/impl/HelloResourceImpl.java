package io.github.hdhxby.example.zookeeper.example.web.rest.impl;

import io.github.hdhxby.example.zookeeper.example.feign.WorldClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@RequestMapping("/api")
@RestController
public class HelloResourceImpl {

    private static final String WORD_API = "http://world/api/world";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WorldClient worldClient;

//    @Value("${foo}")
    private String foo;

    @GetMapping("/hello/rest")
    public ResponseEntity<String> rest(@RequestParam(value = "name",defaultValue = "world",required = false) String name, @RequestParam(value = "millis",defaultValue = "0",required = false) Long millis){
        return restTemplate.getForEntity(URI.create(String.format(WORD_API +"?name=%s&millis=%d",name,millis)),String.class);
    }


    @GetMapping("/hello/feign")
    public ResponseEntity<String> client(@RequestParam(value = "name",defaultValue = "world",required = false) String name,@RequestParam(value = "millis",defaultValue = "0",required = false) Long millis){
        return worldClient.world(name,millis);
    }


    @GetMapping("/hello/world")
    public ResponseEntity<String> world(){
        return ResponseEntity.ok("hello world");
    }

    @GetMapping("/hello/foo")
    public ResponseEntity<String> foo(){
        return ResponseEntity.ok(foo);
    }
}
