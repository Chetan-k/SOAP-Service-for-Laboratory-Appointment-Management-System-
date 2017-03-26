/*
created by CRK
*/

package service;
import business.*;
import java.util.*;
import javax.jws.*;

@WebService(serviceName= "LAMSService")
public class LAMSService{
   
   BusinessLayer BL = new BusinessLayer();
   
   @WebMethod(operationName="initialize")
   public String initialize(){
      String output;
      output = BL.initialize();
      return output;   
   }
       
   @WebMethod(operationName="getAllAppointments")
   public String getAllAppointments(){
      String output;
      output = BL.getAllAppointments();
      return output;   
   }

   @WebMethod(operationName="getAppointment")
   public String getAppointment(String id){
      String output;
      output = BL.getAppointment(id);
      return output;   
   }

   @WebMethod(operationName="addAppointment")
   public String addAppointment(String xml){
      String output;
      output = BL.addAppointment(xml);
      return output;   
   }
      
   @WebMethod(operationName="addPhlebotomist")
   public String addPhlebotomist(String name){
      String output;
      output = BL.addPhlebotomist(name);
      return output;   
   }
   
   @WebMethod(operationName="getAllPhlebotomists")
   public String getAllPhlebotomists(){
      String output;
      output = BL.getAllPhlebotomists();
      return output;   
   }
   
   @WebMethod(operationName="addPhysician")
   public String addPhysician(String name){
      String output;
      output = BL.addPhysician(name);
      return output;   
   }
   
   @WebMethod(operationName="getAllPhysicians")
   public String getAllPhysicians(){
      String output;
      output = BL.getAllPhysicians();
      return output;   
   }

   @WebMethod(operationName="addPSC")
   public String addPSC(String name){
      String output;
      output = BL.addPSC(name);
      return output;   
   }
   
   @WebMethod(operationName="getAllPSCs")
   public String getAllPSCs(){
      String output;
      output = BL.getAllPSCs();
      return output;   
   }
   
//    public static void main(String[] args){
//       
//       LAMSService LS = new LAMSService();
//       String abs = LS.getAllPSCs();
//       System.out.println(abs);
//    
//    }
     
}