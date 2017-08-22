The Spark framework was used to create a simple RESTful API. Two controllers were created with example methods making possible
to manipulate users and accounts. Unfortunately, there was no time to create integration tests for the controllers, but several
tests were created for the DAO e Service layers.
I do not know if it was expected for the task, but during the implementation I realized it was not a simple problem to transfer
money between two accounts in a multithreading environment. I decided to implement the DAO totally in memory, so I had to figure out a way
to implement a simple locking and transactional control mechanism. I decided to use a pessimist locking approach as I think we should not
show the users dirty financial data, but I am not sure how it is done in the real world. I Reentrant Read Write Lock was used for a better performance.
There is an IT test showing the code works when more than one threading is trying to access the same accounts at the same time.

I ended up spending a lot of time creating the DAO, but I think it was a valuable learning, better than just using an in-memory databse.
I could improve the code reducing code duplication, but unfortunately there was no time. I hope it meets the company's requirements
and I would be happy to talk more about the project.