package com.java.query;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.java.query.anotation.Column;
import com.java.query.anotation.Table;

public class JQTest {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/";
	static final String USER = "root";
	static final String PASS = "root";
	private Connection conn;
	private Statement stmt;
	private JQ<Person>  jq;

	@Before
	public void setUp() throws Exception {
		conn = null;
		stmt = null;
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		stmt = conn.createStatement();
		stmt.executeUpdate("use test");
		stmt.executeUpdate("CREATE TABLE IF NOT EXISTS person (id int, name varchar(10))");
		stmt.executeUpdate("INSERT INTO person (id, name ) values(10, 'test')");
		stmt.executeUpdate("INSERT INTO person (id, name ) values(12, 'test12')");

		jq = new JQ<Person>(Person.class);
	}
	
	@Test
	public void testList() {
		List<Person> all = jq.list();

		assertFalse(all.isEmpty());
		assertEquals(10, all.get(0).getId());
		assertEquals("test", all.get(0).getName());
	}

	@Test
	public void testFirst() {
		Person person = jq.first();

		assertNotNull(person);
		assertEquals(10, person.getId());
		assertEquals("test", person.getName());
	}

	
	@Test
	public void testWhere() {
		Person person = jq.where("name", "test12").first();

		assertEquals(12, person.getId());
		assertEquals("test12", person.getName());
	}

	@Test
	public void testSave() {
		Person person = newPerson();

		jq.save(person);

		assertEquals(3, jq.list().size());
		assertEquals("sdiawara", jq.list().get(2).getName());
		assertEquals(25, jq.list().get(2).getId());
	}
	
	@Test
	public void testDelete() {
		Person person = addPerson();

		jq.delete(person);

		assertEquals(2, jq.list().size());
	}

	@Test
	public void testCanHaveTableByAnomation() throws Exception {
		JQ<You> jq = new JQ<You>(You.class);
		
		List<You> all = jq.list();

		assertFalse(all.isEmpty());
	}

	@Test
	public void testCanHaveColumnNameByAnomation() throws Exception {
		JQ<French> jq = new JQ<French>(French.class);
		
		List<French> all = jq.list();

		assertFalse(all.isEmpty());
	}
	
	private Person newPerson() {
		Person person = new Person();
		person.setId(25);
		person.setName("sdiawara");
		return person;
	}

	private Person addPerson() {
		Person person = new Person();
		person.setId(25);
		person.setName("sdiawara");

		jq.save(person);
		return person;
	}

	@After
	public void after() throws Exception {
		stmt.executeUpdate("DROP TABLE IF EXISTS person");
		conn.close();
		stmt.close();
	}

}

class Person {
	private long id;
	private String name;

	// private Titi titi;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

@Table(name = "person")
class You {
	@SuppressWarnings("unused")
	private long id;
	@SuppressWarnings("unused")
	private String name;
}

@Table(name = "person")
class French {
	@SuppressWarnings("unused")
	private long id;
	@Column(name ="name")
	private String test;
}
