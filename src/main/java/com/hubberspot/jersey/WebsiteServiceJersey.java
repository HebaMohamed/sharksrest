/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hubberspot.jersey;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
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
    
//    Connection conn;
            
    @GET //test only
    @Path("/go")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getgo(){
        String output = "gooooooooooooooooooooo Hebat" ;
        return Response.status(200).entity(output).build();
    }
   
    JSONArray arr;
    JSONObject resobj;
    Firebase myFirebaseRef;
    
    @GET
    @Path("/getdrivers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDrivers(){
//        String query = "SELECT * FROM driver";
//        JSONObject obj = new JSONObject();
        try {
//            ResultSet rs = getDBResultSet(query);
//            obj.put("success", "1");
//            obj.put("msg", "done");
            
            resobj = new JSONObject();
            arr = new JSONArray();
            final CountDownLatch latch = new CountDownLatch(1);
            
            
            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
            
                myFirebaseRef.child("driver").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    int did = Integer.parseInt(postSnapshot.getName());
                                    String dname = postSnapshot.child("fullname").getValue(String.class);
                                    String demail = postSnapshot.child("email").getValue(String.class);
                                    int vid = postSnapshot.child("vid").getValue(Integer.class);

                                    JSONObject o = new JSONObject();
                                    o.put("did", did);
                                    o.put("dname", dname);
                                    o.put("demail", demail);
                                    o.put("vid", vid);
                                    arr.add(o);
                        }
                        
                          resobj.put("drivers", arr);  
                          resobj.put("success", "1");
                          resobj.put("msg", "Selected Successfully");
                          latch.countDown();   
                        
                    }

                @Override
                public void onCancelled() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    
                }
                });
            
                   
//            while(rs.next())
//             {
//                 int did = rs.getInt(1);
//                 String dname = rs.getString(2);
//                 String demail = rs.getString(12);
//                 int vid = rs.getInt(10);                
//                 
//                 JSONObject o = new JSONObject();
//                 o.put("did", did);
//                 o.put("dname", dname);
//                 o.put("demail", demail);
//                 o.put("vid", vid);
//                 arr.add(o);
//             }
                 
//            obj.put("drivers", arr);  
//            conn.close();
            latch.await();
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(resobj).build();
    }
    
    @GET
    @Path("/deletedriver/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteDriver(@PathParam("id") int id){
//        JSONObject obj = new JSONObject();
        try {
//            excDB("DELETE FROM driver WHERE driver_id = "+id);
    
        resobj = new JSONObject();
        myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
        myFirebaseRef.child("driver").child(String.valueOf(id)).setValue("0");


            resobj.put("success", "1");
            resobj.put("msg", "Deleted Successfully");
            
//            conn.close();
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(resobj).build();

    }
    
    @GET
    @Path("/getdriver/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDriver(@PathParam("id") final int id){
//        JSONObject obj = new JSONObject();

            resobj = new JSONObject();
            arr = new JSONArray();
            final CountDownLatch latch = new CountDownLatch(1);
            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
        try {
            int vid = 0;
             myFirebaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String dname = dataSnapshot.child("driver").child(String.valueOf(id)).child("fullname").getValue(String.class);
                        String demail = dataSnapshot.child("driver").child(String.valueOf(id)).child("email").getValue(String.class);
                        int vid = dataSnapshot.child("driver").child(String.valueOf(id)).child("vid").getValue(Integer.class);
                        String img = dataSnapshot.child("driver").child(String.valueOf(id)).child("img").getValue(String.class);

                        JSONObject driverobj = new JSONObject();  
                        driverobj.put("id", id);
                        driverobj.put("name", dname);
                        driverobj.put("vehicle_id", vid);
                        driverobj.put("email", demail);
                        driverobj.put("password", "");
                        driverobj.put("image", img);
                        
                        
                        
                        JSONArray ristrictedroute = new JSONArray();
                        for (DataSnapshot postSnapshot : dataSnapshot.child("driver").child(String.valueOf(id)).child("route_restrictions").getChildren()) {
                            int rid = Integer.parseInt(postSnapshot.getName());
                            double lat = postSnapshot.child("lat").getValue(Double.class);
                            double lng = postSnapshot.child("lng").getValue(Double.class);
                            
                            JSONObject routeobj = new JSONObject(); 
                            routeobj.put("xlongitude", lat);
                            routeobj.put("ylatitude", lng);
                            ristrictedroute.add(routeobj);
                        }
                        
                        
                        
                        String model = dataSnapshot.child("vehicles").child(String.valueOf(vid)).child("model").getValue(String.class);
                        String color = dataSnapshot.child("vehicles").child(String.valueOf(vid)).child("color").getValue(String.class);
                        String plate_number = dataSnapshot.child("vehicles").child(String.valueOf(vid)).child("plate_number").getValue(String.class);

                        JSONObject vobj = new JSONObject();
                        vobj.put("vehicle_id", vid);
                        vobj.put("model", model);
                        vobj.put("color", color);
                        vobj.put("plate_number", plate_number);
                        
                        resobj.put("ristrictedroute", ristrictedroute);
                        resobj.put("driver", driverobj);
                        resobj.put("vehicle", vobj);
                        resobj.put("success", "1");
                        resobj.put("msg", "Done Successfully");
                        latch.countDown();   
                    }

                @Override
                public void onCancelled() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    
                }
                });
            
