/*
created by CRK
*/

package business;

import java.util.*;
import components.data.*;
import java.text.*;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;
import org.xml.sax.InputSource;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


public class BusinessLayer{

   //for initilizing the database
   DBSingleton dbSingleton;
   
   //global object holders
   Patient patientobject = null;
   Phlebotomist phlebotomistobject = null;
   PSC pscobject = null;

   public String initialize(){
   
      //DBSingleton dbSingleton = new DBSingleton();
      dbSingleton = DBSingleton.getInstance();
      dbSingleton.db.initialLoad("LAMS");
      return "Database Initialized"; 
   }
   
   //getAllAppointments
   public String getAllAppointments(){
   
      dbSingleton = DBSingleton.getInstance();
      List<Object> objs = dbSingleton.db.getData("Appointment","");
      createXML xml = new createXML();
      String allAppString = xml.getAllAppointmentsXML(objs);
      return allAppString;
   }
   
   //getAppointment
   public  String getAppointment(String appid){
    
    dbSingleton = DBSingleton.getInstance();
    List<Object> objs  = dbSingleton.db.getData("Appointment", "id='"+appid+"'");
    createXML xml = new createXML();
    String appString ="";
    if (objs.isEmpty()) {
      appString = xml.getAppointmentXML();
      //System.out.println(appString);
      //return appString;
    }else{
      appString = xml.getAllAppointmentsXML(objs);
    }
    return appString;
   }
   
