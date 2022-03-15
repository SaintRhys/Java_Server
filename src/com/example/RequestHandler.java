package com.example;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

public class RequestHandler implements Runnable {
    Region[] regions = { new Region("1", "London"),
            new Region("2", "Midlands"),
            new Region("3", "North East"),
            new Region("4", "North West"),
            new Region("5", "South East"),
            new Region("6", "South West") };

    private final Socket client;
    ServerSocket serverSocket = null;
    ObjectOutputStream outObjStream;
    ObjectInputStream inObjStream;

    public RequestHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {

        try {
            outObjStream = new ObjectOutputStream(client.getOutputStream());
            inObjStream = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
            System.out.println(
                    "Thread started with name: " + Thread.currentThread().getName() + " on port: " + client.getPort());

            Packet packet = new Packet(
                    "Hi! - Welcome to Saxon Heritage Server\nWhat would you like to do today?\n1 - New Campaign\n2 - Previous Campaigns\n3 - Reports\n0 - exit");
            outObjStream.writeObject(packet);

            Boolean firstTime = true;
            Packet recvPacket;
            while ((recvPacket = (Packet) inObjStream.readObject()) != null) {
                receivedMessage(recvPacket.message);
                if (firstTime) {
                    firstTime = false;
                } else {
                    packet = new Packet(
                            "What would you like to do today?\n1 - New Campaign\n2 - Previous Campaigns\n3 - Reports\n0 - exit");
                    outObjStream.writeObject(packet);
                    recvPacket = (Packet) inObjStream.readObject();
                    receivedMessage(recvPacket.message);
                }
                switch (recvPacket.message) {
                    case "1":
                        addNewCampaign();
                        break;
                    case "2":
                        getPreviousCampaigns();
                        break;
                    case "3":
                        createReport();
                        break;
                    case "0":
                        break;
                    default:
                        break;
                }
            }

        } catch (IOException e) {
            System.out.println("I/O exception: " + e);
        } catch (Exception ex) {
            System.out.println("Exception in Thread Run. Exception : " + ex);
        }
    }

    private void addNewCampaign() throws IOException, ClassNotFoundException {
        Connection c = null;
        Statement stmt = null;
        String str = "";
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:saxonDb.db");
            stmt = c.createStatement();

            // get region details
            String regionText = prepArray(regions);
            Packet packet = new Packet(
                    "----------New Campaign----------\nSelect a region\n" + regionText + "0 - exit");
            outObjStream.writeObject(packet);

            Packet recvPacket = (Packet) inObjStream.readObject();
            int r_id = Integer.parseInt(recvPacket.message);
            receivedMessage(recvPacket.message);

            // get site details
            String sql = "SELECT ID AS id, NAME AS name " +
                    "FROM sites;";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                str += id + " - " + name + "\n";
            }
            packet = new Packet(
                    "\nSelect a site\n" + str + "0 - exit");
            outObjStream.writeObject(packet);
            recvPacket = (Packet) inObjStream.readObject();
            int s_id = Integer.parseInt(recvPacket.message);
            receivedMessage(recvPacket.message);

            // get length details
            packet = new Packet(
                    "\nInput length of campaign\n(in days)\n0 - exit");
            outObjStream.writeObject(packet);
            recvPacket = (Packet) inObjStream.readObject();
            int length = Integer.parseInt(recvPacket.message);
            receivedMessage(recvPacket.message);

            // get next id for campaigns
            sql = "SELECT COUNT(*) + 1 AS count " +
                    "FROM campaigns";
            rs = stmt.executeQuery(sql);
            int campId = 0;
            while (rs.next()) {
                campId = rs.getInt("count");
            }
            String insertQuery = "INSERT INTO campaigns (ID,SITE_ID,REGION_ID,LENGTH) " +
                    "VALUES (" + campId + ", '" + s_id + "', '" + r_id + "', " + length + ") ;";
            stmt.executeUpdate(insertQuery);

            stmt.close();
            c.close();
            packet = new Packet(
                    "SUCCESS\n\nDo you need anything else?\n1 - Yes\n0 - No");
            outObjStream.writeObject(packet);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            // System.exit(0);
        }

    }

    private String prepArray(Region[] arr) {
        String str = "";
        for (int i = 0; i < arr.length; i++) {
            str += "" + arr[i].getId() + " - " + arr[i].getName() + "\n";
        }
        return str;
    }

    private void getPreviousCampaigns() throws IOException {
        Connection c = null;
        Statement stmt = null;
        String str = "";

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:saxonDb.db");
            stmt = c.createStatement();

            String sql = "SELECT campaigns.ID AS id, regions.NAME AS r_name, sites.NAME AS s_name " +
                    "FROM campaigns " +
                    "INNER JOIN regions ON campaigns.REGION_ID = regions.ID " +
                    "INNER JOIN sites ON campaigns.SITE_ID = sites.ID;";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String s_name = rs.getString("s_name");
                String r_name = rs.getString("r_name");
                str += id + ": " + s_name + " in " + r_name + "\n";
            }

            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
            str = "ERROR";
        }

        Packet packet = new Packet(
                "----------Previous Campaigns----------\n" + str + "\n\nDo you need anything else?\n1 - Yes\n0 - No");
        outObjStream.writeObject(packet);
    }

    private void createReport() throws IOException {
        Connection c = null;
        Statement stmt = null;
        String str = "";

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:saxonDb.db");
            stmt = c.createStatement();

            String sql = "SELECT region_sites.ID AS id, regions.NAME AS r_name, sites.NAME AS s_name, region_sites.TAG AS tag, region_sites.VISITORS AS visitors "
                    +
                    "FROM region_sites " +
                    "INNER JOIN regions ON region_sites.REGION_ID = regions.ID " +
                    "INNER JOIN sites ON region_sites.SITE_ID = sites.ID " +
                    "ORDER BY visitors " +
                    "LIMIT 10;";
            ResultSet rs = stmt.executeQuery(sql);

            int count = 1;
            str += "no. \t| site \t\t| region \t| tag \t\t| visitors \t|\n";
            str += "-------------------------------------------------------------------------\n";
            while (rs.next()) {
                int id = count++;
                String s_name = rs.getString("s_name");
                String r_name = rs.getString("r_name");
                String tag = rs.getString("tag");
                String visitors = rs.getString("visitors");
                str += id + " \t| " + s_name + " \t| " + r_name + " \t| " + tag + " \t| " + visitors + " \t\t| " + "\n";
            }

            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
            str = "ERROR";
        }

        Packet packet = new Packet(
                "--------------------Report: top 10 least visited sites-------------------\n" + str
                        + "\n\nDo you need anything else?\n1 - Yes\n0 - No");
        outObjStream.writeObject(packet);
    }

    private void receivedMessage(String message) {
        System.out.println("Received message: '" + message + "' from " + client.toString() + " on "
                + Thread.currentThread().getName());
    }
}