//            ResultSet rs = getDBResultSet("SELECT * FROM driver WHERE driver_id = "+id);
//            JSONObject driverobj = new JSONObject();   
//            while(rs.next())
//             {        
//                 vid = rs.getInt(10);
//                 driverobj.put("id", rs.getInt(1));
//                 driverobj.put("name", rs.getString(2));
//                 driverobj.put("sharp_turns_freq", rs.getDouble(3));
//                 driverobj.put("lane_changing_freq", rs.getDouble(4));
//                 driverobj.put("harch_acc_freq", rs.getDouble(5));
//                 driverobj.put("last_trip_behavoir_map", rs.getBlob(6));
//                 driverobj.put("wrong_u_turns_severity", rs.getDouble(7));
//                 driverobj.put("harsh_breaking_freq", rs.getDouble(8));
//                 driverobj.put("awareness_level", rs.getDouble(9));
//                 driverobj.put("vehicle_id", vid);
//                 driverobj.put("vehicle_datetime", rs.getString(11));
//                 driverobj.put("email", rs.getString(12));
//                 driverobj.put("password", rs.getString(13));
//                 //driverobj.put("image", rs.getString(14));
//                 
//                 Blob imageBlob = rs.getBlob(14);
//                 byte[] byteArray = imageBlob.getBytes(1, (int) imageBlob.length());
//                 String str = new sun.misc.BASE64Encoder().encode(byteArray);
//                 driverobj.put("image", str);
//                 
//             }            
//            conn.close();
            
            
//            ResultSet rs2 = getDBResultSet("SELECT * FROM route_restrictions WHERE driver_id = "+id);
//            JSONArray ristrictedroute = new JSONArray();
//            while(rs2.next())
//            {
//                JSONObject routeobj = new JSONObject(); 
//                routeobj.put("xlongitude", rs2.getDouble(3));
//                routeobj.put("ylatitude", rs2.getDouble(4));
//                ristrictedroute.add(routeobj);
//            }
            
//            ResultSet rs3 = getDBResultSet("SELECT * FROM vehicle WHERE vehicle_id = "+vid);
//            JSONObject vobj = new JSONObject(); 
//            while(rs3.next())
//            {
//                vobj.put("vehicle_id", rs3.getInt(1));
//                vobj.put("model", rs3.getString(2));
//                vobj.put("color", rs3.getString(3));
//                vobj.put("plate_number", rs3.getString(5));
//            }
            
            
            
//            obj.put("ristrictedroute", ristrictedroute);
//            obj.put("driver", driverobj);
//            obj.put("vehicle", vobj);
//            obj.put("success", "1");
//            obj.put("msg", "Done Successfully");
//            conn.close();
            latch.await();
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.status(200).entity(resobj).build();

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
            final String name = dataObj.getString("name");
            final String email = dataObj.getString("email");
            final String password = dataObj.getString("password");
            final String img = dataObj.getString("img");
            
//    JSONObject obj = new JSONObject();
        resobj = new JSONObject();
        myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
        
        try {
            
             
                myFirebaseRef.child("driver").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long count = dataSnapshot.getChildrenCount();      
                        long insertedid = count+1;
                        
                        myFirebaseRef.child("driver").child(String.valueOf(insertedid)).child("email").setValue(email);
                        myFirebaseRef.child("driver").child(String.valueOf(insertedid)).child("fullname").setValue(name);
                        myFirebaseRef.child("driver").child(String.valueOf(insertedid)).child("password").setValue(password);
                        myFirebaseRef.child("driver").child(String.valueOf(insertedid)).child("img").setValue(img);

                        resobj.put("success", "1");
                        resobj.put("msg", "Added Successfully");
                    }

                @Override
                public void onCancelled() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    
                }
                });
                
            //for img 
//            byte[] bytearr = new sun.misc.BASE64Decoder().decodeBuffer(img);
//            excPreparedStatmentDB("INSERT INTO driver (driver_id, fullname, sharp_turns_freq, lane_changing_freq, harch_acc_freq, last_trip_behavoir_map, wrong_u_turns_severity, harsh_breaking_freq, awareness_level, vehicle_id, vehicle_datetime, email, password, image)"+
//                                                         " VALUES (NULL, '"+name+"', 0, 0, 0, NULL, 0, 0, 0, NULL, NULL, '"+email+"', '"+password+"', ? );",bytearr);
          
            
//            conn.close();
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(resobj).build();
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
        JSONObject dataobj = JSONObject.fromObject(data);
        int id = dataobj.getInt("id");
        String name = dataobj.getString("name");
        String email = dataobj.getString("email");
        String password = dataobj.getString("password");
        String img = dataobj.getString("img");