   //check the user time of an appointment with service timings
   public boolean checkServTime(String time){
   
   boolean flag = true;
   try {
   //PSC opening timings
    String opentime = "07:59:59";
    Date t1 = new SimpleDateFormat("HH:mm:ss").parse(opentime);
    Calendar c1 = Calendar.getInstance();
    c1.setTime(t1);
    
    //PSC closing timings
    String closetime = "16:45:00";
    Date t2 = new SimpleDateFormat("HH:mm:ss").parse(closetime);
    Calendar c2 = Calendar.getInstance();
    c2.setTime(t2);
    
    //check the user mentioned time with PSC timings
    String appttime = time;
    Date t3 = new SimpleDateFormat("HH:mm:ss").parse(appttime);
    Calendar c3 = Calendar.getInstance();
    c3.setTime(t3);
    
    Date t4 = c3.getTime();
    if (t4.after(c1.getTime()) && t4.before(c2.getTime())) {
        flag = true;
    }
    else{
    flag = false;
    }
   } catch (ParseException e) {
    e.printStackTrace();
    flag = false;
   }
   return flag;
   }//check appt time
   
   
   //addappointment need to pass xml string
   public String addAppointment(String xml){
   
    int fault = 0;//for final check of conditions
    
    //data holders for input from xml string
    String datexml = "", timexml = "" ,ptIdxml = "",phyIdxml = "",pscIdxml = "",phlebIdxml = "", feedback = "";
    createXML finaloutput = new createXML();
    //Arraylist for holding all the faults list
    ArrayList<String> faultsList = new ArrayList<String>();
    
    DBSingleton dbSingleton = DBSingleton.getInstance();
    //get the entire data
    List<Object> data = dbSingleton.db.getData("Appointment", "");
    //get the lastrecord to grab lastid from db
    Object lastRecord = data.get(data.size() - 1);
    String lastid = (((Appointment)lastRecord).getId()).toString();
    int lastIdInt = Integer.parseInt(lastid);
    lastIdInt = lastIdInt+1;
    String newid = Integer.toString(lastIdInt);
     
    //setting the new appointment     
    try{
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      
      Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
      //optional but recommended
      doc.getDocumentElement().normalize();
      
      Element rootElement = doc.getDocumentElement();      
      
      datexml =  doc.getElementsByTagName("date").item(0).getTextContent();
      
      timexml =  doc.getElementsByTagName("time").item(0).getTextContent();
      
      ptIdxml =  doc.getElementsByTagName("patientId").item(0).getTextContent();
      
      phyIdxml =  doc.getElementsByTagName("physicianId").item(0).getTextContent();
      
      pscIdxml =  doc.getElementsByTagName("pscId").item(0).getTextContent();
      
      //System.out.println("phlebId: "+doc.getElementsByTagName("phlebotomistId").item(0).getTextContent());
      phlebIdxml =  doc.getElementsByTagName("phlebotomistId").item(0).getTextContent();
                  
      //check for the service timings
      boolean serviceTimings = checkServTime(timexml);
      if(serviceTimings == false){
         faultsList.add("Please check your appointment time, which should lie in between 8am to 5pm");
         fault = 1;
      }
            
      }catch(Exception e){
         e.printStackTrace();
         faultsList.add("Please check your appointment time, which should lie in between 8am to 5pm");
         fault = 1;
      }//catch 1-service timings
      
      //check for the date and time format entered
      boolean dateformat = true, timeformat = true;
      try{
         SimpleDateFormat formater1 = new SimpleDateFormat("yyyy-MM-dd");
         SimpleDateFormat formater2 = new SimpleDateFormat("kk:mm:ss");
         
         Date date = formater1.parse(datexml);
         Date time = formater2.parse(timexml);
         
         boolean dateValid = true, timeValid = true;
         dateValid = datexml.equals(formater1.format(date));
         timeValid = timexml.equals(formater2.format(time));
         
         if(dateValid == false){
            faultsList.add("Please check your date, which should be in yyyy-MM-dd format");
            fault = 1;
         }
         
         if(timeValid == false){
            faultsList.add("Please check your time, which should be in kk:mm:ss format");
            fault = 1;
         }
         
      }catch(Exception e){
         e.printStackTrace();
         faultsList.add("Please check the date and time format entered");
         fault = 1;
      }//catch date-time validity
      
      //check for time and phlebotomist availability on user mentioned date at same place
      String timec1="", timec2="";
      try{
         SimpleDateFormat tc1 = new SimpleDateFormat("HH:mm");
         Date t1 = tc1.parse(timexml);
         Calendar cal1 = Calendar.getInstance();
         cal1.setTime(t1);
         //as each appointment takes 15 minutes
         cal1.add(Calendar.MINUTE, -15);
         timec1 = tc1.format(cal1.getTime());//to check before time
         
         SimpleDateFormat tc2 = new SimpleDateFormat("HH:mm");
         Date t2 = tc2.parse(timexml);
         Calendar cal2 = Calendar.getInstance();
         cal2.setTime(t2);
         cal2.add(Calendar.MINUTE, 15);
         timec2 = tc2.format(cal2.getTime());//to check after time
         
         //get the specified object with user entered details
         List<Object> specificRecord = dbSingleton.db.getData("Appointment","pscid='"+pscIdxml+"'"+"AND phlebid='"+phlebIdxml+"'"+"AND apptdate='"+datexml+"'");
         String specTime="";
         int flag=0;
         boolean timeIntr = false, checker= false;
         flag = specificRecord.size();
         if(flag > 0){
            for (Object datarec : specificRecord){
               Date ptime = null, ftime = null, presentTime = null;
               specTime = (((Appointment)datarec).getAppttime()).toString();
               SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
               ptime = sdf.parse(timec1);
               presentTime = sdf.parse(specTime);
               ftime = sdf.parse(timec2);
               checker = (ftime.compareTo(ptime) < 0);
               if(checker == true){
                  timeIntr = (presentTime.after(ptime) || presentTime.before(ftime));
               }else{
                  timeIntr = (presentTime.after(ptime) && presentTime.before(ftime));
               }
               if(timeIntr == true){
                  faultsList.add("Requested time interval is busy");
                  fault = 1;
               }
            }//for
         }//if count
      }catch(Exception e){
      faultsList.add("User requested time slot is not available with the requested phlebotomist");
      fault = 1;
      }
      
      //check for user mentioned patientid, physicianid, pscid, phlebotomistid present in the database
      try{
         List<Object> lookpatient = dbSingleton.db.getData("Patient", "id='"+ptIdxml+"'");
         //System.out.println("123------");
         
         //System.out.println(lookpatient);
         if(lookpatient.isEmpty()){
            faultsList.add("Requested PatientID id not found in the database");
            fault = 1;
         }else{
            patientobject = (Patient)lookpatient.get(0);
         }
         //for physician
         List<Object> lookphysician = dbSingleton.db.getData("Physician", "id='"+phyIdxml+"'");  
         if (lookphysician.isEmpty()) {
           faultsList.add("Requested physicianID id not found in the database");
           fault = 1;
         }
         //for psc
         List<Object> lookpsc = dbSingleton.db.getData("PSC", "id='"+pscIdxml+"'");  
         if (lookpsc.isEmpty()) {
            faultsList.add("Requested PSCID id not found in the database");
            fault = 1;
         }else{
            pscobject = (PSC)lookpsc.get(0);
         } 
         //for phlebotomist
         List<Object> lookphle = dbSingleton.db.getData("Phlebotomist", "id='"+phlebIdxml+"'");
         if (lookphle.isEmpty()) {
            faultsList.add("Requested phlebotomistID id not found in the database");
            fault = 1;
         }else{
            phlebotomistobject = (Phlebotomist)lookphle.get(0);
         }
      }catch(Exception e){
      faultsList.add("cannot recognize user mentioned patientid, physicianid, pscid, phlebotomistid in the database");
      fault = 1;
      }
      
      //if all the conditions are satisfied then, add the new appointment to the database
      if(fault == 0 && faultsList.size() == 0){
      Appointment newAppt = new Appointment(newid,java.sql.Date.valueOf(datexml),java.sql.Time.valueOf(timexml));
      List<AppointmentLabTest> Labtests = new ArrayList<AppointmentLabTest>();   
      try{
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbFactory.newDocumentBuilder();
        Document doc = db.parse(new InputSource(new StringReader(xml)));
        NodeList nodeList = doc.getElementsByTagName("test");
        
        String testid="",dxcode="";
        for(int i=0;i<nodeList.getLength(); i++) {
          testid=((Element)nodeList.item(i)).getAttribute("id");
          dxcode=((Element)nodeList.item(i)).getAttribute("dxcode");
          AppointmentLabTest test = new AppointmentLabTest(newid,testid,dxcode);
          
          String x = "code='"+dxcode+"'";
          test.setDiagnosis((Diagnosis)dbSingleton.db.getData("Diagnosis", x).get(0));
          String y = "id='"+testid+"'";
          test.setLabTest((LabTest)dbSingleton.db.getData("LabTest",y).get(0));
          Labtests.add(test);
          
        }
        newAppt.setAppointmentLabTestCollection(Labtests);
        newAppt.setPatientid(patientobject);
        newAppt.setPhlebid(phlebotomistobject);
        newAppt.setPscid(pscobject);
        //put everything into database
        boolean dataintodb = dbSingleton.db.addData(newAppt);
     
      }catch(Exception e)
      {
        faultsList.add("Cannot set new appointment according to user mentioned details");
        feedback = finaloutput.errorArrayXML(faultsList);
      }
      //if everyting works well, get the newly add appointment
      feedback = getAppointment(newid);
      
    }//if
    else{
      feedback = finaloutput.errorArrayXML(faultsList);
    }
   return feedback;
   } 
   
