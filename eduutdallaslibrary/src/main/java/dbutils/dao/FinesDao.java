package dbutils.dao;

import dbutils.DbConnectionHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by ramya on 16/3/17.
 */
public class FinesDao {
    String Late_Books_Out = "select loan_id from book_loans where due_date < curdate() and date_in = '1970-01-01'";
    String Late_Books_Returned = "select loan_id from book_loans where date_in != '1970-01-01' and due_date < curdate() and due_date < date_in";
    String Fine_Calculation_returned = "select datediff(date_in, due_date)*.25 as fine from book_loans where loan_id = ?";
    String Fine_Calculation_out = "select datediff(curdate(), due_date)*.25 as fine from book_loans where loan_id = ?";
    String Insert_Fines = "Insert into fines(loan_id, fine_amt) values(?,?)";
    String Update_Fines = "Update fines set fine_amt = ? where loan_id = ?";
    String Check_loanID_Fines = "select loan_id from fines where loan_id = ?";
    String Check_Loan_Paid = "select paid from fines where loan_id = ?";
    String Get_Loan_Amount = "select sum(f.fine_amt) as sum from fines as f, book_loans as bl " +
            "where bl.loan_id = f.loan_id and bl.card_id = ? and f.paid=0";
    String Make_Payment = "update fines as f join book_loans as bl on bl.loan_id = f.loan_id set paid = 1 where bl.card_id = ? and " +
            "bl.date_in != '1970-01-01' and bl.loan_id = ?";
    String Get_Loan_IDs = "select loan_id from book_loans where card_id = ?";
    String Get_Fine_Display = "select sum(f.fine_amt) as sum, bl.card_id, f.paid from fines as f join book_loans as bl on bl.loan_id = f.loan_id where f.paid = false group by bl.card_id";
    String Get_Fine_Display_All = "select sum(f.fine_amt) as sum, bl.card_id, f.paid from fines as f join book_loans as bl on bl.loan_id = f.loan_id where f.paid = true group by bl.card_id Union " +
            "select sum(f.fine_amt) as sum, bl.card_id, f.paid from fines as f join book_loans as bl on bl.loan_id = f.loan_id where f.paid = false group by bl.card_id";
    String Get_CardID_From_ISBN = "select bl.card_id from book_loans as bl, fines as f where bl.loan_id = f.loan_id and bl.isbn = ? and f.paid = 0";
    String Get_Books_Out_for_CardID = "select isbn from book_loans where card_id = ? and date_in = '1970-01-01'";

    public void calculateFineForLateBooksOut() throws SQLException {
        DbConnectionHelper dbConnectionHelper = DbConnectionHelper.getDbInstance();
        Connection con = dbConnectionHelper.getConnection();
        PreparedStatement stmt = con.prepareStatement(Late_Books_Out);
        ResultSet rs = stmt.executeQuery();
        ArrayList<String> loan_Ids = new ArrayList<String>();
        while(rs.next()){
            loan_Ids.add(rs.getString("loan_id"));
        }
        calculateFines(loan_Ids, false);
        dbConnectionHelper.returnConnection(con);
    }
    public String getFindCardIdFromISBN(String ISBN) throws SQLException {
        DbConnectionHelper dbConnectionHelper = DbConnectionHelper.getDbInstance();
        Connection con = dbConnectionHelper.getConnection();
        PreparedStatement stmt = con.prepareStatement(Get_CardID_From_ISBN);
        stmt.setString(1,ISBN);
        ResultSet rs = stmt.executeQuery();
        String card_ID = "";
        while(rs.next()){
            card_ID = rs.getString("card_id");
        }

        dbConnectionHelper.returnConnection(con);
        return card_ID;
    }
    public void calculateFineForLateBooksReturned() throws SQLException {
        DbConnectionHelper dbConnectionHelper = DbConnectionHelper.getDbInstance();
        Connection con = dbConnectionHelper.getConnection();
        PreparedStatement stmt = con.prepareStatement(Late_Books_Returned);
        ResultSet rs = stmt.executeQuery();
        ArrayList<String> loan_Ids = new ArrayList<String>();
        while(rs.next()){
            loan_Ids.add(rs.getString("loan_id"));
        }
        calculateFines(loan_Ids, true);
        dbConnectionHelper.returnConnection(con);
    }
    public double calculateFines(ArrayList<String> loan_ids, Boolean isBookReturned) throws SQLException {

        DbConnectionHelper dbConnectionHelper = DbConnectionHelper.getDbInstance();
        Connection con = dbConnectionHelper.getConnection();
        double fine = 0.0;
        PreparedStatement stmt;
        if(isBookReturned)
            stmt = con.prepareStatement(Fine_Calculation_returned);
        else
            stmt = con.prepareStatement(Fine_Calculation_out);
        for (String loan_id: loan_ids) {
//            stmt.setString(1, String.valueOf(13));
            System.out.println("Laon _ id"+loan_id);
            stmt.setString(1, loan_id);
            ResultSet rs = stmt.executeQuery();
            while(rs.next())
                fine = rs.getDouble("fine");
                insertOrUpdateFineTable(loan_id,fine);

        }
        dbConnectionHelper.returnConnection(con);
        return  fine;
    }

