
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hubberspot.jersey;


import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.history.PNHistoryItemResult;
import com.pubnub.api.models.consumer.history.PNHistoryResult;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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

/**
 *
 * @author dell
 */

@ApplicationPath("/")
@Path("/passengerservice")
public class PassengerAppServiceJersey {
   // Connection conn;
    
    List<Integer> driversIDs;
    List<Double> driversLats,driversLngs, driverDistance;
    //int pickupSelectedDriverID;

    public static Firebase myFirebaseRef;
    
    JSONObject resobj;
    int f;

    @GET //test only
    @Path("/go")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getgo(){
        String output = "gooooooooooooooooooooo Hebatest" ;
        return Response.status(200).entity(output).build();
    }
    
    public static String sendFireNotification(String token, String title, String msg){
        try{
       HttpURLConnection httpcon = (HttpURLConnection) ((new URL("https://fcm.googleapis.com/fcm/send").openConnection()));
        httpcon.setDoOutput(true);
        httpcon.setRequestProperty("Content-Type", "application/json");
        httpcon.setRequestProperty("Authorization", "key=AAAAT9pcEV8:APA91bHABEPrQ6Id8QQnGjEP7YFwdTMW1Mt6vI2wdMuK0D3j3_HiodBfx-Bg_2ApoA6k7Y0Kj3l9CfJgP98cKI7mERxH3ao5fXbQwqA9_9iE9VZemt2lYbX7VSCMpnpbYHDnXaopq1L1");//firebase key
        httpcon.setRequestMethod("POST");
        httpcon.connect();
        System.out.println("Connected!");

//        byte[] outputBytes = String.valueOf("{\"notification\":{\"title\": \""+title+"\", \"text\": \""+msg+"\", \"sound\": \"default\"}, \"to\": \""+token+"\"}").getBytes("UTF-8");
        JSONObject notify = new JSONObject();
        JSONObject notification = new JSONObject();
        notification.put("title", title);
        notification.put("body", msg);
        
        notify.put("notification", notification);
        notify.put("to", token);
        
        
        byte[] outputBytes = notify.toString().getBytes("UTF-8");


        
        
        OutputStream os = httpcon.getOutputStream();
        os.write(outputBytes);
        os.close();

        // Reading response
        InputStream input = httpcon.getInputStream();
        
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);

            String res = responseStrBuilder.toString();
            return res;
        } catch (Exception ex) {
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
            return "error";
        }
    }
    
    
    @POST
    @Path("/getneardrivers")
    @Produces(MediaType.APPLICATION_JSON)    
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addfirebasepassenger(String data) throws Exception{
        JSONObject obj = new JSONObject();
        final CountDownLatch latch = new CountDownLatch(1);

           try {
               
            resobj = new JSONObject();

            JSONObject objj = JSONObject.fromObject(data);   

            final double ilat = objj.getDouble("ilat");                        
            final double ilng = objj.getDouble("ilng");            

            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");

                myFirebaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        JSONArray darr = new JSONArray();
                        for (DataSnapshot postSnapshot : dataSnapshot.child("vehicles").getChildren()) {
                            try{
                            int vid = Integer.parseInt(postSnapshot.getName());
                            String model = postSnapshot.child("model").getValue(String.class);
                            double lat = postSnapshot.child("Latitude").getValue(Double.class);
                            double lng = postSnapshot.child("Longitude").getValue(Double.class);
                            int status = postSnapshot.child("status").getValue(Integer.class);
                            
                            //check if it has a live trip
                            boolean intripflag = false;
                            for (DataSnapshot postSnapshot2 : dataSnapshot.child("trips").getChildren()) {
                                String tripstatus = postSnapshot2.child("status").getValue(String.class);
                                if(tripstatus.equals("requested") || tripstatus.equals("started")|| tripstatus.equals("approved")){
                                    intripflag = true;
                                }
                            }
                            
                            if(status==1 && intripflag==false){
                                
                                double dist = distance(ilat, lat, ilng, lng);
                                
                                if(dist<= 100000){
                                    for (DataSnapshot postSnapshot2 : dataSnapshot.child("driver").getChildren()) {
                                        int dvid = postSnapshot2.child("vid").getValue(int.class);
                                        if(dvid==vid){
                                            int did = Integer.parseInt(postSnapshot2.getName());
                                            int avg = postSnapshot2.child("avg").getValue(int.class);
                                            String avgtxt = postSnapshot2.child("avgtxt").getValue(String.class);
                                            String img = postSnapshot2.child("img").getValue(String.class);
                                            String fullname = postSnapshot2.child("fullname").getValue(String.class);

                                            JSONObject d = new JSONObject();
                                            d.put("did", did);
                                            d.put("avg", avg);
                                            d.put("img", img);
                                            d.put("avgtxt", avgtxt);
                                            d.put("vid", vid);
                                            d.put("model", model);
                                            d.put("dist", dist);
                                            d.put("fullname",fullname);
                                            darr.add(d);
                                        }
                                    }
                                }
                            
                            }
                            

                            
                            }catch(NullPointerException ne){
                                Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ne);
                            }catch(NumberFormatException ne){
                                Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ne);
                            }
                        }
                        
                        
                         
                         
                        
                        resobj.put("neardrivers", darr);                        
                        resobj.put("success", "1");
                        resobj.put("msg", "Selected");

                        response = Response.status(200).entity(resobj).build();
                        latch.countDown();
                    }

                @Override
                public void onCancelled() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
                });


           } catch (Exception ex) {
            obj.put("success", "0");
            obj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }

           try{
            latch.await();
        } catch (Exception ex) {
            obj.put("success", "0");
            obj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
            response=Response.status(200).entity(obj).build();
        }
        return response; //Response.status(200).entity(obj).build();
    }
    
    
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)    
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response LoginPassenger(String data) throws Exception{  
        JSONObject obj = new JSONObject();
        resobj = new JSONObject();
        final CountDownLatch latch = new CountDownLatch(1);


           try {
               myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");


            JSONObject objj = JSONObject.fromObject(data);   
            final String email = objj.getString("email");            
            final String password = objj.getString("password");

            
                myFirebaseRef.child("passenger").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        JSONObject d = new JSONObject();

                        resobj.put("success", "0");
                        resobj.put("msg", "Wrong Cred");//e7tyaty

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            try{
                            int pid = Integer.parseInt(postSnapshot.getName());
                            String useremail = postSnapshot.child("useremail").getValue(String.class);
                            String userpassword = postSnapshot.child("password").getValue(String.class);
                               
                            if(email.equals(useremail)&&password.equals(userpassword)){
                                int age = postSnapshot.child("age").getValue(Integer.class);
                                String fullname = postSnapshot.child("fullname").getValue(String.class);
                                String gender = postSnapshot.child("gender").getValue(String.class);
                                String language = postSnapshot.child("language").getValue(String.class);
                                String password = postSnapshot.child("password").getValue(String.class);
                                String phone = postSnapshot.child("phone").getValue(String.class);
                                String relatedphone = postSnapshot.child("relatedphone").getValue(String.class);
                                boolean active = postSnapshot.child("active").getValue(boolean.class);

                                if(active){
                                d.put("passenger_id", pid);
                                d.put("fullname", fullname);
                                d.put("gender", gender);
                                d.put("age", age);
                                d.put("phone", phone);
                                d.put("relatedphone", relatedphone);
                                d.put("language", language);
                                d.put("email", email);
                                d.put("active",active);

                                resobj.put("passenger", d);
                                
                                
                                resobj.put("success", "1");
                                resobj.put("msg", "logged in");
                                response = Response.status(200).entity(resobj).build();
                                
                                }
                                else{
                                resobj.put("success", "0");
                                resobj.put("msg", "Your account is deactivated");
                                response = Response.status(200).entity(resobj).build();
                                }
                                latch.countDown();
                            }
                            
                            
                            

                            
                            }catch(NullPointerException ne){
                                Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ne);
                            }catch(NumberFormatException ne){
                                Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ne);
                            }
                        }
                        latch.countDown();
                        
                    }

                @Override
                public void onCancelled() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    
                }
                });



