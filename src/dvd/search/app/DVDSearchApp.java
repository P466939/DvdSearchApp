package dvd.search.app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import javax.swing.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

public class DVDSearchApp extends JFrame {
    
    JTextField txtTitle;
    JButton butSearch;
    JList list;
    JScrollPane scrollResults;
    ArrayList <String> dvdList;
    
    final String mainClass = DVDSearchApp.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    private final String configFilename = "./config.properties";
    private final String query = "SELECT Title FROM Dvds ORDER BY Id;";
    private final String defaultDbDriver = "org.sqlite.JDBC";
    private final String defaultDbConString = "jdbc:sqlite:./sqlite.db";
    
    public DVDSearchApp() {
        
        setTitle("DVD Search App");
        setLayout(null);
        
        DefaultListModel dlm = new DefaultListModel();
        list = new JList(dlm);
        dvdList = new ArrayList();
        txtTitle = new JTextField();
        txtTitle.setBounds(30, 30, 150, 25);
        butSearch = new JButton("Title Search");
        butSearch.setBounds(200, 30, 120, 25);
        scrollResults = new JScrollPane(list);
        scrollResults.setBounds(30, 85, 290, 150);
        
        try {
            //load app config from file
            Properties prop = new Properties();
            //InputStream configStream = getClass().getClassLoader()
            //        .getResourceAsStream(configFilename);
            //if (configStream==null)
            //    throw new FileNotFoundException("Configuration file '"+ configFilename + "' not found.");
            InputStream in;
            
            
            try {
                in = new FileInputStream(configFilename);
            } catch (Exception e) {
                //if the properties file could not be opened, use this dummy stream to get defaults
                in = new ByteArrayInputStream(new byte[0]);
            }
            
            prop.load(in);
            
            //load database config from config file
            String dbDriver = prop.getProperty("db.driver", defaultDbDriver);
            String dbConString = prop.getProperty("db.connecton", defaultDbConString);
            String dbUser = prop.getProperty("db.username");
            String dbPassword = prop.getProperty("db.password", "");
            
            //connect to database
            if (dbDriver != null)
                Class.forName(dbDriver);
            
            Connection con = (dbUser == null) ?
                    DriverManager.getConnection(dbConString) :
                    DriverManager.getConnection(dbConString, dbUser, dbPassword);
            
            //load DVD data from database
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);
            
            while (rs.next()) {
                dvdList.add(rs.getString(1));
            }
            
            con.close();
            
        } catch (Exception e) {
            txtTitle.setText("Failed to Build DVD List");
            System.out.println(e);
        }
        
        butSearch.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    dlm.removeAllElements();
                    
                    for (String curVal : dvdList) {
                        String tempVal = curVal.toLowerCase();
                        if(tempVal.contains(txtTitle.getText().toLowerCase())) {
                            dlm.addElement(curVal);
                        }
                    }
                } catch (Exception e) {
                    txtTitle.setText("Something Went Wrong!");
                    System.out.println(e);
                }
            }
        });
        
        add(txtTitle);
        add(butSearch);
        add(scrollResults);
                
        setSize(360, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new DVDSearchApp();
    }
}
