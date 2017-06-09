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
import java.util.Calendar;
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
        String output = "gooooooooooooooooooooo Hebat";
//        Firebase  myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
//        myFirebaseRef.child("driver").removeValue();
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
                        boolean logged = dataSnapshot.child("driver").child(String.valueOf(id)).child("logged").getValue(boolean.class);

                        JSONObject driverobj = new JSONObject();  
                        driverobj.put("id", id);
                        driverobj.put("name", dname);
                        driverobj.put("vehicle_id", vid);
                        driverobj.put("email", demail);
                        driverobj.put("password", "");
                        driverobj.put("image", img);
                        driverobj.put("logged", logged);
                        
                        driverobj.put("intrip", false);//for init only

                        //get current trip status
                        for (DataSnapshot postSnapshot : dataSnapshot.child("trips").getChildren()) {
                            int tid = Integer.parseInt(postSnapshot.getName());
                            int did = postSnapshot.child("did").getValue(int.class);
                            if(did == id){
                                String status = postSnapshot.child("status").getValue(String.class);
                                if(status.equals("started")){
                                    driverobj.put("intrip", true);
                                    
                                    double ilat = postSnapshot.child("ilat").getValue(double.class);
                                    double ilng = postSnapshot.child("ilng").getValue(double.class);

                                    double destlat = postSnapshot.child("destlat").getValue(double.class);
                                    double destlng = postSnapshot.child("destlng").getValue(double.class);

                                    long start = postSnapshot.child("start").getValue(long.class);

                                    JSONObject trip = new JSONObject();
                                    trip.put("tid", tid);
                                    trip.put("ilat", ilat);
                                    trip.put("ilng", ilng);
                                    trip.put("destlat", destlat);
                                    trip.put("destlng", destlng);
                                    trip.put("start",start);
                                    
                                    driverobj.put("trip", trip);
                                }
                            }
                        }

                        
                        
                        JSONArray ristrictedroute = new JSONArray();
                        for (DataSnapshot postSnapshot : dataSnapshot.child("driver").child(String.valueOf(id)).child("route_restrictions").getChildren()) {
                            int rid = Integer.parseInt(postSnapshot.getName());
                            double lat = postSnapshot.child("lat").getValue(Double.class);
                            double lng = postSnapshot.child("lng").getValue(Double.class);
                            
                            JSONObject routeobj = new JSONObject(); 
                            routeobj.put("xlongitude", lng);
                            routeobj.put("ylatitude", lat);
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
    long insertedid;
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
        insertedid = 0;
        myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
        final CountDownLatch latch = new CountDownLatch(1);

        
        try {
            
             
                myFirebaseRef.child("driver").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long count = dataSnapshot.getChildrenCount();      
                        insertedid = count+1;
                        latch.countDown();   

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
          
            latch.await();
            
            
                        
                        myFirebaseRef.child("driver").child(String.valueOf(insertedid)).child("email").setValue(email);
                        myFirebaseRef.child("driver").child(String.valueOf(insertedid)).child("fullname").setValue(name);
                        myFirebaseRef.child("driver").child(String.valueOf(insertedid)).child("password").setValue(password);
                        myFirebaseRef.child("driver").child(String.valueOf(insertedid)).child("img").setValue(img);
                        myFirebaseRef.child("driver").child(String.valueOf(insertedid)).child("token").setValue(" ");
                        myFirebaseRef.child("driver").child(String.valueOf(insertedid)).child("logged").setValue(0);
                        myFirebaseRef.child("driver").child(String.valueOf(insertedid)).child("vid").setValue(0);

                        resobj.put("success", "1");
                        resobj.put("msg", "Added Successfully");

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
//            byte[] bytearr = new sun.misc.BASE64Decoder().decodeBuffer(img);
//            String j="";
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
            
            
            myFirebaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot : dataSnapshot.child("vehicles").getChildren()) {
                                    int vehicle_id = Integer.parseInt(postSnapshot.getName());
                                    String model = postSnapshot.child("model").getValue(String.class);
                                    String color = postSnapshot.child("color").getValue(String.class);
                                    String plate_number = postSnapshot.child("plate_number").getValue(String.class);
                                    String outside_working_time_state = "yes";
                                    for (DataSnapshot postSnapshot2 : dataSnapshot.child("driver").getChildren()) {
                                    int vid = postSnapshot2.child("vid").getValue(int.class);
                                        if(vid==vehicle_id){
                                            outside_working_time_state = "no";
                                        }
                                    }
                                    

                                    JSONObject o = new JSONObject();
                                    o.put("vehicle_id", vehicle_id);
                                    o.put("model", model);
                                    o.put("color", color);
                                    o.put("outside_working_time_state", outside_working_time_state);
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
    public Response getVehicle(@PathParam("id") final int id){
//        JSONObject obj = new JSONObject();

            resobj = new JSONObject();
            final CountDownLatch latch = new CountDownLatch(1);
            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");

        try {
            myFirebaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                                    int vehicle_id = Integer.parseInt(dataSnapshot.child("vehicles").child(String.valueOf(id)).getName());
                                    String model = dataSnapshot.child("vehicles").child(String.valueOf(id)).child("model").getValue(String.class);
                                    String color = dataSnapshot.child("vehicles").child(String.valueOf(id)).child("color").getValue(String.class);
                                    String plate_number = dataSnapshot.child("vehicles").child(String.valueOf(id)).child("plate_number").getValue(String.class);
                                    
                                    String outside_working_time_state = "yes";
                                    for (DataSnapshot postSnapshot2 : dataSnapshot.child("driver").getChildren()) {
                                    int vid = postSnapshot2.child("vid").getValue(int.class);
                                        if(vid==vehicle_id){
                                            outside_working_time_state = "no";
                                        }
                                    }

                                    JSONObject vehicleobj = new JSONObject();
                                    vehicleobj.put("vehicle_id", vehicle_id);
                                    vehicleobj.put("model", model);
                                    vehicleobj.put("color", color);
                                    vehicleobj.put("outside_working_time_state", outside_working_time_state);
                                    vehicleobj.put("plate_number", plate_number);
                                    JSONArray driversarr = new JSONArray();
                                    
                                    
                                    
                                    
                                    

                                    //get list of the last drivers
                                    for (DataSnapshot postSnapshot : dataSnapshot.child("vehicleshistory").getChildren()) {

                                        
                                     int pvid = postSnapshot.child("vid").getValue(int.class);
                                     if(pvid==id){
                                         int pdid = postSnapshot.child("did").getValue(int.class);
                                         String pdname = dataSnapshot.child("driver").child(String.valueOf(pdid)).child("fullname").getValue(String.class);
                                         long timestamp = Long.parseLong(postSnapshot.getName());
                                         
                                         
                                         JSONObject d = new JSONObject();
                                         d.put("did",pdid);
                                         d.put("name",pdname);
                                         d.put("starttimestamp",timestamp);

                                         
                                         driversarr.add(d);

                                     }
                                    }
                                    //get pattrens names
                                    String n1="",n2="",n3="",n4="",n5="",n6="",n7="",n8="",n9="",n10="",n11="",n12="";
                                    for (DataSnapshot postSnapshot : dataSnapshot.child("pattrens").getChildren()) {

                                        
                                     int pattrenid = Integer.parseInt(postSnapshot.getName());
                                     if(pattrenid==1)
                                         n1=postSnapshot.child("name").getValue(String.class);
                                     else if(pattrenid==2)
                                         n2=postSnapshot.child("name").getValue(String.class);
                                     else if(pattrenid==3)
                                         n3=postSnapshot.child("name").getValue(String.class);
                                     else if(pattrenid==4)
                                         n4=postSnapshot.child("name").getValue(String.class);
                                     else if(pattrenid==5)
                                         n5=postSnapshot.child("name").getValue(String.class);
                                     else if(pattrenid==6)
                                         n6=postSnapshot.child("name").getValue(String.class);
                                     else if(pattrenid==7)
                                         n7=postSnapshot.child("name").getValue(String.class);
                                     else if(pattrenid==8)
                                         n8=postSnapshot.child("name").getValue(String.class);
                                     else if(pattrenid==9)
                                         n9=postSnapshot.child("name").getValue(String.class);
                                     else if(pattrenid==10)
                                         n10=postSnapshot.child("name").getValue(String.class);
                                     else if(pattrenid==11)
                                         n11=postSnapshot.child("name").getValue(String.class);
                                     else if(pattrenid==12)
                                         n12=postSnapshot.child("name").getValue(String.class);
                                    }
                                    
                                    //get pattrens count for each driver
                                    for (int i = 0; i < driversarr.size(); i++) {
                                        int p1=0,p2=0,p3=0,p4=0,p5=0,p6=0,p7=0,p8=0,p9=0,p10=0,p11=0,p12=0;
                                        long t1,t2;
                                        t1 =  driversarr.getJSONObject(i).getLong("starttimestamp");
                                        if(i!=driversarr.size()-1)
                                            t2 =  driversarr.getJSONObject(i+1).getLong("starttimestamp");
                                        else
                                            t2=t1;
                                        for (DataSnapshot postSnapshot : dataSnapshot.child("vehicles").child(String.valueOf(id)).child("Patterns detected").getChildren()) {
                                            long pattrentimestamp = Long.parseLong(postSnapshot.getName());
//                                            if(t1==t2){
//                                                t2=pattrentimestamp;//3shn d a5r mra
//                                            }
                                            int pattrenid = postSnapshot.getValue(int.class);
                                                if((pattrentimestamp>=t1 && pattrentimestamp<=t2) || (t1==t2)){
                                                    if(pattrenid==1)
                                                        p1++;
                                                    else if(pattrenid==2)
                                                        p2++;
                                                    else if(pattrenid==3)
                                                        p3++;
                                                    else if(pattrenid==4)
                                                        p4++;
                                                    else if(pattrenid==5)
                                                        p5++;
                                                    else if(pattrenid==6)
                                                        p6++;
                                                    else if(pattrenid==7)
                                                        p7++;
                                                    else if(pattrenid==8)
                                                        p8++;
                                                    else if(pattrenid==9)
                                                        p9++;
                                                    else if(pattrenid==10)
                                                        p10++;
                                                    else if(pattrenid==11)
                                                        p11++;
                                                    else if(pattrenid==12)
                                                        p12++;
                                                }
                                            
                                        }
                                        
                                        JSONObject pattrenscount = new JSONObject();
                                        pattrenscount.put(n1, p1);
                                        pattrenscount.put(n2, p2);
                                        pattrenscount.put(n3, p3);
                                        pattrenscount.put(n4, p4);
                                        pattrenscount.put(n5, p5);
                                        pattrenscount.put(n6, p6);
                                        pattrenscount.put(n7, p7);
                                        pattrenscount.put(n8, p8);
                                        pattrenscount.put(n9, p9);
                                        pattrenscount.put(n10, p10);
                                        pattrenscount.put(n11, p11);
                                        pattrenscount.put(n12, p12);

                                        driversarr.getJSONObject(i).put("pattrens", pattrenscount);
                                    }
//                                    for (DataSnapshot postSnapshot : dataSnapshot.child("vehicles").child(String.valueOf(id)).child("Patterns detected").getChildren()) {
//                                        long pattrentimestamp = Long.parseLong(postSnapshot.getName());
//                                        int pattrenid = postSnapshot.getValue(int.class);
//                                            if(i!=driversarr.size()-1){
//                                                long t1 =  driversarr.getJSONObject(i).getLong("starttimestamp");
//                                                long t2 =  driversarr.getJSONObject(i+1).getLong("starttimestamp");
//                                                if(pattrentimestamp>=t1 && pattrentimestamp<=t2){
//                                                    
//                                                }
//                                            }
//                                        }
//                                    }
                                    
                                    
                        
                                    resobj.put("vehicle", vehicleobj);  
                                    resobj.put("drivers", driversarr);
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
                            String avgtxt = postSnapshot.child("avgtxt").getValue(String.class);
                            int avg = postSnapshot.child("avg").getValue(int.class);
                            int lastavg = postSnapshot.child("lastavg").getValue(int.class);

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
                                    o.put("avgtxt", avgtxt);
                                    o.put("avg", avg);
                                    o.put("lastavg", lastavg);
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
            
            myFirebaseRef.child("monitoring_member").child(id).addValueEventListener(new ValueEventListener() {
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
                    resobj.put("msg", "Loggedin Successfully");
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
                            
                            try{

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
                            catch(NumberFormatException nex){
                                //it is notification count not monitoring member
                            }
                                
                            
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
                                    String status = postSnapshot.child("status").getValue(String.class);

                                    
                                    double fromlat = postSnapshot.child("ilat").getValue(double.class);
                                    double fromlng = postSnapshot.child("ilng").getValue(double.class);
                                    double tolat = postSnapshot.child("destlat").getValue(double.class);
                                    double tolng = postSnapshot.child("destlng").getValue(double.class);
                                    
                                    JSONObject o = new JSONObject();
                                    o.put("trip_id", tid);
                                    o.put("start", start);
                                    o.put("end", end);
                                    o.put("price", price);
                                    o.put("comment", comment);
                                    o.put("ratting", ratting);
                                    o.put("passenger_id", pid);
                                    o.put("driver_id", did);
                                    
                                    o.put("fromlat", fromlat);
                                    o.put("fromlng", fromlng);
                                    o.put("tolat", tolat);
                                    o.put("tolng", tolng);

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
                                    
                                    if(status.equals("ended")){
                                        arr.add(o);
                                    }
                                    
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
    
    
    ////////////////////////////////////////////////////////////////for report
    @GET
    @Path("/getdriveravg/{did}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDriverAvg(@PathParam("did") final int driverid){

            resobj = new JSONObject();
            arr = new JSONArray();
            final CountDownLatch latch = new CountDownLatch(1);
            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
        try {
            myFirebaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        
                        long start_timestamp = 0;             
                        int svid = 0;
                        long end_timestamp = 0;
                        
                        ArrayList<Long> starts = new ArrayList<Long>() ;
                        ArrayList<Long> ends = new ArrayList<Long>() ;
                        ArrayList<Integer> vehicles = new ArrayList<Integer>() ;
                        int i = 0;
                        
                        int vcount = (int) dataSnapshot.child("vehicleshistory").getChildrenCount();


                        for (DataSnapshot postSnapshot : dataSnapshot.child("vehicleshistory").getChildren()) {
                            i++;
                            Long timestamp = Long.parseLong(postSnapshot.getName());
                            int did = postSnapshot.child("did").getValue(int.class);   
                            int vid = postSnapshot.child("vid").getValue(int.class);     
                            
                            if(did==driverid){
                                if(start_timestamp==0){
                                    start_timestamp=timestamp;
                                    svid=vid;
                                    
                                    //tb momkn akon ana bs da a5r record y3ny check
                                    if (i==vcount){
                                    end_timestamp=System.currentTimeMillis();//y3ny wsl ll a5r w lsa sh8al feha l7d nw
                                    //kda a5dt range
                                    starts.add(start_timestamp);                                    
                                    ends.add(end_timestamp);
                                    vehicles.add(svid);
                                    
                                    //sfr for other ranges
                                    start_timestamp=0;
                                    end_timestamp=0;
                                    svid=0;
                            }
                                }
                            }
                            else if (i==vcount){
                                    end_timestamp=System.currentTimeMillis();//y3ny wsl ll a5r w lsa sh8al feha l7d nw
                                    //kda a5dt range
                                    starts.add(start_timestamp);                                    
                                    ends.add(end_timestamp);
                                    vehicles.add(svid);
                                    
                                    //sfr for other ranges
                                    start_timestamp=0;
                                    end_timestamp=0;
                                    svid=0;
                            }
                            else if(start_timestamp!=0){//da lw l driver l vehicle bta3to et8yrt
                                    end_timestamp=timestamp;
                                    //kda a5dt range
                                    starts.add(start_timestamp);                                    
                                    ends.add(end_timestamp);
                                    vehicles.add(svid);
                                    
                                    //sfr for other ranges
                                    start_timestamp=0;
                                    end_timestamp=0;
                                    svid=0;
                                    
                                    //tb lw d a5r w7da 
                                }
                        }
                        
                        JSONArray pattrens = new JSONArray();
                        
                        //kda gbt l range bta3ty na2s l pattrens mn l vehicle
                        for (int j = 0; j < vehicles.size(); j++) {
                            int currentvid = vehicles.get(j);
                            for (DataSnapshot postSnapshot : dataSnapshot.child("vehicles").child(String.valueOf(currentvid)).child("Patterns detected").getChildren()) {
                                Long timestamp = Long.parseLong(postSnapshot.getName());
                                if(svid == currentvid && starts.get(j)<=timestamp && ends.get(j)>=timestamp){
                                    int pattrenid = postSnapshot.getValue(int.class);     
                                    JSONObject p = new JSONObject();
                                    p.put("pattrenid", pattrenid);
                                    p.put("timestamp",timestamp);
                                    pattrens.add(p);
                                }
                            }
                        }
                        //kda gbt list bl pattrens elly 3mlha fy 7yato kolha
                        
                        int acceptedcount = 0;
                        int ignoredcount = 0;
                        
                        int rates_1 = 0;                        
                        int rates_2 = 0;
                        int rates_3 = 0;
                        int rates_4 = 0;
                        int rates_5 = 0;
                        for (DataSnapshot postSnapshot : dataSnapshot.child("trips").getChildren()) {
                            int did = postSnapshot.child("did").getValue(int.class);    
                            if(did==driverid){
                                int triprate = postSnapshot.child("ratting").getValue(int.class); 
                                String status = postSnapshot.child("status").getValue(String.class); 
                                if(status.equals("ignored"))
                                    ignoredcount++;
                                else
                                    acceptedcount++;

                                if(triprate==1)
                                    rates_1++;
                                else if(triprate==2)
                                    rates_2++;
                                else if(triprate==3)
                                    rates_3++;
                                else if(triprate==4)
                                    rates_4++;
                                else if(triprate==5)
                                    rates_5++;
                            }
                        }
                        
                        
                        //calculate avg fl lat week
                        int p1 = 0;
                        int p2 = 0;
                        int p3 = 0;
                        int p4 = 0;
                        int p5 = 0;
                        int p6 = 0;
                        int p7 = 0;
                        int p8 = 0;
                        int p9 = 0;
                        int p10 = 0;
                        int p11 = 0;
                        int p12 = 0;

                        
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DAY_OF_YEAR, -7);
                        long sevenDaysAgo = cal.getTimeInMillis();
                        
                        for (int j = 0; j < pattrens.size(); j++) {
                            JSONObject pattrenobj = pattrens.getJSONObject(j);
                            long pattrenobj_timestamp = pattrenobj.getLong("timestamp");
                            if(pattrenobj_timestamp>=sevenDaysAgo){
                                int pattrenid = pattrenobj.getInt("pattrenid");
                                if(pattrenid==1)
                                    p1++;
                                else if(pattrenid==2)
                                    p2++;
                                else if(pattrenid==3)
                                    p3++;
                                else if(pattrenid==4)
                                    p4++;
                                else if(pattrenid==5)
                                    p5++;
                                else if(pattrenid==6)
                                    p6++;
                                else if(pattrenid==7)
                                    p7++;
                                else if(pattrenid==8)
                                    p8++;
                                else if(pattrenid==9)
                                    p9++;
                                else if(pattrenid==10)
                                    p10++;
                                else if(pattrenid==11)
                                    p11++;
                                else if(pattrenid==12)
                                    p12++;
                            }
                        }
                        
                        //kda gbt l occurance le kol pattren
                        //ashof l max b2a
                        
                        String p1_name="",p2_name="",p3_name="",p4_name="",p5_name="",p6_name="",p7_name="",p8_name="",p9_name="",p10_name="",p11_name="",p12_name="";
                        
                        int avg = 0;
                        
                        for (DataSnapshot postSnapshot : dataSnapshot.child("pattrens").getChildren()) {
                            Long cpattrenid = Long.parseLong(postSnapshot.getName());
                            int max = postSnapshot.child("max").getValue(int.class);    
                            String name = postSnapshot.child("name").getValue(String.class);    
                            if(cpattrenid==1){
                                p1_name=name;
                                if(max>p1)
                                    avg++;
                            }
                            else if(cpattrenid==2){
                                p2_name=name;
                                if(max>p2)
                                    avg++;
                            }
                            else if(cpattrenid==3){
                                p3_name=name;
                                if(max>p3)
                                    avg++;
                            }
                            else if(cpattrenid==4){
                                p4_name=name;
                                if(max>p4)
                                    avg++;
                            }
                            else if(cpattrenid==5){
                                p5_name=name;
                                if(max>p5)
                                    avg++;
                            }
                            else if(cpattrenid==6){
                                p6_name=name;
                                if(max>p6)
                                    avg++;
                            }
                            else if(cpattrenid==7){
                                p7_name=name;
                                if(max>p7)
                                    avg++;
                            }
                            else if(cpattrenid==8){
                                p8_name=name;
                                if(max>p8)
                                    avg++;
                            }
                            else if(cpattrenid==9){
                                p9_name=name;
                                if(max>p9)
                                    avg++;
                            }
                            else if(cpattrenid==10){
                                p10_name=name;
                                if(max>p10)
                                    avg++;
                            }
                            else if(cpattrenid==11){
                                p11_name=name;
                                if(max>p11)
                                    avg++;
                            }
                            else if(cpattrenid==12){
                                p12_name=name;
                                if(max>p12)
                                    avg++;
                            }
                            
                        }
                        
                        //added also more parameters
                        avg+=gettripsavg(rates_1, rates_2, rates_3, rates_4, rates_5, 1,1);//ignored, accepted);
                        
                        //avg text
                        String avgtxt = "";
                        if(avg<3)
                            avgtxt="Bad";
                        else if(avg<6)
                            avgtxt="Good";
                        else if(avg<9)
                            avgtxt="Very Good";
                        else if(avg<=12)
                            avgtxt="Excellent";
                        
                       myFirebaseRef.child("driver").child(String.valueOf(driverid)).child("avgtxt").setValue(avgtxt);

                       String dname = dataSnapshot.child("driver").child(String.valueOf(driverid)).child("fullname").getValue(String.class); 

                        
                        //respond
                        
                    resobj.put("success", "1");
                    resobj.put("msg", "Selected Successfully");
                    resobj.put("pattrens", pattrens);  
                    resobj.put("rates_1", rates_1);  
                    resobj.put("rates_2", rates_2);  
                    resobj.put("rates_3", rates_3);  
                    resobj.put("rates_4", rates_4);  
                    resobj.put("rates_5", rates_5);  
                    
                    resobj.put("p1_name", p1_name);  
                    resobj.put("p2_name", p2_name);  
                    resobj.put("p3_name", p3_name);  
                    resobj.put("p4_name", p4_name);  
                    resobj.put("p5_name", p5_name);  
                    resobj.put("p6_name", p6_name);  
                    resobj.put("p7_name", p7_name);  
                    resobj.put("p8_name", p8_name);  
                    resobj.put("p9_name", p9_name);  
                    resobj.put("p10_name", p10_name);  
                    resobj.put("p11_name", p11_name);  
                    resobj.put("p12_name", p12_name);  
                    
                    resobj.put("acceptedcount", acceptedcount);  
                    resobj.put("ignoredcount", ignoredcount);  
                    
                    resobj.put("avg", avg);  
                    resobj.put("avgtxt", avgtxt);  
                    resobj.put("dname", dname);  

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
    
    //////////////
    int gettripsavg(int rates_1, int rates_2, int rates_3, int rates_4, int rates_5, int ignored, int accepted){
        int total1 = 0;
        total1 = (rates_1/5) + (rates_2/5) + (rates_3/5) + (rates_4/5) + (rates_5/5);
        int total2 = (accepted/ignored) ;
        return total1+total2;
    }
    
    //////////////////////////////////////////tenaaa
    @GET
    @Path("/getpattrens")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getpatterns(){
       
        try {
           
               
            resobj = new JSONObject();
            arr = new JSONArray();
            final CountDownLatch latch = new CountDownLatch(1);
            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
            
            
            myFirebaseRef.child("pattrens").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    int pattrnsid = Integer.parseInt(postSnapshot.getName());
                                    String name = postSnapshot.child("name").getValue(String.class);
                                    String max = postSnapshot.child("max").getValue(String.class);
                                

                                    JSONObject o = new JSONObject();
                                    o.put("id", pattrnsid);
                                    o.put("max", max);
                                    o.put("name", name);
                                    
                                    arr.add(o);
                        }
                        
                          resobj.put("pattrens", arr);  
                          resobj.put("success", "1");
                          resobj.put("msg", "Selected Successfully");
                          latch.countDown();   
                        
                    }

                @Override
                public void onCancelled() {
                    throw new UnsupportedOperationException("Not supported yet."); 
                    
                }
                });
            
            

            latch.await();
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(resobj).build();
    }
    
    
    ////
    @GET
    @Path("/getwanings")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getwanings(){

        resobj = new JSONObject();
        arr = new JSONArray();
        final CountDownLatch latch = new CountDownLatch(1);
        myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");

        try {
//            ResultSet rs = getDBResultSet(query);
            resobj.put("success", "1");
            resobj.put("msg", "done");
            
            myFirebaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {                              
                                    try{
                                        for (DataSnapshot postSnapshot2 : dataSnapshot.child("warning").child("ristrected").getChildren()) {

//                                            int timestamp = postSnapshot2.child("timestamp").getValue(int.class);
                                            String timestamp = postSnapshot2.getName();
                                            int driver_id = postSnapshot2.child("did").getValue(int.class);
                                            
                                            String dname = dataSnapshot.child("driver").child(String.valueOf(driver_id)).child("fullname").getValue(String.class);

                                            
                                            JSONArray ristrictedroute = new JSONArray();
                                            for (DataSnapshot postSnapshot : dataSnapshot.child("driver").child(String.valueOf(driver_id)).child("route_restrictions").getChildren()) {
                                                int rid = Integer.parseInt(postSnapshot.getName());
                                                double lat = postSnapshot.child("lat").getValue(Double.class);
                                                double lng = postSnapshot.child("lng").getValue(Double.class);

                                                JSONObject routeobj = new JSONObject(); 
                                                routeobj.put("xlongitude", lat);
                                                routeobj.put("ylatitude", lng);
                                                ristrictedroute.add(routeobj);
                                            }
                                            
                                            
                                            
                                            JSONObject o = new JSONObject();
                                            o.put("timestamp",timestamp);                    
                                            o.put("did", driver_id);
                                            o.put("dname", dname);
                                            o.put("ristrictedroute",ristrictedroute);                    
                                            arr.add(o);

                                        }
                                    }catch(NullPointerException ne){
                                        Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ne);
                                    }catch(NumberFormatException ne){
                                        Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ne);
                                    }
                               
                                    
                                    

                            
                            
//                        }
                        
                          resobj.put("warning", arr);
                          resobj.put("success", "1");
                          resobj.put("msg", "Selected Successfully");
                          latch.countDown();   
                        
                    }

                @Override
                public void onCancelled() {
                    throw new UnsupportedOperationException("Not supported yet."); 
                    
                }
                });


            latch.await();
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(resobj).build();
    }
    
    
    ///////
    @GET
    @Path("/getfemaleevents")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getfemaleevents(){

        resobj = new JSONObject();
        arr = new JSONArray();
        final CountDownLatch latch = new CountDownLatch(1);
        myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
 
        try {
//            ResultSet rs = getDBResultSet(query);
            resobj.put("success", "1");
            resobj.put("msg", "done");
            
            myFirebaseRef.child("warning").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                            try{
                                        for (DataSnapshot postSnapshot2 : dataSnapshot.child("femalesaftey").getChildren()) {

                                            String ts = postSnapshot2.getName();
                                            double lat = postSnapshot2.child("lat").getValue(double.class);
                                            double lng = postSnapshot2.child("lng").getValue(double.class);
                                            int trip_id = postSnapshot2.child("tid").getValue(int.class);

                                            JSONObject events = new JSONObject();
                                            events.put("lat", lat);                    
                                            events.put("lng", lng);
                                            events.put("tid", trip_id);
                                            events.put("timestamp", ts);


                                           arr .add(events);

                                        }
                                    }catch(NullPointerException ne){
                                        Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ne);
                                    }catch(NumberFormatException ne){
                                        Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ne);
                                    }
                               
                           
                          resobj.put("warning", arr);
                          resobj.put("success", "1");
                          resobj.put("msg", "Selected Successfully");
                          latch.countDown();   
                        
                    }

                @Override
                public void onCancelled() {
                    throw new UnsupportedOperationException("Not supported yet."); 
                    
                }
                });


            latch.await();
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(resobj).build();
    }
    
    
    /////////////////////
    
