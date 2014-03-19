jq (Java Query)
==
Jq is a light data access objet using mysql. You just define your data base structure and just use java to access data base. It's recommanded for small project.
Example:
--

- Define Object
```
@Table(value = "tata") // optionnal 
class Titi {
	private long id;
	private String test;
}
```


- Get first tata or titi according Titi is defined with @Table annotation
```
  JQ<Titi> jq = new JQ<Titi>(Titi.class);
  Titi titi = jq.list();
```

- Get all 
```
	JQ<Titi> jq = new JQ<Titi>(Titi.class);
  List<Titi> titis = jq.list();
```
