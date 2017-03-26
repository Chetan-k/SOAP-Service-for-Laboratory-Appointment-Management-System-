/*
created by CRK
*/

package business;

import components.data.*;

public class DBSingleton {

   public IComponentsData db; 

   private static DBSingleton instance = null;
   private DBSingleton() {
      // Exists only to defeat instantiation.
      db = new DB();
   }
   public static DBSingleton getInstance() {
      if(instance == null) {
         instance = new DBSingleton();
      }
      return instance;
   }
}