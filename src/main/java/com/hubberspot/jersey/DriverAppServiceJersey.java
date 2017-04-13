/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hubberspot.jersey;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import static com.hubberspot.jersey.PassengerAppServiceJersey.sendFireNotification;
import java.util.logging.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;
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
    
    public static Firebase myFirebaseRef;
    
    
    @GET
    @Path("/initFirebase")    
    public Response initFirebase(){
        myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
       myFirebaseRef.child("test").setValue(1);
       
       String g = myFirebaseRef.child("trips").child("1").child("status").endAt().toString();

               
       String testtoken = "cyLo5aqZHFQ:APA91bF9j1_GeV55qDAmlNOlNs1CkLtsuoCwgfE8nHWqV8eTMfRLdJi3n9eGNNIMWIAGqeowF--1ycczbgsRo0e9oafgbLER5o2zn9I8X28Rc6eQAEfxWpGyH6mYuoI2d6wkW4oUgR-j"; 
        //send notification to driver
       sendFireNotification(testtoken,"Trip Request Test","You have new trip request");
       return Response.status(200).entity(g+" k").build();

    }

    ResultSet getDBResultSet(String query) throws Exception{
                    
        Class.forName("com.mysql.jdbc.Driver");            
        //conn = DriverManager.getConnection("jdbc:mysql://localhost/hebadb?" + "user=root&password=");
        //conn = DriverManager.getConnection("jdbc:mysql://db4free.net/nashwa346db?" + "user=nashwa346&password=123456");
        //conn = DriverManager.getConnection("jdbc:mysql://sql8.freesqldatabase.com/sql8166151?" + "user=sql8166151&password=CnJ3KUzlDR");
        //conn = DriverManager.getConnection("jdbc:mysql://db4free.net/nashwa346db?" + "user=nashwa346&password=123456");
        conn = DriverManager.getConnection("jdbc:mysql://sharksspace-heba-mohamed.c9users.io:8080/mysharksdb?" + "user=hoba_hoby&password=");
        
        //conn = DriverManager.getConnection("jdbc:mysql://johnny.heliohost.org/hobahob1_sharks?" + "user=hobahob1&password=HOBAHOBY1995");

        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);
        return rs;            
    }
    void excDB(String query) throws Exception{
                    
        Class.forName("com.mysql.jdbc.Driver");            
        //conn = DriverManager.getConnection("jdbc:mysql://localhost/hebadb?" + "user=root&password=");
        //conn = DriverManager.getConnection("jdbc:mysql://sql8.freesqldatabase.com/sql8166151?" + "user=sql8166151&password=CnJ3KUzlDR");
        //conn = DriverManager.getConnection("jdbc:mysql://johnny.heliohost.org/hobahob1_sharks?" + "user=hobahob1&password=HOBAHOBY1995");
        conn = DriverManager.getConnection("jdbc:mysql://db4free.net/nashwa346db?" + "user=nashwa346&password=123456");
        Statement st = conn.createStatement();
        st.executeUpdate(query);//
    }
    
    
    
    //    @Path("/driver login")
    //public Response LoginDriver(@PathParam("id") String id, @PathParam("password") String password) throws Exception{
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)    
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response LoginDriver(String data) throws Exception{  
//        JSONObject obj = new JSONObject();
            resobj = new JSONObject();
            final CountDownLatch latch = new CountDownLatch(1);
            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
           try {
              
            JSONObject objj = JSONObject.fromObject(data);   
            final int id = objj.getInt("driver_id");            
            final String password = objj.getString("password");
            
            
            
                myFirebaseRef.child("driver").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        JSONObject d = new JSONObject();


                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            try{
                            int did = Integer.parseInt(postSnapshot.getName());
                            String pass = postSnapshot.child("password").getValue(String.class);
                               
                            if(did==id&&pass.equals(password)){
                                String email = postSnapshot.child("email").getValue(String.class);
                                String fullname = postSnapshot.child("fullname").getValue(String.class);
                                int vid = postSnapshot.child("vid").getValue(Integer.class);

                                d.put("email", email);
                                d.put("fullname", fullname);
                                d.put("vehicle_id", vid);

                                resobj.put("driver", d);
                                
                                
                                resobj.put("success", "1");
                                resobj.put("msg", "logged in");
//                                response = Response.status(200).entity(resobj).build();
                                latch.countDown();
                            }
                            }catch(NullPointerException ne){
                                Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ne);
                            }catch(NumberFormatException ne){
                                Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ne);
                            }
                        }
                        
                    }

                @Override
                public void onCancelled() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.   
                }
                });



