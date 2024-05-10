#Pre-reqs 

-Docker Compose
-Observability Cloud environment and Ingest token

#How to set up the project

-Build the docker compose Stack 

```docker compose up``` 

-Generate request on the stack 

Test that the api is reachable:

- ```localhost:8080/echo```

Send a payload to the recursive task function :

- ```localhost:8080/recursive```

Example request the function will take every number from the Array of values for the key ar and add them to a sum recursively. (It is a recursive process)

``` 
curl --location 'localhost:8080/recursive' \
--header 'Content-Type: application/json' \
--data '{"ar": ["213131321","231321", "1321"] }' 
```

What you should see in Observability Cloud : 

View the Service Map:


Click of a successful trace on the recursive endpoint (allow a little time for the trace to appear in the UI):

Notice the link symbol in the trace detail:

Corresponding Code in ```CustomRecursiveTask.java```

We get the span contect from the collection of CustomRecursiveTask which then creates the various Tasks based on the number of Integer in the Array. 
```
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
  ```

In the CustomRecursive task we retrieve the Tracer build the span and add the link to the span and perform the code logic here add adding integer to the sum . 

```
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
```

Click the link in the Span link section of the trace detail:

View of the linked trace:


