package dbutils.dao;

import dbutils.DbConnectionHelper;

//import java.sql.*;
import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by ramya on 15/3/17.
 */
public class CheckInDao {
    String Book_ISBN = "select b.isbn, b.title, bl.card_id , bl.date_out from " +
            "book_loans as bl join borrower as br on bl.card_id = br.card_id " +
            "join book as b on bl.isbn = b.isbn where bl.isbn = ?";
    String Book_CardId = "select b.isbn, b.title, bl.card_id, bl.date_out from " +
            "book_loans as bl join borrower as br on bl.card_id = br.card_id join book as b on bl.isbn = b.isbn where br.card_id = ?";
    String Book_Name = "select b.isbn, b.title, bl.card_id ,bl.date_out from " +
            "book_loans as bl join borrower as br on bl.card_id = br.card_id " +
            "join book as b on bl.isbn = b.isbn where br.fname = ? AND br.lname = ?";
    String update_Book_Loans = "update book_loans set date_in = curdate() where isbn = ? and card_id = ?";
    String Book_Availability_Update = "update book set availability = ? where isbn = ?";
    String Check_For_Fines = "select bl.loan_id from book_loans as bl join fines as f on bl.loan_id = f.loan_id where bl.card_id = ? and f.paid=0";
    String Fine_Amount = "select fine_amt from fines where loan_id = ?";
    String Card_ID = "select card_id from borrower where fname=? and lname=?";
    String Get_CardId_ISBN = "select br.card_id from borrower as br, book_loans as bl where bl.card_id = br.card_id and bl.isbn = ?";
    String Get_Checkin_Details = "select bl.card_id, br.lname,br.fname ,bl.isbn from book_loans as bl, borrower as br where bl.card_id = br.card_id and isbn = ?";
    String isbn="";
    String cardid="";
    Date date= null;
    public double getDetailsByISBN (String ISBN) throws SQLException, ParseException {
        DbConnectionHelper dbConnectionHelper = DbConnectionHelper.getDbInstance();
        Connection conn = dbConnectionHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(Book_ISBN);
        stmt.setString(1, ISBN);
        ResultSet rs = stmt.executeQuery();
        while(rs.next() && rs != null){
            isbn = rs.getString("isbn");
            cardid = rs.getString("card_id");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            date = rs.getTimestamp("date_out");
            System.out.println("date string before formatinf"+date);
            String dateString = df.format(date);
            System.out.println("date string after format"+dateString);
            date = df.parse(dateString);
            System.out.println("date string"+date);
        }
        double fine = checkForExistingFines(cardid);
        dbConnectionHelper.returnConnection(conn);
        return fine;
    }
    public ArrayList<String> getDetailsBycardId (String cardid) throws SQLException, ParseException {
        DbConnectionHelper dbConnectionHelper = DbConnectionHelper.getDbInstance();
        Connection conn = dbConnectionHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(Book_CardId);
        stmt.setString(1, cardid);
        ResultSet rs = stmt.executeQuery();
        ArrayList<String> isbnList = new ArrayList<String>();
        while(rs.next() && rs != null){
            isbn = rs.getString("isbn");
            isbnList.add(isbn);
            cardid = rs.getString("card_id");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            date = rs.getTimestamp("date_out");
            System.out.println("date string before formatinf"+date);
            String dateString = df.format(date);
            System.out.println("date string after format"+dateString);
            date = df.parse(dateString);
            System.out.println("date string"+date);
        }

//        double fine = checkForExistingFines(cardid);
/*
        if(fine ==0.0) {

        }else
            JOptionPane.showOptionDialog(null, "Please pay due fine amount $"+fine+" before check in ", "Alert", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);*/
        dbConnectionHelper.returnConnection(conn);
        return isbnList;
    }
    public ArrayList<String> getDetailsByName (String fname, String lname) throws SQLException, ParseException {
        DbConnectionHelper dbConnectionHelper = DbConnectionHelper.getDbInstance();
        Connection conn = dbConnectionHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(Book_Name);
        stmt.setString(1, fname);
        stmt.setString(2, lname);
        ResultSet rs = stmt.executeQuery();
        ArrayList<String> isbnList = new ArrayList<String>();
        while(rs.next() && rs != null){
            isbn = rs.getString("isbn");
            isbnList.add(isbn);
            cardid = rs.getString("card_id");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            date = rs.getTimestamp("date_out");
            System.out.println("date string before formatinf"+date);
            String dateString = df.format(date);
            System.out.println("date string after format"+dateString);
            date = df.parse(dateString);
            System.out.println("date string"+date);
        }

       /* double fine = checkForExistingFines(cardid);

        if(fine ==0.0) {

        }else
            JOptionPane.showOptionDialog(null, "Please pay due fine amount $"+fine+" before check in ", "Alert", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
       */ dbConnectionHelper.returnConnection(conn);
        return isbnList;
    }
    public void updateBookLoans(String cardid, String isbn) throws SQLException, ParseException {
        DbConnectionHelper dbConnectionHelper = DbConnectionHelper.getDbInstance();
        Connection conn = dbConnectionHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(update_Book_Loans);
        stmt.setString(1,isbn);
        stmt.setString(2,cardid);
        int update = stmt.executeUpdate();
        if(update ==  1)
            updateBook(isbn);
        dbConnectionHelper.returnConnection(conn);
    }
    public void updateBook(String isbn) throws SQLException, ParseException {
        DbConnectionHelper dbConnectionHelper = DbConnectionHelper.getDbInstance();
        Connection conn = dbConnectionHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(Book_Availability_Update);
        stmt.setString(1, "available");
        stmt.setString(2, isbn);
        stmt.executeUpdate();
        dbConnectionHelper.returnConnection(conn);
    }
    public String getCardID(String fname, String lname) throws SQLException, ParseException {
        DbConnectionHelper dbConnectionHelper = DbConnectionHelper.getDbInstance();
        Connection conn = dbConnectionHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(Card_ID);
        stmt.setString(1, fname);
        stmt.setString(2, lname);
        ResultSet rs = stmt.executeQuery();
        String card_id = "";
        while(rs.next() && rs != null){
            card_id = rs.getString("card_id");
        }
        dbConnectionHelper.returnConnection(conn);
        return card_id;
    }
    public String getCardIDFromIsbn(String isbn) throws SQLException, ParseException {
        DbConnectionHelper dbConnectionHelper = DbConnectionHelper.getDbInstance();
        Connection conn = dbConnectionHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(Get_CardId_ISBN);
        stmt.setString(1, isbn);
        ResultSet rs = stmt.executeQuery();
        String card_id = "";
        while(rs.next() && rs != null){
            card_id = rs.getString("card_id");
        }
        dbConnectionHelper.returnConnection(conn);
        return card_id;
    }
    public ArrayList<String> getCheckinDetails(String isbn) throws SQLException, ParseException {
        DbConnectionHelper dbConnectionHelper = DbConnectionHelper.getDbInstance();
        Connection conn = dbConnectionHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(Get_Checkin_Details);
        stmt.setString(1, isbn);
        ResultSet rs = stmt.executeQuery();
        ArrayList<String> checkInDetails = new ArrayList<>();
        while(rs.next() && rs != null){
            checkInDetails.add(rs.getString("card_id"));
            checkInDetails.add(rs.getString("isbn"));
            checkInDetails.add(rs.getString("fname"));
            checkInDetails.add(rs.getString("lname"));

        }
        for (String search: checkInDetails) {
            System.out.println("Check in Details"+search);
        }
        dbConnectionHelper.returnConnection(conn);
        return checkInDetails;
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
                fine = fine + 0;
            else
                fine = fine + rs_fine.getDouble("fine_amt");
        }
        dbConnectionHelper.returnConnection(conn);
        return fine;
    }
}
