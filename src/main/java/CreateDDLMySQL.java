import java.awt.*;
import java.awt.event.*;
import javax.swing.*;   
import javax.swing.event.*;
import java.io.*;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateDDLMySQL extends EdgeConvertCreateDDL {

   protected String databaseName;
   //this array is for determining how MySQL refers to datatypes
   protected String[] strDataType = {"VARCHAR", "BOOL", "INT", "DOUBLE"};
   protected StringBuffer sb;
   public  static Logger logger=LogManager.getLogger(CreateDDLMySQL.class.getName());

   public CreateDDLMySQL(EdgeTable[] inputTables, EdgeField[] inputFields) {
      super(inputTables, inputFields);
      logger.debug(String.format("EdgeConvertCreateDDL super constructor ran with params: %s, %s",inputTables,inputFields));
      sb = new StringBuffer();
      logger.info("CreateDDLMMySQL initialization complete");
   } //CreateDDLMySQL(EdgeTable[], EdgeField[])
   
   public CreateDDLMySQL() { //default constructor with empty arg list for to allow output dir to be set before there are table and field objects
      logger.info("CreateDDLMySQL initialization complete with empty constructor");
   }
   
   public void createDDL() {
      logger.info("starting DDL creation");
      EdgeConvertGUI.setReadSuccess(true);
      this.databaseName = generateDatabaseName();
      logger.trace(String.format("database name generated %s",databaseName));
      sb.append("CREATE DATABASE " + databaseName + ";\r\n");
      sb.append("USE " + databaseName + ";\r\n");
      logger.trace(String.format("Database level DDL generated %s",sb.toString()));
      logger.trace("starting table DDL Generation");
      for (int boundCount = 0; boundCount <= maxBound; boundCount++) { //process tables in order from least dependent (least number of bound tables) to most dependent
         for (int tableCount = 0; tableCount < numBoundTables.length; tableCount++) { //step through list of tables
            logger.debug(String.format("starting table generation %s",tables[tableCount].getName()));
            if (numBoundTables[tableCount] == boundCount) { //
               sb.append("CREATE TABLE " + tables[tableCount].getName() + " (\r\n");
               int[] nativeFields = tables[tableCount].getNativeFieldsArray();
               int[] relatedFields = tables[tableCount].getRelatedFieldsArray();
               boolean[] primaryKey = new boolean[nativeFields.length];
               int numPrimaryKey = 0;
               int numForeignKey = 0;
               logger.trace(String.format("starting field generation for %s table",tables[tableCount].getName()));
               for (int nativeFieldCount = 0; nativeFieldCount < nativeFields.length; nativeFieldCount++) { //print out the fields
                  EdgeField currentField = getField(nativeFields[nativeFieldCount]);
                  logger.trace(String.format("Generating field %s",currentField));
                  sb.append("\t" + currentField.getName() + " " + strDataType[currentField.getDataType()]);
                  if (currentField.getDataType() == 0) { //varchar
                     sb.append("(" + currentField.getVarcharValue() + ")"); //append varchar length in () if data type is varchar
                  }
                  if (currentField.getDisallowNull()) {
                     sb.append(" NOT NULL");
                  }
                  if (!currentField.getDefaultValue().equals("")) {
                     if (currentField.getDataType() == 1) { //boolean data type
                        sb.append(" DEFAULT " + convertStrBooleanToInt(currentField.getDefaultValue()));
                     } else { //any other data type
                        sb.append(" DEFAULT " + currentField.getDefaultValue());
                     }
                  }
                  if (currentField.getIsPrimaryKey()) {
                     primaryKey[nativeFieldCount] = true;
                     numPrimaryKey++;
                  } else {
                     primaryKey[nativeFieldCount] = false;
                  }
                  if (currentField.getFieldBound() != 0) {
                     numForeignKey++;
                  }
                  if(nativeFieldCount < nativeFields.length-1 || numPrimaryKey > 0 || numForeignKey > 0) {
                     sb.append(",");
                  }
                  sb.append("\r\n"); //end of field
               }
              logger.trace(String.format("field generation for %s table complete",tables[tableCount].getName()));

               if (numPrimaryKey > 0) { //table has primary key(s)
                  logger.trace(String.format("generating primary key for table %s",tables[tableCount].getName()));
                  sb.append("CONSTRAINT " + tables[tableCount].getName() + "_PK PRIMARY KEY (");
                  for (int i = 0; i < primaryKey.length; i++) {
                     if (primaryKey[i]) {
                        sb.append(getField(nativeFields[i]).getName());
                        numPrimaryKey--;
                        if (numPrimaryKey > 0) {
                           sb.append(", ");
                        }
                     }
                  }
                  sb.append(")");
                  if (numForeignKey > 0) {
                     sb.append(",");
                  }
                  sb.append("\r\n");
               }
               if (numForeignKey > 0) { //table has foreign keys
                  logger.trace(String.format("generating foreign keys for table %s",tables[tableCount].getName()));
                  int currentFK = 1;
                  for (int i = 0; i < relatedFields.length; i++) {
                     logger.trace(String.format("checking foreign key constraints for column %s",relatedFields[i]));
                     if (relatedFields[i] != 0) {
                        sb.append("CONSTRAINT " + tables[tableCount].getName() + "_FK" + currentFK +
                                  " FOREIGN KEY(" + getField(nativeFields[i]).getName() + ") REFERENCES " +
                                  getTable(getField(nativeFields[i]).getTableBound()).getName() + "(" + getField(relatedFields[i]).getName() + ")");
                        if (currentFK < numForeignKey) {
                           sb.append(",\r\n");
                        }
                        currentFK++;
                     }
                  }
                  sb.append("\r\n");
               }
               sb.append(");\r\n\r\n"); //end of table
            }
            logger.debug(String.format("table generation complete %s",tables[tableCount].getName()));
         }
      }
      logger.info("DDL generation complete");
   }

   protected int convertStrBooleanToInt(String input) { //MySQL uses '1' and '0' for boolean types
      if (input.equals("true")) {
         return 1;
      } else {
         return 0;
      }
   }
   
   public String generateDatabaseName() { //prompts user for database name
      String dbNameDefault = "MySQLDB";
      String workingDbName = "";
      //String databaseName = "";
      logger.debug("prompting for DB name");

      do {
         workingDbName = (String)JOptionPane.showInputDialog(
                       null,
                       "Enter the database name:",
                       "Database Name",
                       JOptionPane.PLAIN_MESSAGE,
                       null,
                       null,
                       dbNameDefault);
         logger.debug("DB NAME");
         logger.debug(workingDbName);
         if (workingDbName == null) {
            EdgeConvertGUI.setReadSuccess(false);
            JOptionPane.showMessageDialog(null, "You must select a name for your database.");
            workingDbName = "";
         }
         if (workingDbName.equals("")) {
            JOptionPane.showMessageDialog(null, "You must select a name for your database.");
         }
      } while (workingDbName.equals(""));
      logger.debug(String.format("db name extracted as %s",workingDbName));
      return workingDbName;
   }
   
   public String getDatabaseName() {
      return databaseName;
   }
   
   public String getProductName() {
      return "MySQL";
   }

   public String getSQLString() {
      createDDL();
      return sb.toString();
   }
   
}//EdgeConvertCreateDDL
