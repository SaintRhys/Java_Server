package com.example;

import java.sql.*;

public class DatabaseSetup {
        public static void main(String args[]) {
                // setup database, create the tables, input some rows
                Connection c = null;
                Statement stmt = null;

                try {
                        Class.forName("org.sqlite.JDBC");
                        c = DriverManager.getConnection("jdbc:sqlite:saxonDb.db"); // connect to server
                        System.out.println("Created database successfully");

                        // regions
                        stmt = c.createStatement(); // new statement
                        String sql1 = "CREATE TABLE regions " +
                                        "(ID STRING PRIMARY KEY     NOT NULL," +
                                        " NAME           TEXT    NOT NULL)"; // sql query
                        stmt.executeUpdate(sql1); // execute statement

                        System.out.println("regions table created successfully");

                        String insertQuery1 = "INSERT INTO regions (ID,NAME) " +
                                        "VALUES (1, 'London') " +
                                        ", (2, 'Midlands') " +
                                        ", (3, 'North East') " +
                                        ", (4, 'North West') " +
                                        ", (5, 'South East') " +
                                        ", (6, 'South West') ;"; // sql query
                        stmt.executeUpdate(insertQuery1); // execute statement

                        System.out.println("rows added to regions successfully");

                        // campaigns
                        String sql2 = "CREATE TABLE campaigns " +
                                        "(ID STRING PRIMARY KEY     NOT NULL," +
                                        " SITE_ID          STRING    NOT NULL, " +
                                        " REGION_ID          STRING    NOT NULL, " +
                                        " LENGTH        INT NOT NULL)"; // sql query
                        stmt.executeUpdate(sql2); // execute statement

                        System.out.println("campaigns table created successfully");

                        String insertQuery2 = "INSERT INTO campaigns (ID,SITE_ID,REGION_ID,LENGTH) " +
                                        "VALUES (1, '1', '6', 30) " +
                                        ", (2, '6', '2', 14) " +
                                        ", (3, '1', '4', 7) " +
                                        ", (4, '5', '6', 30) " +
                                        ", (5, '2', '1', 60) " +
                                        ", (6, '3', '3', 7) ;"; // sql query
                        stmt.executeUpdate(insertQuery2); // execute statement

                        System.out.println("rows added to campaigns successfully");

                        // sites
                        String sql3 = "CREATE TABLE sites " +
                                        "(ID STRING PRIMARY KEY     NOT NULL," +
                                        " NAME           TEXT    NOT NULL);";
                        stmt.executeUpdate(sql3); // execute statement

                        System.out.println("sites table created successfully");

                        String insertQuery3 = "INSERT INTO sites (ID,NAME) " +
                                        "VALUES (1, 'Buildings'), " +
                                        "(2, 'Castles') " +
                                        ", (3, 'Cities') " +
                                        ", (4, 'Gardens') " +
                                        ", (5, 'Landscapes') " +
                                        ", (6, 'Monuments') ;"; // sql query
                        stmt.executeUpdate(insertQuery3); // execute statement

                        System.out.println("rows added to sites successfully");

                        // region sites
                        String sql4 = "CREATE TABLE region_sites " +
                                        "(ID STRING PRIMARY KEY     NOT NULL," +
                                        "REGION_ID STRING NOT NULL," +
                                        "SITE_ID STRING NOT NULL," +
                                        "TAG STRING," +
                                        "VISITORS INT NOT NULL);"; // sql query
                        stmt.executeUpdate(sql4); // execute statement

                        System.out.println("region_sites table created successfully");

                        String insertQuery4 = "INSERT INTO region_sites (ID,REGION_ID,SITE_ID,TAG,VISITORS) " +
                        // london
                                        "VALUES ('1', '1', '1', 'Bronze', 2500), " +
                                        "('2', '1', '2', 'Silver', 25000), " +
                                        "('3', '1', '3', 'Gold', 50000), " +
                                        "('4', '1', '4', 'Bronze', 5000), " +
                                        "('5', '1', '5', 'Bronze', 500), " +
                                        "('6', '1', '6', 'Gold', 500000), " +
                                        // midlands
                                        "('7', '2', '2', 'Silver', 25000), " +
                                        "('8', '2', '3', 'Gold', 50000), " +
                                        "('9', '2', '4', 'Bronze', 5000), " +
                                        "('10', '2', '5', 'Bronze', 500), " +
                                        "('11', '2', '6', 'Gold', 500000), " +
                                        // north east
                                        "('12', '3', '2', 'Silver', 25000), " +
                                        "('13', '3', '3', 'Gold', 50000), " +
                                        "('14', '3', '4', 'Bronze', 5000), " +
                                        "('15', '3', '5', 'Bronze', 500), " +
                                        "('16', '3', '6', 'Gold', 500000), " +
                                        // north west
                                        "('17', '4', '2', 'Silver', 25000), " +
                                        "('18', '4', '3', 'Gold', 50000), " +
                                        "('19', '4', '4', 'Bronze', 5000), " +
                                        "('20', '4', '5', 'Bronze', 500), " +
                                        "('21', '4', '6', 'Gold', 500000), " +
                                        // south east
                                        "('22', '5', '2', 'Silver', 25000), " +
                                        "('23', '5', '3', 'Gold', 50000), " +
                                        "('24', '5', '4', 'Bronze', 5000), " +
                                        "('25', '5', '5', 'Bronze', 500), " +
                                        "('26', '5', '6', 'Gold', 500000), " +
                                        // south west
                                        "('27', '6', '2', 'Silver', 25000), " +
                                        "('28', '6', '3', 'Gold', 50000), " +
                                        "('29', '6', '4', 'Bronze', 5000), " +
                                        "('30', '6', '5', 'Bronze', 500), " +
                                        "('31', '6', '6', 'Gold', 500000) " +
                                        ";"; // sql query
                        stmt.executeUpdate(insertQuery4); // execute statement

                        System.out.println("rows added to region_sites successfully");

                        stmt.close(); // close statement
                        c.close(); // close connection
                } catch (Exception e) {
                        // handle errors, output name and close
                        System.err.println(e.getClass().getName() + ": " + e.getMessage());
                        System.exit(0);
                }
        }
}
