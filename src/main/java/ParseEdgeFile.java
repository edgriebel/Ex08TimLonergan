import java.io.*;
import java.util.*;
import javax.swing.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParseEdgeFile extends EdgeConvertFile {
    public static Logger logger = LogManager.getLogger(ParseEdgeFile.class.getName());

    public ParseEdgeFile(File constructorFile) throws IOException {
        super();
        logger.debug(String.format("ParseEdgeFile constructor ran with params: %s", constructorFile));
        parseFile = constructorFile;
        try {
            parseResult = openFile();
        } catch (FileNotFoundException fnfe) {
            System.out.println("Cannot find \"" + parseFile.getName() + "\".");
            logger.error("Cannot find \"" + parseFile.getName() + "\"." + "Aborting...");
            throw fnfe;//throw back to UI anyway
        }
    }

    protected boolean openFile() throws IOException {
        logger.info("Opening file " + parseFile.getName());
        Boolean readSuccess = false;
        readSuccess = this.parse(); // parse the file
        makeArrays(); // convert ArrayList objects into arrays of the appropriate Class type
        if(readSuccess) {
            readSuccess = this.resolveConnectors(); // Identify nature of Connector endpoints
        }
        return readSuccess;
    }

    protected boolean parse() throws IOException {
        Boolean readSuccess = true;
        logger.info("Starting parse with file " + parseFile);
        logger.info("Building statements...");
        FileReader fr = new FileReader(parseFile);
        BufferedReader br = new BufferedReader(fr);
        String currentLine = null;
        while ((currentLine = br.readLine()) != null) {
            Boolean isAttribute = false;
            Boolean isEntity = false;
            Boolean isUnderlined = false;
            currentLine = currentLine.trim();
            if (currentLine.startsWith("Figure ")) { // this is the start of a Figure entry
                logger.trace("Figure entry found");
                int numFigure = Integer.parseInt(substringSpace(currentLine)); // get the Figure
                // number
                currentLine = br.readLine().trim(); // this should be "{"
                currentLine = br.readLine().trim();
                if (!currentLine.startsWith("Style")) { // this is to weed out other Figures, like Labels
                    continue;
                } else {
                    String style = currentLine.substring(currentLine.indexOf("\"") + 1, currentLine.lastIndexOf("\"")); // get
                                                                                                                 // the
                                                                                                                 // Style
                                                                                                                 // parameter
                    if (style.startsWith("Relation")) { // presence of Relations implies lack of normalization
                        JOptionPane.showMessageDialog(null, "The Edge Diagrammer file\n" + parseFile
                                + "\ncontains relations.  Please resolve them and try again.");
                        readSuccess = false;
                        break;
                    }
                    if (style.startsWith("Entity")) {
                        isEntity = true;
                        logger.trace("Style is of type entity!");
                    }
                    if (style.startsWith("Attribute")) {
                        isAttribute = true;
                        logger.trace("Style is of type attribute!");
                    }
                    if (!(isEntity || isAttribute)) { // these are the only Figures we're interested in
                        logger.trace("Figure object confirmed!");
                        continue;
                    }
                    currentLine = br.readLine().trim(); // this should be Text
                    logger.trace("Text object detetcted");
                    String text = currentLine.substring(currentLine.indexOf("\"") + 1, currentLine.lastIndexOf("\""))
                            .replaceAll(" ", ""); // get the Text parameter
                    if (text.equals("")) {
                        JOptionPane.showMessageDialog(null,
                                "There are entities or attributes with blank names in this diagram.\nPlease provide names for them and try again.");
                        readSuccess = false;
                        logger.debug("Text object empty! Exiting...");
                        break;
                    }
                    int escape = text.indexOf("\\");
                    if (escape > 0) { // Edge denotes a line break as "\line", disregard anything after a backslash
                        text = text.substring(0, escape);
                    }

                    do { // advance to end of record, look for whether the text is underlined
                        currentLine = br.readLine().trim();
                        if (currentLine.startsWith("TypeUnderl")) {
                            isUnderlined = true;
                            logger.trace("Current text detected as underlined!");
                        }
                    } while (!currentLine.equals("}")); // this is the end of a Figure entry
                    logger.trace("Figure entry complete!");

                    if (isEntity) { // create a new EdgeTable object and add it to the alTables ArrayList
                        if (isTableDup(text)) {
                            JOptionPane.showMessageDialog(null, "There are multiple tables called " + text
                                    + " in this diagram.\nPlease rename all but one of them and try again.");
                            readSuccess = false;
                            logger.error("Duplicate table name detected! Aborting...");
                            break;
                        }
                        EdgeTable tempTable = new EdgeTable(numFigure + DELIM + text);
                        tempTable.makeArrays(); 
                        alTables.add(tempTable);
                        logger.trace("New table added to alTables! " + alTables.get(alTables.size() - 1));
                    }
                    if (isAttribute) { // create a new EdgeField object and add it to the alFields ArrayList
                        EdgeField tempField = new EdgeField(numFigure + DELIM + text);
                        tempField.setIsPrimaryKey(isUnderlined);
                        alFields.add(tempField);
                        logger.trace("New field added to alFields! " + alFields.get(alFields.size() - 1));
                    }
                    // reset flags
                    isEntity = false;
                    isAttribute = false;
                    isUnderlined = false;
                }
            } // if("Figure")
            if (currentLine.startsWith("Connector ")) { // this is the start of a Connector entry
                int numConnector = Integer.parseInt(substringSpace(currentLine)); // get the
                // Connector
                // number
                logger.trace("Connector entry found");
                currentLine = br.readLine().trim(); // this should be "{"
                currentLine = br.readLine().trim(); // not interested in Style
                currentLine = br.readLine().trim(); // Figure1
                logger.trace("Figure object 1 detected!");
                int endPoint1 = Integer.parseInt(substringSpace(currentLine));
                currentLine = br.readLine().trim(); // Figure2
                logger.trace("Figure object 2 detected!");
                int endPoint2 = Integer.parseInt(substringSpace(currentLine));
                currentLine = br.readLine().trim(); // not interested in EndPoint1
                currentLine = br.readLine().trim(); // not interested in EndPoint2
                currentLine = br.readLine().trim(); // not interested in SuppressEnd1
                currentLine = br.readLine().trim(); // not interested in SuppressEnd2
                currentLine = br.readLine().trim(); // End1
                String endStyle1 = currentLine.substring(currentLine.indexOf("\"") + 1, currentLine.lastIndexOf("\"")); // get
                                                                                                                 // the
                                                                                                                 // End1
                                                                                                                 // parameter
                logger.trace("End1 style detected as " + endStyle1);
                currentLine = br.readLine().trim(); // End2
                String endStyle2 = currentLine.substring(currentLine.indexOf("\"") + 1, currentLine.lastIndexOf("\"")); // get
                                                                                                                 // the
                                                                                                                 // End2
                                                                                                                 // parameter
                logger.trace("End2 style detected as " + endStyle2);

                do { // advance to end of record
                    currentLine = br.readLine().trim();
                } while (!currentLine.equals("}")); // this is the end of a Connector entry
                logger.debug("Connector entry building complete!");

                alConnectors.add(new EdgeConnector(
                        numConnector + DELIM + endPoint1 + DELIM + endPoint2 + DELIM + endStyle1 + DELIM + endStyle2));
                logger.trace("New connector added to alConnectors!", alConnectors.get(alConnectors.size() - 1));
            } // if("Connector")
        } // while()
        br.close();
        return readSuccess;
    } // parseEdgeFile()

    private boolean resolveConnectors() { // Identify nature of Connector endpoints
        logger.debug("Resolving connectors... Identifying connector endpoints...");
        Boolean readSuccess = true;
        int endPoint1, endPoint2;
        int fieldIndex = 0, table1Index = 0, table2Index = 0;
        int connectorsLength = connectors.length;
        int fieldsLength = fields.length;
        int tablesLength = tables.length;
        for (int cIndex = 0; cIndex < connectorsLength; cIndex++) {
            endPoint1 = connectors[cIndex].getEndPoint1();
            endPoint2 = connectors[cIndex].getEndPoint2();
            fieldIndex = -1;
            for (int fIndex = 0; fIndex < fieldsLength; fIndex++) { // search fields array for endpoints
                if (endPoint1 == fields[fIndex].getNumFigure()) { // found endPoint1 in fields array
                    connectors[cIndex].setIsEP1Field(true); // set appropriate flag
                    fieldIndex = fIndex; // identify which element of the fields array that endPoint1 was found in
                }
                logger.trace("Connector " + connectors[cIndex].getNumConnector() + " endPoint1 " + endPoint1
                        + " is a field: " + connectors[cIndex].getIsEP1Field());
                if (endPoint2 == fields[fIndex].getNumFigure()) { // found endPoint2 in fields array
                    connectors[cIndex].setIsEP2Field(true); // set appropriate flag
                    fieldIndex = fIndex; // identify which element of the fields array that endPoint2 was found in
                }
                logger.trace("Connector " + connectors[cIndex].getNumConnector() + " endPoint2 " + endPoint2
                        + " is a field: " + connectors[cIndex].getIsEP2Field());
            }
            for (int tIndex = 0; tIndex < tablesLength; tIndex++) { // search tables array for endpoints
                if (endPoint1 == tables[tIndex].getNumFigure()) { // found endPoint1 in tables array
                    connectors[cIndex].setIsEP1Table(true); // set appropriate flag
                    table1Index = tIndex; // identify which element of the tables array that endPoint1 was found in
                }
                logger.trace("Endpoint 1 " + endPoint1 + " is a table: " + connectors[cIndex].getIsEP1Table());
                if (endPoint2 == tables[tIndex].getNumFigure()) { // found endPoint1 in tables array
                    connectors[cIndex].setIsEP2Table(true); // set appropriate flag
                    table2Index = tIndex; // identify which element of the tables array that endPoint2 was found in
                }
                logger.trace("Endpoint 2 " + endPoint2 + " is a table: " + connectors[cIndex].getIsEP2Table());
            }

            if (connectors[cIndex].getIsEP1Field() && connectors[cIndex].getIsEP2Field()) { // both endpoints are
                                                                                            // fields,
                                                                                            // implies lack of
                                                                                            // normalization
                JOptionPane.showMessageDialog(null, "The Edge Diagrammer file\n" + parseFile
                        + "\ncontains composite attributes. Please resolve them and try again.");
                readSuccess = false; // this tells GUI not to populate JList components
                logger.error("Composite attributes detected! Aborting...");
                break; // stop processing list of Connectors
            }

            if (connectors[cIndex].getIsEP1Table() && connectors[cIndex].getIsEP2Table()) { // both endpoints are tables
                if ((connectors[cIndex].getEndStyle1().indexOf("many") >= 0) &&
                        (connectors[cIndex].getEndStyle2().indexOf("many") >= 0)) { // the connector represents a
                                                                                    // many-many
                                                                                    // relationship, implies lack of
                                                                                    // normalization
                    JOptionPane.showMessageDialog(null,
                            "There is a many-many relationship between tables\n\"" + tables[table1Index].getName()
                                    + "\" and \"" + tables[table2Index].getName() + "\""
                                    + "\nPlease resolve this and try again.");
                    readSuccess = false; // this tells GUI not to populate JList components
                    logger.error("Many-many relationship detected! Aborting...");
                    break; // stop processing list of Connectors
                } else { // add Figure number to each table's list of related tables
                    tables[table1Index].addRelatedTable(tables[table2Index].getNumFigure());
                    tables[table2Index].addRelatedTable(tables[table1Index].getNumFigure());
                    logger.trace("Table " + tables[table1Index].getNumFigure() + " related to table "
                            + tables[table2Index].getNumFigure());
                    continue; // next Connector
                }
            }

            if (fieldIndex >= 0 && fields[fieldIndex].getTableID() == 0) { // field has not been assigned to a table yet
                if (connectors[cIndex].getIsEP1Table()) { // endpoint1 is the table
                    tables[table1Index].addNativeField(fields[fieldIndex].getNumFigure()); // add to the appropriate
                                                                                           // table's
                                                                                           // field list
                    fields[fieldIndex].setTableID(tables[table1Index].getNumFigure()); // tell the field what table it
                                                                                       // belongs to
                    logger.trace("Field " + fields[fieldIndex].getNumFigure() + " added to table "
                            + tables[table1Index].getNumFigure());
                } else { // endpoint2 is the table
                    tables[table2Index].addNativeField(fields[fieldIndex].getNumFigure()); // add to the appropriate
                                                                                           // table's
                                                                                           // field list
                    fields[fieldIndex].setTableID(tables[table2Index].getNumFigure()); // tell the field what table it
                                                                                       // belongs to
                    logger.trace("Field " + fields[fieldIndex].getNumFigure() + " added to table "
                            + tables[table2Index].getNumFigure());
                }
            } else if (fieldIndex >= 0) { // field has already been assigned to a table
                JOptionPane.showMessageDialog(null, "The attribute " + fields[fieldIndex].getName()
                        + " is connected to multiple tables.\nPlease resolve this and try again.");
                readSuccess = false; // this tells GUI not to populate JList components
                logger.error(
                        "Field " + fields[fieldIndex].getNumFigure() + " connected to multiple tables! Aborting...");
                break; // stop processing list of Connectors
            }
        } // connectors for() loop
        return readSuccess;
    } // resolveConnectors()

    private boolean isTableDup(String testTableName) {
        logger.debug("Checking for duplicate table name... of "+testTableName);
        for (int i = 0; i < alTables.size(); i++) {
            EdgeTable tempTable = (EdgeTable) alTables.get(i);
            logger.trace("Check "+i+" for duplicate between "+tempTable.getName()+" and search "+testTableName);
            if (tempTable.getName().equals(testTableName)) {
                return true;
            }
        }
        return false;
    }
}
