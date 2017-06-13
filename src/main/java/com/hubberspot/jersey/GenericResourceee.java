/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hubberspot.jersey;


import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@ApplicationPath("/")
@Path("/websiteservicee")
public class GenericResourceee {
    
//    Connection conn;
    
    
    int f;
            
    JSONArray arr;
    JSONObject resobj;
    Firebase myFirebaseRef;
    
    @GET //test only
    @Path("/go")    
    @Produces(MediaType.APPLICATION_JSON)
    public Response getgo(){
        String output = "gooooooooooooooooooooo Hebat";
//        Firebase  myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
//        myFirebaseRef.child("driver").removeValue();
        return Response.status(200).entity(output).build();
    }
   
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
                    
                      if(pass == null){//wrong id
                            resobj.put("success", "0");
                            resobj.put("msg", "Wrong ID");
                            latch.countDown();      
                        }else if(pass.equals(password)){
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
    
}
