
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hubberspot.jersey;


import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.history.PNHistoryItemResult;
import com.pubnub.api.models.consumer.history.PNHistoryResult;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
    Connection conn;
    
    List<Integer> driversIDs;
    List<Double> driversLats,driversLngs, driverDistance;
    int pickupSelectedDriverID;

    @GET //test only
    @Path("/go")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getgo(){
        String output = "gooooooooooooooooooooo Hebat" ;
        return Response.status(200).entity(output).build();
    }
    
    ResultSet getDBResultSet(String query) throws Exception{
                    
        Class.forName("com.mysql.jdbc.Driver");            
        //conn = DriverManager.getConnection("jdbc:mysql://localhost/hebadb?" + "user=root&password=");
        //conn = DriverManager.getConnection("jdbc:mysql://sql11.freesqldatabase.com/sql11164022?" + "user=sql11164022&password=GLj4H4TT5N");
        conn = DriverManager.getConnection("jdbc:mysql://sql8.freesqldatabase.com/sql8166151?" + "user=sql8166151&password=CnJ3KUzlDR");

        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);
        return rs;            
    }
    void excDB(String query) throws Exception{
                    
        Class.forName("com.mysql.jdbc.Driver");            
        //conn = DriverManager.getConnection("jdbc:mysql://localhost/hebadb?" + "user=root&password=");
        //conn = DriverManager.getConnection("jdbc:mysql://sql11.freesqldatabase.com/sql11164022?" + "user=sql11164022&password=GLj4H4TT5N");
        conn = DriverManager.getConnection("jdbc:mysql://sql8.freesqldatabase.com/sql8166151?" + "user=sql8166151&password=CnJ3KUzlDR");

        Statement st = conn.createStatement();
        st.executeUpdate(query);
    }
    
    int excDBgetID(String query) throws Exception{
                    
        Class.forName("com.mysql.jdbc.Driver");            
        //conn = DriverManager.getConnection("jdbc:mysql://localhost/hebadb?" + "user=root&password=");
        //conn = DriverManager.getConnection("jdbc:mysql://sql11.freesqldatabase.com/sql11164022?" + "user=sql11164022&password=GLj4H4TT5N");
        conn = DriverManager.getConnection("jdbc:mysql://sql8.freesqldatabase.com/sql8166151?" + "user=sql8166151&password=CnJ3KUzlDR");

        Statement st = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
//        ResultSet rs = st.executeQuery(query);
//        ResultSet rs = st.executeQuery("SELECT LAST_INSERT_ID()");
//        int insertedid = 0;
//        if (rs.next()){
//            insertedid =rs.getInt(1);
//        }
        int insertedid = st.executeUpdate(query);

        return insertedid;
    }
    
    
    
    
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)    
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response LoginPassenger(String data) throws Exception{  
        JSONObject obj = new JSONObject();
           try {

            JSONObject objj = JSONObject.fromObject(data);   
            String email = objj.getString("email");            
            String password = objj.getString("password");

            ResultSet rs = getDBResultSet("SELECT * FROM passenger WHERE useremail = '"+email+"'");//knt user_id
            obj.put("success", "0");
            obj.put("msg", "Wrong email or Password");
            while(rs.next())
            {
                 String pass = rs.getString(2);
                 if(pass.equals(password))
                 {
                     //loginpassenger
                     obj.put("success", "1");
                     obj.put("msg", "Logged in successfully");
                     
                     JSONObject d = new JSONObject();
                     String passenger_id = rs.getString(1);
                     String fullname = rs.getString(3);
                     String gender = rs.getString(4);
                     int age = rs.getInt(5);
                     int phone = rs.getInt(6);
                     int relatedphone = rs.getInt(7);
                     String language = rs.getString("language");

                     d.put("passenger_id", passenger_id);
                     d.put("fullname", fullname);
                     d.put("gender", gender);
                     d.put("age", age);
                     d.put("phone", phone);
                     d.put("relatedphone", relatedphone);
                     d.put("language", language);
                     d.put("email", email);

                     obj.put("passenger", d);

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
    
    
    @POST
    @Path("/signup")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response passengersignup(String data) throws Exception{//note : img is byte array string
            //as post request
            JSONObject dataObj = JSONObject.fromObject(data);
            String name = dataObj.getString("name");
            String email = dataObj.getString("email");
            String password = dataObj.getString("password");
            int phone = dataObj.getInt("phone");
            int relatedphone = dataObj.getInt("relatedphone");
            String gender = dataObj.getString("gender");
            int age = dataObj.getInt("age");
            String language = dataObj.getString("language");


            JSONObject obj = new JSONObject();
            try {
                int insertedid = excDBgetID("INSERT INTO passenger (fullname, useremail, phone, password,relatedphone,gender, age, language)"+
                      " VALUES ( '"+name+"', '"+email+"', '"+phone+"', '"+password+"', '"+relatedphone+"', '"+gender+"', '"+age+"', '"+language+"')");
                
                //excDB("INSERT INTO passenger"+
                  //    " VALUES ( '"+password+"', '"+name+"', '"+gender+"', '"+age+"', '"+phone+"', '"+relatedphone+"', '"+language+"', '"+email+"')");
                
                
                obj.put("success", "1");
                obj.put("msg", "Added Successfully");
                
                obj.put("insertedid", insertedid);


                conn.close();
            } catch (Exception ex) {
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
                
                 String start = rs.getString(2);
                 String end = rs.getString(3);
                 String price = rs.getString(4);  
                 String comment = rs.getString(5);
                 String ratting = rs.getString(6);
                 String passenger_id = rs.getString(7);
                 String driver_id = rs.getString(8);
                 
                 
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
            JSONObject obj = new JSONObject();
            try {
                excDB("update passenger set fullname='"+name+"',useremail='"+email+"',phone="+phone+",password='"+password+"',relatedphone="+relatedphone+" where passenger_id='"+id+"'");
                
                obj.put("success", "1");
                obj.put("msg","'"+data+"' update successful");
                conn.close();
            } catch (Exception ex) {
                obj.put("success", "0");
                obj.put("msg", ex.getMessage());
                Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        return Response.status(200).entity(obj).build();
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
        JSONObject obj = new JSONObject();
        try {
         
            //excDB("INSERT INTO trip (comment, trip_id, ratting)"+" VALUES ( '"+comment+"',  "+tripid+", "+rate+")");
            excDB("UPDATE trip SET comment = '"+comment+"', ratting = '"+rate+"' WHERE trip_id = "+tripid+" ");

            obj.put("success", "1");
            obj.put("msg", " Success");
            
            conn.close();
        } catch (Exception ex) {
            obj.put("success", "0");
            obj.put("msg", ex.getMessage());
            Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(obj).build();
    }
    
    
    
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

            JSONObject obj = new JSONObject();
            driversIDs = new ArrayList<Integer>();
            driversLats = new ArrayList<Double>();
            driversLngs = new ArrayList<Double>();
            
            try {
                
                PNConfiguration pnConfiguration = new PNConfiguration();
                pnConfiguration.setSubscribeKey("sub-c-a92c9e70-e683-11e6-b3b8-0619f8945a4f");
                pnConfiguration.setPublishKey("pub-c-b04f5dff-3f09-4dc6-8b4e-58034b4b85bb");
                pnConfiguration.setSecure(false);

                PubNub pubnub = new PubNub(pnConfiguration);

//                pubnub.history()
//                .channel("locschannel") // where to fetch history from
//                .count(100) // how many items to fetch
//                .async(new PNCallback<PNHistoryResult>() {
//                    @Override
//                    public void onResponse(PNHistoryResult result, PNStatus status) {
//                        List<PNHistoryItemResult> msgsList = result.getMessages();
////                        String text  = msgsList.get(0).getEntry().get("text").textValue();
//                        for (int i = 0; i < msgsList.size(); i++) {
//                            int did  = msgsList.get(i).getEntry().get("did").intValue();
//                            double lat  = msgsList.get(i).getEntry().get("lat").doubleValue();
//                            double lng  = msgsList.get(i).getEntry().get("lng").doubleValue();
//                            int dstatus  = msgsList.get(i).getEntry().get("status").intValue(); // 0 free 1 in trip
//                            
//                            if(dstatus==0){
//                                int f = 0;
//                                for (int j = 0; j < driversIDs.size(); j++) {
//                                    if(driversIDs.get(j)==did){
//                                        f=1;
//                                        driversLats.set(j, lat);
//                                        driversLngs.set(j, lng);
//                                        driverDistance.set(j, distance(ilat, lat, ilng, lng));
//                                    }
//                                }
//                                if(f==0){
//                                    driversIDs.add(did);
//                                    driversLats.add(lat);
//                                    driversLngs.add(lng);
//                                    driverDistance.add(distance(ilat, lat, ilng, lng));
//                                }
//                            }
//                        }
//                        
//                        //kda m3aya list bl ehsarat kolha
//                        //find minimum distance driver b2a
//                        double mindistance = driverDistance.get(0);
//                        int minindex = 0;
//                        for (int i = 0; i < driverDistance.size(); i++) {
//                            if(mindistance>driverDistance.get(i)){
//                                mindistance=driverDistance.get(i);
//                                minindex=i;
//                            }
//                        }
//                        
//                        pickupSelectedDriverID = driversIDs.get(minindex);
//                        
//                        
//
//                    }
//                });

                pickupSelectedDriverID=1;//testtttt

                int tripid = excDBgetID("INSERT INTO trip(passenger_id, driver_id,start,end,price,comment,ratting) VALUES ("+passengerid+","+pickupSelectedDriverID+",'2017-00-00 00:00:00','2017-00-00 00:00:00','0.0','.',0.0)");

                ResultSet rs = getDBResultSet("SELECT * FROM driver WHERE driver_id = "+pickupSelectedDriverID);
                while(rs.next())
                {
                    JSONObject d = new JSONObject();
                     String fullname = rs.getString(2);
//                     double sharp_turns_freq = rs.getDouble(3);
//                     double lane_changing_freq = rs.getDouble(4);
//                     double harch_acc_freq = rs.getDouble(5);
//                     double wrong_u_turns_severity = rs.getDouble(7);
//                     int vehicle_id = rs.getInt(10);
                     
                     d.put("driver_id", pickupSelectedDriverID);
                     d.put("fullname", fullname);
//                     d.put("sharp_turns_freq", sharp_turns_freq);
//                     d.put("lane_changing_freq", lane_changing_freq);
//                     d.put("harch_acc_freq", harch_acc_freq);
//                     d.put("wrong_u_turns_severity", wrong_u_turns_severity);
//                     d.put("vehicle_id", vehicle_id);

                     obj.put("driver", d);

                 }
                obj.put("tripid", tripid);
                obj.put("success", "1");
                obj.put("msg","Done successful");
                conn.close();
            } catch (Exception ex) {
                obj.put("success", "0");
                obj.put("msg", ex.getMessage());
                Logger.getLogger(WebsiteServiceJersey.class.getName()).log(Level.SEVERE, null, ex);
            }
        return Response.status(200).entity(obj).build();
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
}
