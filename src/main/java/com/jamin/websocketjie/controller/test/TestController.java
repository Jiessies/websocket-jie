package com.jamin.websocketjie.controller.test;

import com.alibaba.fastjson.JSON;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class TestController {

    @PostMapping(value = "/gzip" ,produces = "application/json")
    public String getString(@RequestParam(value = "name", required = false) String name) {
        String string = UUID.randomUUID().toString() + name;

        return JSON.toJSONString(string);
    }
}