//            ResultSet rs = getDBResultSet("SELECT * FROM passenger WHERE useremail = '"+email+"'");//knt user_id
//            obj.put("success", "0");
//            obj.put("msg", "Wrong email or Password");
//            while(rs.next())
//            {
//                 String pass = rs.getString(2);
//                 if(pass.equals(password))
//                 {
//                     //loginpassenger
//                     obj.put("success", "1");
//                     obj.put("msg", "Logged in successfully");
//                     
//                     JSONObject d = new JSONObject();
//                     String passenger_id = rs.getString(1);
//                     String fullname = rs.getString(3);
//                     String gender = rs.getString(4);
//                     int age = rs.getInt(5);
//                     int phone = rs.getInt(6);
//                     int relatedphone = rs.getInt(7);
//                     String language = rs.getString("language");
//
//                     d.put("passenger_id", passenger_id);
//                     d.put("fullname", fullname);
//                     d.put("gender", gender);
//                     d.put("age", age);
//                     d.put("phone", phone);
//                     d.put("relatedphone", relatedphone);
//                     d.put("language", language);
//                     d.put("email", email);
//
//                     obj.put("passenger", d);
//
//                 }
//                 else {
//                     obj.put("success", "0");
//                     obj.put("msg", "Wrong Credentials");
//                 }}

             
            
              

        try{
            latch.await();
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
            response=Response.status(200).entity(resobj).build();
        }
            //conn.close();
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.status(200).entity(resobj).build();

    }
    
    
    @POST
    @Path("/signup")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response passengersignup(String data) throws Exception{
            //as post request
            JSONObject dataObj = JSONObject.fromObject(data);
            final String name = dataObj.getString("name");
            final String email = dataObj.getString("email");
            final String password = dataObj.getString("password");
            final int phone = dataObj.getInt("phone");
            final int relatedphone = dataObj.getInt("relatedphone");
            final String gender = dataObj.getString("gender");
            final int age = dataObj.getInt("age");
            final String language = dataObj.getString("language");
            
            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");

            

//            JSONObject obj = new JSONObject();
            final CountDownLatch latch = new CountDownLatch(1);
           f=0;
           
                //get children count
                
                myFirebaseRef.child("passenger").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(f==0){
                            f=1;
                        
                        long count = dataSnapshot.getChildrenCount();      
                        long insertedid = count+1;
                        
                        myFirebaseRef.child("passenger").child(String.valueOf(insertedid)).child("age").setValue(age);
                        myFirebaseRef.child("passenger").child(String.valueOf(insertedid)).child("useremail").setValue(email);
                        myFirebaseRef.child("passenger").child(String.valueOf(insertedid)).child("gender").setValue(gender);
                        myFirebaseRef.child("passenger").child(String.valueOf(insertedid)).child("fullname").setValue(name);
                        myFirebaseRef.child("passenger").child(String.valueOf(insertedid)).child("phone").setValue(phone);
                        myFirebaseRef.child("passenger").child(String.valueOf(insertedid)).child("password").setValue(password);
                        myFirebaseRef.child("passenger").child(String.valueOf(insertedid)).child("relatedphone").setValue(relatedphone);
                        myFirebaseRef.child("passenger").child(String.valueOf(insertedid)).child("language").setValue(language);
                        myFirebaseRef.child("passenger").child(String.valueOf(insertedid)).child("active").setValue(true);

                            resobj.put("success", "1");
                            resobj.put("msg", "Added Successfully");

                            resobj.put("insertedid", insertedid);
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
                 response=Response.status(200).entity(resobj).build();
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
            
           
            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
            
                myFirebaseRef.child("trips").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                int pid = postSnapshot.child("pid").getValue(Integer.class);
                                String status = postSnapshot.child("status").getValue(String.class);

                                JSONArray paths = new JSONArray();
                                if(pid == id && status.equals("ended")){

                                    int tid = Integer.parseInt(postSnapshot.getName());
                                    String comment = postSnapshot.child("comment").getValue(String.class);
//                                    String details = postSnapshot.child("details").getValue(String.class);
                                    String did = postSnapshot.child("did").getValue(String.class);
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
                        
                        String dname = dataSnapshot.child("driver").child(String.valueOf(pid)).child("fullname").getValue(String.class);

                        
                        tripobj.put("start", start);
                        tripobj.put("end", end);
                        tripobj.put("price", price);
                        tripobj.put("comment",  comment);
                        tripobj.put("ratting", ratting);
                        tripobj.put("passenger_id", pid);
                        tripobj.put("driver_id",  did );
                        tripobj.put("driver_name",  dname );


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
   
    
    @POST
    @Path("/editpassengerr")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response editpassenger(String data){
            //as post request
            JSONObject dataObj = JSONObject.fromObject(data);
            String name = dataObj.getString("name");
            String email = dataObj.getString("useremail");
            String password = dataObj.getString("password");
            int phone = dataObj.getInt("phone");
            int relatedphone = dataObj.getInt("relatedphone");
            int id = dataObj.getInt("id");
            
            resobj = new JSONObject();

            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
            
            //myFirebaseRef.child("passenger").child(String.valueOf(id)).child("age").setValue(age);
            //myFirebaseRef.child("passenger").child(String.valueOf(id)).child("gender").setValue(gender);
            myFirebaseRef.child("passenger").child(String.valueOf(id)).child("fullname").setValue(name);
            myFirebaseRef.child("passenger").child(String.valueOf(id)).child("phone").setValue(phone);
            myFirebaseRef.child("passenger").child(String.valueOf(id)).child("password").setValue(password);
            myFirebaseRef.child("passenger").child(String.valueOf(id)).child("relatedphone").setValue(relatedphone);
            myFirebaseRef.child("passenger").child(String.valueOf(id)).child("useremail").setValue(email);
            //myFirebaseRef.child("passenger").child(String.valueOf(id)).child("language").setValue(language);

            resobj.put("success", "1");
            resobj.put("msg", "Edited Successfully");
            
        
        return Response.status(200).entity(resobj).build();
    }

  
    
    @POST
    @Path("/sendfeedback")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response sendfeedback(String data){
        //as post request
        JSONObject dataObj = JSONObject.fromObject(data);
        String comment = dataObj.getString("comment");
        int tripid = dataObj.getInt("trip_id");
        int rate = dataObj.getInt("ratting");
        
        resobj=new JSONObject();
        myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");

            myFirebaseRef.child("trips").child(String.valueOf(tripid)).child("comment").setValue(comment);
            myFirebaseRef.child("trips").child(String.valueOf(tripid)).child("ratting").setValue(rate);

            
            resobj.put("success", "1");
            resobj.put("msg", "Success");
            
        
        return Response.status(200).entity(resobj).build();
    }
    
    
    Response response;
    //JSONObject obj;
    int selectednearvid;
    @POST
    @Path("/submitpickup")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response submitpickup(String data){
            JSONObject dataObj = JSONObject.fromObject(data);
            final double ilat = dataObj.getDouble("lat");
            final double ilng = dataObj.getDouble("lng");
            final String details = dataObj.getString("details");
            final int passengerid = dataObj.getInt("passengerid");
            final int nearestvehicleid = dataObj.getInt("vid");
            
            f = 0;

            resobj = new JSONObject();
            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
            final CountDownLatch latch = new CountDownLatch(1);

//            driversIDs = new ArrayList<Integer>();
//            driversLats = new ArrayList<Double>();
//            driversLngs = new ArrayList<Double>();
            
//            min_id = 0;
//            min_distance = 0;
                
            try{
                myFirebaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        
                        if(f == 0){
                            
                        f = 1;
                        
                        //if nearest vehicle id is 0 so pick the nearest else it is selected
                        if(nearestvehicleid==0){
                            double mindis = 0; int minid = 0;
                             for (DataSnapshot postSnapshot : dataSnapshot.child("vehicles").getChildren()) {
                                 int nvid = Integer.parseInt(postSnapshot.getName());
                                 double vlat = postSnapshot.child("Latitude").getValue(double.class);
                                 double vlng = postSnapshot.child("Longitude").getValue(double.class);
                                 
                                 //check if this vehicle is free of trips
                                 boolean intripflag = false;
                                  for (DataSnapshot postSnapshot2 : dataSnapshot.child("trips").getChildren()) {
                                      int tripvid = postSnapshot2.child("vid").getValue(int.class);
                                      String tripstatus = postSnapshot2.child("status").getValue(String.class);
                                      if(tripvid==nearestvehicleid){
                                          if(tripstatus.equals("started") || tripstatus.equals("requested")){
                                              intripflag=true;
                                          }
                                      }
                                  }
                                  if(!intripflag){//not on trip socheck distance
                                    double dist = distance(ilat, vlat, ilng, vlng);
                                    if(dist<= 100000){
                                        if(mindis == 0){
                                           mindis = dist;
                                           minid = nvid;
                                        }
                                        else if (dist<mindis){
                                           mindis = dist;
                                           minid = nvid;
                                        }
                                    }
                                }
                             }
                             selectednearvid=minid;
                        }
                        else{
                            selectednearvid=nearestvehicleid;
                        }
                        
                        
                        //if vid == 0 so there is no near drivers
                        if(selectednearvid==0){
                            resobj.put("success", "0");
                            resobj.put("msg","There is no empty nearby drivers!");
                            latch.countDown();
                        }else{
                        //////////////////
                        
                        String selecteddrivertoken = "";
                        int pickupSelectedDriverID = 0;
                        for (DataSnapshot postSnapshot : dataSnapshot.child("driver").getChildren()) {
                            int did = Integer.parseInt(postSnapshot.getName());
                            int vid = postSnapshot.child("vid").getValue(Integer.class);
                            if(vid==selectednearvid){
                             JSONObject d = new JSONObject();
                             String fullname = postSnapshot.child("fullname").getValue(String.class);
                             selecteddrivertoken = postSnapshot.child("token").getValue(String.class);
                             d.put("driver_id", did);
                             d.put("fullname", fullname);
                             resobj.put("driver", d);
                             pickupSelectedDriverID=did;
                            }
                            
                        }
                        for (DataSnapshot postSnapshot : dataSnapshot.child("vehicles").getChildren()) {
                            int vid = Integer.parseInt(postSnapshot.getName());
                            if(vid==selectednearvid){
                                JSONObject v = new JSONObject();
                                String model = postSnapshot.child("model").getValue(String.class);
                                String color = postSnapshot.child("color").getValue(String.class);
                                String plate_number = postSnapshot.child("plate_number").getValue(String.class);

                                v.put("vehicle_id", selectednearvid);
                                v.put("model", model);
                                v.put("color", color);
                                v.put("plate_number", plate_number);
                                resobj.put("vehicle", v);
                            }
                            
                        }
                        
                        long tripscount = dataSnapshot.child("trips").getChildrenCount();
                        long tripid = tripscount+1;
                        for (DataSnapshot postSnapshot : dataSnapshot.child("trips").getChildren()) {
                            int tid = Integer.parseInt(postSnapshot.getName());
                            if(tid==tripid)
                                tripid++;
                        }
//                        ////////////////////////////////////////////////////////////////////////////////
                        //set trip status
                        myFirebaseRef.child("trips").child(String.valueOf(tripid)).child("status").setValue("requested");
                        myFirebaseRef.child("trips").child(String.valueOf(tripid)).child("ilat").setValue(ilat);
                        myFirebaseRef.child("trips").child(String.valueOf(tripid)).child("ilng").setValue(ilng);
                        myFirebaseRef.child("trips").child(String.valueOf(tripid)).child("details").setValue(details);
                        myFirebaseRef.child("trips").child(String.valueOf(tripid)).child("timestamp").setValue(System.currentTimeMillis());
                        myFirebaseRef.child("trips").child(String.valueOf(tripid)).child("did").setValue(pickupSelectedDriverID);
                        myFirebaseRef.child("trips").child(String.valueOf(tripid)).child("pid").setValue(passengerid);
                        
                        myFirebaseRef.child("trips").child(String.valueOf(tripid)).child("start").setValue(0);
                        myFirebaseRef.child("trips").child(String.valueOf(tripid)).child("end").setValue(0);
                        myFirebaseRef.child("trips").child(String.valueOf(tripid)).child("price").setValue(0);
                        myFirebaseRef.child("trips").child(String.valueOf(tripid)).child("comment").setValue(0);
                        myFirebaseRef.child("trips").child(String.valueOf(tripid)).child("ratting").setValue(0);




//                        String testtoken = "ccH74m0Ipzo:APA91bFS0qrW0U65OOqzV330GaX3i3wLnhnK9rTMfcPpd2yogdaCsqEqogMTbsdLJVUWDAOqzRnNn4YB_4ID4HLJtAfuzOyOIKdF5fMxJ344Rs5lex4b7VuAthMLOhqg8DqQUI_kBh-2"; 
                        //send notification to driver
                        sendFireNotification(selecteddrivertoken,"Trip Request","You have new trip request");
                        
                        resobj.put("tripid", tripid);
                        resobj.put("success", "1");
                        resobj.put("msg","Done successful");
                        
                        response = Response.status(200).entity(resobj).build();
                        latch.countDown();
                        }
                        
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
            response=Response.status(200).entity(resobj).build();
        }
        return Response.status(200).entity(resobj).build();
    }
    
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
         
         
         
         //////edit data
         
    @POST
    @Path("/editpassenger")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
  public Response editDriver(String data){
        JSONObject dataobj = JSONObject.fromObject(data);
        int pid = dataobj.getInt("pid");
        String name = dataobj.getString("name");
        String email = dataobj.getString("email");
        String pass = dataobj.getString("pass");
        String ph = dataobj.getString("ph");
        String relativeph = dataobj.getString("relativeph");

//        JSONObject obj = new JSONObject();
        resobj = new JSONObject();
        myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
        try {         
            
            myFirebaseRef.child("passenger").child(String.valueOf(pid)).child("fullname").setValue(name);
            myFirebaseRef.child("passenger").child(String.valueOf(pid)).child("useremail").setValue(email);
            myFirebaseRef.child("passenger").child(String.valueOf(pid)).child("password").setValue(pass);
            myFirebaseRef.child("passenger").child(String.valueOf(pid)).child("phone").setValue(ph);
            myFirebaseRef.child("passenger").child(String.valueOf(pid)).child("relatedphone").setValue(relativeph);

            
            resobj.put("success", "1");
            resobj.put("msg", "Edited Successfully");
            
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(resobj).build();
    }
  
  
}
