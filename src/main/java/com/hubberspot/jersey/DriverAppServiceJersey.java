/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hubberspot.jersey;
import java.util.logging.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.sf.json.JSONObject;

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
        conn = DriverManager.getConnection("jdbc:mysql://localhost/hebadb?" + "user=root&password=");
        //conn = DriverManager.getConnection("jdbc:mysql://sql11.freesqldatabase.com/sql11164022?" + "user=sql11164022&password=GLj4H4TT5N");
        
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);
        return rs;            
    }
    void excDB(String query) throws Exception{
                    
        Class.forName("com.mysql.jdbc.Driver");            
        conn = DriverManager.getConnection("jdbc:mysql://localhost/hebadb?" + "user=root&password=");
        //conn = DriverManager.getConnection("jdbc:mysql://sql11.freesqldatabase.com/sql11164022?" + "user=sql11164022&password=GLj4H4TT5N");
        Statement st = conn.createStatement();
        st.executeUpdate(query);
    }
    
    //    @Path("/driver login")
    //public Response LoginDriver(@PathParam("id") String id, @PathParam("password") String password) throws Exception{
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)    
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)

    public Response LoginDriver(String data) throws Exception{  
        JSONObject obj = new JSONObject();
           try {
              
            JSONObject objj = JSONObject.fromObject(data);   
            int id = objj.getInt("driver_id");            
            String password = objj.getString("password");

            ResultSet rs = getDBResultSet("SELECT * FROM driver WHERE driver_id = "+id);//knt user_id
            obj.put("success", "0");
            obj.put("msg", "Wrong ID or Password");
            while(rs.next())
            {
                 String pass = rs.getString(13);//msh 2
                 if(pass.equals(password))
                 {
                     //logindriver
                     obj.put("success", "1");
                     obj.put("msg", "Logged in successfully");
                     
                     JSONObject d = new JSONObject();
                     String fullname = rs.getString(2);
                     double sharp_turns_freq = rs.getDouble(3);
                     double lane_changing_freq = rs.getDouble(4);
                     double harch_acc_freq = rs.getDouble(5);
                     double wrong_u_turns_severity = rs.getDouble(7);
                     int vehicle_id = rs.getInt(10);
                     
                     d.put("fullname", fullname);
                     d.put("sharp_turns_freq", sharp_turns_freq);
                     d.put("lane_changing_freq", lane_changing_freq);
                     d.put("harch_acc_freq", harch_acc_freq);
                     d.put("wrong_u_turns_severity", wrong_u_turns_severity);
                     d.put("vehicle_id", vehicle_id);

//                     String name = rs.getString(3);
//                     String gender = rs.getString(4);
//                     String lastlogin_time = rs.getString(5);
//                     String account_state = rs.getString(6);
//                     
//                     d.put("id", id);
//                     d.put("name", name);
//                     d.put("gender", gender);
//                     d.put("lastlogin_time", lastlogin_time);
//                     d.put("account_state", account_state);

                     obj.put("driver", d);

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
    
}