   public String addPhlebotomist(String name){
      Phlebotomist newphlebotomist = null;
      createXML Phlexml = new createXML();
      DBSingleton dbSingleton = DBSingleton.getInstance();
      //get all phlebotomists to grab the last ID
      List<Object> allPhlebotomists = dbSingleton.db.getData("Phlebotomist", "");
      Object lastPhlebotomist = allPhlebotomists.get(allPhlebotomists.size() - 1);
      String lastphleid = (((Phlebotomist)lastPhlebotomist).getId()).toString();
      int lastphleIdInt = Integer.parseInt(lastphleid);
      lastphleIdInt = lastphleIdInt+1;
      String newphleid = Integer.toString(lastphleIdInt);
      newphlebotomist = new Phlebotomist(newphleid,name);
      dbSingleton.db.addData(newphlebotomist);
      //to give user feedback, get the newly inserted phlebotomist in xml format
      List<Object> objs= dbSingleton.db.getData("Phlebotomist", "id='"+newphleid+"'");
      String xmlString = Phlexml.addPhlebotomistXML(objs);
      return xmlString;
    
   }
   
   public String getAllPhlebotomists(){
    createXML getphlebs = new createXML();
    DBSingleton dbSingleton = DBSingleton.getInstance();
    List<Object> allrecord = dbSingleton.db.getData("Phlebotomist", "");
    String xmlString = getphlebs.addPhlebotomistXML(allrecord);
    return xmlString;
   }
   
