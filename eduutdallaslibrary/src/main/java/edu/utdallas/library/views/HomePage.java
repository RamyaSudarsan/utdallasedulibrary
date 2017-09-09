package edu.utdallas.library.views;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by ramya on 17/3/17.
 */
public class HomePage extends JFrame{
    
    private JPanel panel;
    private JButton bookSearchButton;
    private JButton checkOutButton;
    private JButton checkInButton;
    private JButton borrowerButton;
    private JButton fineButton;
    public static HomePage homePage = null;

    public static HomePage getInstance(){
        if(homePage == null)
            return new HomePage();
        else
            return homePage;
    }


    public HomePage() {
        buildUI();
    }

    private void buildUI() {
        panel = new JPanel();



        bookSearchButton = new JButton("Book Search");
        checkOutButton = new JButton("Checkout");
        checkInButton = new JButton("Checkin");
        borrowerButton = new JButton("Borrower");
        fineButton = new JButton("Fine");

        bookSearchButton.setBounds(9,80,300, 30);
        checkInButton.setBounds(9,120,300, 30);
        checkOutButton.setBounds(9,160,300, 30);
        borrowerButton.setBounds(9,200,300, 30);
        fineButton.setBounds(9,240,300, 30);

        bookSearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                removeAll();
                BookSearch.getInstance();
            }
        });

        checkInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                removeAll();
                CheckIn.getInstance("");
            }
        });

        checkOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                removeAll();
                CheckOut.getInstance("");
            }
        });

        borrowerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                removeAll();
                BorrowerManagement.getInstance();
            }
        });

        fineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                removeAll();
                Fines.getInstance("");
            }
        });


        panel.setLayout(null);
        panel.add(bookSearchButton);
        panel.add(checkOutButton);
        panel.add(checkInButton);
        panel.add(borrowerButton);
        panel.add(fineButton);

        getContentPane().add(panel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Home Page");
        setSize(900, 900);
        setLocationRelativeTo(null);
        setVisible(true);

    }
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {

//                new HomePage();
                homePage.getInstance();
            }
        });
    }
}
