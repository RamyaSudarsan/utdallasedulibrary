package dbutils;

import org.apache.commons.dbcp.BasicDataSource;


import java.sql.Connection;
import java.sql.SQLException;


/**
 * Created by ramya on 13/3/17.
 */

public class DbConnectionHelper {


    private static BasicDataSource dataSource;
    private final String username = "root";
    private final String password = "IndComp@123";
    private final String dbHostname = "localhost";
    private final String dbname = "library";
//    private final String dbname = "lcd1";
    public static DbConnectionHelper dbConnectionHelper;

    private BasicDataSource getDataSource()
    {
        if (dataSource == null)
        {
            BasicDataSource ds = new BasicDataSource();
            ds.setUrl("jdbc:mysql://localhost/"+dbname);
            ds.setUsername(username);
            ds.setPassword(password);
            ds.setMinIdle(5);
            ds.setMaxIdle(10);
            ds.setMaxOpenPreparedStatements(100);

            dataSource = ds;
            return  dataSource;
        }
        else
            return dataSource;
    }
    public DbConnectionHelper(){
        dataSource = this.getDataSource();
    }
    public static DbConnectionHelper getDbInstance(){
        if(dbConnectionHelper == null)
            return  new DbConnectionHelper();
        else
            return dbConnectionHelper;
    }
    public Connection getConnection() throws SQLException {

        return dataSource.getConnection();
    }
    public void returnConnection(Connection connection){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




}
