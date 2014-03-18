package com.java.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

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
	private JQ<Tata>  jq;

	@Before
	public void setUp() throws Exception {
		conn = null;
		stmt = null;
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		stmt = conn.createStatement();
		stmt.executeUpdate("DROP DATABASE IF EXISTS toto ");
		stmt.executeUpdate("CREATE DATABASE toto ");
		stmt.executeUpdate("use toto");
		stmt.executeUpdate("CREATE TABLE tata (id int, test varchar(10))");
		stmt.executeUpdate("INSERT INTO tata (id, test ) values(10, 'test')");
		stmt.executeUpdate("INSERT INTO tata (id, test ) values(12, 'test12')");

		jq = new JQ<Tata>(Tata.class);
	}

	@Test
	public void testList() {
		List<Tata> all = jq.list();

		assertFalse(all.isEmpty());
		assertEquals(10, all.get(0).getId());
		assertEquals("test", all.get(0).getTest());
	}

	@Test
	public void testFirst() {
		Tata tata = jq.first();

		assertNotNull(tata);
		assertEquals(10, tata.getId());
		assertEquals("test", tata.getTest());
	}

	
	@Test
	public void testWhere() {
		Tata tata = jq.where("test", "test12").first();

		assertEquals(12, tata.getId());
		assertEquals("test12", tata.getTest());
	}

	@Test
	public void testSave() {
		Tata tata = newTata();

		jq.save(tata);

		assertEquals(3, jq.list().size());
		assertEquals("5555", jq.list().get(2).getTest());
		assertEquals(25, jq.list().get(2).getId());
	}
	
	@Test
	public void testDelete() {
		Tata tata = addTata();

		jq.delete(tata);

		assertEquals(2, jq.list().size());
	}

	@Test
	public void testCanHaveTableByAnomation() throws Exception {
		JQ<Titi> jq = new JQ<Titi>(Titi.class);
		
		List<Titi> all = jq.list();

		assertFalse(all.isEmpty());
	}

	@Test
	public void testCanHaveColumnNameByAnomation() throws Exception {
		JQ<Pipi> jq = new JQ<Pipi>(Pipi.class);
		
		List<Pipi> all = jq.list();

		assertFalse(all.isEmpty());
	}
	
	private Tata newTata() {
		Tata tata = new Tata();
		tata.setId(25);
		tata.setTest("5555");
		return tata;
	}

	private Tata addTata() {
		Tata tata = new Tata();
		tata.setId(25);
		tata.setTest("5555");

		jq.save(tata);
		return tata;
	}

	@After
	public void after() throws Exception {
		conn.close();
		stmt.close();
	}

}

class Tata {
	private long id;
	private String test;

	// private Titi titi;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}
}

@Table(value = "tata")
class Titi {
	@SuppressWarnings("unused")
	private long id;
	@SuppressWarnings("unused")
	private String test;
}

@Table(value = "tata")
class Pipi {
	@SuppressWarnings("unused")
	private long id;
	@Column(value ="test")
	private String column;
}
