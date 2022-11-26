package io.github.hdhxby.example.zookeeper.web.rest.impl;

import io.github.hdhxby.example.zookeeper.feign.WorldClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@RequestMapping("/api/hello")
@RestController
public class HelloResourceImpl {

    @Autowired
    private WorldClient worldClient;

    @GetMapping("/world")
    public ResponseEntity<String> world(@RequestParam(value = "name",defaultValue = "world",required = false) String name,@RequestParam(value = "millis",defaultValue = "0",required = false) Long millis){
        return worldClient.world(name,millis);
    }

}
