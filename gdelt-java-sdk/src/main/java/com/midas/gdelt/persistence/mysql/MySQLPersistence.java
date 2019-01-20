package com.midas.gdelt.persistence.mysql;

import com.midas.gdelt.models.GdeltEventResource;
import com.midas.gdelt.persistence.Persistence;

import java.sql.*;
import java.util.Map;

public class MySQLPersistence implements Persistence {

    private String url;
    private int port;
    private String database;
    private String username;
    private String password;
    private String table;
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    public MySQLPersistence(String url, int port, String database, String username, String password) {
        this.url = url;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        System.out.print("INITIALIZED WITH " + url);
    }

    public void setTable(String table) {
        this.table = table;
    }


    public boolean persist(Map<String, Object> data) throws Exception {

        if(this.table == null) {
            throw new Exception("Error: No table for persistence selected!");
        }

        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw e;
        }

        try {
            // Setup the connection with the DB
            String connectionString = "jdbc:mysql://" + url + ":" + port + "/"+database;
            connect = DriverManager
                    .getConnection(connectionString, username, password);

            // Build the query
            String query = "INSERT INTO " + this.table;
            String columns = "(";
            String value_placeholders = "values(";

            String createTable = "CREATE TABLE Events (";
            String type = "";
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                columns = columns + " " + key + ",";
                value_placeholders = value_placeholders + " ?,";


                if(value instanceof Integer) {
                    type = "INT";
                } else if(value instanceof String) {
                    type = "varchar(100)";
                } else if(value instanceof Double) {
                    type = "DOUBLE(32, 24)";
                } else if(Class.forName("java.util.Date").isInstance(value)) {
                    type = "DATE()";
                }
                else if(value instanceof Boolean) {
                   type = "TINYINT(3)";
                }

                createTable = createTable + key + " " + type + ",\n";

            }

            createTable = createTable + ")";
            System.out.println(createTable);

            columns = columns.replaceAll(",$", "");
            value_placeholders = value_placeholders.replaceAll(",$", "");
            columns += ")";
            value_placeholders += ")";
            query = query + " " + columns + " " + value_placeholders;

            // Prepare the statement
            preparedStatement = connect.prepareStatement(query);
            int index = 1;
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if(value instanceof Integer) {
                    preparedStatement.setInt(index, (int) value);
                } else if(value instanceof String) {
                    preparedStatement.setString(index, (String) value);
                } else if(value instanceof Double) {
                    preparedStatement.setDouble(index, (double) value);
                } else if(Class.forName("java.util.Date").isInstance(value)) {
                    java.util.Date date = (java.util.Date) value;
                    java.sql.Date sqlDate = new java.sql.Date(date.getTime());
                    preparedStatement.setDate(index, sqlDate);
                }
                else if(value instanceof Boolean) {
                    preparedStatement.setBoolean(index, (Boolean) value);
                }

                if(index == 34) {
                    System.out.println(key);
                    System.out.println(value.getClass().getName());
                }

                index++;
            }


            // execute query, and return number of rows created
            int rowCount = preparedStatement.executeUpdate();
            System.out.println("rowCount=" + rowCount);

            /*
            // Statements allow to issue SQL queries to the database
            statement = connect.createStatement();
            // Result set get the result of the SQL query
            resultSet = statement
                    .executeQuery("select * from ged181 LIMIT 20");
            writeResultSet(resultSet);
            */

        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }





        return false;
    }


    private void writeResultSet(ResultSet resultSet) throws SQLException {
        // ResultSet is initially before the first data set
        while (resultSet.next()) {
            // It is possible to get the columns via name
            // also possible to get the columns via the column number
            // which starts at 1
            // e.g. resultSet.getSTring(2);
            String user = resultSet.getString("conflict_name");
            String website = resultSet.getString("dyad_name");
            System.out.println("Conflict: " + user);
            System.out.println("Dyad: " + website);
        }
    }


    // You need to close the resultSet
    private void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {

        }
    }




}
