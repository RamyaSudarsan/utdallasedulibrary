package edu.utdallas.library.views;

import dbutils.dao.CheckInDao;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by ramya on 15/3/17.
 */
public class CheckIn extends JFrame {

    private JTextField searchField;

    private JPanel panel;
    private JLabel textLabel;
    private JTextField textField;
    private JButton checkInButton;
    private JButton homePageButton;

    public static CheckIn checkIn = null;
    String bookSearchISBN = "-1";

    public static CheckIn getInstance(String i){
        if(checkIn == null)
            return new CheckIn(i);
        else
            return checkIn;
    }

    public CheckIn(String isbn) {
        bookSearchISBN = isbn;
        checkIn();
    }

    private void checkIn() {
        panel = new JPanel();
        panel.setLayout(null);
        String criteria[]={"Lname,Fname","ISBN","Card Id"};
        final JComboBox cb=new JComboBox(criteria);
        cb.setBounds(10, 50,150,30);
        checkInButton = new JButton("Check In");
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
        checkInButton.setBounds(200, 50, 300, 30);
        textLabel = new JLabel("Please enter the details");
        textLabel.setBounds(9, 90, 300, 30);
        textField = new JTextField();
        final CheckInDao checkInDao = new CheckInDao();

//        try {
            cb.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ArrayList<String> checkinDetails = new ArrayList<>();
                    if(!bookSearchISBN.equals(""))
                        try {
                            checkinDetails =  checkInDao.getCheckinDetails(bookSearchISBN);
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }


                    String selectionCriteria = (String) cb.getItemAt(cb.getSelectedIndex());

                    if(selectionCriteria.equals("Lname,Fname"))
                    {
                        if(!bookSearchISBN.equals(""))
                            textField.setText(checkinDetails.get(3)+","+checkinDetails.get(2));
                        textField.setBounds(9, 120, 300, 30);
                        panel.add(textField);

                        checkInButton.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                String name = textField.getText();
                                String[] flname = name.split(",");
                                System.out.println("Name : "+name);
                                CheckInDao checkInDao = new CheckInDao();
                                if(name.equals("") || name==null)
                                    JOptionPane.showOptionDialog(null, "Text box is empty. Please enter the details", "Alert", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                                else
                                    try {
                                        ArrayList<String> isbnlist = checkInDao.getDetailsByName(flname[1],flname[0]);
                                        String cardId = checkInDao.getCardID(flname[1],flname[0]);
                                        System.out.println("card _ID" + cardId);
                                        if(isbnlist.size()==1)
                                        {
//
                                                checkInDao.updateBookLoans(cardId, isbnlist.get(0));
                                                checkInDao.updateBook(isbnlist.get(0));
                                                int option = JOptionPane.showOptionDialog(null, "Book was successfully checked in", "Alert", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                                                if(option == 0){
                                                    setVisible(false);
                                                    removeAll();
                                                    BookSearch.getInstance();
                                                }
//
                                        }else{

//
                                            String[] options = {"OK"};
                                            JPanel panel1 = new JPanel();
                                            JLabel lbl = new JLabel("Enter the ISBN:");
                                            JTextField txt = new JTextField(10);
                                            panel1.add(lbl);
                                            panel1.add(txt);
                                            int option = JOptionPane.showOptionDialog(null, panel1, "ISBN Details", JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options , options[0]);
                                            if(option == 0){
                                                String isbn = txt.getText();
                                                checkInDao.updateBookLoans(cardId, isbn);
                                                checkInDao.updateBook(isbn);
                                                int opt = JOptionPane.showOptionDialog(null, "Book was successfully checked in", "Alert", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                                                if(opt == 0){
                                                    setVisible(false);
                                                    removeAll();
                                                    BookSearch.getInstance();
                                                }
                                            }

                                        }
                                    } catch (SQLException e1) {
                                        e1.printStackTrace();
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }


                            }
                        });

                    }else if(selectionCriteria.equals("ISBN")){
                        if(!bookSearchISBN.equals(""))
                            textField.setText(checkinDetails.get(1));

                        textField.setBounds(9, 120, 300, 30);

                        panel.add(textField);
                        checkInButton.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                String ISBN = textField.getText();
                                System.out.println("ISBN : "+ISBN);
                                CheckInDao checkInDao = new CheckInDao();
                                if(ISBN.equals("") || ISBN==null)
                                    JOptionPane.showOptionDialog(null, "Text box is empty. Please enter the details", "Alert", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                                else
                                    try {
//
                                        String cardid = checkInDao.getCardIDFromIsbn(ISBN);
//
                                            checkInDao.updateBookLoans(cardid, ISBN);
                                            checkInDao.updateBook(ISBN);
                                            int option = JOptionPane.showOptionDialog(null, "Book was successfully checked in", "Alert", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                                            if(option == 0){
                                                setVisible(false);
                                                removeAll();
                                                BookSearch.getInstance();
                                            }
//
                                    } catch (SQLException e1) {
                                        e1.printStackTrace();
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }


                            }
                        });

                    }else if(selectionCriteria.equals("Card Id")){
                        if(!bookSearchISBN.equals(""))
                            textField.setText(checkinDetails.get(0));
                        textField.setBounds(9, 120, 300, 30);
                        panel.add(textField);
                        checkInButton.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                String cardId = textField.getText();
                                System.out.println("CArd ID : "+cardId);
                                CheckInDao checkInDao = new CheckInDao();
                                if(cardId.equals("") || cardId==null)
                                    JOptionPane.showOptionDialog(null, "Text box is empty. Please enter the details", "Alert", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                                else
                                    try {
                                        ArrayList<String> isbnlist = checkInDao.getDetailsBycardId(cardId);

                                        if(isbnlist.size()==1)
                                        {
//
                                                checkInDao.updateBookLoans(cardId, isbnlist.get(0));
                                                checkInDao.updateBook(isbnlist.get(0));
                                                int option = JOptionPane.showOptionDialog(null, "Book was successfully checked in", "Alert", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                                                if(option == 0){
                                                    setVisible(false);
                                                    removeAll();
                                                    BookSearch.getInstance();
                                                }
//

                                        }else{

//                                            String isbn = JOptionPane.showInputDialog(null, "Enter the ISBN");
                                            String[] options = {"OK"};
                                            JPanel panel1 = new JPanel();
                                            JLabel lbl = new JLabel("Enter the ISBN:");
                                            JTextField txt = new JTextField(10);
                                            panel1.add(lbl);
                                            panel1.add(txt);
                                            int option = JOptionPane.showOptionDialog(null, panel1, "ISBN Details", JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options , options[0]);
                                            if(option == 0){
                                                String isbn = txt.getText();
                                                checkInDao.updateBookLoans(cardId, isbn);
                                                checkInDao.updateBook(isbn);
                                                int opt = JOptionPane.showOptionDialog(null, "Book was successfully checked in", "Alert", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                                                if(opt == 0){
                                                    setVisible(false);
                                                    removeAll();
                                                    BookSearch.getInstance();
                                                }
                                            }
//

//
                                        }
                                    } catch (SQLException e1) {
                                        e1.printStackTrace();
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }


                            }
                        });

                    }
                }
            });


        checkInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {

                        if(textField.getText().equals("")) {
                            int option = JOptionPane.showOptionDialog(null, "Please select the book details", "Alert", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                        }
                    }
                });
            }
        });
        panel.add(cb);
        panel.add(homePageButton);
        panel.add(checkInButton);
        panel.add(textLabel);
        getContentPane().add(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("CheckIn Books");
        setSize(900, 900);
        setLocationRelativeTo(null);
        setVisible(true);
//
    }
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {ArrayList<String> checkinDetails = null;

//                CheckIn.getInstance("9780060901011");
                CheckIn.getInstance("");
//            CheckOut.getInstance("9780060901012");

            }
        });
    }
}