//        JSONObject obj = new JSONObject();
        resobj = new JSONObject();
        myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
        try {         
            //for img 
            byte[] bytearr = new sun.misc.BASE64Decoder().decodeBuffer(img);
            String j="";
            if(password.equals("null")&&img.equals("")){//if pass="" so it dont changed otherwise change it
//                j = "UPDATE driver SET fullname = '"+name.replaceAll(","," ")+"' , email = '"+email+"'  WHERE driver_id = "+id+";";
//                excDB(j);
                myFirebaseRef.child("driver").child(String.valueOf(id)).child("email").setValue(email);
                myFirebaseRef.child("driver").child(String.valueOf(id)).child("fullname").setValue(name.replaceAll(","," "));
            }else if (!password.equals("null")&&img.equals("")){
//                j = "UPDATE driver SET fullname = '"+name.replaceAll(","," ")+"' , email = '"+email+"' , password = '"+password+"' WHERE driver_id = "+id+";";
//                excDB(j);
                myFirebaseRef.child("driver").child(String.valueOf(id)).child("email").setValue(email);
                myFirebaseRef.child("driver").child(String.valueOf(id)).child("fullname").setValue(name.replaceAll(","," "));
                myFirebaseRef.child("driver").child(String.valueOf(id)).child("password").setValue(password);
            }else if (!password.equals("null")&&!img.equals("")){
//                j = "UPDATE driver SET fullname = '"+name.replaceAll(","," ")+"' , email = '"+email+"' , password = '"+password+"' , image = ? WHERE driver_id = "+id+";";
//                excPreparedStatmentDB(j,bytearr);
                myFirebaseRef.child("driver").child(String.valueOf(id)).child("email").setValue(email);
                myFirebaseRef.child("driver").child(String.valueOf(id)).child("fullname").setValue(name.replaceAll(","," "));
                myFirebaseRef.child("driver").child(String.valueOf(id)).child("password").setValue(password);
                myFirebaseRef.child("driver").child(String.valueOf(id)).child("img").setValue(img);
            }else if (password.equals("null")&&!img.equals("")){
//                j = "UPDATE driver SET fullname = '"+name.replaceAll(","," ")+"' , email = '"+email+"' , image = ? WHERE driver_id = "+id+";";
//                excPreparedStatmentDB(j,bytearr);
                myFirebaseRef.child("driver").child(String.valueOf(id)).child("email").setValue(email);
                myFirebaseRef.child("driver").child(String.valueOf(id)).child("fullname").setValue(name.replaceAll(","," "));
                myFirebaseRef.child("driver").child(String.valueOf(id)).child("img").setValue(img);
            }
            //
            
            resobj.put("success", "1");
            resobj.put("msg", "Edited Successfully");
            
//            conn.close();
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(resobj).build();
    }
    
    
    
    @GET
    @Path("/assignvehicle/{vid}/{did}/{ovid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response assignvehicle(@PathParam("vid") int vid, @PathParam("did") int did, @PathParam("ovid") int ovid){//old vid
//        JSONObject obj = new JSONObject();
        try {
            resobj = new JSONObject();
            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
            
            myFirebaseRef.child("driver").child(String.valueOf(did)).child("vid").setValue(vid);
            
            long t = System.currentTimeMillis();
            myFirebaseRef.child("vehicleshistory").child(String.valueOf(t)).child("did").setValue(did);
            myFirebaseRef.child("vehicleshistory").child(String.valueOf(t)).child("vid").setValue(vid);

            //get current datetime 
//            java.util.Date dt = new java.util.Date();
//            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String currentTime = sdf.format(dt);
//            String j = "UPDATE driver SET vehicle_id = '"+vid+"' , vehicle_datetime = '"+currentTime+"' WHERE driver_id = "+did+";";
//            excDB(j);
//            conn.close();
            
//            String jj = "UPDATE vehicle SET outside_working_time_state = 'no' WHERE vehicle_id = "+vid+";";
//            excDB(jj);
//            conn.close();
//            
//            String jjj = "UPDATE vehicle SET outside_working_time_state = 'yes' WHERE vehicle_id = "+ovid+";";
//            excDB(jjj);
//            conn.close();
            
            resobj.put("success", "1");
            resobj.put("msg", "Edited Successfully");
            
            
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(resobj).build();
    }
    
    
    
    
    //////////////////////////////////////////////////////////////for Vehicles
    @GET
    @Path("/getvehicles")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVehicles(){
//        String query = "SELECT * FROM vehicle";
//        JSONObject obj = new JSONObject();
        try {
//            ResultSet rs = getDBResultSet(query);
//            obj.put("success", "1");
//            obj.put("msg", "done");
            
//            JSONArray arr = new JSONArray();
               
            resobj = new JSONObject();
            arr = new JSONArray();
            final CountDownLatch latch = new CountDownLatch(1);
            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
            
            
            myFirebaseRef.child("vehicles").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    int vehicle_id = Integer.parseInt(postSnapshot.getName());
                                    String model = postSnapshot.child("model").getValue(String.class);
                                    String color = postSnapshot.child("color").getValue(String.class);
                                    String plate_number = postSnapshot.child("plate_number").getValue(String.class);

                                    JSONObject o = new JSONObject();
                                    o.put("vehicle_id", vehicle_id);
                                    o.put("model", model);
                                    o.put("color", color);
                                    o.put("outside_working_time_state", "yes");// for ex
                                    o.put("plate_number", plate_number);
                                    arr.add(o);
                        }
                        
                          resobj.put("vehicles", arr);  
                          resobj.put("success", "1");
                          resobj.put("msg", "Selected Successfully");
                          latch.countDown();   
                        
                    }

                @Override
                public void onCancelled() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    
                }
                });
            
            
//            while(rs.next())
//             {
//                 int vehicle_id = rs.getInt(1);
//                 String model = rs.getString(2);
//                 String color = rs.getString(3);
//                 String outside_working_time_state = rs.getString(4);  
//                 String plate_number = rs.getString(5);
//                 
//                 JSONObject o = new JSONObject();
//                 o.put("vehicle_id", vehicle_id);
//                 o.put("model", model);
//                 o.put("color", color);
//                 o.put("outside_working_time_state", outside_working_time_state);
//                 o.put("plate_number", plate_number);
//
//                 arr.add(o);
//             }
                 
//            obj.put("vehicles", arr);  
//            conn.close();
            latch.await();
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(resobj).build();
    }
    
    @GET
    @Path("/getvehicle/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVehicle(@PathParam("id") int id){
//        JSONObject obj = new JSONObject();

            resobj = new JSONObject();
            final CountDownLatch latch = new CountDownLatch(1);
            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");

        try {
            myFirebaseRef.child("vehicles").child(String.valueOf(id)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                                    int vehicle_id = Integer.parseInt(dataSnapshot.getName());
                                    String model = dataSnapshot.child("model").getValue(String.class);
                                    String color = dataSnapshot.child("color").getValue(String.class);
                                    String plate_number = dataSnapshot.child("plate_number").getValue(String.class);

                                    JSONObject vehicleobj = new JSONObject();
                                    vehicleobj.put("vehicle_id", vehicle_id);
                                    vehicleobj.put("model", model);
                                    vehicleobj.put("color", color);
                                    vehicleobj.put("outside_working_time_state", "yes");
                                    vehicleobj.put("plate_number", plate_number);
                        
                                    resobj.put("vehicle", vehicleobj);  
                                    resobj.put("success", "1");
                                    resobj.put("msg", "Selected Successfully");
                                    latch.countDown();  
                    }

                @Override
                public void onCancelled() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    
                }
                });
            
