
## REFLECTION

1. Based on Percival’s framework for evaluating testing objective there are several things that we need to conclude in order to determine wether a TDD flow is useful or not

- Does it provide a safety net?
Yes. By writing tests like testUpdateStatusInvalidStatus and testUpdateStestUpdateStatusInvalidOrderId we created a fail-safe mechanism. when later we encountered a NullPointerExceptioj, the test could immediately signaled that the safety net wasnt properly tied to the service
- Does it help with design?
yes, because we did the test first we already knew what was going to happened late, so it helps build the code better

so with all of those, the TDD flow is useful enough for me

2. F.I.R.S.T Priciple (Fast, Independent, Repeateable, Self-Validating, Timely)

Fast = since we used Mockito for the service test, the wont touch a database or network, making them execute in miliseconds

Independent = using a @BeforeEach to reset the orders list and orderRepository ensures that testSaveUpdate doesn't depend on the state left over by testSaveCreate

Repeatable = in the the test provided by the module, it uses a harcoded UUID and timestamps. this ensures the result are identical whether run on my laptop or a CI/CD server

Self-Validating = because we use assertEquals, assertTrue, and asserThrows. the test clearly pass or fail without me needing to manually check a log file

Timely = because we write the test first to see if it fail and then write the implementation, it fulfills the timely principle 