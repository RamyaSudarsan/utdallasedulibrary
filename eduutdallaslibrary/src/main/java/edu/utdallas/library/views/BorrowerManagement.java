package edu.utdallas.library.views;

import dbutils.dao.BorrowerManagementDao;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * Created by ramya on 16/3/17.
 */
public class BorrowerManagement extends JFrame {

    private JPanel panel;
    private JLabel textLabel;
    private JLabel fNametextLabel;
    private JTextField fNametextField;
    private JLabel lNametextLabel;
    private JTextField lNametextField;
    private JLabel ssntextLabel;
    private JTextField ssntextField;
    private JLabel citytextLabel;
    private JTextField citytextField;
    private JLabel statetextLabel;
    private JTextField statetextField;
    private JLabel addresstextLabel;
    private JTextField addresstextField;
    private JLabel phonetextLabel;
    private JTextField phonetextField;
    private JButton addButton;
    private JButton homePageButton;

    public static BorrowerManagement borrower = null;


    public static BorrowerManagement getInstance(){
        if(borrower == null)
            return new BorrowerManagement();
        else
            return borrower;
    }

    public BorrowerManagement() {

        borrowerManagement();
    }

    private void borrowerManagement() {

        panel = new JPanel();
        panel.setLayout(null);
        homePageButton = new JButton("Home Page");
        homePageButton.setBounds(9, 50, 300, 30);
        homePageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                removeAll();
                HomePage.getInstance();
            }
        });
        textLabel = new JLabel("Please enter the details");
        textLabel.setBounds(9, 90, 300, 30);
        fNametextLabel = new JLabel("First Name : ");
        fNametextLabel.setBounds(9, 120, 300, 30);
        fNametextField = new JTextField();
        fNametextField.setBounds(320, 120, 300, 30);

        lNametextLabel = new JLabel("Last Name : ");
        lNametextLabel.setBounds(9, 160, 300, 30);
        lNametextField = new JTextField();
        lNametextField.setBounds(320, 160, 300, 30);

        ssntextLabel = new JLabel("SSN : ");
        ssntextLabel.setBounds(9, 200, 300, 30);
        ssntextField = new JTextField();
        ssntextField.setBounds(320, 200, 300, 30);

        addresstextLabel = new JLabel("Address : ");
        addresstextLabel.setBounds(9, 240, 300, 30);
        addresstextField = new JTextField();
        addresstextField.setBounds(320, 240, 300, 30);

        citytextLabel = new JLabel("City : ");
        citytextLabel.setBounds(9, 280, 300, 30);
        citytextField = new JTextField();
        citytextField.setBounds(320, 280, 300, 30);

        statetextLabel = new JLabel("State : ");
        statetextLabel.setBounds(9, 320, 300, 30);
        statetextField = new JTextField();
        statetextField.setBounds(320, 320, 300, 30);

        phonetextLabel = new JLabel("Phone : ");
        phonetextLabel.setBounds(9, 360, 300, 30);
        phonetextField = new JTextField();
        phonetextField.setBounds(320, 360, 300, 30);

        addButton = new JButton("Add");
        addButton.setBounds(320 , 400, 300,30);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String fname = fNametextField.getText();
                String lname = lNametextField.getText();
                String ssn = ssntextField.getText();
                String address = addresstextField.getText();
                String city = citytextField.getText();
                String state = statetextField.getText();
                String phone = phonetextField.getText();
                if(fname.equals("") || fname==null || lname.equals("") || lname==null || ssn.equals("") || ssn==null ){
                    JOptionPane.showOptionDialog(null, "Name and SSN are mandatory fields. Please enter the details", "Alert", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                }else
                {
                    BorrowerManagementDao borrowerManagementDao = new BorrowerManagementDao();
                    try {
                        borrowerManagementDao.addBorrower(ssn,fname,lname,address,city,state,phone);
                        JOptionPane.showOptionDialog(null, "Borrower successfully added.", "Alert", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    );

//        addButton = new JButton("Add");
//        addButton.setBounds(200, 50, 300, 30);

        panel.add(textLabel);
        panel.add(fNametextField);
        panel.add(fNametextLabel);
        panel.add(lNametextField);
        panel.add(lNametextLabel);
        panel.add(ssntextField);
        panel.add(ssntextLabel);
        panel.add(addresstextField);
        panel.add(addresstextLabel);
        panel.add(citytextField);
        panel.add(citytextLabel);
        panel.add(statetextField);
        panel.add(statetextLabel);
        panel.add(phonetextField);
        panel.add(phonetextLabel);
        panel.add(addButton);
        panel.add(homePageButton);
        getContentPane().add(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Borrower Management");
        setSize(900, 900);
        setLocationRelativeTo(null);
        setVisible(true);


    }
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {

                BorrowerManagement.getInstance();
//            CheckOut.getInstance("9780060901012");

            }
        });
    }

}