//            ResultSet rs = getDBResultSet("SELECT * FROM vehicle WHERE vehicle_id = "+id);
//            JSONObject vehicleobj = new JSONObject();   
//            while(rs.next())
//             {           
//                 int vehicle_id = rs.getInt(1);
//                 String model = rs.getString(2);
//                 String color = rs.getString(3);
//                 String outside_working_time_state = rs.getString(4);  
//                 String plate_number = rs.getString(5);
//                 
//                 vehicleobj.put("vehicle_id", vehicle_id);
//                 vehicleobj.put("model", model);
//                 vehicleobj.put("color", color);
//                 vehicleobj.put("outside_working_time_state", outside_working_time_state);
//                 vehicleobj.put("plate_number", plate_number);
//             }
//            obj.put("vehicle", vehicleobj);
//            obj.put("success", "1");
//            obj.put("msg", "Selected Successfully");
            
//            conn.close();
            latch.await();
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.status(200).entity(resobj).build();

    }
    
    @GET
    @Path("/addvehicle/{model}/{color}/{plate_number}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addVehicle(@PathParam("model") final String model, @PathParam("color") final String color, @PathParam("plate_number") final String plate_number){
//        JSONObject obj = new JSONObject();
        try {
            
            resobj = new JSONObject();
            final CountDownLatch latch = new CountDownLatch(1);
            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
            
            
            myFirebaseRef.child("vehicles").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long count = dataSnapshot.getChildrenCount();      
                        long insertedid = count+1;
                        
                    myFirebaseRef.child("vehicles").child(String.valueOf(insertedid)).child("Altitude").setValue("0");
                    myFirebaseRef.child("vehicles").child(String.valueOf(insertedid)).child("Heading").setValue("0");
                    myFirebaseRef.child("vehicles").child(String.valueOf(insertedid)).child("Latitude").setValue("0");
                    myFirebaseRef.child("vehicles").child(String.valueOf(insertedid)).child("Load").setValue("0");
                    myFirebaseRef.child("vehicles").child(String.valueOf(insertedid)).child("Longitude").setValue("0");
                    myFirebaseRef.child("vehicles").child(String.valueOf(insertedid)).child("RPM").setValue("0");
                    myFirebaseRef.child("vehicles").child(String.valueOf(insertedid)).child("Speed").setValue("0");
                    myFirebaseRef.child("vehicles").child(String.valueOf(insertedid)).child("Throttle").setValue("0");
                    myFirebaseRef.child("vehicles").child(String.valueOf(insertedid)).child("color").setValue(color);
                    myFirebaseRef.child("vehicles").child(String.valueOf(insertedid)).child("lat").setValue("0");
                    myFirebaseRef.child("vehicles").child(String.valueOf(insertedid)).child("lng").setValue("0");
                    myFirebaseRef.child("vehicles").child(String.valueOf(insertedid)).child("model").setValue(model);
                    myFirebaseRef.child("vehicles").child(String.valueOf(insertedid)).child("plate_number").setValue(plate_number);
                    myFirebaseRef.child("vehicles").child(String.valueOf(insertedid)).child("status").setValue("0");

                    resobj.put("success", "1");
                    resobj.put("msg", "Added Successfully");

                    resobj.put("insertedid", insertedid);
                 }

                @Override
                public void onCancelled() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
                });
            
//            excDB("INSERT INTO vehicle (vehicle_id, model, color, outside_working_time_state, plate_number) "+
//                                 "VALUES (NULL, '"+model+"', '"+color+"', 'yes', '"+plate_number+"');");
//            resobj.put("success", "1");
//            resobj.put("msg", "Added Successfully");
        
            latch.await();
            
