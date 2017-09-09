package edu.utdallas.library.views;

import dbutils.dao.CheckOutDao;
import dbutils.dao.FinesDao;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Created by ramya on 14/3/17.
 */
public class CheckOut extends JFrame{
    private JTextField cardIdField;
    private JLabel cardIdLabel;
    private JPanel panel;
    private JButton checkoutButton;
    private JButton checkinButton;
    private  JButton homePageButton;
    String bookSearchISBN = "-1";
    public static CheckOut checkOut = null;

    public static CheckOut getInstance(String i){
        if(checkOut == null)
            return new CheckOut(i);
        else
            return checkOut;
    }


//    public CheckOut(){
//    buildUI();
//
//    }
    public CheckOut(String i){
        bookSearchISBN = i;
        buildUI();
    }
    public void buildUI(){
        panel = new JPanel();
        cardIdField = new JTextField();
        cardIdLabel = new JLabel("Enter Card Id");
        checkoutButton = new JButton("Check Out");
        checkinButton = new JButton("Check In");
        panel.setLayout(null);
        homePageButton = new JButton("Home Page");
        homePageButton.setBounds(9, 30, 300, 30);
        homePageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                removeAll();
                HomePage.getInstance();
            }
        });
        cardIdLabel.setBounds(9, 80, 300, 30);
        cardIdField.setBounds(120, 80, 300, 30);
        checkoutButton.setBounds(450, 80, 120, 30);
        checkinButton.setBounds(450, 160, 120, 30);

            checkoutButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String cardId = cardIdField.getText();
                    if(bookSearchISBN.equals(""))
                    {
                        String[] options = {"OK"};
                        JPanel panel1 = new JPanel();
                        JLabel lbl = new JLabel("Enter the ISBN:");
                        JTextField txt = new JTextField(10);
                        panel1.add(lbl);
                        panel1.add(txt);
                        int option = JOptionPane.showOptionDialog(null, panel1, "ISBN Details", JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options , options[0]);
                        if(option == 0)
                            bookSearchISBN = txt.getText();
                    }

                    if (cardId.equals("") || cardId == null) {
                    JOptionPane.showOptionDialog(null, "Please enter the card details", "Alert", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                }else{
                        CheckOutDao checkOutDao = new CheckOutDao();
                        try {
                            if(checkOutDao.getMaxCount(cardId)<3)
                            {
                                String strDateFormat = "yyyy-MM-dd";
                                SimpleDateFormat dateFormat = new SimpleDateFormat(strDateFormat);
                                Date date = dateFormat.parse("1970-01-01");
                                checkOutDao.checkAvailability(bookSearchISBN);

                                if(!checkOutDao.checkAvailability(bookSearchISBN).equals(date))
                                {
                                    float fine = (float) checkOutDao.checkForExistingFines(cardId);
                                    if(fine == 0.0)
                                    {
                                        if(checkOutDao.checkOut(bookSearchISBN, Integer.parseInt(cardId))==1) {
                                            checkOutDao.updateBookCheckOut(bookSearchISBN);
                                            int option = JOptionPane.showOptionDialog(null, "Book was successfully checkout", "Alert", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

                                            if (option == 0) {
                                                setVisible(false);
                                                removeAll();
                                                BookSearch.getInstance();
                                            }
                                        }
                                    }else {
                                        int option = JOptionPane.showOptionDialog(null, "Please pay due fine amount $" + fine + " before checkout ", "Alert", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

                                        if (option == 0) {
                                            setVisible(false);
                                            removeAll();
                                            Fines.getInstance("");
//                                            FinesDao finesDao = new FinesDao();
//                                            finesDao.makePaymentAfterCheckIn(cardId);
                                        }
                                    }

                                }
                                else {
                                    int option = JOptionPane.showOptionDialog(null, "Selected Book is unavailable.  Please select another", "Alert", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                                    if(option == 0) {
                                        setVisible(false);
                                        removeAll();
                                        BookSearch.getInstance();
                                    }
                                }

                            }else{
                                int option = JOptionPane.showOptionDialog(null, "Three books checked out already.  Please return a book to checkout another", "Alert", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                                if(option == 0) {
                                    setVisible(false);
                                    removeAll();
                                    BookSearch.getInstance();
                                }
                            }


                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
            checkinButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                }
            });


        panel.add(cardIdField);
        panel.add(checkoutButton);
//        panel.add(checkinButton);
        panel.add(cardIdLabel);
        panel.add(homePageButton);
        getContentPane().add(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Check Out");
        setSize(600, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {

            CheckOut.getInstance("");
//            CheckOut.getInstance("9780060901012");

            }
        });
    }
}
