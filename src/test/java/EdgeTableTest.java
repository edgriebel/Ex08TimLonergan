import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EdgeTableTest {

    EdgeTable testObj;
    private int numFigure;
    private String name;
    private ArrayList alRelatedTables, alNativeFields;
    private int[] relatedTables, relatedFields, nativeFields;
    public static Logger logger=LogManager.getLogger(EdgeConnectorTest.class.getName());

    @Before
    public void setUp() throws Exception {
        logger.trace("Initalizing EdgeTableTest properties...");
        testObj = new EdgeTable("1|testTable1");
        numFigure = 1;
        name = "testTable1";
        alRelatedTables = new ArrayList();
        alNativeFields = new ArrayList();
        relatedTables = new int[0];
        relatedFields = new int[0];
        nativeFields = new int[0];
        logger.trace("Initialized EdgeTableTest Test Object!",testObj);
    }

    @After
    public void logEnd() {
		logger.debug("Finsihed testing...");
    }

    @Test
    public void givenNumFigure_returnNumFigure() {
        logger.trace("Testing givenNumFigure_returnNumFigure()...");
        assertEquals(numFigure, testObj.getNumFigure());
    }

    @Test
    public void givenName_returnName() {
        logger.trace("Testing givenName_returnName()...");
        assertEquals(name, testObj.getName());
    }

    @Test
    public void givenRelatedTable_addToRelatedTablesArray() {
        logger.trace("Testing givenRelatedTable_addToRelatedTablesArray()...");
        testObj.addRelatedTable(1);
        alRelatedTables.add(1);
        assertEquals(alRelatedTables, testObj.getRelatedTablesArray());
    }

    @Test
    public void givenInstantiatedRelatedTablesArray_returnRelatedTablesArray() {
        logger.trace("Testing givenInstantiatedRelatedTablesArray_returnRelatedTablesArray()...");
        assertEquals(relatedTables, testObj.getRelatedTablesArray());
    }

    @Test
    public void givenInstantiatedRelatedFieldsArray_returnRelatedFieldsArray() {
        logger.trace("Testing givenInstantiatedRelatedFieldsArray_returnRelatedFieldsArray()...");
        assertEquals(relatedFields, testObj.getRelatedFieldsArray());
    }

    @Test
    public void givenProperRelatedField_setProperFieldasRelatedField() {
        logger.trace("Testing givenProperRelatedField_setProperFieldasRelatedField()...");
        testObj.setRelatedField(0, 1);
        relatedFields[0] = 1;
        assertEquals(relatedFields, testObj.getRelatedFieldsArray());
    }

    @Test
    public void givenInstantiatedNativeFieldsArray_returnNativeFieldsArray() {
        logger.trace("Testing givenInstantiatedNativeFieldsArray_returnNativeFieldsArray()...");
        assertEquals(nativeFields, testObj.getNativeFieldsArray());
    }

    @Test
    public void givenNativeFieldType_addToNativeFieldArray() {
        logger.trace("Testing givenNativeFieldType_addToNativeFieldArray()...");
        testObj.addNativeField(1);
        alNativeFields.add(1);
        assertEquals(alNativeFields, testObj.getNativeFieldsArray());
    }

    @Test
    public void givenInstantiatedNativeFieldsArray_AND_properMethodInput_executeMoveFieldUp() {
        logger.trace("Testing givenInstantiatedNativeFieldsArray_AND_properMethodInput_executeMoveFieldUp()...");
        testObj.addNativeField(1);
        testObj.addNativeField(2);
        testObj.addNativeField(3);
        testObj.moveFieldUp(2);
        nativeFields[0] = 1;
        nativeFields[1] = 3;
        nativeFields[2] = 2;
        assertEquals(nativeFields, testObj.getNativeFieldsArray());
    }

    @Test
    public void givenInstantiatedNativeFieldsArray_AND_properMethodInput_executeMoveFieldDown() {
        logger.trace("Testing givenInstantiatedNativeFieldsArray_AND_properMethodInput_executeMoveFieldDown()...");
        testObj.addNativeField(1);
        testObj.addNativeField(2);
        testObj.addNativeField(3);
        testObj.moveFieldDown(1);
        nativeFields[0] = 1;
        nativeFields[1] = 3;
        nativeFields[2] = 2;
        assertEquals(nativeFields, testObj.getNativeFieldsArray());
    }

    @Test
    public void givenProperTableName_returnTableName() {
        logger.trace("Testing givenProperTableName_returnTableName()...");
        assertEquals("1|testTable1", testObj.toString());
    }

}

