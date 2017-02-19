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


@ApplicationPath("/")
@Path("/websiteservice")
public class WebsiteServiceJersey {
    
    Connection conn;
            
    @GET //test only
    @Path("/go")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getgo(){
        String output = "gooooooooooooooooooooo Hebat" ;
        return Response.status(200).entity(output).build();
    }
   
    
    
    
    @GET
    @Path("/getdrivers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDrivers(){
        String query = "SELECT * FROM driver";
        JSONObject obj = new JSONObject();
        try {
            ResultSet rs = getDBResultSet(query);
            obj.put("success", "1");
            obj.put("msg", "done");
            
            JSONArray arr = new JSONArray();
                   
            while(rs.next())
             {
                 int did = rs.getInt(1);
                 String dname = rs.getString(2);
                 String demail = rs.getString(12);
                 int vid = rs.getInt(10);                
                 
                 JSONObject o = new JSONObject();
                 o.put("did", did);
                 o.put("dname", dname);
                 o.put("demail", demail);
                 o.put("vid", vid);
                 arr.add(o);
             }
                 
            obj.put("drivers", arr);  
            conn.close();
        } catch (Exception ex) {
            obj.put("success", "0");
            obj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(obj).build();
    }
    
    @GET
    @Path("/deletedriver/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteDriver(@PathParam("id") int id){
        JSONObject obj = new JSONObject();
        try {
            excDB("DELETE FROM driver WHERE driver_id = "+id);
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
    
    @GET
    @Path("/getdriver/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDriver(@PathParam("id") int id){
        JSONObject obj = new JSONObject();

        try {
            int vid = 0;
            ResultSet rs = getDBResultSet("SELECT * FROM driver WHERE driver_id = "+id);
            JSONObject driverobj = new JSONObject();   
            while(rs.next())
             {        
                 vid = rs.getInt(10);
                 driverobj.put("id", rs.getInt(1));
                 driverobj.put("name", rs.getString(2));
                 driverobj.put("sharp_turns_freq", rs.getDouble(3));
                 driverobj.put("lane_changing_freq", rs.getDouble(4));
                 driverobj.put("harch_acc_freq", rs.getDouble(5));
                 driverobj.put("last_trip_behavoir_map", rs.getBlob(6));
                 driverobj.put("wrong_u_turns_severity", rs.getDouble(7));
                 driverobj.put("harsh_breaking_freq", rs.getDouble(8));
                 driverobj.put("awareness_level", rs.getDouble(9));
                 driverobj.put("vehicle_id", vid);
                 driverobj.put("vehicle_datetime", rs.getString(11));
                 driverobj.put("email", rs.getString(12));
                 driverobj.put("password", rs.getString(13));
                 //driverobj.put("image", rs.getString(14));
                 
                 Blob imageBlob = rs.getBlob(14);
                 byte[] byteArray = imageBlob.getBytes(1, (int) imageBlob.length());
                 String str = new sun.misc.BASE64Encoder().encode(byteArray);
                 driverobj.put("image", str);
                 
             }            
            conn.close();
            
            
            ResultSet rs2 = getDBResultSet("SELECT * FROM route_restrictions WHERE driver_id = "+id);
            JSONArray ristrictedroute = new JSONArray();
            while(rs2.next())
            {
                JSONObject routeobj = new JSONObject(); 
                routeobj.put("xlongitude", rs2.getDouble(3));
                routeobj.put("ylatitude", rs2.getDouble(4));
                ristrictedroute.add(routeobj);
            }
            
            ResultSet rs3 = getDBResultSet("SELECT * FROM vehicle WHERE vehicle_id = "+vid);
            JSONObject vobj = new JSONObject(); 
            while(rs3.next())
            {
                vobj.put("vehicle_id", rs3.getInt(1));
                vobj.put("model", rs3.getString(2));
                vobj.put("color", rs3.getString(3));
                vobj.put("plate_number", rs3.getString(5));
            }
            
            
            
            obj.put("ristrictedroute", ristrictedroute);
            obj.put("driver", driverobj);
            obj.put("vehicle", vobj);
            obj.put("success", "1");
            obj.put("msg", "Done Successfully");
            conn.close();
            
        } catch (Exception ex) {
            obj.put("success", "0");
            obj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.status(200).entity(obj).build();

    }
    
    //@GET
    //@Path("/adddriver/{name}/{email}/{password}/{img}")
    @POST
    @Path("/adddriver")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    //public Response addDriver(@PathParam("name") String name, @PathParam("email") String email, @PathParam("password") String password, @PathParam("img") String img){//note : img is byte array string
        public Response addDriver(String data){//note : img is byte array string
            //as post request
            JSONObject dataObj = JSONObject.fromObject(data);
            String name = dataObj.getString("name");
            String email = dataObj.getString("email");
            String password = dataObj.getString("password");
            String img = dataObj.getString("img");
            
    JSONObject obj = new JSONObject();
        try {
            //work but without img
//            excDB("INSERT INTO driver (driver_id, fullname, sharp_turns_freq, lane_changing_freq, harch_acc_freq, last_trip_behavoir_map, wrong_u_turns_severity, harsh_breaking_freq, awareness_level, vehicle_id, vehicle_datetime, email, password)"+
//                                                         " VALUES (NULL, '"+name+"', 0, 0, 0, NULL, 0, 0, 0, NULL, NULL, '"+email+"', '"+password+"');");
            obj.put("success", "1");
            obj.put("msg", "Added Successfully");
            
            //for img 
            byte[] bytearr = new sun.misc.BASE64Decoder().decodeBuffer(img);
            excPreparedStatmentDB("INSERT INTO driver (driver_id, fullname, sharp_turns_freq, lane_changing_freq, harch_acc_freq, last_trip_behavoir_map, wrong_u_turns_severity, harsh_breaking_freq, awareness_level, vehicle_id, vehicle_datetime, email, password, image)"+
                                                         " VALUES (NULL, '"+name+"', 0, 0, 0, NULL, 0, 0, 0, NULL, NULL, '"+email+"', '"+password+"', ? );",bytearr);
          
            
            conn.close();
        } catch (Exception ex) {
            obj.put("success", "0");
            obj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(obj).build();
    }
    
//    @GET
//    @Path("/editdriver/{id}/{name}/{email}/{password}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response editDriver(@PathParam("id") int id, @PathParam("name") String name, @PathParam("email") String email, @PathParam("password") String password){
              
    @POST
    @Path("/editdriver")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
  public Response editDriver(String data){
        JSONObject resobj = JSONObject.fromObject(data);
        int id = resobj.getInt("id");
        String name = resobj.getString("name");
        String email = resobj.getString("email");
        String password = resobj.getString("password");
        String img = resobj.getString("img");
        JSONObject obj = new JSONObject();
        try {         
            //for img 
            byte[] bytearr = new sun.misc.BASE64Decoder().decodeBuffer(img);
            String j="";
            if(password.equals("null")&&img.equals("")){//if pass="" so it dont changed otherwise change it
                j = "UPDATE driver SET fullname = '"+name.replaceAll(","," ")+"' , email = '"+email+"'  WHERE driver_id = "+id+";";
                excDB(j);
            }else if (!password.equals("null")&&img.equals("")){
                j = "UPDATE driver SET fullname = '"+name.replaceAll(","," ")+"' , email = '"+email+"' , password = '"+password+"' WHERE driver_id = "+id+";";
                excDB(j);
            }else if (!password.equals("null")&&!img.equals("")){
                j = "UPDATE driver SET fullname = '"+name.replaceAll(","," ")+"' , email = '"+email+"' , password = '"+password+"' , image = ? WHERE driver_id = "+id+";";
                excPreparedStatmentDB(j,bytearr);
            }else if (password.equals("null")&&!img.equals("")){
                j = "UPDATE driver SET fullname = '"+name.replaceAll(","," ")+"' , email = '"+email+"' , image = ? WHERE driver_id = "+id+";";
                excPreparedStatmentDB(j,bytearr);
            }
            //
            
            obj.put("success", "1");
            obj.put("msg", "Edited Successfully");
            
            conn.close();
        } catch (Exception ex) {
            obj.put("success", "0");
            obj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(obj).build();
    }
    
    
    
    @GET
    @Path("/assignvehicle/{vid}/{did}/{ovid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response assignvehicle(@PathParam("vid") int vid, @PathParam("did") int did, @PathParam("ovid") int ovid){//old vid
        JSONObject obj = new JSONObject();
        try {
            //get current datetime 
            java.util.Date dt = new java.util.Date();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = sdf.format(dt);
            String j = "UPDATE driver SET vehicle_id = '"+vid+"' , vehicle_datetime = '"+currentTime+"' WHERE driver_id = "+did+";";
            excDB(j);
            conn.close();
            
            String jj = "UPDATE vehicle SET outside_working_time_state = 'no' WHERE vehicle_id = "+vid+";";
            excDB(jj);
            conn.close();
            
            String jjj = "UPDATE vehicle SET outside_working_time_state = 'yes' WHERE vehicle_id = "+ovid+";";
            excDB(jjj);
            conn.close();
            
            obj.put("success", "1");
            obj.put("msg", "Edited Successfully");
            
            
        } catch (Exception ex) {
            obj.put("success", "0");
            obj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(obj).build();
    }
    
    
    
    
    //////////////////////////////////////////////////////////////for Vehicles
    @GET
    @Path("/getvehicles")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVehicles(){
        String query = "SELECT * FROM vehicle";
        JSONObject obj = new JSONObject();
        try {
            ResultSet rs = getDBResultSet(query);
            obj.put("success", "1");
            obj.put("msg", "done");
            
            JSONArray arr = new JSONArray();
                   
            while(rs.next())
             {
                 int vehicle_id = rs.getInt(1);
                 String model = rs.getString(2);
                 String color = rs.getString(3);
                 String outside_working_time_state = rs.getString(4);  
                 String plate_number = rs.getString(5);
                 
                 JSONObject o = new JSONObject();
                 o.put("vehicle_id", vehicle_id);
                 o.put("model", model);
                 o.put("color", color);
                 o.put("outside_working_time_state", outside_working_time_state);
                 o.put("plate_number", plate_number);

                 arr.add(o);
             }
                 
            obj.put("vehicles", arr);  
            conn.close();
        } catch (Exception ex) {
            obj.put("success", "0");
            obj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(obj).build();
    }
    
    @GET
    @Path("/getvehicle/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVehicle(@PathParam("id") int id){
        JSONObject obj = new JSONObject();

        try {
            ResultSet rs = getDBResultSet("SELECT * FROM vehicle WHERE vehicle_id = "+id);
            JSONObject vehicleobj = new JSONObject();   
            while(rs.next())
             {           
                 int vehicle_id = rs.getInt(1);
                 String model = rs.getString(2);
                 String color = rs.getString(3);
                 String outside_working_time_state = rs.getString(4);  
                 String plate_number = rs.getString(5);
                 
                 vehicleobj.put("vehicle_id", vehicle_id);
                 vehicleobj.put("model", model);
                 vehicleobj.put("color", color);
                 vehicleobj.put("outside_working_time_state", outside_working_time_state);
                 vehicleobj.put("plate_number", plate_number);
             }
            obj.put("vehicle", vehicleobj);
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
    @Path("/addvehicle/{model}/{color}/{plate_number}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addVehicle(@PathParam("model") String model, @PathParam("color") String color, @PathParam("plate_number") String plate_number){
        JSONObject obj = new JSONObject();
        try {
            excDB("INSERT INTO vehicle (vehicle_id, model, color, outside_working_time_state, plate_number) "+
                                 "VALUES (NULL, '"+model+"', '"+color+"', 'yes', '"+plate_number+"');");
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
    
    @GET
    @Path("/editvehicle/{id}/{model}/{color}/{plate_number}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response editVehicle(@PathParam("id") int id, @PathParam("model") String model, @PathParam("color") String color, @PathParam("plate_number") String plate_number){
        JSONObject obj = new JSONObject();
        try {
            excDB("UPDATE vehicle SET model = '"+model+"', color = '"+color+"', plate_number = '"+plate_number+"' WHERE vehicle_id = "+id+";");
            obj.put("success", "1");
            obj.put("msg", "Edited Successfully");
            
            conn.close();
        } catch (Exception ex) {
            obj.put("success", "0");
            obj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(obj).build();
    }
   
    @GET
    @Path("/deletevehicle/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteVehicle(@PathParam("id") int id){
        JSONObject obj = new JSONObject();
        try {
            excDB("DELETE FROM vehicle WHERE vehicle_id = "+id);
            obj.put("success", "1");
            obj.put("msg", "Deleted Successfully");
            
            conn.close();
        } catch (Exception ex) {
            obj.put("success", "0");
            obj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(obj).build();

    }
    
    
    //////////////////////////////////////////////////////////////for Monitoring
    @GET
    @Path("/getcurrentvds")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentVDs(){
        String query = "SELECT * FROM vehicle, driver WHERE outside_working_time_state = 'no' AND vehicle.vehicle_id = driver.vehicle_id";
        JSONObject obj = new JSONObject();
        try {
            ResultSet rs = getDBResultSet(query);
            obj.put("success", "1");
            obj.put("msg", "done");
            
            JSONArray arr = new JSONArray();
                   
            int onlineDriversCount=0;
            while(rs.next())
             {
                 onlineDriversCount++;
                 int vehicle_id = rs.getInt(1);
                 String model = rs.getString(2);
                 String color = rs.getString(3);
                 String plate_number = rs.getString(5);
                 
                 String driver_id = rs.getString(6);
                 String fullname = rs.getString(7);
                 String vehicle_datetime = rs.getString(16);

                 
                 JSONObject o = new JSONObject();
                 o.put("vehicle_id", vehicle_id);
                 o.put("model", model);
                 o.put("color", color);
                 o.put("plate_number", plate_number);
                 o.put("driver_id", driver_id);
                 o.put("fullname", fullname);
                 o.put("vehicle_datetime", vehicle_datetime);

                 arr.add(o);
             }
                 
            obj.put("currentvms", arr);  
            conn.close();
            
            //for another controls
            ResultSet rs2 = getDBResultSet("SELECT COUNT(*) FROM driver");
            rs2.first();
            int alldriverscount =Integer.parseInt(rs2.getString(1));
            int offlineDriversCount = alldriverscount-onlineDriversCount;
            conn.close();
            
            ResultSet rs3 = getDBResultSet("SELECT COUNT(*) FROM passenger");
            rs3.first();
            int passengerscount =Integer.parseInt(rs3.getString(1));
            
            
            int tripscount =0;            
            JSONArray rattingArr = new JSONArray();
            ResultSet rs4 = getDBResultSet("SELECT * FROM trip");
            while(rs4.next())
             {
                 tripscount++;
                 int trip_id = rs4.getInt(1);
                 int ratting = rs4.getInt(6);
                 JSONObject o = new JSONObject();
                 o.put("trip_id", trip_id);
                 o.put("ratting", ratting);
                 rattingArr.add(o);
             }
            obj.put("alldriverscount", alldriverscount);
            obj.put("passengerscount", passengerscount);
            obj.put("tripscount", tripscount);
            obj.put("rattingArr", rattingArr);
            obj.put("onlineDriversCount", onlineDriversCount);
            obj.put("offlineDriversCount", offlineDriversCount);
            
            conn.close();
        } catch (Exception ex) {
            obj.put("success", "0");
            obj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(obj).build();
    }
    
    /////////////////////////////////////////////////////////////forlogin
    
    @GET
    @Path("/loginmember/{id}/{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response LoginMonitor(@PathParam("id") String id, @PathParam("password") String password){
        JSONObject obj = new JSONObject();

        try {
            ResultSet rs = getDBResultSet("SELECT * FROM monitoring_member WHERE user_id = "+id);
                             
            //in case if there is no member mh hyd5l fl loop asln
            obj.put("success", "0");
            obj.put("msg", "Wrong ID");
            while(rs.next())
             {           

                 
                 String pass = rs.getString(2);
                 if(pass.equals(password))
                 {
                     //login
                     obj.put("success", "1");
                     obj.put("msg", "Logged in successfully");
                     
                     JSONObject m = new JSONObject();
                     String name = rs.getString(3);
                     String gender = rs.getString(4);
                     String lastlogin_time = rs.getString(5);
                     String account_state = rs.getString(6);
                     
                     m.put("id", id);
                     m.put("name", name);
                     m.put("gender", gender);
                     m.put("lastlogin_time", lastlogin_time);
                     m.put("account_state", account_state);

                     obj.put("member", m);

                 }
                 else {
                     obj.put("success", "0");
                     obj.put("msg", "Wrong Password");
                 }

             }
            
            conn.close();
        } catch (Exception ex) {
            obj.put("success", "0");
            obj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.status(200).entity(obj).build();

    }
    
    @GET
    @Path("/addmonitormember/{name}/{gender}/{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMonitor(@PathParam("name") String name, @PathParam("gender") String gender, @PathParam("password") String password){
        JSONObject obj = new JSONObject();
        try {
            //check if admin
            String accstate = "";
            ResultSet rs = getDBResultSet("SELECT COUNT(*) FROM monitoring_member");
            int count =Integer.parseInt(rs.getString(1));
            if(count==0)
                accstate="Admin";
            else
                accstate="Pending";
            
            excDB("INSERT INTO monitoring_member (user_id, password, fullname, gender, lastlogin_time, account_state, vehicle_id, datetime_vehicle_monitor, datetime_monitor_driver, driver_id) "+
                                             " VALUES (NULL, '"+password+"', '"+name+"', '"+gender+"', NULL, '"+accstate+"', NULL, NULL, NULL, NULL);");
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
    
    
    //////////////////////////////////////////////////////////////for pending
        
    @GET
    @Path("/getpmembers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMembers(){
        String query = "SELECT * FROM monitoring_member";
        JSONObject obj = new JSONObject();
        try {
            ResultSet rs = getDBResultSet(query);
            obj.put("success", "1");
            obj.put("msg", "done");
            
            JSONArray arr = new JSONArray();
                   
            while(rs.next())
             {
                 String account_state = rs.getString(6);   
                 if(account_state.equals("pending")){

                 int user_id = rs.getInt(1);
                 String fullname = rs.getString(3);
                 String gender = rs.getString(4);
                 String lastlogin_time = rs.getString(5);                

                 JSONObject o = new JSONObject();
                 o.put("user_id", user_id);
                 o.put("fullname", fullname);
                 o.put("gender", gender);
                 o.put("lastlogin_time", lastlogin_time);
                 arr.add(o);
                 }
             }
                 
            obj.put("members", arr);  
            conn.close();
        } catch (Exception ex) {
            obj.put("success", "0");
            obj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(obj).build();
    }
    
    /////////////////////////////////////////////////////////////////////////////for trips
    @GET
    @Path("/gettrips")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTrips(){
        String query = "SELECT * FROM trip";
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
                 Double price = rs.getDouble(4);
                 String comment = rs.getString(5);
                 Double ratting = rs.getDouble(6);
                 int passenger_id = rs.getInt(7);    
                 int driver_id = rs.getInt(8);
                 
                 JSONObject o = new JSONObject();
                 o.put("trip_id", trip_id);
                 o.put("start", start);
                 o.put("end", end);
                 o.put("price", price);
                 o.put("comment", comment);
                 o.put("ratting", ratting);
                 o.put("passenger_id", passenger_id);
                 o.put("driver_id", driver_id);
                 arr.add(o);
             }
                 
            obj.put("trips", arr);  
            conn.close();
        } catch (Exception ex) {
            obj.put("success", "0");
            obj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(obj).build();
    }
    
    //get trip pathway map
    @GET
    @Path("/getpathwaymap/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPathwaymap(@PathParam("id") int id){
        String query = "SELECT * FROM pathwaymap WHERE trip_id = "+id;
        JSONObject obj = new JSONObject();
        try {
            ResultSet rs = getDBResultSet(query);
            obj.put("success", "1");
            obj.put("msg", "done");
            
            JSONArray arr = new JSONArray();
                   
            while(rs.next())
             {
                 int trip_id = rs.getInt(1);
                 Double yattitude = rs.getDouble(2);
                 Double xlongitude = rs.getDouble(3);
                 
                 JSONObject o = new JSONObject();
                 o.put("trip_id", trip_id);
                 o.put("yattitude", yattitude);
                 o.put("xlongitude", xlongitude);
                 arr.add(o);
             }
                 
            obj.put("pathwaymap", arr);  
            conn.close();
        } catch (Exception ex) {
            obj.put("success", "0");
            obj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(obj).build();
    }
    //add pathway map
    @POST
    @Path("/addristrictedroute")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addristrictedroute(String data){
                
        JSONObject obj = new JSONObject();
        try {
            
        JSONObject objj = JSONObject.fromObject(data);        
        JSONArray arr = objj.getJSONArray("routes");
        int id = objj.getInt("driver_id");
        //first delete last route restriction
        excDB("DELETE FROM route_restrictions WHERE driver_id = "+id);
        conn.close();
        
        for (int i = 0; i < arr.size(); i++) {
            Double lat = arr.getJSONObject(i).getDouble("lat");
            Double lng = arr.getJSONObject(i).getDouble("lng");
            
            excDB("INSERT INTO route_restrictions (driver_id, xlongitude, ylatitude) VALUES ("+id+","+lng+","+lat+")");
            conn.close();
        }
        obj.put("success", "1");
        obj.put("msg", "done");
       } catch (Exception ex) {
            obj.put("success", "0");
            obj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
       }
        
        return Response.status(200).entity(obj).build();
    }
    
    
    
    
    
    
    ResultSet getDBResultSet(String query) throws Exception{
                    
        Class.forName("com.mysql.jdbc.Driver");            
        //conn = DriverManager.getConnection("jdbc:mysql://localhost/hebadb?" + "user=root&password=");
        conn = DriverManager.getConnection("jdbc:mysql://sql9.freesqldatabase.com/sql9159113?" + "user=sql9159113&password=wREVb19m3G");
        
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);
        return rs;            
    }
    void excDB(String query) throws Exception{
                    
        Class.forName("com.mysql.jdbc.Driver");            
        //conn = DriverManager.getConnection("jdbc:mysql://localhost/hebadb?" + "user=root&password=");
        conn = DriverManager.getConnection("jdbc:mysql://sql9.freesqldatabase.com/sql9159113?" + "user=sql9159113&password=wREVb19m3G");
        Statement st = conn.createStatement();
        st.executeUpdate(query);
    }
    
        
    void excPreparedStatmentDB(String query, byte[] bytearr) throws Exception{
                    
        Class.forName("com.mysql.jdbc.Driver");            
        //conn = DriverManager.getConnection("jdbc:mysql://localhost/hebadb?" + "user=root&password=");
        conn = DriverManager.getConnection("jdbc:mysql://sql9.freesqldatabase.com/sql9159113?" + "user=sql9159113&password=wREVb19m3G");

//        Blob blob = conn.createBlob();
//        blob.setBytes(1, bytearr);
        Blob blob = new javax.sql.rowset.serial.SerialBlob(bytearr);
            
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setBlob(1, blob);
        //pstmt.executeUpdate(query);
        //ResultSet rs=pstmt.executeQuery(query);
        pstmt.execute();
    }
    
    
    
    
    
    

}
