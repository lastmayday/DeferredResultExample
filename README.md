### An Example of Usage of Spring `DeferredResult`

[DeferredResult](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/context/request/async/DeferredResult.html)
can be used to implement long polling.

Here is an example.


First run the `Application` in `async-server` module, then you can run tests in `async-client` module.
When running tests, please first run `AsyncClientTest`, then `AsyncClientTest2`.
Then wait a minute, it will shows that client2 sends a message to client1 and client1 responses a message.
