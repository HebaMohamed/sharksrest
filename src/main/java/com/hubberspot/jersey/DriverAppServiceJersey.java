/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hubberspot.jersey;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;

/**
 *
 * @author dell
 */
@ApplicationPath("/")
@Path("/driverservice")
public class DriverAppServiceJersey {
    Connection conn;
    
    ResultSet getDBResultSet(String query) throws Exception{
                    
        Class.forName("com.mysql.jdbc.Driver");            
        //conn = DriverManager.getConnection("jdbc:mysql://localhost/hebadb?" + "user=root&password=");
        conn = DriverManager.getConnection("jdbc:mysql://sql6.freesqldatabase.com/sql6157057?" + "user=sql6157057&password=yJvmWDS6xJ");
        
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);
        return rs;            
    }
    void excDB(String query) throws Exception{
                    
        Class.forName("com.mysql.jdbc.Driver");            
        //conn = DriverManager.getConnection("jdbc:mysql://localhost/hebadb?" + "user=root&password=");
        conn = DriverManager.getConnection("jdbc:mysql://sql6.freesqldatabase.com/sql6157057?" + "user=sql6157057&password=yJvmWDS6xJ");
        Statement st = conn.createStatement();
        st.executeUpdate(query);
    }
}
