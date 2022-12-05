import java.io.*;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParseSaveFile extends EdgeConvertFile {
    public static Logger logger = LogManager.getLogger(ParseSaveFile.class.getName());

    public ParseSaveFile(File constructorFile) throws IOException {
        super();
        logger.debug(String.format("ParseSaveFile constructor ran with params: %s", constructorFile));
        parseFile = constructorFile;
        try {
            parseResult = openFile();
        } catch (FileNotFoundException fnfe) {
            System.out.println("Cannot find \"" + parseFile.getName() + "\".");
            logger.error("Cannot find \"" + parseFile.getName() + "\"." + "Aborting...");
            throw fnfe;// throw back to UI anyway
        }
    }

    protected boolean openFile() throws IOException {
        logger.info("Opening file " + parseFile.getName());
        this.parse(); // parse the file
        makeArrays(); // convert ArrayList objects into arrays of the appropriate Class type
        return true;
    }

    protected boolean parse() throws IOException { // this method is unclear and confusing in places
        logger.debug("Parsing save file...");
        FileReader fr = new FileReader(parseFile);
        BufferedReader br = new BufferedReader(fr);
        String currentLine;
        currentLine = br.readLine();
        currentLine = br.readLine();
        currentLine = br.readLine(); // this should be "Table: "
        while (currentLine.startsWith("Table: ")) {
            int numFigure = Integer.parseInt(substringSpace(currentLine)); // get the Table number
            logger.info("Fetched Table number " + numFigure);
            currentLine = br.readLine(); // this should be "{"
            currentLine = br.readLine(); // this should be "TableName"
            String tableName = substringSpace(currentLine);
            EdgeTable tempTable = new EdgeTable(numFigure + DELIM + tableName);
            currentLine = br.readLine(); // this should be the NativeFields list
            StringTokenizer stNatFields = new StringTokenizer(substringSpace(currentLine), DELIM);
            int numFields = stNatFields.countTokens();
            for (int i = 0; i < numFields; i++) {
                tempTable.addNativeField(Integer.parseInt(stNatFields.nextToken()));
            }
            logger.info("Fetched " + numFields + " native fields for Table " + numFigure);

            currentLine = br.readLine(); // this should be the RelatedTables list
            StringTokenizer stTables = new StringTokenizer(substringSpace(currentLine), DELIM);
            int numTables = stTables.countTokens();
            for (int i = 0; i < numTables; i++) {
                tempTable.addRelatedTable(Integer.parseInt(stTables.nextToken()));
            }
            logger.trace("Making arrays for tempTable");
            tempTable.makeArrays();
            logger.info("Fetched " + numTables + " related tables for Table " + numFigure);

            currentLine = br.readLine(); // this should be the RelatedFields list
            StringTokenizer stRelFields = new StringTokenizer(substringSpace(currentLine), DELIM);
            numFields = stRelFields.countTokens();

            for (int i = 0; i < numFields; i++) {
                tempTable.setRelatedField(i, Integer.parseInt(stRelFields.nextToken()));
            }
            logger.debug("Setting related fields for Table " + numFigure);

            alTables.add(tempTable);
            logger.info("Added Table " + numFigure + " to ArrayList");
            currentLine = br.readLine(); // this should be "}"
            currentLine = br.readLine(); // this should be "\n"
            currentLine = br.readLine(); // this should be either the next "Table: ", #Fields#
        }
        while ((currentLine = br.readLine()) != null) {
            StringTokenizer stField = new StringTokenizer(currentLine, DELIM);
            int numFigure = Integer.parseInt(stField.nextToken());
            String fieldName = stField.nextToken();
            EdgeField tempField = new EdgeField(numFigure + DELIM + fieldName);
            tempField.setTableID(Integer.parseInt(stField.nextToken()));
            tempField.setTableBound(Integer.parseInt(stField.nextToken()));
            tempField.setFieldBound(Integer.parseInt(stField.nextToken()));
            tempField.setDataType(Integer.parseInt(stField.nextToken()));
            tempField.setVarcharValue(Integer.parseInt(stField.nextToken()));
            tempField.setIsPrimaryKey(Boolean.valueOf(stField.nextToken()).booleanValue());
            tempField.setDisallowNull(Boolean.valueOf(stField.nextToken()).booleanValue());
            if (stField.hasMoreTokens()) { // Default Value may not be defined
                tempField.setDefaultValue(stField.nextToken());
                logger.info("Setting default value for Field " + numFigure);
            }
            alFields.add(tempField);
        }
        logger.info("Finished parsing save file!");
        br.close();
        return true;
    } // parseSaveFile()

}