//            ResultSet rs = getDBResultSet("SELECT * FROM driver WHERE driver_id = "+id);//knt user_id
//            obj.put("success", "0");
//            obj.put("msg", "Wrong ID or Password");
//            while(rs.next())
//            {
//                 String pass = rs.getString(13);//msh 2
//                 if(pass.equals(password))
//                 {
//                     //logindriver
//                     obj.put("success", "1");
//                     obj.put("msg", "Logged in successfully");
//                     
//                     JSONObject d = new JSONObject();
//                     String fullname = rs.getString(2);
//                     double sharp_turns_freq = rs.getDouble(3);
//                     double lane_changing_freq = rs.getDouble(4);
//                     double harch_acc_freq = rs.getDouble(5);
//                     double wrong_u_turns_severity = rs.getDouble(7);
//                     int vehicle_id = rs.getInt(10);
//                     
//                     d.put("fullname", fullname);
//                     d.put("sharp_turns_freq", sharp_turns_freq);
//                     d.put("lane_changing_freq", lane_changing_freq);
//                     d.put("harch_acc_freq", harch_acc_freq);
//                     d.put("wrong_u_turns_severity", wrong_u_turns_severity);
//                     d.put("vehicle_id", vehicle_id);
//
////                     String name = rs.getString(3);
////                     String gender = rs.getString(4);
////                     String lastlogin_time = rs.getString(5);
////                     String account_state = rs.getString(6);
////                     
////                     d.put("id", id);
////                     d.put("name", name);
////                     d.put("gender", gender);
////                     d.put("lastlogin_time", lastlogin_time);
////                     d.put("account_state", account_state);
//
//                     obj.put("driver", d);
//
//                 }
//                 else {
//                     obj.put("success", "0");
//                     obj.put("msg", "Wrong Credentials");
//                 }
//
//             }
            
//            conn.close();
            latch.await();
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
//            response=Response.status(200).entity(resobj).build();
        }

        return Response.status(200).entity(resobj).build();

    }
    
    JSONArray tripsarr;
    
    @GET
    @Path("/getlasttrips/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response getLasttrips(@PathParam("id") final int id) throws Exception{
        
                 //String query = "SELECT * FROM trip WHERE driver_id = "+id;
        //JSONObject obj = new JSONObject();
        resobj = new JSONObject();
        final CountDownLatch latch = new CountDownLatch(1);
        try {
//            ResultSet rs = getDBResultSet(query);
//            resobj.put("success", "1");
//            resobj.put("msg", "done");
            
            tripsarr = new JSONArray();
            
           
            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
            
                myFirebaseRef.child("trips").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                int did = postSnapshot.child("did").getValue(Integer.class);
                                JSONArray paths = new JSONArray();
                                if(did == id){

                                    int tid = Integer.parseInt(postSnapshot.getName());
                                    String comment = postSnapshot.child("comment").getValue(String.class);
//                                    String details = postSnapshot.child("details").getValue(String.class);
                                    String pid = postSnapshot.child("pid").getValue(String.class);
                                    String end = postSnapshot.child("end").getValue(String.class);
                                    String price = postSnapshot.child("price").getValue(String.class);
                                    String ratting = postSnapshot.child("ratting").getValue(String.class);
                                    String start = postSnapshot.child("start").getValue(String.class);

                                    JSONObject o = new JSONObject();
                                    o.put("trip_id",tid  );
                                    o.put("start", start);
                                    o.put("end", end);
                                    o.put("price ",price );
                                    o.put("comment", comment);
                                    o.put("ratting", ratting);
                                    o.put("driver_id",did);
                                    o.put("passenger_id",pid);

                                    
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
                                    tripsarr.add(o);
                                    
                                }
                            
                            
                        }
                        
                          resobj.put("trips", tripsarr);
                          resobj.put("success", "1");
                          resobj.put("msg", "Selected Successfully");
                          latch.countDown();   
                        
                    }

                @Override
                public void onCancelled() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    
                }
                });