//        @Path("/editpattren/{id}/{name}/{max}")
//    public Response editPattren(@PathParam("id") int id, @PathParam("name") String name, @PathParam("max") String max){
    @POST
    @Path("/editpattren")
    @Produces(MediaType.APPLICATION_JSON)  
    public Response editPattren(String data){

             //as post request
            JSONObject dataObj = JSONObject.fromObject(data);
            final String id = dataObj.getString("id");
            final String name = dataObj.getString("name");
            final String max = dataObj.getString("max");

        try {
            resobj = new JSONObject();
            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
            myFirebaseRef.child("pattrens").child(String.valueOf(id)).child("name").setValue(name);
            myFirebaseRef.child("pattrens").child(String.valueOf(id)).child("max").setValue(max);
            
            resobj.put("success", "1");
            resobj.put("msg", "Edited Successfully");
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(resobj).build();
    }
    
    ////get 
    @GET
    @Path("/getdrivertrips/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDriverTripsReport(@PathParam("id") final int id){
        resobj = new JSONObject();
        arr = new JSONArray();
        final CountDownLatch latch = new CountDownLatch(1);
        myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");

        try {
//            ResultSet rs = getDBResultSet(query);
            resobj.put("success", "1");
            resobj.put("msg", "done");
            
            myFirebaseRef.addValueEventListener(new ValueEventListener() {
                     @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        
                        double wallet =  dataSnapshot.child("driver").child(String.valueOf(id)).child("wallet").getValue(double.class);
                        String dname =  dataSnapshot.child("driver").child(String.valueOf(id)).child("fullname").getValue(String.class);

                        int ignoredcount = 0, acceptedcount = 0;

                        for (DataSnapshot postSnapshot : dataSnapshot.child("trips").getChildren()) {
                                int pid = postSnapshot.child("pid").getValue(Integer.class);
                                JSONArray paths = new JSONArray();
//                                if(pid == id){

                                    int tid = Integer.parseInt(postSnapshot.getName());
                                    String comment = postSnapshot.child("comment").getValue(String.class);
//                                    String details = postSnapshot.child("details").getValue(String.class);
                                    int did = postSnapshot.child("did").getValue(int.class);
                                    if(did == id){
                                    
                                    String end = postSnapshot.child("end").getValue(String.class);
                                    String price = postSnapshot.child("price").getValue(String.class);
                                    String ratting = postSnapshot.child("ratting").getValue(String.class);
                                    String start = postSnapshot.child("start").getValue(String.class);
                                    String status = postSnapshot.child("status").getValue(String.class);
                                    
                                    if(status.equals("ignored")){
                                        ignoredcount++;
                                    }
                                    else{
                                        acceptedcount++;
                                    }

                                    
                                    double fromlat = postSnapshot.child("ilat").getValue(double.class);
                                    double fromlng = postSnapshot.child("ilng").getValue(double.class);
                                    double tolat = postSnapshot.child("destlat").getValue(double.class);
                                    double tolng = postSnapshot.child("destlng").getValue(double.class);
                                    
                                    JSONObject o = new JSONObject();
                                    o.put("trip_id", tid);
                                    o.put("start", start);
                                    o.put("end", end);
                                    o.put("price", price);
                                    o.put("comment", comment);
                                    o.put("ratting", ratting);
                                    o.put("passenger_id", pid);
                                    o.put("driver_id", did);
                                    
                                    o.put("status", status);

                                    o.put("fromlat", fromlat);
                                    o.put("fromlng", fromlng);
                                    o.put("tolat", tolat);
                                    o.put("tolng", tolng);

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
                            
                            
                        }
                        
                          resobj.put("trips", arr);
                          resobj.put("ignoredcount", ignoredcount);
                          resobj.put("acceptedcount", acceptedcount);
                          resobj.put("dname", dname);
                          resobj.put("wallet", wallet);
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
    
    
    @GET
    @Path("/gettrip/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTrip(@PathParam("id") final int id){
        resobj = new JSONObject();
        final CountDownLatch latch = new CountDownLatch(1);
        myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");

        try {
//            ResultSet rs = getDBResultSet(query);
            resobj.put("success", "1");
            resobj.put("msg", "done");
            
            myFirebaseRef.addValueEventListener(new ValueEventListener() {
                     @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        
                        DataSnapshot tnode = dataSnapshot.child("trips").child(String.valueOf(id));
                        int did =  tnode.child("did").getValue(int.class);
                        String drivername = dataSnapshot.child("driver").child(String.valueOf(did)).child("fullname").getValue(String.class);
                        int vid = dataSnapshot.child("driver").child(String.valueOf(did)).child("vid").getValue(int.class);//bs d 8lt 3shn momkn tb2a l vehicle et8yert b3dha impppppp
                        int pid =  tnode.child("pid").getValue(int.class);
                        String passengername = dataSnapshot.child("passenger").child(String.valueOf(pid)).child("fullname").getValue(String.class);
                        
                        String status = tnode.child("status").getValue(String.class);
                        long start = tnode.child("start").getValue(long.class);
                        long end = tnode.child("end").getValue(long.class);
                        double ilat = tnode.child("ilat").getValue(double.class);
                        double ilng = tnode.child("ilng").getValue(double.class);
                        double destlat = tnode.child("destlat").getValue(double.class);
                        double destlng = tnode.child("destlng").getValue(double.class);
                        double price = tnode.child("price").getValue(double.class);
                        int ratting = tnode.child("ratting").getValue(int.class);
                        String comment = tnode.child("comment").getValue(String.class);
                        long timestamp = tnode.child("timestamp").getValue(long.class);
                        JSONArray paths = new JSONArray();
                        try{
                            for (DataSnapshot postSnapshot2 : tnode.child("pathway").getChildren()) {
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

                        //get pattrens
                        JSONArray pattrens = new JSONArray();
                        for (DataSnapshot postSnapshot2 : dataSnapshot.child("vehicles").child(String.valueOf(vid)).child("Patterns detected").getChildren()) {
                                Long pattimestamp = Long.parseLong(postSnapshot2.getName());
                                if(start<=pattimestamp && end>=pattimestamp){
                                    int pattrenid = postSnapshot2.getValue(int.class);     
                                    JSONObject p = new JSONObject();
                                    p.put("pattrenid", pattrenid);
                                    p.put("timestamp",pattimestamp);
                                    pattrens.add(p);
                                }
                            
                        }
                        
                        String p1_name="",p2_name="",p3_name="",p4_name="",p5_name="",p6_name="",p7_name="",p8_name="",p9_name="",p10_name="",p11_name="",p12_name="";
                        for (DataSnapshot postSnapshot : dataSnapshot.child("pattrens").getChildren()) {
                            Long cpattrenid = Long.parseLong(postSnapshot.getName());
                            int max = postSnapshot.child("max").getValue(int.class);    
                            String name = postSnapshot.child("name").getValue(String.class);    
                            if(cpattrenid==1){
                                p1_name=name;
                            }else if(cpattrenid==2){
                                p2_name=name;
                            }else if(cpattrenid==3){
                                p3_name=name;
                            }else if(cpattrenid==4){
                                p4_name=name;
                            }else if(cpattrenid==5){
                                p5_name=name;
                            }else if(cpattrenid==6){
                                p6_name=name;
                            }else if(cpattrenid==7){
                                p7_name=name;
                            }else if(cpattrenid==8){
                                p8_name=name;
                            }else if(cpattrenid==9){
                                p9_name=name;
                            }else if(cpattrenid==10){
                                p10_name=name;
                            }else if(cpattrenid==11){
                                p11_name=name;
                            }else if(cpattrenid==12){
                                p12_name=name;
                            }
                        }
                        
                            
                        JSONObject t = new JSONObject();
                        t.put("did", did);
                        t.put("drivername", drivername);
                        t.put("pid", pid);
                        t.put("passengername", passengername);
                        t.put("status", status);
                        t.put("start", start);
                        t.put("end", end);
                        t.put("ilat", ilat);
                        t.put("ilng", ilng);
                        t.put("destlat", destlat);
                        t.put("destlng", destlng);
                        t.put("price", price);
                        t.put("ratting", ratting);
                        t.put("comment", comment);
                        t.put("timestamp", timestamp);
                        t.put("pathway", paths);
                        resobj.put("pattrens", pattrens);//
                        
                        resobj.put("p1_name", p1_name);  
                        resobj.put("p2_name", p2_name);  
                        resobj.put("p3_name", p3_name);  
                        resobj.put("p4_name", p4_name);  
                        resobj.put("p5_name", p5_name);  
                        resobj.put("p6_name", p6_name);  
                        resobj.put("p7_name", p7_name);  
                        resobj.put("p8_name", p8_name);  
                        resobj.put("p9_name", p9_name);  
                        resobj.put("p10_name", p10_name);  
                        resobj.put("p11_name", p11_name);  
                        resobj.put("p12_name", p12_name);  

                        resobj.put("trip", t);
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
    
    
    
    /////////////
    @GET
    @Path("/getfemaleevent/{timestamp}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFemaleEvent(@PathParam("timestamp") final int timestamp){
//        JSONObject obj = new JSONObject();

            resobj = new JSONObject();
            arr = new JSONArray();
            final CountDownLatch latch = new CountDownLatch(1);
            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
        try {
             myFirebaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        long tripid = dataSnapshot.child("warning").child("femalesaftey").child(String.valueOf(timestamp)).child("tid").getValue(long.class);
                        double lat = dataSnapshot.child("warning").child("femalesaftey").child(String.valueOf(timestamp)).child("lat").getValue(double.class);
                        double lng = dataSnapshot.child("warning").child("femalesaftey").child(String.valueOf(timestamp)).child("lng").getValue(double.class);
                        int pid = dataSnapshot.child("trips").child(String.valueOf(tripid)).child("pid").getValue(int.class);
                        int did = dataSnapshot.child("trips").child(String.valueOf(tripid)).child("did").getValue(int.class);
                        String dname = dataSnapshot.child("driver").child(String.valueOf(did)).child("fullname").getValue(String.class);
                        String pname = dataSnapshot.child("passenger").child(String.valueOf(pid)).child("fullname").getValue(String.class);

                        //nearst driver
                        int vid = dataSnapshot.child("driver").child(String.valueOf(did)).child("vid").getValue(int.class);
                        double vlat = dataSnapshot.child("vehicles").child(String.valueOf(vid)).child("Latitude").getValue(double.class);
                        double vlng = dataSnapshot.child("vehicles").child(String.valueOf(vid)).child("Longitude").getValue(double.class);

                        int neardist = 1000000;
                        int nearid = 0;
                        String nearname = "";
                        for (DataSnapshot postSnapshot : dataSnapshot.child("vehicles").getChildren()) {
                            int cvid = Integer.parseInt(postSnapshot.getName());
                            double v2lat = postSnapshot.child("Latitude").getValue(double.class);
                            double v2lng = postSnapshot.child("Longitude").getValue(double.class);
                            int dist = (int) calculateDistance(vlat, vlng, v2lat, v2lng);
                            if(dist<neardist){
                                neardist=dist;
                                for (DataSnapshot postSnapshot2 : dataSnapshot.child("driver").getChildren()) {
                                    int cvid2 = postSnapshot2.child("vid").getValue(int.class);
                                    if(cvid2 == cvid){
                                        int did2 = Integer.parseInt(postSnapshot2.getName());
                                        nearid = did2;
                                        nearname = postSnapshot2.child("fullname").getValue(String.class);
                                    }
                                }
                        }
                    }
                        
                        resobj.put("tripid", tripid);
                        resobj.put("lat", lat);
                        resobj.put("lng", lng);
                        resobj.put("pid", pid);
                        resobj.put("did", did);
                        resobj.put("vid", vid);
                        resobj.put("dname", dname);
                        resobj.put("pname", pname);
                        
                        JSONObject neard = new JSONObject();
                        neard.put("id", nearid);
                        neard.put("name", nearname);

                        resobj.put("neard", neard);
                        
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
    
    
       public static float calculateDistance(double lat1, double lon1, double lat2, double lon2)
    {
        float dLat = (float) Math.toRadians(lat2 - lat1);
        float dLon = (float) Math.toRadians(lon2 - lon1);
        float a =
                (float) (Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2));
        float c = (float) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
        int earthRadius = 6371;
        float d = earthRadius * c;
        return d;
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
