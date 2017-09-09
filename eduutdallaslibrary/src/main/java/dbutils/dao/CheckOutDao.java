package dbutils.dao;

import dbutils.DbConnectionHelper;

import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ramya on 14/3/17.
 */
public class CheckOutDao {
    String Max_Count = "select count(*) from book_loans where card_id = ? and date_in = '1970-01-01' ";
    String Check_Availability = "select date_in from book_loans where isbn = ?";
    String Check_For_Fines = "select bl.loan_id from book_loans as bl join fines as f on bl.loan_id = f.loan_id where bl.card_id = ?";
    String Fine_Amount = "select fine_amt from fines where loan_id = ?";
    String Book_Loan_Insert = "Insert into book_loans (isbn, card_id, date_out, due_date) values (?,?, curdate(), date_add(date_out, interval 14 day)); ";
    String Book_Availability_Update = "update book set availability = ? where isbn = ?";

    public int getMaxCount(String cardId) throws SQLException {
        DbConnectionHelper dbConnectionHelper = DbConnectionHelper.getDbInstance();
        Connection conn = dbConnectionHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(Max_Count);
        stmt.setString(1, cardId);
        int count = -1;
        ResultSet rs = stmt.executeQuery();
        while(rs.next() && rs != null){
            count = rs.getInt("count(*)");
        }
        System.out.println("Count of cardID : "+cardId);
        dbConnectionHelper.returnConnection(conn);
        return count;
    }
    public double checkForExistingFines(String cardId) throws SQLException {
        DbConnectionHelper dbConnectionHelper = DbConnectionHelper.getDbInstance();
        Connection conn = dbConnectionHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(Check_For_Fines);
        stmt.setInt(1, Integer.parseInt(cardId));
        ArrayList<Integer> loan_ids = new ArrayList<Integer>();
        double fine =0;
        ResultSet rs = stmt.executeQuery();
        if(!rs.next()) {
            return fine;
        }else {
            rs.beforeFirst();
            while (rs.next()) {
                loan_ids.add(rs.getInt("loan_id"));
            }
        }

        for ( int loan_id:loan_ids) {
            PreparedStatement fine_stmt = conn.prepareStatement(Fine_Amount);
            fine_stmt.setInt(1, loan_id);

            ResultSet rs_fine = fine_stmt.executeQuery();
            if(!rs_fine.next())
                fine = fine+0;
            else
                    fine = fine+rs_fine.getDouble("fine_amt");
        }
        System.out.println("Fine : "+fine);
        dbConnectionHelper.returnConnection(conn);
        return fine;
    }

    public Date checkAvailability(String isbn) throws SQLException, ParseException {
        DbConnectionHelper dbConnectionHelper = DbConnectionHelper.getDbInstance();
        Connection conn = dbConnectionHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(Check_Availability);
        stmt.setString(1, isbn);
        Date date = new Date();
        ResultSet rs = stmt.executeQuery();
        if(!rs.next())
        {
//            String startDateString = "1970-01-01";
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");


//                date = df.parse(date);
                String newDateString = df.format(date);
                System.out.println(newDateString);
                System.out.println(date);

        }else {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            date = rs.getTimestamp("date_in");
            String dateString = df.format(date);
            System.out.println(dateString);
            date = df.parse(dateString);
            System.out.println(date);
        }
        System.out.println("Date : "+date);
        dbConnectionHelper.returnConnection(conn);
        return date;
    }
    public int checkOut(String isbn,int cardId) throws SQLException, ParseException {
        DbConnectionHelper dbConnectionHelper = DbConnectionHelper.getDbInstance();
        Connection conn = dbConnectionHelper.getConnection();
        System.out.println("ISBM "+isbn);
        System.out.println("CArd IF "+cardId);
        PreparedStatement stmt = conn.prepareStatement(Book_Loan_Insert);
        stmt.setString(1, isbn);
        stmt.setInt(2, cardId);
        int rs =stmt.executeUpdate();
        dbConnectionHelper.returnConnection(conn);
        return rs;
    }
    public void updateBookCheckOut(String isbn) throws SQLException, ParseException {
        DbConnectionHelper dbConnectionHelper = DbConnectionHelper.getDbInstance();
        Connection conn = dbConnectionHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(Book_Availability_Update);
        stmt.setString(1, "unavailable");
        stmt.setString(2, isbn);
        stmt.executeUpdate();
        dbConnectionHelper.returnConnection(conn);
    }
}
