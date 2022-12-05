import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EdgeFieldTest {

    EdgeField testObj;
    private int numFigure, tableID, tableBound, fieldBound, dataType, varcharValue;
    private String name, defaultValue;
    private boolean disallowNull, isPrimaryKey;
    private static String[] strDataType = {"Varchar", "Boolean", "Integer", "Double"};
    public static final int VARCHAR_DEFAULT_LENGTH = 1;
	public static Logger logger=LogManager.getLogger(EdgeConnectorTest.class.getName());

    @Before
    public void setUp() throws Exception {
        logger.trace("Initalizing EdgeFieldTest properties...");
        testObj = new EdgeField("1|testField1");
        numFigure = 1;
        name = "testField1";
        tableID = 0;
        tableBound = 0;
        fieldBound = 0;
        disallowNull = false;
        isPrimaryKey = false;
        defaultValue = "";
        varcharValue = VARCHAR_DEFAULT_LENGTH;
        dataType = 0;
        logger.trace("Initialized EdgeFieldTest Test Object!",testObj);
    }

    @After
    public void logEnd() {
		logger.debug("Finsihed test");
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
    public void givenProperTableID_returnTableID() {
        logger.trace("Testing givenProperTableID_returnTableID()...");
        assertEquals(tableID, testObj.getTableID());
    }

    @Test
    public void givenProperTableIDpass_setTableID() {
        logger.trace("Testing givenProperTableIDpass_setTableID()...");
        testObj.setTableID(1);
        assertEquals(1, testObj.getTableID());
    }

    @Test
    public void givenInstansiatedTableBound_returnTableBound() {
        logger.trace("Testing givenInstansiatedTableBound_returnTableBound()...");
        assertEquals(tableBound, testObj.getTableBound());
    }

    @Test
    public void givenProperTableBound_setTableBound() {
        logger.trace("Testing givenProperTableBound_setTableBound()...");
        testObj.setTableBound(1);
        assertEquals(1, testObj.getTableBound());
    }

    @Test
    public void givenInstansiatedFieldBound_returnFieldBound() {
        logger.trace("Testing givenInstansiatedFieldBound_returnFieldBound()...");
        assertEquals(fieldBound, testObj.getFieldBound());
    }

    @Test
    public void givenProperFieldBound_setFieldBound() {
        logger.trace("Testing givenProperFieldBound_setFieldBound()...");
        testObj.setFieldBound(1);
        assertEquals(1, testObj.getFieldBound());
    }

    @Test
    public void givenInstansiatedDisallowNull_returnDisallowNull() {
        logger.trace("Testing givenInstansiatedDisallowNull_returnDisallowNull()...");
        assertEquals(disallowNull, testObj.getDisallowNull());
    }

    @Test
    public void givenProperDisallowNull_setDisallowNull() {
        logger.trace("Testing givenProperDisallowNull_setDisallowNull()...");
        testObj.setDisallowNull(true);
        assertEquals(true, testObj.getDisallowNull());
    }

    @Test
    public void givenInstansiatedPrimaryKey_returnPrimaryKey() {
        logger.trace("Testing givenInstansiatedPrimaryKey_returnPrimaryKey()...");
        assertEquals(isPrimaryKey, testObj.getIsPrimaryKey());
    }

    @Test
    public void givenProperPrimaryKey_setPrimaryKey() {
        logger.trace("Testing givenProperPrimaryKey_setPrimaryKey()...");
        testObj.setIsPrimaryKey(true);
        assertEquals(true, testObj.getIsPrimaryKey());
    }

    @Test
    public void givenInstansiatedDefaultValue_returnDefaultValue() {
        logger.trace("Testing givenInstansiatedDefaultValue_returnDefaultValue()...");
        assertEquals(defaultValue, testObj.getDefaultValue());
    }

    @Test
    public void givenProperDefaultValue_setDefaultValue() {
        logger.trace("Testing givenProperDefaultValue_setDefaultValue()...");
        testObj.setDefaultValue("test");
        assertEquals("test", testObj.getDefaultValue());
    }

    @Test
    public void givenInstansiatedVarcharValue_returnVarcharValue() {
        logger.trace("Testing givenInstansiatedVarcharValue_returnVarcharValue()...");
        assertEquals(varcharValue, testObj.getVarcharValue());
    }

    @Test
    public void givenProperVarcharValue_setVarcharValue() {
        logger.trace("Testing givenProperVarcharValue_setVarcharValue()...");
        testObj.setVarcharValue(2);
        assertEquals(2, testObj.getVarcharValue());
    }

    @Test
    public void givenInstansiatedDataType_returnDataType() {
        logger.trace("Testing givenInstansiatedDataType_returnDataType()...");
        assertEquals(dataType, testObj.getDataType());
    }

    @Test
    public void givenProperDataType_setDataType() {
        logger.trace("Testing givenProperDataType_setDataType()...");
        testObj.setDataType(1);
        assertEquals(1, testObj.getDataType());
    }

    @Test
    public void givenInstansiatedStrDataType_returnStrDataType() {
        logger.trace("Testing givenInstansiatedStrDataType_returnStrDataType()...");
        assertEquals(strDataType, testObj.getStrDataType());
    }

    @Test
    public void givenProperString_returnFieldName() {
        logger.trace("Testing givenProperString_returnFieldName()...");
        assertEquals("1|testField1", testObj.toString());
    }
    
}
