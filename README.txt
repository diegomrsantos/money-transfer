The Spark framework was used to create a simple REST API. It is not a RESTful API cause HATEOAS was not implemented.
The project has three modules: application, domain and infrastructure. The system uses a H2 database with READ_COMMITTED isolation level and Multiversion concurrency control.
In a concurrent environment after checking if the source account has sufficient funds, a parallel transaction could update the account's balance to a
value lower than what is required and it would cause an invalid operation. To avoid that, an approach inspired by the Compare and Swap method was used.
The system decreases the source account balance only if it has not been changed, otherwise it retries the whole operation.

Application module

This module contains Account and Transfer resources. The "/accounts" endpoint provides GET, POST and DELETE operations in order to manage accounts.
It should not be possible to physically delete an account because of foreign keys and historic reasons and DELETE method should make a logical
deletion, i.e., disabling the account. But for the sake of simplicity it was not implemented in this version. The deposit operation is performed through a POST method
as it does not fit well to a full resource update using PUT. I believe the right way to implement that would be to create an "/transactions" endpoint.
In this way we could request asynchronouslly a deposit, withdraw or transfer operation and create a new resource which would reflect the status of the task.
But for the sake of simplicity Transfer resource publish the "/transfers" endpoint where it is possible to create a transfer using the POST method.

It also contains three Integration tests:  one which covers a successful money transfer, one which covers the case when there is an error and the whole transfer transaction is rolled back and
a concurrent test where thread safety is checked.

Unfortunately, there was no time to create integration tests for the API itself.

Domain module

This module contains business logic. It consists of domain entities, Repositories, Services and Transaction handling interfaces as well ass Services implementations.
I think unit tests would not add enough value to this module.

Infrastructure module

This module contains Repositories implementations handling JDBC and transaction control. With the help of a thread local it was possible to implement
transaction support on Services classes and use a Singleton Repository with no explicit dependency to a Connection

I'm sorry for not so concise explanation. If I had more time, I would have written a shorter and better version.