//            conn.close();
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(resobj).build();
    }
    
    @GET
    @Path("/editvehicle/{id}/{model}/{color}/{plate_number}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response editVehicle(@PathParam("id") int id, @PathParam("model") String model, @PathParam("color") String color, @PathParam("plate_number") String plate_number){
//        JSONObject obj = new JSONObject();
        try {
            resobj = new JSONObject();
            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
            myFirebaseRef.child("vehicles").child(String.valueOf(id)).child("color").setValue(color);
            myFirebaseRef.child("vehicles").child(String.valueOf(id)).child("model").setValue(model);
            myFirebaseRef.child("vehicles").child(String.valueOf(id)).child("plate_number").setValue(plate_number);
            
//            excDB("UPDATE vehicle SET model = '"+model+"', color = '"+color+"', plate_number = '"+plate_number+"' WHERE vehicle_id = "+id+";");
            resobj.put("success", "1");
            resobj.put("msg", "Edited Successfully");
            
//            conn.close();
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(resobj).build();
    }
   
    @GET
    @Path("/deletevehicle/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteVehicle(@PathParam("id") int id){
//        JSONObject obj = new JSONObject();
        try {
//            excDB("DELETE FROM vehicle WHERE vehicle_id = "+id);

            resobj = new JSONObject();
            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
            myFirebaseRef.child("vehicles").child(String.valueOf(id)).setValue("0");
            
            resobj.put("success", "1");
            resobj.put("msg", "Deleted Successfully");
            
//            conn.close();
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(resobj).build();

    }
    
    
    //////////////////////////////////////////////////////////////for Monitoring
    @GET
    @Path("/getcurrentvds")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentVDs(){
        
            resobj = new JSONObject();
            arr = new JSONArray();
            final CountDownLatch latch = new CountDownLatch(1);
            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
        
//        String query = "SELECT * FROM vehicle, driver WHERE outside_working_time_state = 'no' AND vehicle.vehicle_id = driver.vehicle_id";
//        JSONObject obj = new JSONObject();
        try {
//            ResultSet rs = getDBResultSet(query);
            resobj.put("success", "1");
            resobj.put("msg", "done");
            
                myFirebaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                    int onlineDriversCount=0;

                        for (DataSnapshot postSnapshot : dataSnapshot.child("driver").getChildren()) {
                               
                            int did = Integer.parseInt(postSnapshot.getName());

                            String vid = postSnapshot.child("vid").getValue(String.class);
                            String fullname = postSnapshot.child("fullname").getValue(String.class);

                                    String model = dataSnapshot.child("vehicles").child(String.valueOf(vid)).child("model").getValue(String.class);
                                    String color = dataSnapshot.child("vehicles").child(String.valueOf(vid)).child("color").getValue(String.class);
                                    String plate_number = dataSnapshot.child("vehicles").child(String.valueOf(vid)).child("plate_number").getValue(String.class);
                                    int status = dataSnapshot.child("vehicles").child(String.valueOf(vid)).child("status").getValue(Integer.class);
                                    if(status==0)
                                        onlineDriversCount++;
                                    
                                    JSONObject o = new JSONObject();
                                    o.put("vehicle_id", vid);
                                    o.put("model", model);
                                    o.put("color", color);
                                    o.put("plate_number", plate_number);
                                    o.put("driver_id", did);
                                    o.put("fullname", fullname);
                                    o.put("vehicle_datetime", "");

                            arr.add(o);
                        }
                        resobj.put("currentvms", arr);  
                        
                        
                        long alldriverscount =dataSnapshot.child("driver").getChildrenCount();
                        int offlineDriversCount = (int) (alldriverscount-onlineDriversCount);
                        long passengerscount =dataSnapshot.child("passenger").getChildrenCount();
                        long tripscount = dataSnapshot.child("trips").getChildrenCount();
                        
                        JSONArray rattingArr = new JSONArray();
                        for (DataSnapshot postSnapshot : dataSnapshot.child("trips").getChildren()) {
                            int trip_id = Integer.parseInt(postSnapshot.getName());
                            int ratting = postSnapshot.child("ratting").getValue(Integer.class);
                            JSONObject o = new JSONObject();
                            o.put("trip_id", trip_id);
                            o.put("ratting", ratting);
                            rattingArr.add(o);      
                        }
                        
//               
                        resobj.put("success", "1");
                        resobj.put("msg", "Selected Successfully");

                        resobj.put("alldriverscount", alldriverscount);
                        resobj.put("passengerscount", passengerscount);
                        resobj.put("tripscount", tripscount);
                        resobj.put("rattingArr", rattingArr);
                        resobj.put("onlineDriversCount", onlineDriversCount);
                        resobj.put("offlineDriversCount", offlineDriversCount);
                        latch.countDown();   
                        
                    }

                @Override
                public void onCancelled() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
                });
        
            
//            JSONArray arr = new JSONArray();
                   
//            int onlineDriversCount=0;
//            while(rs.next())
//             {
//                 onlineDriversCount++;
//                 int vehicle_id = rs.getInt(1);
//                 String model = rs.getString(2);
//                 String color = rs.getString(3);
//                 String plate_number = rs.getString(5);
//                 
//                 String driver_id = rs.getString(6);
//                 String fullname = rs.getString(7);
//                 String vehicle_datetime = rs.getString(16);
//
//                 
//                 JSONObject o = new JSONObject();
//                 o.put("vehicle_id", vehicle_id);
//                 o.put("model", model);
//                 o.put("color", color);
//                 o.put("plate_number", plate_number);
//                 o.put("driver_id", driver_id);
//                 o.put("fullname", fullname);
//                 o.put("vehicle_datetime", vehicle_datetime);
//
//                 arr.add(o);
//             }
                 
//            obj.put("currentvms", arr);  
//            conn.close();
            
            //for another controls
//            ResultSet rs2 = getDBResultSet("SELECT COUNT(*) FROM driver");
//            rs2.first();
//            int alldriverscount =Integer.parseInt(rs2.getString(1));
//            int offlineDriversCount = alldriverscount-onlineDriversCount;
//            conn.close();
            
//            ResultSet rs3 = getDBResultSet("SELECT COUNT(*) FROM passenger");
//            rs3.first();
//            int passengerscount =Integer.parseInt(rs3.getString(1));
            
            
//            int tripscount =0;            
//            JSONArray rattingArr = new JSONArray();
//            ResultSet rs4 = getDBResultSet("SELECT * FROM trip");
//            while(rs4.next())
//             {
//                 tripscount++;
//                 int trip_id = rs4.getInt(1);
//                 int ratting = rs4.getInt(6);
//                 JSONObject o = new JSONObject();
//                 o.put("trip_id", trip_id);
//                 o.put("ratting", ratting);
//                 rattingArr.add(o);
//             }
//            obj.put("alldriverscount", alldriverscount);
//            obj.put("passengerscount", passengerscount);
//            obj.put("tripscount", tripscount);
//            obj.put("rattingArr", rattingArr);
//            obj.put("onlineDriversCount", onlineDriversCount);
//            obj.put("offlineDriversCount", offlineDriversCount);
            
//            conn.close();
            latch.await();
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            latch.countDown();  
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(resobj).build();
    }
    
    /////////////////////////////////////////////////////////////forlogin
    
    @GET
    @Path("/loginmember/{id}/{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response LoginMonitor(@PathParam("id") final String id, @PathParam("password") final String password){
//        JSONObject obj = new JSONObject();
          resobj = new JSONObject();
          final CountDownLatch latch = new CountDownLatch(1);
          myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
          
                          
            //in case if there is no member mh hyd5l fl loop asln
            resobj.put("success", "0");
            resobj.put("msg", "Wrong ID");

        try {
            
            myFirebaseRef.child("monitoring_member").child(String.valueOf(id)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        
                    String pass = dataSnapshot.child("password").getValue(String.class);                    
                    if(pass.equals(password)){
                        int mid = Integer.parseInt(dataSnapshot.getName());
                        String name = dataSnapshot.child("fullname").getValue(String.class);                    
                        String gender = dataSnapshot.child("gender").getValue(String.class);                    
                        String lastlogin_time = dataSnapshot.child("lastlogin_time").getValue(String.class);                    
                        String account_state = dataSnapshot.child("account_state").getValue(String.class);     
                        
                    resobj.put("success", "1");
                    resobj.put("msg", "Added Successfully");
                    JSONObject m = new JSONObject();
                     m.put("id", mid);
                     m.put("name", name);
                     m.put("gender", gender);
                     m.put("lastlogin_time", lastlogin_time);
                     m.put("account_state", account_state);

                     resobj.put("member", m);
                     latch.countDown();                       
                    }
                    else{
                        resobj.put("success", "0");
                        resobj.put("msg", "Wrong Password");
                        latch.countDown();      
                    }
                    
                 }

                @Override
                public void onCancelled() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
                });
            