    public void insertOrUpdateFineTable(String loan_id, Double fine) throws SQLException {
        System.out.println("INSIDE FINE");
        DbConnectionHelper dbConnectionHelper = DbConnectionHelper.getDbInstance();
        Connection con = dbConnectionHelper.getConnection();
        if(!checkIfLoanIDPresentInFines(loan_id)){
            PreparedStatement stmt = con.prepareStatement(Insert_Fines);
            stmt.setString(1,loan_id);
            stmt.setDouble(2,fine);
            int rs = stmt.executeUpdate();
        }else if(!checkIfLoanIsPaid(loan_id)){
            PreparedStatement stmt = con.prepareStatement(Update_Fines);
            stmt.setDouble(1,fine);
            stmt.setString(2,loan_id);
            int rs = stmt.executeUpdate();
        }
        dbConnectionHelper.returnConnection(con);
    }
    public boolean checkIfLoanIDPresentInFines(String loan_id) throws SQLException {
        DbConnectionHelper dbConnectionHelper = DbConnectionHelper.getDbInstance();
        Connection con = dbConnectionHelper.getConnection();
        boolean isPresent = false;
        PreparedStatement stmt = con.prepareStatement(Check_loanID_Fines);
        stmt.setString(1,loan_id);
        ResultSet rs = stmt.executeQuery();
        while (rs.next())
            isPresent = true;
        dbConnectionHelper.returnConnection(con);
        return  isPresent;

    }
    public boolean checkIfLoanIsPaid(String loanId) throws SQLException {
        boolean paid = false;
        DbConnectionHelper dbConnectionHelper = DbConnectionHelper.getDbInstance();
        Connection con = dbConnectionHelper.getConnection();
        PreparedStatement stmt = con.prepareStatement(Check_Loan_Paid);
        stmt.setString(1,loanId);
        ResultSet rs = stmt.executeQuery();
        while(rs.next()){
            paid = rs.getBoolean("paid");
        }
        dbConnectionHelper.returnConnection(con);
        return paid;
    }

    public double getFineAmount(String cardId) throws SQLException {
        double fine = 0.0;
        DbConnectionHelper dbConnectionHelper = DbConnectionHelper.getDbInstance();
        Connection con = dbConnectionHelper.getConnection();
        PreparedStatement stmt = con.prepareStatement(Get_Loan_Amount);
        stmt.setString(1,cardId);
        ResultSet rs = stmt.executeQuery();
        while(rs.next()){
            fine = rs.getDouble("sum");
        }
        dbConnectionHelper.returnConnection(con);
        return fine;
    }

    public boolean makePaymentAfterCheckIn(String cardId) throws SQLException {
        DbConnectionHelper dbConnectionHelper = DbConnectionHelper.getDbInstance();
        Connection con = dbConnectionHelper.getConnection();
        ArrayList<String> loan_ids = getLoanIds(cardId);
        boolean notReturned = false;
        PreparedStatement stmt = con.prepareStatement(Make_Payment);
        for (String loan_id: loan_ids ) {

            stmt.setString(1,cardId);
            stmt.setString(2,loan_id);
            int rs = stmt.executeUpdate();
            if(rs == 0)
                notReturned = true;
        }
        dbConnectionHelper.returnConnection(con);
        return notReturned;
    }

    public boolean finepaymentforcardID(String cardId) throws SQLException {
        DbConnectionHelper dbConnectionHelper = DbConnectionHelper.getDbInstance();
        Connection con = dbConnectionHelper.getConnection();
        ArrayList<String> loan_ids = getLoanIds(cardId);
        boolean notReturned = false;

        for (String loan_id: loan_ids ) {
            if(checkIfLoanIsPaid(loan_id))
                loan_ids.remove(loan_id);
        }
        for (String loan_id: loan_ids ) {
            PreparedStatement stmt = con.prepareStatement(Make_Payment);
            stmt.setString(1,cardId);
            stmt.setString(2,loan_id);
            stmt.executeUpdate();

        }

        dbConnectionHelper.returnConnection(con);
        return notReturned;
    }
    public ArrayList<String> getBooksOutForCardId(String cardID) throws SQLException {
        ArrayList<String> isbnlist = new ArrayList<>();
        DbConnectionHelper dbConnectionHelper = DbConnectionHelper.getDbInstance();
        Connection con = dbConnectionHelper.getConnection();
        PreparedStatement stmt = con.prepareStatement(Get_Books_Out_for_CardID);
        stmt.setString(1,cardID);
        ResultSet rs = stmt.executeQuery();
        while(rs.next())
            isbnlist.add(rs.getString("isbn"));
        return isbnlist;
    }

    public ArrayList<String> getLoanIds(String cardID) throws SQLException {
        ArrayList<String> loan_ids = new ArrayList<String>();
        DbConnectionHelper dbConnectionHelper = DbConnectionHelper.getDbInstance();
        Connection con = dbConnectionHelper.getConnection();
        PreparedStatement stmt = con.prepareStatement(Get_Loan_IDs);
        stmt.setString(1,cardID);
        ResultSet rs = stmt.executeQuery();
        while(rs.next()){
            loan_ids.add(rs.getString("loan_id"));
        }
        dbConnectionHelper.returnConnection(con);
        return  loan_ids;
    }
    public String[][] getFineDisplayDetails(boolean all) throws SQLException {
        DbConnectionHelper dbConnectionHelper = DbConnectionHelper.getDbInstance();
        Connection con = dbConnectionHelper.getConnection();
        PreparedStatement stmt;
        if(all)
            stmt = con.prepareStatement(Get_Fine_Display_All);
        else
            stmt = con.prepareStatement(Get_Fine_Display);
        ResultSet rs = stmt.executeQuery();

        ArrayList<String[]> displayDetails = new ArrayList<String[]>();
        while(rs.next()){
            String[] row = {String.valueOf(rs.getDouble("sum")),rs.getString("card_id"), String.valueOf(rs.getBoolean("paid"))};
            displayDetails.add(row);
        }
        String[][] tableData = new String[displayDetails.size()][];
        for (int i = 0; i < displayDetails.size(); i++) {
            String[] row = displayDetails.get(i);
            tableData[i] = row;
        }
        dbConnectionHelper.returnConnection(con);
        return tableData;
    }


}