   public String addPhysician(String name){
     Physician newphysician = null;
     createXML Phyxml = new createXML();
     DBSingleton dbSingleton = DBSingleton.getInstance();
     //get all physicians to grab the last ID
     List<Object> allPhysicians = dbSingleton.db.getData("Physician", "");
     Object lastPhysician = allPhysicians.get(allPhysicians.size() - 1);
     String lastphyid = (((Physician)lastPhysician).getId()).toString();
     int lastphyIdInt = Integer.parseInt(lastphyid);
     lastphyIdInt = lastphyIdInt+1;
     String newphyid = Integer.toString(lastphyIdInt);
     newphysician = new Physician(newphyid,name);
     dbSingleton.db.addData(newphysician);
     //to give user feedback, get the newly inserted physician in xml format
     List<Object> objs= dbSingleton.db.getData("Physician", "id='"+newphyid+"'");
     String xmlString = Phyxml.addPhysicianXML(objs);
     return xmlString;
   } 
   
   public String getAllPhysicians(){
    createXML getphys = new createXML();
    DBSingleton dbSingleton = DBSingleton.getInstance();
    List<Object> allrecord = dbSingleton.db.getData("Physician", "");
    String xmlString = getphys.addPhysicianXML(allrecord);
    return xmlString;
   }

   public String addPSC(String name){
      PSC newpsc = null;
      createXML Pscxml = new createXML();
      DBSingleton dbSingleton = DBSingleton.getInstance();
      //get all psc to grab the last ID
      List<Object> allPSCs = dbSingleton.db.getData("PSC", "");
      Object lastPSC = allPSCs.get(allPSCs.size() - 1);
      String lastpscid = (((PSC)lastPSC).getId()).toString();
      int lastpscIdInt = Integer.parseInt(lastpscid);
      lastpscIdInt = lastpscIdInt+1;
      String newpscid = Integer.toString(lastpscIdInt);
      newpsc = new PSC(newpscid,name);
      dbSingleton.db.addData(newpsc);
      //to give user feedback, get the newly inserted psc in xml format
      List<Object> objs= dbSingleton.db.getData("PSC", "id='"+newpscid+"'");
      String xmlString = Pscxml.addPSCXML(objs);
      return xmlString;
    
   }
   
   public String getAllPSCs(){
    createXML getpscs = new createXML();
    DBSingleton dbSingleton = DBSingleton.getInstance();
    List<Object> allrecord = dbSingleton.db.getData("PSC", "");
    String xmlString = getpscs.addPSCXML(allrecord);
    return xmlString;
   }

   
   public static void main(String[] args){
   
      BusinessLayer BL = new BusinessLayer();
      String abc = BL.initialize();
      System.out.println("---------");

      System.out.println(abc);
      //System.out.println("---------");
      
      //String app = BL.getAllAppointments();
      //System.out.println(app);
      System.out.println("---------");
       
//       String qwe = BL.getAppointment("790");

      String xml = "<appointment>"+
                     "<date>2016-12-30</date>"+
                     "<time>10:00:00</time>"+
                     "<patientId>220</patientId>"+
                     "<physicianId>20</physicianId>"+
                     "<pscId>520</pscId>"+
                     "<phlebotomistId>110</phlebotomistId>"+
                     "<labTests>"+
                     "<test id="+"'86900'"+" dxcode="+"'292.9'"+" />"+
                     "<test id="+"'86609'"+" dxcode="+"'307.3'"+" />"+
                     "</labTests>"+"</appointment>";   
   } 



}