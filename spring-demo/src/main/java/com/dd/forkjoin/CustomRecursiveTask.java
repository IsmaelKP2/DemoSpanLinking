package com.dd.forkjoin;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class CustomRecursiveTask extends RecursiveTask<Integer> {
  public static final String RECURSIVE_TASK = "recursive-task";
  private static final int THRESHOLD = 2;
  private int[] arr;

  private Context context;

  public CustomRecursiveTask(Context context, int[] arr) {
    this.context = context;
    if (this.context == null) {
      this.context = Context.root();
    }
    this.arr = arr;
  }

  @Override
  protected Integer compute() {
    if (arr.length > THRESHOLD) {
      return ForkJoinTask.invokeAll(createSubtasks()).stream().mapToInt(ForkJoinTask::join).sum();
    } else {
      return processing(arr);
    }
  }

  private Collection<CustomRecursiveTask> createSubtasks() {
    Tracer tracer = GlobalOpenTelemetry.getTracer(RECURSIVE_TASK);
    Span span =
        tracer
            .spanBuilder("subtasks")
            .addLink(
                Span.fromContext(this.context).getSpanContext(),
                Attributes.builder().put("array-length", String.valueOf(arr.length)).build())
            .startSpan();

    Context ctx = Context.current().with(span);
    List<CustomRecursiveTask> dividedTasks = new ArrayList<>();
    dividedTasks.add(new CustomRecursiveTask(ctx, Arrays.copyOfRange(arr, 0, arr.length / 2)));
    dividedTasks.add(
        new CustomRecursiveTask(ctx, Arrays.copyOfRange(arr, arr.length / 2, arr.length)));
    span.end();
    return dividedTasks;
  }

  private Integer processing(int[] arr) {
    System.out.println("in processing");
    Tracer tracer = GlobalOpenTelemetry.getTracer(RECURSIVE_TASK);
    Span span =
        tracer
            .spanBuilder("processing")
            .addLink(
                Span.fromContext(this.context).getSpanContext(),
                Attributes.builder().put("array-length", String.valueOf(arr.length)).build())
            .startSpan();
    int sum = Arrays.stream(arr).sum();
    span.end();
    return sum;
  }
}
