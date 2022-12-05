import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class EdgeConvertCreateDDL {
   public static Logger logger=LogManager.getLogger(EdgeConvertCreateDDL.class.getName());
   static String[] products = {"MySQL"};
   protected EdgeTable[] tables; //master copy of EdgeTable objects
   protected EdgeField[] fields; //master copy of EdgeField objects
   protected int[] numBoundTables;
   protected int maxBound;
   protected StringBuffer sb;
   protected int selected;
   
   public EdgeConvertCreateDDL(EdgeTable[] tables, EdgeField[] fields) {
      this.tables = tables;
      this.fields = fields;
      initialize();
      logger.debug(String.format("EdgeConvertCreateDDL constructor ran with params: %s, %s",tables,fields));
   } //EdgeConvertCreateDDL(EdgeTable[], EdgeField[])
   
   public EdgeConvertCreateDDL() { //default constructor with empty arg list for to allow output dir to be set before there are table and field objects
      logger.info("EdgeConvertCreateDLL ran with empty constructor");

   } //EdgeConvertCreateDDL()

   public void initialize() {
      logger.trace(String.format("Initializing DLL with numBoundTables %s - maxBound %d",numBoundTables,maxBound));
      numBoundTables = new int[tables.length];
      maxBound = 0;
      sb = new StringBuffer();


      for (int i = 0; i < tables.length; i++) { //step through list of tables
         int numBound = 0; //initialize counter for number of bound tables
         logger.trace(String.format("stepping through tables to count bound tables",numBound));
         int[] relatedFields = tables[i].getRelatedFieldsArray();
         for (int j = 0; j < relatedFields.length; j++) { //step through related fields list
            if (relatedFields[j] != 0) {
               numBound++; //count the number of non-zero related fields
            }
         }
         numBoundTables[i] = numBound;
         if (numBound > maxBound) {
            maxBound = numBound;
         }
      }
      logger.info("DLL initialization complete");
   }
   
   protected EdgeTable getTable(int numFigure) {
      logger.debug(String.format("Getting tables with numFigure %d - maxBound %d",numFigure,maxBound));
      for (int tIndex = 0; tIndex < tables.length; tIndex++) {
         if (numFigure == tables[tIndex].getNumFigure()) {
            logger.info("Tables returned!");
            return tables[tIndex];
         }
      }
      return null;
   }
   
   protected EdgeField getField(int numFigure) {
      logger.debug(String.format("Getting fields with numFigure %d - maxBound %d",numFigure,maxBound));
      for (int fIndex = 0; fIndex < fields.length; fIndex++) {
         if (numFigure == fields[fIndex].getNumFigure()) {
            logger.info("Fields returned!");
            return fields[fIndex];
         }
      }
      return null;
   }

   public abstract String getDatabaseName();

   public abstract String getProductName();
   
   public abstract String getSQLString();
   
   public abstract void createDDL();
   
}//EdgeConvertCreateDDL
