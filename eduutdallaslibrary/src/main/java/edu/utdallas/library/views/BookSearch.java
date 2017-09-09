package edu.utdallas.library.views;

/**
 * Created by ramya on 13/3/17.
 */

import dbutils.dao.BookSearchDao;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;


public class BookSearch extends JFrame {

    private JTextField searchField;
    private JPanel panel;
    private JTable table;
    private JScrollPane scroll;
    private JLabel searchLabel;
    private JButton searchButton;
    private JButton checkOutButton;
    private JButton checkInButton;
    private JButton homePageButton;
    public static BookSearch bookSearch = null;

    public static BookSearch getInstance(){
        if(bookSearch == null)
            return new BookSearch();
        else
            return bookSearch;
    }

    public BookSearch() {

        bookSearch();
    }

    private void bookSearch() {

        panel = new JPanel();
        panel.setLayout(null);

        searchField = new JTextField();
        searchLabel = new JLabel("Search : ");
        searchButton = new JButton("Search");
        checkOutButton = new JButton("Checkout");
        checkInButton = new JButton("Checkin");
        homePageButton = new JButton("Home Page");


        homePageButton.setBounds(9, 30, 300, 30);
        searchLabel.setBounds(9, 80, 300, 30);
        searchField.setBounds(70, 80, 300, 30);
        searchButton.setBounds(400, 80, 100, 30);
        checkOutButton.setBounds(540, 80, 100, 30);
        checkInButton.setBounds(680, 80, 100, 30);
        homePageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                removeAll();
                HomePage.getInstance();
            }
        });

        searchButton.addActionListener(new ActionListener() {
            String[]  columnHeaders = {"ISBN","Title", "Author", "Availability"};
            Object  data[][] = null;
            public void actionPerformed(ActionEvent e) {


                String value = searchField.getText();
                if(value.equals("")) {
                    int option = JOptionPane.showOptionDialog(null, "Please enter book details ", "Alert", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                }
                else {
                    value = searchField.getText();
                    BookSearchDao bs = new BookSearchDao();
                    try {
                        data = bs.BookSearch(value);
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                    DefaultTableModel model = new DefaultTableModel(data, columnHeaders);
                    table = new JTable(model);


                    MouseListener tableMouseListener = new MouseAdapter() {

                        @Override
                        public void mouseClicked(MouseEvent e) {

                            final ArrayList<String> bookDetails = new ArrayList<>();
                            int row = table.rowAtPoint(e.getPoint());
                            bookDetails.add((String) data[row][0]);
                            bookDetails.add((String) data[row][3]);

//                            seletedISBN = new String[bookDetails.size()];

//                            seletedISBN[0] = (String) data[row][0];
//                            seletedISBN[1] = (String) data[row][3];
//
//                        for (int i = 0; i < data[row].length; i++) {
//                            System.out.println("Selected row at i : "+i);
//                            System.out.println(data[row][i]);
//                        }
                            checkOutButton.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    SwingUtilities.invokeLater(new Runnable() {

                                        public void run() {
                                            if(bookDetails.get(1).equals("unavailable"))
                                            {

                                                JOptionPane.showOptionDialog(null, "Selected book is unavailable", "Alert", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                                                setVisible(false);
                                                removeAll();
                                                BookSearch.getInstance();
                                            }

                                            else{
                                                setVisible(false);
                                                removeAll();
                                                CheckOut.getInstance(bookDetails.get(0));
                                            }
                                        }
                                    });
                                }
                            });
                            checkInButton.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    SwingUtilities.invokeLater(new Runnable() {

                                        public void run() {
                                            setVisible(false);
                                            removeAll();
                                            CheckIn.getInstance(bookDetails.get(0));
                                        }
                                    });
                                }
                            });
                        }
                    };


                    table.addMouseListener(tableMouseListener);

                    scroll = new JScrollPane(table);
                    scroll.setBounds(9, 200, 900, 500);
                    panel.add(scroll);


                }
            }
        });
        checkOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        String value = searchField.getText();
                        if(value.equals("") || value==null) {
                            int option = JOptionPane.showOptionDialog(null, "Please select the book to Check Out", "Alert", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                        }
                    }
                });
            }
        });
        checkInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        String value = searchField.getText();
                        if(value.equals("") || value==null) {
                            int option = JOptionPane.showOptionDialog(null, "Please select the book to Check In", "Alert", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                        }
                    }
                });
            }
        });
        panel.add(homePageButton);
        panel.add(searchLabel);
        panel.add(searchField);
        panel.add(searchButton);
        panel.add(checkOutButton);
        panel.add(checkInButton);

        getContentPane().add(panel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Book Search");
        setSize(900, 900);
        setLocationRelativeTo(null);
        setVisible(true);
    }



    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {

//                new BookSearch();
                BookSearch.getInstance();
            }
        });
    }
}
