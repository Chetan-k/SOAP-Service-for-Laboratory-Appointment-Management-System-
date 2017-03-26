/*
created by CRK
*/

package business;

import java.util.*;
import components.data.*;
//xml packages
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;
import org.xml.sax.InputSource;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.util.*;
import java.lang.reflect.*;
import java.lang.reflect.Field;


public class createXML{
   
   public String getAllAppointmentsXML(List<Object> applist){
    
    //for returing xml string  
    String rxml= "";
   
    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      String xml = "<AppointmentList> </AppointmentList>";
      Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));

      //grab root element i.e AppointmentList
      Element element = doc.getDocumentElement(); 
      for (Object obj : applist) {
        
        //appointment
        Element listNode = doc.createElement("appointment");
        listNode.setAttribute("date",(((Appointment)obj).getApptdate()).toString());
        listNode.setAttribute("id",(((Appointment)obj).getId()).toString());
        listNode.setAttribute("time",(((Appointment)obj).getAppttime()).toString()); 
        
        //patient
        Element patientNode = doc.createElement("patient");
        patientNode.setAttribute("id",(((Appointment)obj).getPatientid()).getId()); 
        listNode.appendChild(patientNode);
        
        //p-name
        Element patientNameNode = doc.createElement("name");
        patientNode.appendChild(patientNameNode);
        Text patientNameText = doc.createTextNode((((Appointment)obj).getPatientid()).getName());
        patientNameNode.appendChild(patientNameText);
        
        //p-address
        Element patientAddNode = doc.createElement("address");
        patientNode.appendChild(patientAddNode);
        Text patientAddText = doc.createTextNode((((Appointment)obj).getPatientid()).getAddress());
        patientAddNode.appendChild(patientAddText);
        
        //p-insurance
        Element patientInsuNode = doc.createElement("insurance");
        patientNode.appendChild(patientInsuNode);
        //as insurance is only single charcter...convert its type to string
        Text patientInsuText = doc.createTextNode(Character.toString((((Appointment)obj).getPatientid()).getInsurance()));            
        patientInsuNode.appendChild(patientInsuText);
        
        //p-dob
        Element patientDOBNode = doc.createElement("dob");
        patientNode.appendChild(patientDOBNode);
        Text patientDOBText = doc.createTextNode(((((Appointment)obj).getPatientid()).getDateofbirth()).toString());            
        patientDOBNode.appendChild(patientDOBText);

        //stick patient to the root element so that next elements can be appended to appointment node
        element.appendChild(listNode);
        
        //phlebotomist
        Element phlebotomistNode = doc.createElement("phlebotomist");
        phlebotomistNode.setAttribute("id",(((Appointment)obj).getPhlebid()).getId());
        listNode.appendChild(phlebotomistNode);
        
        //ph-name
        Element phlNameNode = doc.createElement("name");
        phlebotomistNode.appendChild(phlNameNode);
        Text phlNameText = doc.createTextNode(((Appointment)obj).getPhlebid().getName());
        phlNameNode.appendChild(phlNameText);  
        
        //psc
        Element pscNode = doc.createElement("psc");
        pscNode.setAttribute("id",(((Appointment)obj).getPscid()).getId());
        listNode.appendChild(pscNode);
        
        //psc-name
        Element pscNameNode = doc.createElement("name");
        pscNode.appendChild(pscNameNode);
        Text pscNameText = doc.createTextNode((((Appointment)obj).getPscid()).getName());
        pscNameNode.appendChild(pscNameText);
        
        //allLabTests
        Element allLabTestsNode = doc.createElement("allLabTests");
        
        //chance where single patient may have more than one diagonsis and labtest for single appointment..LOOPIT
        List<AppointmentLabTest> alt = ((Appointment)obj).getAppointmentLabTestCollection();
        String labTestId="", AppId="",dxCode="";
        for(int i=0;i<alt.size();i++){
          AppId=alt.get(i).getAppointmentLabTestPK().getApptid();
          dxCode=alt.get(i).getAppointmentLabTestPK().getDxcode();
          labTestId=alt.get(i).getAppointmentLabTestPK().getLabtestid();
          
          //appointmentLabTest
          Element altNode = doc.createElement("appointmentLabTest");
          altNode.setAttribute("appointmentId",AppId);
          altNode.setAttribute("dxcode",dxCode);
          altNode.setAttribute("labTestId",labTestId);
          
         //append altNode to allTestsNode
          allLabTestsNode.appendChild(altNode);
        }//allAPT loop
        listNode.appendChild(allLabTestsNode);
        
      }//obj loop
      
      //optional but recommend
      //doc.getDocumentElement().normalize();
      
      DOMSource source = new DOMSource(doc);
      
      //to a string
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
      transformer.setOutputProperty(OutputKeys.INDENT,"yes");

      
      StringWriter writer = new StringWriter();
      StreamResult result2 = new StreamResult(writer);
      transformer.transform(source,result2);
      //to return the generated xml copy it to rxml
      rxml = writer.toString();      
    }
    catch(Exception e){
      e.printStackTrace();
    }          
    return rxml;

   }//getAllAppointmentsXML
   
   public String getAppointmentXML(){
   
      String rxml = "<error> </error>";
      try{
       DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
       DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
       
       Document doc = dBuilder.parse(new InputSource(new StringReader(rxml)));
       //get the root element
       Element element = doc.getDocumentElement();
                
       Text error = doc.createTextNode("No user mentioned ID exists");
        
       element.appendChild(error);
       
       
       DOMSource source = new DOMSource(doc);
       //to a string
       TransformerFactory transformerFactory = TransformerFactory.newInstance();
       Transformer transformer = transformerFactory.newTransformer();
       transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
       transformer.setOutputProperty(OutputKeys.INDENT,"yes");
       StringWriter writer = new StringWriter();
       StreamResult result2 = new StreamResult(writer);
       transformer.transform(source,result2); 
       rxml = writer.toString();   
       
     }
     catch(Exception e){
       e.printStackTrace();
     }
     return rxml;  
   
   
   
   }
   
   public String errorArrayXML(List<String> data){
   
      String rxml = "<AppointmentList> </AppointmentList>";
      try{
         DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
         
         Document doc = dBuilder.parse(new InputSource(new StringReader(rxml)));
         Element element = doc.getDocumentElement();
         int id = 0;
         for (String data1 : data) {
           Element recordNode = doc.createElement("error");
           recordNode.setAttribute("id",Integer.toString(id)); 
           
           Text texterror = doc.createTextNode(data1);
           recordNode.appendChild(texterror);
           element.appendChild(recordNode);
           id++;
      }
            
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
      transformer.setOutputProperty(OutputKeys.INDENT,"yes");
      
      DOMSource source = new DOMSource(doc);

      StringWriter writer = new StringWriter();
      StreamResult result = new StreamResult(writer);
      transformer.transform(source,result);
       
      rxml = writer.toString();   
      //System.out.println(rxml);
      
    }
    catch(Exception e){
      e.printStackTrace();
    }
    return rxml; 
   }
   
   //adding physician xml
   public String addPhysicianXML(List<Object> data){
  
     String rxml = "<Physicians> </Physicians>";
     try{
       DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
       DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
       
       Document doc = dBuilder.parse(new InputSource(new StringReader(rxml)));
       //get the root element
       Element element = doc.getDocumentElement();
       
       for (Object value : data) {
         Element recordNode = doc.createElement("Physician");
         recordNode.setAttribute("id",(((Physician)value).getId()).toString());
         element.appendChild(recordNode);
         Element node = doc.createElement("name");
         recordNode.appendChild(node);
         Text phyname = doc.createTextNode(((Physician)value).getName());
         //recordNode.setAttribute("name",((Physician)value).getName()); 
         //element.appendChild(recordNode);
         node.appendChild(phyname);
       }
       
       DOMSource source = new DOMSource(doc);
       //to a string
       TransformerFactory transformerFactory = TransformerFactory.newInstance();
       Transformer transformer = transformerFactory.newTransformer();
       transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
       transformer.setOutputProperty(OutputKeys.INDENT,"yes");
       StringWriter writer = new StringWriter();
       StreamResult result2 = new StreamResult(writer);
       transformer.transform(source,result2); 
       rxml = writer.toString();   
       
     }
     catch(Exception e){
       e.printStackTrace();
     }
     return rxml;   
   }
     
  public String addPhlebotomistXML(List<Object> data){
  
      String r1xml = "<Phlebotomists> </Phlebotomists>";
       try{
         DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
         
         Document doc = dBuilder.parse(new InputSource(new StringReader(r1xml)));
         //get the root element
         Element element = doc.getDocumentElement();
         int count = 0;
         for (Object value : data) {
           count++;
           Element recordNode = doc.createElement("Phlebotomist");
           recordNode.setAttribute("id",(((Phlebotomist)value).getId()).toString());
           element.appendChild(recordNode);
           Element node = doc.createElement("name");
           recordNode.appendChild(node);
           Text phlename = doc.createTextNode(((Phlebotomist)value).getName());
           //recordNode.setAttribute("name",((Physician)value).getName()); 
           //element.appendChild(recordNode);
           node.appendChild(phlename);
         }
         
         DOMSource source = new DOMSource(doc);
         //to a string
         TransformerFactory transformerFactory = TransformerFactory.newInstance();
         Transformer transformer = transformerFactory.newTransformer();
         transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
         transformer.setOutputProperty(OutputKeys.INDENT,"yes");
         StringWriter writer = new StringWriter();
         StreamResult result2 = new StreamResult(writer);
         transformer.transform(source,result2); 
         r1xml = writer.toString();   
      
    }
    catch(Exception e){
      e.printStackTrace();
    }
    return r1xml;   
  
  }
  
  public String addPSCXML(List<Object> data){
  
      String r2xml = "<PSCs> </PSCs>";
       try{
         DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
         
         Document doc = dBuilder.parse(new InputSource(new StringReader(r2xml)));
         //get the root element
         Element element = doc.getDocumentElement();
         int count = 0;
         for (Object value : data) {
           count++;
           Element recordNode = doc.createElement("PSC");
           recordNode.setAttribute("id",(((PSC)value).getId()).toString());
           element.appendChild(recordNode);
           Element node = doc.createElement("name");
           recordNode.appendChild(node);
           Text phlename = doc.createTextNode(((PSC)value).getName());
           //recordNode.setAttribute("name",((Physician)value).getName()); 
           //element.appendChild(recordNode);
           node.appendChild(phlename);
         }
         
         DOMSource source = new DOMSource(doc);
         //to a string
         TransformerFactory transformerFactory = TransformerFactory.newInstance();
         Transformer transformer = transformerFactory.newTransformer();
         transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
         transformer.setOutputProperty(OutputKeys.INDENT,"yes");
         StringWriter writer = new StringWriter();
         StreamResult result2 = new StreamResult(writer);
         transformer.transform(source,result2); 
         r2xml = writer.toString();   
      
    }
    catch(Exception e){
      e.printStackTrace();
    }
    return r2xml;   
  
  }

}//class 