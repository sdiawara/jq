package com.java.query;

import com.java.query.annotation.Column;
import com.java.query.annotation.Table;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class JQ<T> {
	private static final String JQ_PROPERTIES = "jq.properties";
	private static final String JQ_DATABASE_URL = "jq.database.url";
	private static final String JQ_DATABASE_PASSWORD = "jq.database.password";
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String JQ_DATABASE_USERNAME = "jq.database.username";
	private static final String SELECT_FROM = "SELECT * FROM ";
	private static final String REPLACE_INTO = "REPLACE INTO ";
	private final String username;
	private final String password;
	private final String url;

	private Connection conn;
	private PreparedStatement stmt;
	private Class<T> class1;

	private int index = 0;

	public JQ(Class<T> class1) {
		this.class1 = class1;
		try {
		    Properties properties = new Properties();
			properties.load(getClass().getClassLoader().getResourceAsStream(JQ_PROPERTIES));
			this.username = properties.getProperty(JQ_DATABASE_USERNAME);
			this.password = properties.getProperty(JQ_DATABASE_PASSWORD);
			this.url = properties.getProperty(JQ_DATABASE_URL);

			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("PLease check JDBC dependancy");
		} catch (IOException e) {
			throw new RuntimeException("Le fichier de configuration doit être définit " + e.getMessage());
		}
	}

	private JQ<T> filter(String column, Object value) {
		try {
			this.initializeConnection();
			this.stmt = this.conn.prepareStatement(SELECT_FROM + getTableName() + " where " + column + " = ?");
			this.stmt.setObject(++index, value);
		} catch (Exception e) {
			close();
			throw new RuntimeException(e);
		}
		return this;
	}

	private void close() {
		try {
			index = 0;
			if (null != conn) {
				conn.close();
				this.conn = null;
			}
			if (null != stmt) {
				stmt.close();
				this.stmt = null;
			}
		} catch (SQLException e) {
			close();
			throw new RuntimeException(e);
		}
	}

	public void save(T e) {
		try {
			StringBuilder queryBuilder = new StringBuilder(REPLACE_INTO).append(getTableName());
			StringBuilder columnName = new StringBuilder();
			StringBuilder columnValue = new StringBuilder();

			String spacer = " (";
			for (Field f : class1.getDeclaredFields()) {
				f.setAccessible(true);

				columnName.append(spacer);
				columnName.append(f.getName());

				columnValue.append(spacer);
				columnValue.append("'" + f.get(e) + "'");
				spacer = " , ";
			}
			spacer = ")";
			queryBuilder.append(columnName).append(")");
			queryBuilder.append(" values ");
			queryBuilder.append(columnValue).append(")");
			initializeConnection();
			stmt = conn.prepareStatement(queryBuilder.toString());
			stmt.execute(queryBuilder.toString());
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			close();
		}
	}

	public void delete(T item) {
		try {
			initializeConnection();
			stmt = conn.prepareStatement(getDeleteQuery(item).toString());
			stmt.execute(getDeleteQuery(item).toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			close();
		}
	}

	private StringBuilder getDeleteQuery(T item) throws IllegalAccessException {
		StringBuilder queryBuilder = new StringBuilder("delete from ").append(getTableName());
		queryBuilder.append(getWhere(item));
		return queryBuilder;
	}

	private String getWhere(T item) throws IllegalAccessException {
		StringBuilder queryBuilder = new StringBuilder();
		String spacer = "";
		queryBuilder.append(" where ");
		for (Field f : class1.getDeclaredFields()) {
			f.setAccessible(true);
			queryBuilder.append(spacer);
			queryBuilder.append(f.getName()).append("=");
			queryBuilder.append("'" + f.get(item) + "'");
			spacer = " and ";
		}
		return queryBuilder.toString();
	}

	private void setFieldValue(ResultSet resultSet, T instance) throws IllegalAccessException, SQLException {
		for (Field f : class1.getDeclaredFields()) {
			f.setAccessible(true);
			f.set(instance, resultSet.getObject(getColumnName(f)));
		}
	}

	private String getColumnName(Field f) {
		Column annotation = f.getAnnotation(Column.class);
		return annotation == null ? f.getName() : annotation.name();
	}

	private String getTableName() {
		Table annotation = class1.getAnnotation(Table.class);
		return annotation == null ? class1.getSimpleName().toLowerCase() : annotation.name();
	}

	private void initializeStatement(String sql) throws SQLException {
		if (null == stmt) {
			stmt = conn.prepareStatement(sql);
		}
	}

	private void initializeConnection() throws SQLException {
		if (null == conn) {
			conn = DriverManager.getConnection(url, username, password);
		}
	}

	private ResultSet getResultSet() throws SQLException {
		if (stmt instanceof PreparedStatement) {
			return stmt.executeQuery();
		}
		return stmt.executeQuery(SELECT_FROM + getTableName());
	}

	public List<T> list() {
		List<T> items = new ArrayList<T>();
		try {
			initializeConnection();
			initializeStatement(SELECT_FROM + getTableName());
			ResultSet resultSet = getResultSet();
			while (resultSet.next()) {
				T instance = class1.newInstance();
				setFieldValue(resultSet, instance);
				items.add(instance);
			}
		} catch (Exception e) {
			close();
			throw new RuntimeException(e);
		} finally {
			close();
		}
		return items;
	}

	public T first() {
		return list().get(0);
	}

	public JQ<T> where(String columnName, String columnValue) {
		filter(columnName, columnValue);
		return this;
	}
}
