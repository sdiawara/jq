jq (Java Query)
==
[![Build Status](https://travis-ci.org/sdiawara/jq.svg?branch=master)](https://travis-ci.org/sdiawara/jq)

Jq is a light data access objet using mysql. You just define your data base structure and just use java to access data base. It's recommanded for small project.
####Example:
- Define Object
```java
@Table(name = "a_persons") // optionnal 
class Person {
   private long id;
   private String test;
}
```


- Get first a_persons or person according Person is defined with @Table annotation
```java
JQ<Person> jq = new JQ<Person>(Person.class);
Person person = jq.first();
```

- Get all 
```java
JQ<Person> jq = new JQ<Person>(Person.class);
List<Person> persons = jq.list();
```
- Save or update
```java
JQ<Person> jq = new JQ<Person>(Person.class);
Person person = new Person();
person.setId(25);
person.setName("5555");
jq.save(Person);
```

- Delete
```java
jq.delete(person);
```
