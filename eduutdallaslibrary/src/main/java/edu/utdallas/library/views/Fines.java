package edu.utdallas.library.views;

import dbutils.dao.FinesDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by ramya on 16/3/17.
 */
public class Fines extends JFrame {
    public static Fines fines = null;
    JLabel cardIDLabel;
    JLabel updateFinesLabel;
    JTextField cardIDTextField;
    JButton updateFinesDetails;
    JButton updateFinesDetailsAll;
    JButton payFine;
    JButton homePageButton;
    String bookSearchISBN ;

    private JTable table;
    private JScrollPane scroll;

    JPanel panel;

    public static Fines getInstance(String bookSearchISBN){
        if(fines == null)
            return new Fines(bookSearchISBN);
        else
            return fines;
    }

    public Fines(String bookSearchISBN){
        this.bookSearchISBN = bookSearchISBN;
        buildUI();
    }

    private void buildUI() {
        panel = new JPanel();
        panel.setLayout(null);
        updateFinesLabel = new JLabel("Updated Fine Amount");
        updateFinesDetails = new JButton("Display Unpaid Fines");
        updateFinesDetailsAll = new JButton("Display Fines All");
        updateFinesLabel.setBounds(9,50,200,30);
        updateFinesDetails.setBounds(330,50,200,30);
        updateFinesDetailsAll.setBounds(650,50,200,30);
        homePageButton = new JButton("Home Page");
        homePageButton.setBounds(9, 10, 300, 30);
        homePageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                removeAll();
                HomePage.getInstance();
            }
        });
        cardIDLabel = new JLabel("Please enter the cardId");
        cardIDLabel.setBounds(9, 100, 200, 30);
        cardIDTextField = new JTextField();
        cardIDTextField.setBounds(330, 100,200,30);
        final FinesDao finesDao = new FinesDao();
        try {
            if(!bookSearchISBN.equals("") || bookSearchISBN!=null)
            cardIDTextField.setText(finesDao.getFindCardIdFromISBN(bookSearchISBN));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        payFine = new JButton("Pay");
        payFine.setBounds(330, 140,200,30);
        payFine.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String cardId = cardIDTextField.getText();
                if(cardId.equals(""))
                    JOptionPane.showOptionDialog(null, "Please Enter Card Id", "Alert", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                else {
                    try {
                        double fine = finesDao.getFineAmount(cardId);
                        if (fine != 0.0) {
                            int option = JOptionPane.showOptionDialog(null, "Please pay due fine amount $" + fine, "Alert", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                            if (option == 0) {
                                ArrayList<String> isbnNotReturned = finesDao.getBooksOutForCardId(cardId);
                                if (isbnNotReturned.size() > 0) {
                                    for (String isbn : isbnNotReturned) {
                                        CheckIn.getInstance(isbn);
                                    }
                                } else if (finesDao.makePaymentAfterCheckIn(cardId)) {
                                    int option1 = JOptionPane.showOptionDialog(null, "Please return the book before paying the fine" + fine, "Alert", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                                    if (option1 == 0) {
                                        setVisible(false);
                                        removeAll();
//                                CheckIn.getInstance(bookSearchISBN);
                                    }
                                }
                            }
                        } else
                            JOptionPane.showOptionDialog(null, "Fine Amount is Nill", "Alert", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        updateFinesDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {

                    finesDao.calculateFineForLateBooksOut();
                    finesDao.calculateFineForLateBooksReturned();

                    String[]  columnHeaders = {"Fine Amount","Card Id", "Paid"};
                    String[][] data = finesDao.getFineDisplayDetails(false);
                    DefaultTableModel model = new DefaultTableModel(data, columnHeaders);
                    table = new JTable(model);
                    scroll = new JScrollPane(table);
                    scroll.setBounds(9, 300, 900, 500);

                    panel.add(scroll);

                } catch (SQLException e2) {
                    e2.printStackTrace();
                }

            }
        });
        updateFinesDetailsAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    finesDao.calculateFineForLateBooksOut();
                    finesDao.calculateFineForLateBooksReturned();
                    String[]  columnHeaders = {"Fine Amount","Card Id", "Paid"};
                    String[][] data = finesDao.getFineDisplayDetails(true);
                    DefaultTableModel model = new DefaultTableModel(data, columnHeaders);
                    table = new JTable(model);
                    scroll = new JScrollPane(table);
                    scroll.setBounds(9, 300, 900, 500);

                    panel.add(scroll);

                } catch (SQLException e2) {
                    e2.printStackTrace();
                }

            }
        });


        panel.add(updateFinesDetails);
        panel.add(updateFinesDetailsAll);
        panel.add(updateFinesLabel);
        panel.add(cardIDLabel);
        panel.add(cardIDTextField);
        panel.add(payFine);
        panel.add(homePageButton);
        getContentPane().add(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Fines");
        setSize(900, 900);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {

//                Fines.getInstance();
            Fines.getInstance("");

            }
        });
    }


}
