package com.example.rps.controllers;

import java.sql.Connection;

public class Window {

    private Connection conn;

    public Window () {
        conn = null;
    }

    public void initConnection(Connection conn) {
        this.conn = conn;
    }

    public void setUpWindow() {

    }

    public Connection getConnection() {
        return conn;
    }
}