//            ResultSet rs = getDBResultSet("SELECT * FROM monitoring_member WHERE user_id = "+id);
             
//            while(rs.next())
//             {           
//
//                 
//                 String pass = rs.getString(2);
//                 if(pass.equals(password))
//                 {
//                     //login
//                     obj.put("success", "1");
//                     obj.put("msg", "Logged in successfully");
//                     
//                     JSONObject m = new JSONObject();
//                     String name = rs.getString(3);
//                     String gender = rs.getString(4);
//                     String lastlogin_time = rs.getString(5);
//                     String account_state = rs.getString(6);
//                     
//                     m.put("id", id);
//                     m.put("name", name);
//                     m.put("gender", gender);
//                     m.put("lastlogin_time", lastlogin_time);
//                     m.put("account_state", account_state);
//
//                     obj.put("member", m);
//
//                 }
//                 else {
//                     obj.put("success", "0");
//                     obj.put("msg", "Wrong Password");
//                 }
//
//             }
//            
//            conn.close();
            latch.await();
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
            latch.countDown();      
        }

        return Response.status(200).entity(resobj).build();

    }
    
    @GET
    @Path("/addmonitormember/{name}/{gender}/{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMonitor(@PathParam("name") final String name, @PathParam("gender") final String gender, @PathParam("password") final String password){
//        JSONObject obj = new JSONObject();
        try {
            
            
            resobj = new JSONObject();
            final CountDownLatch latch = new CountDownLatch(1);
            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
            
            
            myFirebaseRef.child("monitoring_member").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long count = dataSnapshot.getChildrenCount();      
                        long insertedid = count+1;
                        
                    myFirebaseRef.child("monitoring_member").child(String.valueOf(insertedid)).child("account_state").setValue("0");
                    myFirebaseRef.child("monitoring_member").child(String.valueOf(insertedid)).child("datetime_monitor_driver").setValue("0");
                    myFirebaseRef.child("monitoring_member").child(String.valueOf(insertedid)).child("datetime_vehicle_monitor").setValue("0");
                    myFirebaseRef.child("monitoring_member").child(String.valueOf(insertedid)).child("Load").setValue("0");
                    myFirebaseRef.child("monitoring_member").child(String.valueOf(insertedid)).child("driver_id").setValue("0");
                    myFirebaseRef.child("monitoring_member").child(String.valueOf(insertedid)).child("fullname").setValue(name);
                    myFirebaseRef.child("monitoring_member").child(String.valueOf(insertedid)).child("gender").setValue(gender);
                    myFirebaseRef.child("monitoring_member").child(String.valueOf(insertedid)).child("lastlogin_time").setValue("0");
                    myFirebaseRef.child("monitoring_member").child(String.valueOf(insertedid)).child("password").setValue(password);
                    myFirebaseRef.child("monitoring_member").child(String.valueOf(insertedid)).child("vehicle_id").setValue("0");

                    resobj.put("success", "1");
                    resobj.put("msg", "Added Successfully");

                    resobj.put("insertedid", insertedid);
                    latch.countDown();   
                 }

                @Override
                public void onCancelled() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
                });
            
            //check if admin
//            String accstate = "";
//            ResultSet rs = getDBResultSet("SELECT COUNT(*) FROM monitoring_member");
//            int count =Integer.parseInt(rs.getString(1));
//            if(count==0)
//                accstate="Admin";
//            else
//                accstate="Pending";
            
//            excDB("INSERT INTO monitoring_member (user_id, password, fullname, gender, lastlogin_time, account_state, vehicle_id, datetime_vehicle_monitor, datetime_monitor_driver, driver_id) "+
//                                             " VALUES (NULL, '"+password+"', '"+name+"', '"+gender+"', NULL, '"+accstate+"', NULL, NULL, NULL, NULL);");
//            obj.put("success", "1");
//            obj.put("msg", "Added Successfully");
            
//            conn.close();

            latch.await();
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(resobj).build();
    }
    
    
    //////////////////////////////////////////////////////////////for pending
        
    @GET
    @Path("/getpmembers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMembers(){
//        String query = "SELECT * FROM monitoring_member";
//        JSONObject obj = new JSONObject();

            resobj = new JSONObject();
            arr = new JSONArray();
            final CountDownLatch latch = new CountDownLatch(1);
            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
        try {
            
            
            myFirebaseRef.child("monitoring_member").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            int mid = Integer.parseInt(postSnapshot.getName());
                            String name = postSnapshot.child("fullname").getValue(String.class);                    
                            String gender = postSnapshot.child("gender").getValue(String.class);                    
                            String lastlogin_time = postSnapshot.child("lastlogin_time").getValue(String.class);                    
                            String account_state = postSnapshot.child("account_state").getValue(String.class);  

                            JSONObject o = new JSONObject();
                            o.put("user_id", mid);
                            o.put("fullname", name);
                            o.put("gender", gender);
                            o.put("lastlogin_time", lastlogin_time);
                            o.put("account_state", account_state);
                            if(account_state.equals("0"))
                                arr.add(o);
                        }

                    resobj.put("success", "1");
                    resobj.put("msg", "Selected Successfully");
                    resobj.put("members", arr);  
                    latch.countDown();   
                 }

                @Override
                public void onCancelled() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
                });
            
            
