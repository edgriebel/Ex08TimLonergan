import java.io.*;
import java.util.*;
import javax.swing.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class EdgeConvertFile {
   public static Logger logger = LogManager.getLogger(EdgeConvertFile.class.getName());
   // private String filename = "test.edg";
   protected File parseFile;
   protected ArrayList<EdgeTable> alTables;
   protected ArrayList<EdgeField> alFields;
   protected ArrayList<EdgeConnector> alConnectors;
   protected EdgeTable[] tables;
   protected EdgeField[] fields;
   protected EdgeConnector[] connectors;
   protected boolean parseResult;
   public static final String EDGE_ID = "EDGE Diagram File"; // first line of .edg files should be this
   public static final String SAVE_ID = "EdgeConvert Save File"; // first line of save files should be this
   public static final String DELIM = "|";
   
   public EdgeConvertFile() {
      alTables = new ArrayList<EdgeTable>();
      alFields = new ArrayList<EdgeField>();
      alConnectors = new ArrayList<EdgeConnector>();
   }

   public void makeArrays() { // convert ArrayList objects into arrays of the appropriate Class type
      logger.debug("Converting arrays...");
      if (alTables != null) {
         tables = (EdgeTable[]) alTables.toArray(new EdgeTable[alTables.size()]);
      }
      if (alFields != null) {
         fields = (EdgeField[]) alFields.toArray(new EdgeField[alFields.size()]);
      }
      if (alConnectors != null) {
         connectors = (EdgeConnector[]) alConnectors.toArray(new EdgeConnector[alConnectors.size()]);
      }
      logger.info("Arrays converted!");
   }

   protected String substringSpace(String inputString) {
      return inputString.substring(inputString.indexOf(" ") + 1);
   }

   public EdgeTable[] getEdgeTables() {
      return tables;
   }

   public EdgeField[] getEdgeFields() {
      return fields;
   }

   public boolean getParseResult() {
      return parseResult;
   }

   abstract protected boolean openFile() throws IOException;
   
   abstract protected boolean parse() throws IOException;

   public static EdgeConvertFile parseFile(File inputFile) throws IOException {
      logger.info("Opening file " + inputFile.getName());
      FileReader fr = new FileReader(inputFile);
      BufferedReader br = new BufferedReader(fr);
      // test for what kind of file we have
      String currentLine = br.readLine().trim();
      if (currentLine.startsWith(EDGE_ID)) { // the file chosen is an Edge Diagrammer file
         logger.debug("File was detected as edge file");
         br.close();
         return new ParseEdgeFile(inputFile);
      } else {
         if (currentLine.startsWith(SAVE_ID)) { // the file chosen is a Save file created by this application
            logger.debug("File was detected as save file");
            br.close();
            return new ParseSaveFile(inputFile);
         } else { // the file chosen is something else
            JOptionPane.showMessageDialog(null, "Unrecognized file format");
         }
      }
      br.close();
      return null;
   } // openFile()
} // EdgeConvertFileHandler
