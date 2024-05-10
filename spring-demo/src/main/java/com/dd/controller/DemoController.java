package com.dd.controller;

import com.dd.forkjoin.CustomRecursiveTask;
import com.dd.model.TaskRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import java.time.Instant;
import java.util.concurrent.ForkJoinPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Slf4j
public class DemoController {

  RestTemplate template = new RestTemplate();
  ForkJoinPool fjp = new ForkJoinPool();

  @GetMapping("/echo")
  @ResponseBody
  public String echo(@RequestParam(defaultValue = "test") String id) {
    //log.info("in echo");
    Span span = Span.fromContext(Context.current());
    span.addEvent("echo:" + id, Instant.now());
    return "ID: " + id;
  }

  @PostMapping("/recursive")
  @ResponseBody
  public Integer task(@RequestBody String json) {

    ObjectMapper mapper = new ObjectMapper();

    //log.info("in task, json:" + json);

    try {
      TaskRequest request = mapper.readValue(json, TaskRequest.class);

      return fjp.invoke(new CustomRecursiveTask(Context.current(), request.getAr()));
    } catch (Exception e) {
      //log.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

  @GetMapping("/api/foo")
  @ResponseBody
  public String getFoo(@RequestParam(defaultValue = "test") String id) {
    //log.info("in getFoo");
    String obj = template.getForObject("http://localhost:8081/" + id, String.class);
    //log.info("obj=" + obj);
    return "ID: " + id;
  }
}