//            ResultSet rs = getDBResultSet(query);
//            obj.put("success", "1");
//            obj.put("msg", "done");
//            
//            JSONArray arr = new JSONArray();
                   
//            while(rs.next())
//             {
//                 String account_state = rs.getString(6);   
//                 if(account_state.equals("pending")){
//
//                 int user_id = rs.getInt(1);
//                 String fullname = rs.getString(3);
//                 String gender = rs.getString(4);
//                 String lastlogin_time = rs.getString(5);                
//
//                 JSONObject o = new JSONObject();
//                 o.put("user_id", user_id);
//                 o.put("fullname", fullname);
//                 o.put("gender", gender);
//                 o.put("lastlogin_time", lastlogin_time);
//                 arr.add(o);
//                 }
//             }
                 
//            obj.put("members", arr);  
//            conn.close();

            latch.await();
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(resobj).build();
    }
    
        @GET
    @Path("/acceptmember/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response acceptmember(@PathParam("id") int id){
        try {
        resobj = new JSONObject();
        myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
        myFirebaseRef.child("monitoring_member").child(String.valueOf(id)).child("account_state").setValue("1");

            resobj.put("success", "1");
            resobj.put("msg", "Accepted Successfully");
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(resobj).build();

    }
    
    /////////////////////////////////////////////////////////////////////////////for trips
    @GET
    @Path("/gettrips")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTrips(){
//        String query = "SELECT * FROM trip";
//        JSONObject obj = new JSONObject();
        resobj = new JSONObject();
        arr = new JSONArray();
        final CountDownLatch latch = new CountDownLatch(1);
        myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");

        try {
//            ResultSet rs = getDBResultSet(query);
            resobj.put("success", "1");
            resobj.put("msg", "done");
            
            myFirebaseRef.child("trips").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                int pid = postSnapshot.child("pid").getValue(Integer.class);
                                JSONArray paths = new JSONArray();
//                                if(pid == id){

                                    int tid = Integer.parseInt(postSnapshot.getName());
                                    String comment = postSnapshot.child("comment").getValue(String.class);
//                                    String details = postSnapshot.child("details").getValue(String.class);
                                    String did = postSnapshot.child("did").getValue(String.class);
                                    String end = postSnapshot.child("end").getValue(String.class);
                                    String price = postSnapshot.child("price").getValue(String.class);
                                    String ratting = postSnapshot.child("ratting").getValue(String.class);
                                    String start = postSnapshot.child("start").getValue(String.class);

                                    
                                    JSONObject o = new JSONObject();
                                    o.put("trip_id", tid);
                                    o.put("start", start);
                                    o.put("end", end);
                                    o.put("price", price);
                                    o.put("comment", comment);
                                    o.put("ratting", ratting);
                                    o.put("passenger_id", pid);
                                    o.put("driver_id", did);

                                    try{
                                        for (DataSnapshot postSnapshot2 : postSnapshot.child("pathway").getChildren()) {

                                            double lat = postSnapshot2.child("lat").getValue(Double.class);
                                            double lng = postSnapshot2.child("lng").getValue(Double.class);

                                            JSONObject latlng = new JSONObject();
                                            latlng.put("lat", lat);                    
                                            latlng.put("lng", lng);
                                            paths.add(latlng);

                                        }
                                    }catch(NullPointerException ne){
                                        Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ne);
                                    }catch(NumberFormatException ne){
                                        Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ne);
                                    }
                               
                                    o.put("pathway",paths);
                                    arr.add(o);
                                    
//                                }
                            
                            
                        }
                        
                          resobj.put("trips", arr);
                          resobj.put("success", "1");
                          resobj.put("msg", "Selected Successfully");
                          latch.countDown();   
                        
                    }

                @Override
                public void onCancelled() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    
                }
                });

            
            
//            JSONArray arr = new JSONArray();
                   
//            while(rs.next())
//             {
//                 int trip_id = rs.getInt(1);
//                 String start = rs.getString(2);
//                 String end = rs.getString(3);
//                 Double price = rs.getDouble(4);
//                 String comment = rs.getString(5);
//                 Double ratting = rs.getDouble(6);
//                 int passenger_id = rs.getInt(7);    
//                 int driver_id = rs.getInt(8);
//                 
//                 JSONObject o = new JSONObject();
//                 o.put("trip_id", trip_id);
//                 o.put("start", start);
//                 o.put("end", end);
//                 o.put("price", price);
//                 o.put("comment", comment);
//                 o.put("ratting", ratting);
//                 o.put("passenger_id", passenger_id);
//                 o.put("driver_id", driver_id);
//                 arr.add(o);
//             }
//                 
//            obj.put("trips", arr);  
//            conn.close();

            latch.await();
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(resobj).build();
    }
    
    //get trip pathway map
    @GET
    @Path("/getpathwaymap/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPathwaymap(@PathParam("id") int id){
//        String query = "SELECT * FROM pathwaymap WHERE trip_id = "+id;
//        JSONObject obj = new JSONObject();
        try {
            
            resobj = new JSONObject();
            arr = new JSONArray();
            final CountDownLatch latch = new CountDownLatch(1);
            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");

            myFirebaseRef.child("trips").child(String.valueOf(id)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        
                        int tid = Integer.parseInt(dataSnapshot.getName());

//                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                                int pid = postSnapshot.child("pid").getValue(Integer.class);
//                                JSONArray paths = new JSONArray();
//                                if(pid == id){
                                    try{
                                        for (DataSnapshot postSnapshot2 : dataSnapshot.child("pathway").getChildren()) {

                                            double lat = postSnapshot2.child("lat").getValue(Double.class);
                                            double lng = postSnapshot2.child("lng").getValue(Double.class);
                                            
                                            JSONObject o = new JSONObject();
                                            o.put("trip_id", tid);
                                            o.put("yattitude", lat);
                                            o.put("xlongitude", lng);
                                            arr.add(o);

                                        }
                                    }catch(NullPointerException ne){
                                        Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ne);
                                    }catch(NumberFormatException ne){
                                        Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ne);
                                    }
//                                }
//                        }
                        
                          resobj.put("pathwaymap", arr); 
                          resobj.put("success", "1");
                          resobj.put("msg", "Selected Successfully");
                          latch.countDown();   
                    }

                @Override
                public void onCancelled() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    
                }
                });

            
//            ResultSet rs = getDBResultSet(query);
//            obj.put("success", "1");
//            obj.put("msg", "done");
//            
//            JSONArray arr = new JSONArray();
//                   
//            while(rs.next())
//             {
//                 int trip_id = rs.getInt(1);
//                 Double yattitude = rs.getDouble(2);
//                 Double xlongitude = rs.getDouble(3);
//                 
//                 JSONObject o = new JSONObject();
//                 o.put("trip_id", trip_id);
//                 o.put("yattitude", yattitude);
//                 o.put("xlongitude", xlongitude);
//                 arr.add(o);
//             }
//                 
//            obj.put("pathwaymap", arr);  
//            conn.close();
            latch.await();
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(resobj).build();
    }
    //add pathway map
    @POST
    @Path("/addristrictedroute")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addristrictedroute(String data){
                
            resobj = new JSONObject();
            arr = new JSONArray();
            final CountDownLatch latch = new CountDownLatch(1);
            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
//        JSONObject obj = new JSONObject();
        try {
            
        JSONObject objj = JSONObject.fromObject(data);        
        JSONArray jarr = objj.getJSONArray("routes");
        int id = objj.getInt("driver_id");
//        //first delete last route restriction
//        excDB("DELETE FROM route_restrictions WHERE driver_id = "+id);
//        conn.close();
        
        myFirebaseRef.child("driver").child(String.valueOf(id)).child("route_restrictions").setValue("0");

        for (int i = 0; i < jarr.size(); i++) {
            Double lat = jarr.getJSONObject(i).getDouble("lat");
            Double lng = jarr.getJSONObject(i).getDouble("lng");
            
//            excDB("INSERT INTO route_restrictions (driver_id, xlongitude, ylatitude) VALUES ("+id+","+lng+","+lat+")");
//            conn.close();
        myFirebaseRef.child("driver").child(String.valueOf(id)).child("route_restrictions").child(String.valueOf(i)).child("lat").setValue(lat);
        myFirebaseRef.child("driver").child(String.valueOf(id)).child("route_restrictions").child(String.valueOf(i)).child("lng").setValue(lng);

        }
        resobj.put("success", "1");
        resobj.put("msg", "done");
       } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
       }
        
        return Response.status(200).entity(resobj).build();
    }
    
    
    
    
    
    
//    ResultSet getDBResultSet(String query) throws Exception{
//                    
//        Class.forName("com.mysql.jdbc.Driver");            
//        //conn = DriverManager.getConnection("jdbc:mysql://localhost/hebadb?" + "user=root&password=");
//        //conn = DriverManager.getConnection("jdbc:mysql://sql11.freesqldatabase.com/sql11164022?" + "user=sql11164022&password=GLj4H4TT5N");
//        conn = DriverManager.getConnection("jdbc:mysql://64.62.211.131/hobahob1_sharks?" + "user=hobahob1&password=HOBAHOBY1995");
//        
//        Statement st = conn.createStatement();
//        ResultSet rs = st.executeQuery(query);
//        return rs;            
//    }
//    void excDB(String query) throws Exception{
//                    
//        Class.forName("com.mysql.jdbc.Driver");            
//        //conn = DriverManager.getConnection("jdbc:mysql://localhost/hebadb?" + "user=root&password=");
//        //conn = DriverManager.getConnection("jdbc:mysql://sql11.freesqldatabase.com/sql11164022?" + "user=sql11164022&password=GLj4H4TT5N");
//        conn = DriverManager.getConnection("jdbc:mysql://64.62.211.131/hobahob1_sharks?" + "user=hobahob1&password=HOBAHOBY1995");
//        Statement st = conn.createStatement();
//        st.executeUpdate(query);
//    }
//    
//        
//    void excPreparedStatmentDB(String query, byte[] bytearr) throws Exception{
//                    
//        Class.forName("com.mysql.jdbc.Driver");            
//        //conn = DriverManager.getConnection("jdbc:mysql://localhost/hebadb?" + "user=root&password=");
//        //conn = DriverManager.getConnection("jdbc:mysql://sql11.freesqldatabase.com/sql11164022?" + "user=sql11164022&password=GLj4H4TT5N");
//        conn = DriverManager.getConnection("jdbc:mysql://64.62.211.131/hobahob1_sharks?" + "user=hobahob1&password=HOBAHOBY1995");
////        Blob blob = conn.createBlob();
////        blob.setBytes(1, bytearr);
//        Blob blob = new javax.sql.rowset.serial.SerialBlob(bytearr);
//            
//        PreparedStatement pstmt = conn.prepareStatement(query);
//        pstmt.setBlob(1, blob);
//        //pstmt.executeUpdate(query);
//        //ResultSet rs=pstmt.executeQuery(query);
//        pstmt.execute();
//    }
//    
    
    
    
    
    

}