//             while(rs.next())
//             {
//                 int trip_id = rs.getInt(1);
//                 String start = rs.getString(2);
//                 String end = rs.getString(3);
//                 String price = rs.getString(4);  
//                 String comment = rs.getString(5);
//                 String ratting = rs.getString(6);
//                 String passenger_id= rs.getString(8);
//                                     
//                 JSONObject o = new JSONObject();
//                 o.put("trip_id",trip_id  );
//                 o.put("start", start);
//                 o.put("end", end);
//                 o.put("price ",price );
//                 o.put("comment", comment);
//                 o.put("ratting", ratting);
//                 o.put("passenger_id",passenger_id);
//                 
//                 String query2 = "SELECT * FROM pathwaymap WHERE trip_id = "+id;
//                 ResultSet rs2 = getDBResultSet(query2);
//                 JSONArray paths = new JSONArray();
//                 while(rs2.next())
//                {
//                    Double lat = rs2.getDouble("yattitude");                    
//                    Double lng = rs2.getDouble("xlongitude");
//                    JSONObject latlng = new JSONObject();
//                    latlng.put("lat", lat);                    
//                    latlng.put("lng", lng);
//                    paths.add(latlng);
//                }
//                 o.put("pathway",paths);
//                 arr.add(o);
//             }
               //conn.close();

//            resobj.put("lasttrips", arr);  
    
            latch.await();
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(resobj).build();
        
    }
    
    
    @GET
    @Path("/getlasttrip/{tripid}")
      @Produces(MediaType.APPLICATION_JSON)
   public Response getLasttrip(@PathParam("tripid") int id){
       
        //JSONObject obj = new JSONObject();
    try {
        resobj = new JSONObject();
        final CountDownLatch latch = new CountDownLatch(1);
        myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
        
        
                myFirebaseRef.child("trips").child(String.valueOf(id)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        JSONObject tripobj = new JSONObject();
                        JSONArray paths = new JSONArray();
                        
//                        String status = dataSnapshot.child("status").getValue(String.class);                        
                        String comment = dataSnapshot.child("comment").getValue(String.class);
//                        String details = dataSnapshot.child("details").getValue(String.class);
                        String did = dataSnapshot.child("did").getValue(String.class);
                        String end = dataSnapshot.child("end").getValue(String.class);
                        String pid = dataSnapshot.child("pid").getValue(String.class);
                        String price = dataSnapshot.child("price").getValue(String.class);
                        String ratting = dataSnapshot.child("ratting").getValue(String.class);
                        String start = dataSnapshot.child("start").getValue(String.class);
                        
                        tripobj.put("start", start);
                        tripobj.put("end", end);
                        tripobj.put("price", price);
                        tripobj.put("comment",  comment);
                        tripobj.put("ratting", ratting);
                        tripobj.put("passenger_id", pid);
                        tripobj.put("driver_id ",  did );
                        tripobj.put("passenger_id ",  pid );

                        try{
                            for (DataSnapshot postSnapshot : dataSnapshot.child("pathway").getChildren()) {

                                double lat = postSnapshot.child("lat").getValue(Double.class);
                                double lng = postSnapshot.child("lng").getValue(Double.class);
                                
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
                          tripobj.put("pathway",paths);
                          resobj.put("trip", tripobj);
                          resobj.put("success", "1");
                          resobj.put("msg", "Selected Successfully");
                          latch.countDown();   
                    }

                @Override
                public void onCancelled() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
                });
        
        
//            ResultSet rs = getDBResultSet("SELECT * FROM trip WHERE trip_id = "+id);
//            JSONObject tripobj = new JSONObject();   
//            while(rs.next())
//             {           
//                
//                 String start = rs.getString(2);
//                 String end = rs.getString(3);
//                 String price = rs.getString(4);  
//                 String comment = rs.getString(5);
//                 String ratting = rs.getString(6);
//                 String passenger_id = rs.getString(7);
//                 String driver_id = rs.getString(8);
//                 
//                 
//                 tripobj.put("start", start);
//                 tripobj.put("end", end);
//                 tripobj.put("price", price);
//                 tripobj.put("comment",  comment);
//                 tripobj.put("ratting", ratting);
//                 tripobj.put("passenger_id", passenger_id);
//                tripobj.put("driver_id ",  driver_id );
//                
//                    
//                 String query2 = "SELECT * FROM pathwaymap WHERE trip_id = "+id;
//                 ResultSet rs2 = getDBResultSet(query2);
//                 JSONArray paths = new JSONArray();
//                 while(rs2.next())
//                {
//                    Double lat = rs2.getDouble("yattitude");                    
//                    Double lng = rs2.getDouble("xlongitude");
//                    JSONObject latlng = new JSONObject();
//                    latlng.put("lat", lat);                    
//                    latlng.put("lng", lng);
//                    paths.add(latlng);
//                }
//                 tripobj.put("pathway",paths);
//                
//                obj.put("trip", tripobj);
//             }
//            //obj.put("trip", tripobj);
//            obj.put("success", "1");
//            obj.put("msg", "Selected Successfully");
//            
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
    @Path("/donetrip/{tripid}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
   public Response donetrip(@PathParam("tripid") final int id) throws Exception{
//       JSONObject obj = new JSONObject();
   
            //get current datetime 
//            java.util.Date dt = new java.util.Date();
//            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String currentTime = sdf.format(dt);
            //String j = "UPDATE trip SET end = '"+currentTime+"' WHERE trip_id = "+id+";";
            //excDB(j);
            //conn.close();
            
            //to calculate pathway
            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
            resobj = new JSONObject();
            final CountDownLatch latch = new CountDownLatch(1);
            
            
                myFirebaseRef.child("trips").child(String.valueOf(id)).child("pathway").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                         
                        JSONArray paths = new JSONArray();

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            
                            long timestamp = Long.parseLong(postSnapshot.getName());
                            double lat = postSnapshot.child("lat").getValue(Double.class);
                            double lng = postSnapshot.child("lng").getValue(Double.class);
                            
                            JSONObject latlng = new JSONObject();
                            latlng.put("lat", lat);                    
                            latlng.put("lng", lng);
                            paths.add(latlng);
                        }
                        
                        //calculate b2a
                        double fulldistance = 0;
                        for (int i = 0; i < paths.size()-1; i++) {
                            double lat1 = paths.getJSONObject(i).getDouble("lat");           
                            double lng1 = paths.getJSONObject(i).getDouble("lng");

                            double lat2 = paths.getJSONObject(i+1).getDouble("lat");           
                            double lng2 = paths.getJSONObject(i+1).getDouble("lng");

                            fulldistance+= distance(lat1, lat2, lng1, lng2);
                        }
                        
                            
                            //add it
                            double distancecost = KMCOST/1000*fulldistance;
                            resobj.put("distance", fulldistance);
                            resobj.put("distancecost", distancecost);
            
                            myFirebaseRef.child("trips").child(String.valueOf(id)).child("end").setValue(System.currentTimeMillis());
                            myFirebaseRef.child("trips").child(String.valueOf(id)).child("price").setValue(distancecost);
                        
                            
                            resobj.put("success", "1");
                            resobj.put("msg", "Added Successfully");
                            latch.countDown();
                }
                

                @Override
                public void onCancelled() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
                });

            
        try{
            latch.await();
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(resobj).build();
    }
   
   double calculate_pathway(int id) throws Exception{
       double fulldistance = 0;
       
       
       
       
//       String query = "SELECT * FROM pathwaymap WHERE trip_id = "+id;
//       ResultSet rs = getDBResultSet(query);
//       JSONArray paths = new JSONArray();
//       while(rs.next())
//       {
//           Double lat = rs.getDouble("yattitude"); 
//           Double lng = rs.getDouble("xlongitude");
//                    
//           JSONObject latlng = new JSONObject();
//           latlng.put("lat", lat);                    
//           latlng.put("lng", lng);
//           paths.add(latlng);
//        }
//       double fulldistance = 0;
//       for (int i = 0; i < paths.size()-1; i++) {
//           double lat1 = paths.getJSONObject(i).getDouble("lat");           
//           double lng1 = paths.getJSONObject(i).getDouble("lng");
//           
//           double lat2 = paths.getJSONObject(i+1).getDouble("lat");           
//           double lng2 = paths.getJSONObject(i+1).getDouble("lng");
//
//           fulldistance+= distance(lat1, lat2, lng1, lng2);
//       }
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
     
     
     
     JSONObject resobj;
     
     @GET
     @Path("/accepttrip/{trip_id}/{driverid}/{passengerid}")
     @Produces(MediaType.APPLICATION_JSON)
     @Consumes(MediaType.APPLICATION_JSON)
        public Response assignvehicle(@PathParam("trip_id") int id, @PathParam("driverid") int did, @PathParam("passengerid") int pid) throws SQLException{
//            JSONObject obj = new JSONObject();
            
             myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
            resobj = new JSONObject();
            final CountDownLatch latch = new CountDownLatch(1);
            
            myFirebaseRef.child("trips").child(String.valueOf(id)).child("status").setValue("approved");
            
       
                myFirebaseRef.child("passenger").child(String.valueOf(pid)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String fullname = dataSnapshot.child("fullname").getValue(String.class);
                        String phone = dataSnapshot.child("phone").getValue(String.class);
                        String token = dataSnapshot.child("token").getValue(String.class);

                        
                        JSONObject p = new JSONObject();
                        p.put("fullname", fullname);
                        p.put("phone", phone);

                        resobj.put("passenger", p);
                         latch.countDown();
                    }

                @Override
                public void onCancelled() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    
                }
                });
            

                  //get current datetime 
