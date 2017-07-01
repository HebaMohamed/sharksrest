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
//    Connection conn;
    
    double KMCOST = 2;//2 LE :D
    
    public static Firebase myFirebaseRef;
    
    int f; //to check that ondatachanged fired only once
    
    @GET
    @Path("/initFirebase")    
    public Response initFirebase(){
        myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
       //myFirebaseRef.child("test").setValue(1);
       
       //String g = myFirebaseRef.child("trips").child("1").child("status").endAt().toString();

               
       String testtoken = "cdD9IoljfkM:APA91bFeN7ZYCFdyrFrd9iVpFBkDI_c24TyKhSSCy7F9SzytsOgPv0W2cTA8jld2GuuJ1GNVbZ8Rg-b8kAd0HNWlxJdwX6roGIG7c2YZsKQ6VHwGB1vOvbOAPbqeWD3hTQIZP05vhUcQ"; 
        //send notification to driver
       String r = sendFireNotification(testtoken,"Trip Request Test","You have new trip request");
       
       return Response.status(200).entity(r).build();

    }

//    ResultSet getDBResultSet(String query) throws Exception{
//                    
//        Class.forName("com.mysql.jdbc.Driver");            
//        //conn = DriverManager.getConnection("jdbc:mysql://localhost/hebadb?" + "user=root&password=");
//        //conn = DriverManager.getConnection("jdbc:mysql://db4free.net/nashwa346db?" + "user=nashwa346&password=123456");
//        //conn = DriverManager.getConnection("jdbc:mysql://sql8.freesqldatabase.com/sql8166151?" + "user=sql8166151&password=CnJ3KUzlDR");
//        //conn = DriverManager.getConnection("jdbc:mysql://db4free.net/nashwa346db?" + "user=nashwa346&password=123456");
//        conn = DriverManager.getConnection("jdbc:mysql://sharksspace-heba-mohamed.c9users.io:8080/mysharksdb?" + "user=hoba_hoby&password=");
//        
//        //conn = DriverManager.getConnection("jdbc:mysql://johnny.heliohost.org/hobahob1_sharks?" + "user=hobahob1&password=HOBAHOBY1995");
//
//        Statement st = conn.createStatement();
//        ResultSet rs = st.executeQuery(query);
//        return rs;            
//    }
//    void excDB(String query) throws Exception{
//                    
//        Class.forName("com.mysql.jdbc.Driver");            
//        //conn = DriverManager.getConnection("jdbc:mysql://localhost/hebadb?" + "user=root&password=");
//        //conn = DriverManager.getConnection("jdbc:mysql://sql8.freesqldatabase.com/sql8166151?" + "user=sql8166151&password=CnJ3KUzlDR");
//        //conn = DriverManager.getConnection("jdbc:mysql://johnny.heliohost.org/hobahob1_sharks?" + "user=hobahob1&password=HOBAHOBY1995");
//        conn = DriverManager.getConnection("jdbc:mysql://db4free.net/nashwa346db?" + "user=nashwa346&password=123456");
//        Statement st = conn.createStatement();
//        st.executeUpdate(query);//
//    }
    
    
    
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
            f=0;
            
            
                myFirebaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                      
                        int ff=0;

                        if(f==0){
                            f=1;
                        JSONObject d = new JSONObject();
                        for (DataSnapshot postSnapshot : dataSnapshot.child("driver").getChildren()) {
                            try{
                            int did = Integer.parseInt(postSnapshot.getName());
                            String pass = postSnapshot.child("password").getValue(String.class);
                            if(did==id&&pass.equals(password)){
                                String email = postSnapshot.child("email").getValue(String.class);
                                String fullname = postSnapshot.child("fullname").getValue(String.class);
                                int vid = postSnapshot.child("vid").getValue(Integer.class);
                                String img = postSnapshot.child("img").getValue(String.class);
                                
                                //get restricted route
                                JSONArray ristrictions = new JSONArray();
                                for (DataSnapshot rpostSnapshot : postSnapshot.child("route_restrictions").getChildren()) {
                                    double lat = rpostSnapshot.child("lat").getValue(double.class);
                                    double lng = rpostSnapshot.child("lng").getValue(double.class);
                                    JSONObject ris = new JSONObject();
                                    ris.put("lat", lat);
                                    ris.put("lng", lng);
                                    ristrictions.add(ris);
                                }

                                d.put("email", email);
                                d.put("fullname", fullname);
//                                d.put("vehicle_id", vid);
                                d.put("img", img);
                                d.put("route", ristrictions);

                                resobj.put("driver", d);
                                
                                //car data
                                String color = dataSnapshot.child("vehicles").child(String.valueOf(vid)).child("color").getValue(String.class);
                                String plate_number = dataSnapshot.child("vehicles").child(String.valueOf(vid)).child("plate_number").getValue(String.class);
                                String model = dataSnapshot.child("vehicles").child(String.valueOf(vid)).child("model").getValue(String.class);
                                
                                JSONObject vobj = new JSONObject();
                                vobj.put("vehicle_id",vid);
                                vobj.put("color",color);
                                vobj.put("plate_number",plate_number);
                                vobj.put("model",model);
                                
                                resobj.put("vehicle", vobj);


                                resobj.put("success", "1");
                                resobj.put("msg", "logged in");
                                
                                
                                
                                ff=1;
//                                response = Response.status(200).entity(resobj).build();
                                latch.countDown();
                            }
                            else{
                                if(ff==0){//
                                    resobj.put("success", "0");
                                    resobj.put("msg", "Wrong cred");
                                }
                            }
                            }catch(NullPointerException ne){
                                Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ne);
                            }catch(NumberFormatException ne){
                                Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ne);
                            }
                        }
                        latch.countDown();
                        }
                    }

                @Override
                public void onCancelled() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.   
                }
                });



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
            
            tripsarr = new JSONArray();
            f=0;
            
           
            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
            
                myFirebaseRef.child("trips").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        
                        if(f==0){
                            f=1;
                        

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                int did = postSnapshot.child("did").getValue(Integer.class);
                                String status = postSnapshot.child("status").getValue(String.class);
                                JSONArray paths = new JSONArray();
                                if(did == id && status.equals("ended")){

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
                    }

                @Override
                public void onCancelled() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    
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
    
    
    @GET
    @Path("/getlasttrip/{tripid}")
      @Produces(MediaType.APPLICATION_JSON)
   public Response getLasttrip(@PathParam("tripid") final int id){
       
        //JSONObject obj = new JSONObject();
    try {
        resobj = new JSONObject();
        final CountDownLatch latch = new CountDownLatch(1);
        myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
        
        
                myFirebaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        JSONObject tripobj = new JSONObject();
                        JSONArray paths = new JSONArray();
                        

                        
//                        String status = dataSnapshot.child("trips").child(String.valueOf(id)).child("status").getValue(String.class);                        
                        String comment = dataSnapshot.child("trips").child(String.valueOf(id)).child("comment").getValue(String.class);
//                        String details = dataSnapshot.child("trips").child(String.valueOf(id)).child("details").getValue(String.class);
                        String did = dataSnapshot.child("trips").child(String.valueOf(id)).child("did").getValue(String.class);
                        String end = dataSnapshot.child("trips").child(String.valueOf(id)).child("end").getValue(String.class);
                        String pid = dataSnapshot.child("trips").child(String.valueOf(id)).child("pid").getValue(String.class);
                        String price = dataSnapshot.child("trips").child(String.valueOf(id)).child("price").getValue(String.class);
                        String ratting = dataSnapshot.child("trips").child(String.valueOf(id)).child("ratting").getValue(String.class);
                        String start = dataSnapshot.child("trips").child(String.valueOf(id)).child("start").getValue(String.class);
                        
                        String pname = dataSnapshot.child("passenger").child(String.valueOf(pid)).child("fullname").getValue(String.class);

                        tripobj.put("start", start);
                        tripobj.put("end", end);
                        tripobj.put("price", price);
                        tripobj.put("comment",  comment);
                        tripobj.put("ratting", ratting);
                        tripobj.put("passenger_id", pid);
                        tripobj.put("driver_id",  did );
                        tripobj.put("passenger_id",  pid );
                        tripobj.put("passenger_name",  pname );


                        try{
                            for (DataSnapshot postSnapshot : dataSnapshot.child("trips").child(String.valueOf(id)).child("pathway").getChildren()) {

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
        
                
            latch.await();
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.status(200).entity(resobj).build();

    }
    
   
    @GET
    @Path("/donetrip/{tripid}/{vehicleid}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
   public Response donetrip(@PathParam("tripid") final int id, @PathParam("vehicleid") final int vehicleid) throws Exception{
           
            //to calculate pathway
            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
            resobj = new JSONObject();
            final CountDownLatch latch = new CountDownLatch(1);
            f = 0;
            
                myFirebaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        
                        if(f==0){
                            f=1;
                            
                            //check if trip exists
                            boolean exist = false;
                            for (DataSnapshot postSnapshot : dataSnapshot.child("trips").getChildren()){
                                int stid = Integer.parseInt(postSnapshot.getName());
                                if(stid==id)
                                    exist = true;
                            }
                            if(!exist){
                                resobj.put("success", "0");
                                resobj.put("msg", "There is no trip with this id");
                                latch.countDown();    
                            }
                        
                         
                        JSONArray paths = new JSONArray();

                        for (DataSnapshot postSnapshot : dataSnapshot.child("trips").child(String.valueOf(id)).child("pathway").getChildren()) {
                            
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
                        
                            
                        
                        int p1=0,p2=0,p3=0,p4=0,p5=0,p6=0,p7=0,p8=0,p9=0,p10=0,p11=0,p12=0;
                            //get pattrens during trip
                            long start = dataSnapshot.child("trips").child(String.valueOf(id)).child("start").getValue(long.class);
                            long end = dataSnapshot.child("trips").child(String.valueOf(id)).child("end").getValue(long.class);
                            for (DataSnapshot postSnapshot : dataSnapshot.child("vehicles").child(String.valueOf(vehicleid)).child("Patterns detected").getChildren()) {
                                long patrentimestamp = Long.parseLong(postSnapshot.getName());
                                if(patrentimestamp>=start && patrentimestamp <= end){//m3ana
                                    int pattrenid = postSnapshot.getValue(int.class);
//                                    pattrenobj.put("timestamp", patrentimestamp);
//                                    pattrenobj.put("pattrenid",pattrenid);
//                                    pattrensarr.add(pattrenobj);

                                    if(pattrenid == 1){
                                        p1++;
                                    }
                                    else if(pattrenid == 2){
                                        p2++;
                                    }
                                    else if(pattrenid == 3){
                                        p3++;
                                    }
                                    else if(pattrenid == 4){
                                        p4++;
                                    }
                                    else if(pattrenid == 5){
                                        p5++;
                                    }
                                    else if(pattrenid == 6){
                                        p6++;
                                    }
                                    else if(pattrenid == 7){
                                        p7++;
                                    }
                                    else if(pattrenid == 8){
                                        p8++;
                                    }
                                    else if(pattrenid == 9){
                                        p9++;
                                    }
                                    else if(pattrenid == 10){
                                        p10++;
                                    }
                                    else if(pattrenid == 11){
                                        p11++;
                                    }
                                    else if(pattrenid == 12){
                                        p12++;
                                    }
                                    

                                }
                            }
                            
                            JSONObject pattrenobj = new JSONObject();
                            pattrenobj.put("p1", p1);
                            pattrenobj.put("p2", p2);
                            pattrenobj.put("p3", p3);
                            pattrenobj.put("p4", p4);
                            pattrenobj.put("p5", p5);
                            pattrenobj.put("p6", p6);
                            pattrenobj.put("p7", p7);
                            pattrenobj.put("p8", p8);
                            pattrenobj.put("p9", p9);
                            pattrenobj.put("p10", p10);
                            pattrenobj.put("p11", p11);
                            pattrenobj.put("p12", p12);

                            
                            //get pattrens names
                            String n="";
                            JSONObject pattrennames = new JSONObject();

                            for (DataSnapshot postSnapshot : dataSnapshot.child("pattrens").getChildren()) {
                                int pattrenid = Integer.parseInt(postSnapshot.getName());
                                n=postSnapshot.child("name").getValue(String.class);
                                     
                                pattrennames.put(String.valueOf("p"+pattrenid), n);
                            }
                                    
                            
                        
                        
                            //add it
                            double distancecost = 10;//basic fare
                            distancecost+= (int) (KMCOST*(fulldistance/1000));
                            resobj.put("distance", fulldistance);
                            resobj.put("distancecost", distancecost);
                            resobj.put("pattrenobj", pattrenobj);
                            resobj.put("pattrennames", pattrennames);
                            
                            myFirebaseRef.child("trips").child(String.valueOf(id)).child("end").setValue(System.currentTimeMillis());
                            myFirebaseRef.child("trips").child(String.valueOf(id)).child("price").setValue(distancecost);
                                                       
                            resobj.put("success", "1");
                            resobj.put("msg", "Trip Ended Successfully");
                            latch.countDown();
                        }
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
            f=0;
            
            myFirebaseRef.child("trips").child(String.valueOf(id)).child("status").setValue("approved");
            
       
                myFirebaseRef.child("passenger").child(String.valueOf(pid)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(f==0){
                            f=1;
                        
                            String fullname = dataSnapshot.child("fullname").getValue(String.class);
                            String phone = dataSnapshot.child("phone").getValue(String.class);
                            String token = dataSnapshot.child("token").getValue(String.class);


                            JSONObject p = new JSONObject();
                            p.put("fullname", fullname);
                            p.put("phone", phone);

                            resobj.put("passenger", p);
                             latch.countDown();
                        }
                    }

                @Override
                public void onCancelled() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    
                }
                });
           
            
            resobj.put("success", "1");
            resobj.put("msg", "Done Successfully");
            
            
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
