
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hubberspot.jersey;


import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.ws.spi.http.HttpContext;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 *
 * @author dell
 */

@ApplicationPath("/")
@Path("/passengerservice")
public class PassengerAppServiceJersey {
    Connection conn;
    
    
        
    ResultSet getDBResultSet(String query) throws Exception{
                    
        Class.forName("com.mysql.jdbc.Driver");            
        //conn = DriverManager.getConnection("jdbc:mysql://localhost/hebadb?" + "user=root&password=");
        conn = DriverManager.getConnection("jdbc:mysql://sql11.freesqldatabase.com/sql11164022?" + "user=sql11164022&password=GLj4H4TT5N");
        
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);
        return rs;            
    }
    void excDB(String query) throws Exception{
                    
        Class.forName("com.mysql.jdbc.Driver");            
        //conn = DriverManager.getConnection("jdbc:mysql://localhost/hebadb?" + "user=root&password=");
        conn = DriverManager.getConnection("jdbc:mysql://sql11.freesqldatabase.com/sql11164022?" + "user=sql11164022&password=GLj4H4TT5N");
        Statement st = conn.createStatement();
        st.executeUpdate(query);
    }
    
    
    
    
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)    
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response LoginPassenger(String data) throws Exception{  
        JSONObject obj = new JSONObject();
           try {

            JSONObject objj = JSONObject.fromObject(data);   
            String email = objj.getString("email");            
            String password = objj.getString("password");

            ResultSet rs = getDBResultSet("SELECT * FROM passenger WHERE useremail = '"+email+"'");//knt user_id
            obj.put("success", "0");
            obj.put("msg", "Wrong email or Password");
            while(rs.next())
            {
                 String pass = rs.getString(2);
                 if(pass.equals(password))
                 {
                     //loginpassenger
                     obj.put("success", "1");
                     obj.put("msg", "Logged in successfully");
                     
                     JSONObject d = new JSONObject();
                     String passenger_id = rs.getString(1);
                     String fullname = rs.getString(3);
                     String gender = rs.getString(4);
                     int age = rs.getInt(5);
                     int phone = rs.getInt(6);
                     int relatedphone = rs.getInt(7);
                     String language = rs.getString("language");

                     d.put("passenger_id", passenger_id);
                     d.put("fullname", fullname);
                     d.put("gender", gender);
                     d.put("age", age);
                     d.put("phone", phone);
                     d.put("relatedphone", relatedphone);
                     d.put("language", language);
                     d.put("email", email);

                     obj.put("passenger", d);

                 }
                 else {
                     obj.put("success", "0");
                     obj.put("msg", "Wrong Credentials");
                 }

             }
            
            conn.close();
        } catch (SQLException ex) {
            obj.put("success", "0");
            obj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.status(200).entity(obj).build();

    }
    
    
    @POST
    @Path("/signup")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response passengersignup(String data){//note : img is byte array string
            //as post request
            JSONObject dataObj = JSONObject.fromObject(data);
            String name = dataObj.getString("name");
            String email = dataObj.getString("email");
            String password = dataObj.getString("password");
            int phone = dataObj.getInt("phone");
            int relatedphone = dataObj.getInt("relatedphone");
            JSONObject obj = new JSONObject();
            try {
                excDB("INSERT INTO passenger (fullname, email, phone, password,relatedphone)"+
                        " VALUES ( '"+name+"' '"+email+"', '"+phone+"', '"+password+"', '"+relatedphone+"');");
                obj.put("success", "1");
                obj.put("msg", "Added Successfully");

                conn.close();
            } catch (Exception ex) {
                obj.put("success", "0");
                obj.put("msg", ex.getMessage());
                Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        return Response.status(200).entity(obj).build();
    }
    
}
