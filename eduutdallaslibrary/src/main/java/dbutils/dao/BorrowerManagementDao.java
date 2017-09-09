package dbutils.dao;

import dbutils.DbConnectionHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * Created by ramya on 16/3/17.
 */
public class BorrowerManagementDao {
    String Borrower_Insert = "Insert into borrower (ssn, fname, lname, address, city, state, phone) " +
            "values (?,?,?,?,?,?,?); ";

    public void addBorrower(String ssn,String fname,String lname, String address,String city, String state,String phone) throws SQLException, ParseException {
        DbConnectionHelper dbConnectionHelper = DbConnectionHelper.getDbInstance();
        Connection conn = dbConnectionHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(Borrower_Insert);
        stmt.setString(1, ssn);
        stmt.setString(2,fname);
        stmt.setString(3,lname);
        stmt.setString(4,address);
        stmt.setString(5,city);
        stmt.setString(6,state);
        stmt.setString(7,phone);
        stmt.executeUpdate();
//        return stmt.executeUpdate();
        dbConnectionHelper.returnConnection(conn);
    }
}
