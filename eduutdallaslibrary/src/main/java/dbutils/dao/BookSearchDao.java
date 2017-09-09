package dbutils.dao;

import dbutils.DbConnectionHelper;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by ramya on 13/3/17.
 */
public class BookSearchDao {
    private static String Book_Search_Query = "select b.isbn, b.title, a.name, b.availability" +
            " from book as b join book_authors as ba on b.isbn = ba.isbn join authors as a on ba.author_id = a.author_id" +
            " where" +
            " b.isbn in (select isbn from book where title like ?)" +
            " OR" +
            " ba.isbn in (select ba.isbn from book_authors as ba where name like ? )" +
            " OR" +
            " b.isbn in (select isbn from book where isbn like ?);"
            ;

    public String[][] BookSearch(String searchString) throws SQLException {
        DbConnectionHelper dbConnectionHelper = DbConnectionHelper.getDbInstance();
        Connection conn = dbConnectionHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(Book_Search_Query);
        stmt.setString(1, "%"+searchString+"%");
        stmt.setString(2, "%"+searchString+"%");
        stmt.setString(3, searchString+"%");
        ArrayList<String[]> data = new ArrayList<String[]>();
        ResultSet rs = stmt.executeQuery();
        while(rs.next() && rs != null){
            String row[] = {rs.getString("isbn"), rs.getString("title"),rs.getString("name"), rs.getString("availability")};
            data.add(row);
//            System.out.println(rs.getString("isbn"));
//            System.out.println(rs.getString("title"));
//            System.out.println(rs.getString("name"));
        }

        String[][] tableData = new String[data.size()][];
        for (int i = 0; i < data.size(); i++) {
            String[] row = data.get(i);
            tableData[i] = row;
        }
        dbConnectionHelper.returnConnection(conn);
        return tableData;


    }
}
