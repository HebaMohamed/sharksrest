
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
    
    int f;
//    int min_id = 0;
//    double min_distance = 0;

    @GET //test only
    @Path("/go")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getgo(){
        String output = "gooooooooooooooooooooo Hebat" ;
        return Response.status(200).entity(output).build();
    }
    
    public static String sendFireNotification(String token, String title, String msg){
        try{
       HttpURLConnection httpcon = (HttpURLConnection) ((new URL("https://fcm.googleapis.com/fcm/send").openConnection()));
        httpcon.setDoOutput(true);
        httpcon.setRequestProperty("Content-Type", "application/json");
        httpcon.setRequestProperty("Authorization", "key=AAAAT9pcEV8:APA91bHABEPrQ6Id8QQnGjEP7YFwdTMW1Mt6vI2wdMuK0D3j3_HiodBfx-Bg_2ApoA6k7Y0Kj3l9CfJgP98cKI7mERxH3ao5fXbQwqA9_9iE9VZemt2lYbX7VSCMpnpbYHDnXaopq1L1");
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
//    public static String getFiredata(String url, String param ) throws Exception{
//        
//
//        String charset = "UTF-8"; 
//        URLConnection connection = new URL(url).openConnection();
//        connection.setDoOutput(true); // Triggers POST.
//        connection.setRequestProperty("Accept-Charset", charset);
//        connection.setRequestProperty("Content-Type", "application/json;charset=" + charset);
//
//        OutputStream output = connection.getOutputStream();
//        output.write(param.getBytes(charset));
//
//
//        InputStream response = connection.getInputStream();
//
//        BufferedReader streamReader = new BufferedReader(new InputStreamReader(response, "UTF-8"));
//        StringBuilder responseStrBuilder = new StringBuilder();
//
//        String inputStr;
//        while ((inputStr = streamReader.readLine()) != null)
//            responseStrBuilder.append(inputStr);
//
//        String s = responseStrBuilder.toString();
//
//       return s;
//  }
//    ResultSet getDBResultSet(String query) throws Exception{
//                    
//        Class.forName("com.mysql.jdbc.Driver");            
//        //conn = DriverManager.getConnection("jdbc:mysql://localhost/hebadb?" + "user=root&password=");
//        //conn = DriverManager.getConnection("jdbc:mysql://sql11.freesqldatabase.com/sql11164022?" + "user=sql11164022&password=GLj4H4TT5N");
//        //conn = DriverManager.getConnection("jdbc:mysql://sql8.freesqldatabase.com/sql8166151?" + "user=sql8166151&password=CnJ3KUzlDR");
//        conn = DriverManager.getConnection("jdbc:mysql://db4free.net/nashwa346db?" + "user=nashwa346&password=123456");     
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
//        //conn = DriverManager.getConnection("jdbc:mysql://sql11.freesqldatabase.com/sql11164022?" + "user=sql11164022&password=GLj4H4TT5N");
//       // conn = DriverManager.getConnection("jdbc:mysql://sql8.freesqldatabase.com/sql8166151?" + "user=sql8166151&password=CnJ3KUzlDR");
//        conn = DriverManager.getConnection("jdbc:mysql://db4free.net/nashwa346db?" + "user=nashwa346&password=123456");
//        //conn = DriverManager.getConnection("jdbc:mysql://johnny.heliohost.org/hobahob1_sharks?" + "user=hobahob1&password=HOBAHOBY1995");
//        
//        Statement st = conn.createStatement();
//        st.executeUpdate(query);
//    }
//    int excDBgetID(String query, String tableName) throws Exception{
//                    
//        Class.forName("com.mysql.jdbc.Driver");            
//        //conn = DriverManager.getConnection("jdbc:mysql://localhost/hebadb?" + "user=root&password=");
//        //conn = DriverManager.getConnection("jdbc:mysql://sql11.freesqldatabase.com/sql11164022?" + "user=sql11164022&password=GLj4H4TT5N");
//        //conn = DriverManager.getConnection("jdbc:mysql://sql8.freesqldatabase.com/sql8166151?" + "user=sql8166151&password=CnJ3KUzlDR");
//        conn = DriverManager.getConnection("jdbc:mysql://db4free.net/nashwa346db?" + "user=nashwa346&password=123456");
//        //conn = DriverManager.getConnection("jdbc:mysql://johnny.heliohost.org/hobahob1_sharks?" + "user=hobahob1&password=HOBAHOBY1995");
//        
//        Statement st = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
////        ResultSet rs = st.executeQuery(query);
////        ResultSet rs = st.executeQuery("SELECT LAST_INSERT_ID()");
////        int insertedid = 0;
////        if (rs.next()){
////            insertedid =rs.getInt(1);
////        }
//
//        int insertedidrow = st.executeUpdate(query);
//        ResultSet rs = st.executeQuery("select last_insert_id() as last_id from "+tableName);
//        rs.next();
//        int insertedid = rs.getInt("last_id");        
//
//        return insertedid;
//    }
//    
    
    
    JSONObject resobj;
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
//            int age = objj.getInt("age");            
//            String fullname = objj.getString("fullname");
//            int phone = objj.getInt("phone");
//            int relatedphone = objj.getInt("relatedphone");  
//            String password = objj.getString("password");
//            String language = objj.getString("language");
//            String gender = objj.getString("gender");    

            final double ilat = objj.getDouble("ilat");                        
            final double ilng = objj.getDouble("ilng");            


            
            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");

//            myFirebaseRef.child("passenger").child(String.valueOf(2)).child("age").setValue(age);
//            myFirebaseRef.child("passenger").child(String.valueOf(2)).child("gender").setValue(gender);
//            myFirebaseRef.child("passenger").child(String.valueOf(2)).child("fullname").setValue(fullname);
//            myFirebaseRef.child("passenger").child(String.valueOf(2)).child("phone").setValue(phone);
//            myFirebaseRef.child("passenger").child(String.valueOf(2)).child("password").setValue(password);
//            myFirebaseRef.child("passenger").child(String.valueOf(2)).child("relatedphone").setValue(relatedphone);
//            myFirebaseRef.child("passenger").child(String.valueOf(2)).child("language").setValue(language);
//           

            


                myFirebaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        JSONArray darr = new JSONArray();
                        for (DataSnapshot postSnapshot : dataSnapshot.child("vehicles").getChildren()) {
                            try{
                            int vid = Integer.parseInt(postSnapshot.getName());
                            String model = postSnapshot.child("model").getValue(String.class);
                            double lat = postSnapshot.child("lat").getValue(Double.class);
                            double lng = postSnapshot.child("lng").getValue(Double.class);
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
                                
                                if(dist<= 10000){
                                    for (DataSnapshot postSnapshot2 : dataSnapshot.child("driver").getChildren()) {
                                        int dvid = postSnapshot2.child("vid").getValue(int.class);
                                        if(dvid==vid){
                                            int did = Integer.parseInt(postSnapshot2.getName());
                                            int avg = postSnapshot2.child("avg").getValue(int.class);
                                            String avgtxt = postSnapshot2.child("avgtxt").getValue(String.class);
                                            String fullname = postSnapshot2.child("fullname").getValue(String.class);

                                            JSONObject d = new JSONObject();
                                            d.put("did", did);
                                            d.put("avg", avg);
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

                                d.put("passenger_id", pid);
                                d.put("fullname", fullname);
                                d.put("gender", gender);
                                d.put("age", age);
                                d.put("phone", phone);
                                d.put("relatedphone", relatedphone);
                                d.put("language", language);
                                d.put("email", email);

                                resobj.put("passenger", d);
                                
                                
                                resobj.put("success", "1");
                                resobj.put("msg", "logged in");
                                response = Response.status(200).entity(resobj).build();
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
    public Response passengersignup(String data) throws Exception{//note : img is byte array string
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
            CountDownLatch latch = new CountDownLatch(1);
           f=0;
//                int insertedid = excDBgetID("INSERT INTO passenger (fullname, useremail, phone, password,relatedphone,gender, age, language)"+
//                      " VALUES ( '"+name+"', '"+email+"', '"+phone+"', '"+password+"', '"+relatedphone+"', '"+gender+"', '"+age+"', '"+language+"')", "passenger");
//                
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

                            resobj.put("success", "1");
                            resobj.put("msg", "Added Successfully");

                            resobj.put("insertedid", insertedid);
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
//            ResultSet rs = getDBResultSet(query);
//            resobj.put("success", "1");
//            resobj.put("msg", "done");
            
            tripsarr = new JSONArray();
            
           
            myFirebaseRef = new Firebase("https://sharksmapandroid-158200.firebaseio.com/");
            
                myFirebaseRef.child("trips").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                int pid = postSnapshot.child("pid").getValue(Integer.class);
                                JSONArray paths = new JSONArray();
                                if(pid == id){

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
   
    
    @POST
    @Path("/editpassenger")
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
            
//            JSONObject obj = new JSONObject();
//            try {
//                excDB("update passenger set fullname='"+name+"',useremail='"+email+"',phone="+phone+",password='"+password+"',relatedphone="+relatedphone+" where passenger_id='"+id+"'");
//                
//                obj.put("success", "1");
//                obj.put("msg","'"+data+"' update successful");
//                conn.close();
//            } catch (Exception ex) {
//                obj.put("success", "0");
//                obj.put("msg", ex.getMessage());
//                Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
//            }
        
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
//        JSONObject obj = new JSONObject();
//        try {
//         
//            //excDB("INSERT INTO trip (comment, trip_id, ratting)"+" VALUES ( '"+comment+"',  "+tripid+", "+rate+")");
//            excDB("UPDATE trip SET comment = '"+comment+"', ratting = '"+rate+"' WHERE trip_id = "+tripid+" ");
//
//            obj.put("success", "1");
//            obj.put("msg", " Success");
//            
//            conn.close();
//        } catch (Exception ex) {
//            obj.put("success", "0");
//            obj.put("msg", ex.getMessage());
//            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
//        }
            myFirebaseRef.child("trips").child(String.valueOf(tripid)).child("comment").setValue(comment);
            myFirebaseRef.child("trips").child(String.valueOf(tripid)).child("ratting").setValue(rate);

            
            resobj.put("success", "1");
            resobj.put("msg", "Success");
            
        
        return Response.status(200).entity(resobj).build();
    }
    
    
    Response response;
    //JSONObject obj;

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
                        
                        String selecteddrivertoken = "";
                        int pickupSelectedDriverID = 0;
                        for (DataSnapshot postSnapshot : dataSnapshot.child("driver").getChildren()) {
                            int did = Integer.parseInt(postSnapshot.getName());
                            int vid = postSnapshot.child("vid").getValue(Integer.class);
                            if(vid==nearestvehicleid){
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
                            if(vid==nearestvehicleid){
                                JSONObject v = new JSONObject();
                                String model = postSnapshot.child("model").getValue(String.class);
                                String color = postSnapshot.child("color").getValue(String.class);
                                String plate_number = postSnapshot.child("plate_number").getValue(String.class);

                                v.put("vehicle_id", nearestvehicleid);
                                v.put("model", model);
                                v.put("color", color);
                                v.put("plate_number", plate_number);
                                resobj.put("vehicle", v);
                            }
                            
                        }
                        
                        long tripscount = dataSnapshot.child("trips").getChildrenCount();
                        long tripid = tripscount+1;
//                        ////////////////////////////////////////////////////////////////////////////////
//                        int pickupSelectedDriverID = 0;
//                        ResultSet rs = getDBResultSet("SELECT * FROM driver WHERE vehicle_id = "+nearestvehicleid);
//                        while(rs.next())
//                        {
//                            JSONObject d = new JSONObject();
//                             String fullname = rs.getString(2);
//                             pickupSelectedDriverID = rs.getInt(1);
//
//                             d.put("driver_id", pickupSelectedDriverID);
//                             d.put("fullname", fullname);
//                             obj.put("driver", d);
//                         }
//                        ResultSet rs2 = getDBResultSet("SELECT * FROM vehicle WHERE vehicle_id = "+nearestvehicleid);
//                        while(rs2.next())
//                        {
//                            JSONObject v = new JSONObject();
//                             String model = rs2.getString(2);
//                             String color = rs2.getString(3);
//                             String plate_number = rs2.getString(5);
//
//                             v.put("vehicle_id", nearestvehicleid);
//                             v.put("model", model);
//                             v.put("color", color);
//                             v.put("plate_number", plate_number);
//                             obj.put("vehicle", v);
//                         }
//                        int tripid = excDBgetID("INSERT INTO trip(passenger_id, dr0iver_id,start,end,price,comment,ratting) VALUES ("+passengerid+","+pickupSelectedDriverID+",'2017-00-00 00:00:00','2017-00-00 00:00:00','0.0','.',0.0)", "trip");
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
//                        conn.close();
//                        conn=null;
                        response = Response.status(200).entity(resobj).build();
                        latch.countDown();
                    }
                    }
                    @Override
                    public void onCancelled() {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                        
                    });
                    
//                        } catch (Exception ex) {
//                            obj.put("success", "0");
//                            obj.put("msg", ex.getMessage());
//                            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
//                            response=Response.status(200).entity(obj).build();
////                            latch.countDown();
//                        }
//                        

              

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
            
//            conn.close();
        } catch (Exception ex) {
            resobj.put("success", "0");
            resobj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(resobj).build();
    }
  
  
}