//            java.util.Date dt = new java.util.Date();
//            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String currentTime = sdf.format(dt);
            
//            String k = "UPDATE trip SET driver_id = '"+did+"' AND end = '"+currentTime+"'  WHERE trip_id = "+id+";";
//            excDB(k);
//            ResultSet rs = getDBResultSet("SELECT * FROM trip WHERE driver_id = "+did);
//            JSONObject tripobj = new JSONObject();   
//            while(rs.next())
//             {           
//                
//                 int tripid = rs.getInt(1);
//                 String start = rs.getString(2);
//                 String end = rs.getString(3);
//                 String price = rs.getString(4);  
//                 String comment = rs.getString(5);
//                 String ratting = rs.getString(6);
//                 String passenger_id = rs.getString(7);
//                 String driver_id = rs.getString(8);
//                 
//                 tripobj.put("tripid", tripid);
//                 tripobj.put("start", start);
//                 tripobj.put("end", end);
//                 tripobj.put("price", price);
//                 tripobj.put("comment",  comment);
//                 tripobj.put("ratting", ratting);
//                 tripobj.put("passenger_id", passenger_id);
//                tripobj.put("driver_id",  driver_id );
//                obj.put("tripobj", tripobj);



//                ResultSet rs2 = getDBResultSet("SELECT * FROM passenger WHERE passenger_id = "+passenger_id);
//                while(rs2.next())
//                {
//                    JSONObject p = new JSONObject();
//                     String fullname = rs2.getString(3);
//                     int phone = rs2.getInt(6);
//
//                     p.put("fullname", fullname);
//                     p.put("phone", phone);
//
//                     obj.put("passenger", p);
//                }
//             }
            
            resobj.put("success", "1");
            resobj.put("msg", "Done Successfully");
            
//            conn.close();
            
          try{
            latch.await();
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
//            response=Response.status(200).entity(resobj).build();
        }
          
        return Response.status(200).entity(resobj).build();
    }
       
   
}
