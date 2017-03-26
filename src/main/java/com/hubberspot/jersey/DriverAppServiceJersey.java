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
import javax.ws.rs.GET;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 *
 * @author dell
 */
@ApplicationPath("/")
@Path("/driverservice")
public class DriverAppServiceJersey {
    Connection conn;
    
    double KMCOST = 2;//2 LE :D
    
    ResultSet getDBResultSet(String query) throws Exception{
                    
        Class.forName("com.mysql.jdbc.Driver");            
        //conn = DriverManager.getConnection("jdbc:mysql://localhost/hebadb?" + "user=root&password=");
        conn = DriverManager.getConnection("jdbc:mysql://db4free.net/nashwa346db?" + "user=nashwa346&password=123456");
        
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);
        return rs;            
    }
    void excDB(String query) throws Exception{
                    
        Class.forName("com.mysql.jdbc.Driver");            
        //conn = DriverManager.getConnection("jdbc:mysql://localhost/hebadb?" + "user=root&password=");
        conn = DriverManager.getConnection("jdbc:mysql://db4free.net/nashwa346db?" + "user=nashwa346&password=123456");
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
    
    
    @GET
    @Path("/getlasttrips/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response getLasttrips(@PathParam("id") int id) throws Exception{
         String query = "SELECT * FROM trip WHERE driver_id = "+id;
        JSONObject obj = new JSONObject();
        try {
            ResultSet rs = getDBResultSet(query);
            obj.put("success", "1");
            obj.put("msg", "done");
            
            JSONArray arr = new JSONArray();
             while(rs.next())
             {
                 int trip_id = rs.getInt(1);
                 String start = rs.getString(2);
                 String end = rs.getString(3);
                 String price = rs.getString(4);  
                 String comment= rs.getString(5);
                 String ratting= rs.getString(6);
                 String passenger_id= rs.getString(8);
                                     
                 JSONObject o = new JSONObject();
                 o.put("trip_id",trip_id  );
                 o.put("start", start);
                 o.put("end", end);
                 o.put("price ",price );
                 o.put("comment", comment);
                 o.put("ratting", ratting);
                 o.put("passenger_id",passenger_id);
                 
                 String query2 = "SELECT * FROM pathwaymap WHERE trip_id = "+id;
                 ResultSet rs2 = getDBResultSet(query2);
                 JSONArray paths = new JSONArray();
                 while(rs2.next())
                {
                    Double lat = rs2.getDouble("yattitude");                    
                    Double lng = rs2.getDouble("xlongitude");
                    JSONObject latlng = new JSONObject();
                    latlng.put("lat", lat);                    
                    latlng.put("lng", lng);
                    paths.add(latlng);
                }
                 o.put("pathway",paths);
                 arr.add(o);
             }
                 
            obj.put("lasttrips", arr);  
            conn.close();
        } catch (SQLException ex) {
            obj.put("success", "0");
            obj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(obj).build();
    }
    
    
    @GET
    @Path("/getlasttrip/{tripid}")
      @Produces(MediaType.APPLICATION_JSON)
   public Response getLasttrip(@PathParam("tripid") int id){
        JSONObject obj = new JSONObject();
 try {
            ResultSet rs = getDBResultSet("SELECT * FROM trip WHERE trip_id = "+id);
            JSONObject tripobj = new JSONObject();   
            while(rs.next())
             {           
                
                 String start = rs.getString(1);
                 String end = rs.getString(2);
                 String price = rs.getString(3);  
                 String comment = rs.getString(4);
                 String ratting = rs.getString(5);
                 String passenger_id = rs.getString(6);
                 String driver_id = rs.getString(7);
                 
                 
                 tripobj.put("start", start);
                 tripobj.put("end", end);
                 tripobj.put("price", price);
                 tripobj.put("comment",  comment);
                 tripobj.put("ratting", ratting);
                 tripobj.put("passenger_id", passenger_id);
                tripobj.put("driver_id ",  driver_id );
                
                    
                 String query2 = "SELECT * FROM pathwaymap WHERE trip_id = "+id;
                 ResultSet rs2 = getDBResultSet(query2);
                 JSONArray paths = new JSONArray();
                 while(rs2.next())
                {
                    Double lat = rs2.getDouble("yattitude");                    
                    Double lng = rs2.getDouble("xlongitude");
                    JSONObject latlng = new JSONObject();
                    latlng.put("lat", lat);                    
                    latlng.put("lng", lng);
                    paths.add(latlng);
                }
                 tripobj.put("pathway",paths);
                
                obj.put("trip", tripobj);
             }
            //obj.put("trip", tripobj);
            obj.put("success", "1");
            obj.put("msg", "Selected Successfully");
            
            conn.close();
        } catch (Exception ex) {
            obj.put("success", "0");
            obj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.status(200).entity(obj).build();

    }
    
   
    @GET
    @Path("/donetrip/{tripid}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
   public Response donetrip(@PathParam("tripid") int id) throws Exception{
       JSONObject obj = new JSONObject();
   
        try {
            //get current datetime 
            java.util.Date dt = new java.util.Date();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = sdf.format(dt);
            String j = "UPDATE trip SET end = '"+currentTime+"' WHERE trip_id = "+id+";";
            excDB(j);
            conn.close();
            
            double distance = calculate_pathway(id);
            double distancecost = KMCOST/1000*distance;
            obj.put("distance", distance);
            obj.put("distancecost", distancecost);

            
            obj.put("success", "1");
            obj.put("msg", "Edited Successfully");
             } catch (SQLException ex) {
            obj.put("success", "0");
            obj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(obj).build();
    }
   
   double calculate_pathway(int id) throws Exception{
       String query = "SELECT * FROM pathwaymap WHERE trip_id = "+id;
       ResultSet rs = getDBResultSet(query);
       JSONArray paths = new JSONArray();
       while(rs.next())
       {
           Double lat = rs.getDouble("yattitude"); 
           Double lng = rs.getDouble("xlongitude");
                    
           JSONObject latlng = new JSONObject();
           latlng.put("lat", lat);                    
           latlng.put("lng", lng);
           paths.add(latlng);
        }
       double fulldistance = 0;
       for (int i = 0; i < paths.size()-1; i++) {
           double lat1 = paths.getJSONObject(i).getDouble("lat");           
           double lng1 = paths.getJSONObject(i).getDouble("lng");
           
           double lat2 = paths.getJSONObject(i+1).getDouble("lat");           
           double lng2 = paths.getJSONObject(i+1).getDouble("lng");

           fulldistance+= distance(lat1, lat2, lng1, lng2);
       }
       return fulldistance;
   }

   
   
        /**
      * Calculate distance between two points in latitude and longitude taking
      * into account height difference. If you are not interested in height
      * difference pass 0.0. Uses Haversine method as its base.
      * 
      * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
      * el2 End altitude in meters
      * @returns Distance in Meters
      */
     public static double distance(double lat1, double lat2, double lon1, double lon2) {
         final int R = 6371; // Radius of the earth

         Double latDistance = Math.toRadians(lat2 - lat1);
         Double lonDistance = Math.toRadians(lon2 - lon1);
         Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
         Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
         double distance = R * c * 1000; // convert to meters

         //double height = el1 - el2;

         distance = Math.pow(distance, 2) ;//+ Math.pow(height, 2);

         return Math.sqrt(distance);
     }
     
     
     
     
     
     @GET
     @Path("/accepttrip/{trip_id}/{driverid}")
     @Produces(MediaType.APPLICATION_JSON)
     @Consumes(MediaType.APPLICATION_JSON)
        public Response assignvehicle(@PathParam("trip_id") int id, @PathParam("driverid") int did) throws SQLException{
            JSONObject obj = new JSONObject();
             try {
                  //get current datetime 
            java.util.Date dt = new java.util.Date();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = sdf.format(dt);
            
                  String k = "UPDATE trip SET driver_id = '"+did+"' AND end = '"+currentTime+"'  WHERE trip_id = "+id+";";
                  excDB(k);
            ResultSet rs = getDBResultSet("SELECT * FROM trip WHERE driver_id = "+did);
             JSONObject tripobj = new JSONObject();   
            while(rs.next())
             {           
                
                 String start = rs.getString(1);
                 String end = rs.getString(2);
                 String price = rs.getString(3);  
                 String comment = rs.getString(4);
                 String ratting = rs.getString(5);
                 String passenger_id = rs.getString(6);
                 String driver_id = rs.getString(7);
                 
                 
                 tripobj.put("start", start);
                 tripobj.put("end", end);
                 tripobj.put("price", price);
                 tripobj.put("comment",  comment);
                 tripobj.put("ratting", ratting);
                 tripobj.put("passenger_id", passenger_id);
                tripobj.put("driver_id",  driver_id );
                obj.put("tripobj", tripobj);
             }
            
            obj.put("success", "1");
            obj.put("msg", "Done Successfully");
            
            conn.close();
             }
         catch (Exception ex) {
            obj.put("success", "0");
            obj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(obj).build();
    }
       
   
}
