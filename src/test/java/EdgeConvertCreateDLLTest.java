import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EdgeConvertCreateDLLTest {
    EdgeTable[] sampleTables;
    EdgeField[] sampleFields;
    EdgeConvertCreateDDL testConvertCreateDDL;

	public static Logger logger=LogManager.getLogger(EdgeConnectorTest.class.getName());

    @Before
    public void setup() throws Exception {
		logger.trace("Creating new EdgeConvertCreateDLLTest properties");
        sampleTables = new EdgeTable[3];
        sampleFields = new EdgeField[4];
        for(int i = 0; i < 3; i++) {
            EdgeTable testTable = new EdgeTable(i+"|table"+i);
            testTable.makeArrays();
            sampleTables[i] = testTable;
        }
        for(int i = 0; i < 4; i++) {
            EdgeField testField = new EdgeField(i+"|field"+i);
            sampleFields[i] = testField;
        }
        testConvertCreateDDL = new CreateDDLMySQL(sampleTables, sampleFields);
		logger.trace("Finished creating new EdgeConvertCreateDLLTest properties");
		logger.debug("Starting test");
    }

    @After
    public void logEnd() {
		logger.debug("Finsihed test");
    }

    @Test
    public void givenEmptyConstruction_whenConstructing_noFieldsAreSet() throws Exception {
		logger.debug("givenEmptyConstruction_whenConstructing_noFieldsAreSet");
        EdgeConvertCreateDDL testEmptyConvertCreateDDL = new CreateDDLMySQL();
        assertNull("Fields array should be empty",testEmptyConvertCreateDDL.fields);
        assertNull("Tables array should be empty",testEmptyConvertCreateDDL.tables);
    }

    @Test
    public void givenSimpleConstructor_whenConstructing_fieldsAreSet() throws Exception {
		logger.debug("testing givenSimpleConstructor_whenConstructing_fieldsAreSet");
        assertArrayEquals("Fields array should match what constructor provided it with", sampleFields, testConvertCreateDDL.fields);
        assertArrayEquals("Tables array should match what constructor provided it with", sampleTables, testConvertCreateDDL.tables);
    }

    @Test
    public void givenSimpleConstructor_whenConstructingWithNoTables_constructsFine() throws Exception {
		logger.debug("testing givenSimpleConstructor_whenConstructingWithNoTables_constructsFine");
        EdgeTable[] altTables = new EdgeTable[0];
        EdgeConvertCreateDDL testConvertCreateDDLEmptyTables = new CreateDDLMySQL(altTables,sampleFields);
        assertArrayEquals("Fields array should match what constructor provided it with", sampleFields, testConvertCreateDDLEmptyTables.fields);
        assertArrayEquals("Tables array should match what constructor provided it with", altTables, testConvertCreateDDLEmptyTables.tables);
    }

    @Test
    public void givenSimpleConstructor_whenConstructingWithNoFields_constructsFine() throws Exception {
		logger.debug("testing givenSimpleConstructor_whenConstructingWithNoFields_constructsFine");
        EdgeField[] altFields = new EdgeField[0];
        EdgeConvertCreateDDL testConvertCreateDDLEmptyTables = new CreateDDLMySQL(sampleTables,altFields);
        assertArrayEquals("Fields array should match what constructor provided it with", altFields, testConvertCreateDDLEmptyTables.fields);
        assertArrayEquals("Tables array should match what constructor provided it with", sampleTables, testConvertCreateDDLEmptyTables.tables);
    }

    @Test
    public void givenSimpleConstructor_whenConstructing_numBoundTablesIsSet() throws Exception {
		logger.debug("testing givenSimpleConstructor_whenConstructing_numBoundTablesIsSet");
        assertEquals("numBoundTables is equal in size to tables", sampleTables.length, testConvertCreateDDL.numBoundTables.length);
    }

    @Test
    public void givenTables_whenGetTable_returnCorrectTable() throws Exception {
		logger.debug("testing givenTables_whenGetTable_returnCorrectTable");
        assertEquals("Table 0 is obtained from getTable(0)", sampleTables[0], testConvertCreateDDL.getTable(0));
    }

    @Test
    public void givenTables_whenGetTable9_returnCorrectTable() throws Exception {
		logger.debug("testing givenTables_whenGetTable9_returnCorrectTable");
        EdgeTable[] sampleTables = new EdgeTable[4];
        EdgeField[] sampeFields = new EdgeField[5];
        for(int i = 0; i < 3; i++) {
            EdgeTable testTable = new EdgeTable(i+"|table"+i);
            testTable.makeArrays();
            sampleTables[i] = testTable;
        }
        EdgeTable testTable9 = new EdgeTable("9|table9");
        testTable9.makeArrays();
        for(int i = 0; i < 4; i++) {
            EdgeField testField = new EdgeField(i+"|field"+i);
            sampeFields[i] = testField;
        }
        sampleTables[3] = testTable9;
        EdgeConvertCreateDDL testConvertCreateDDL = new CreateDDLMySQL(sampleTables,sampleFields);
        assertEquals("Table with figure ID 9 is obtained with getTable(9)", sampleTables[3], testConvertCreateDDL.getTable(9));
    }

    @Test
    public void givenTables_whenGetNullTable_returnNull() throws Exception {
		logger.debug("testing givenTables_whenGetNullTable_returnNull");
        assertNull("getTable with a number not matching a field number returns null", testConvertCreateDDL.getTable(999));
    }

    @Test
    public void givenFields_whenGetField_returnCorrectField() throws Exception {
		logger.debug("testing givenFields_whenGetField_returnCorrectField");
        assertEquals("Field 0 is obtained from getField(0)", sampleFields[0], testConvertCreateDDL.getField(0));
    }

    @Test
    public void givenFields_whenGetField9_returnCorrectField() throws Exception {
		logger.debug("testing givenFields_whenGetField9_returnCorrectField");
        EdgeTable[] sampleTables = new EdgeTable[3];
        EdgeField[] sampleFields = new EdgeField[5];
        for(int i = 0; i < 3; i++) {
            EdgeTable testTable = new EdgeTable(i+"|table"+i);
            testTable.makeArrays();
            sampleTables[i] = testTable;
        }
        EdgeField testField9 = new EdgeField("9|field9");
        for(int i = 0; i < 4; i++) {
            EdgeField testField = new EdgeField(i+"|field"+i);
            sampleFields[i] = testField;
        }
        sampleFields[4] = testField9;
        EdgeConvertCreateDDL testConvertCreateDDL = new CreateDDLMySQL(sampleTables, sampleFields);
        assertEquals("Field with figure ID 9 is obtained with getField(9)", sampleFields[4], testConvertCreateDDL.getField(9));
    }

    @Test
    public void givenFields_whenGetNullField_returnNull() throws Exception {
		logger.debug("testing givenFields_whenGetNullField_returnNull");
        assertNull("getField with a number not matching a field number returns null", testConvertCreateDDL.getField(999));
    }

    @Test
    public void givenConstructor_whenConstructing3Relations_maxBounds3() throws Exception {
		logger.debug("testing givenConstructor_whenConstructing3Relations_maxBounds3");
        EdgeTable[] sampleTables = new EdgeTable[2];
        EdgeField[] sampleFields = new EdgeField[4];
        for(int i = 0; i < 4; i++) {
            EdgeField testField = new EdgeField(i+"|field"+i);
            sampleFields[i] = testField;
        }
        EdgeTable testTable = new EdgeTable("1|table1");
        testTable.addNativeField(1);
        testTable.addNativeField(2);
        testTable.addNativeField(3);
        EdgeTable testTable2 = new EdgeTable("0|table0");
        testTable2.makeArrays();
        testTable.makeArrays();
        testTable.setRelatedField(0, 1);
        testTable.setRelatedField(1, 1);
        testTable.setRelatedField(2, 1);
        sampleTables[0] = testTable2;
        sampleTables[1] = testTable;
        EdgeConvertCreateDDL testConvertCreateDDL = new CreateDDLMySQL(sampleTables, sampleFields);
        assertEquals("maxBound is set to 3",3,testConvertCreateDDL.maxBound);
    }

    @Test
    public void givenConstructor_whenConstructing3RelationsLast_maxBounds3() throws Exception {
		logger.debug("testing givenConstructor_whenConstructing3RelationsLast_maxBounds3");
        EdgeTable[] sampleTables = new EdgeTable[2];
        EdgeField[] sampleFields = new EdgeField[4];
        for(int i = 0; i < 4; i++) {
            EdgeField testField = new EdgeField(i+"|field"+i);
            sampleFields[i] = testField;
        }
        EdgeTable testTable = new EdgeTable("1|table1");
        testTable.addNativeField(1);
        testTable.addNativeField(2);
        testTable.addNativeField(3);
        EdgeTable testTable2 = new EdgeTable("0|table0");
        testTable2.makeArrays();
        testTable.makeArrays();
        testTable.setRelatedField(0, 1);
        testTable.setRelatedField(1, 1);
        testTable.setRelatedField(2, 1);
        sampleTables[1] = testTable2;
        sampleTables[0] = testTable;
        EdgeConvertCreateDDL testConvertCreateDDL = new CreateDDLMySQL(sampleTables, sampleFields);
        assertEquals("maxBound is set to 3, when given largest table last",3,testConvertCreateDDL.maxBound);
    }

    @Test
    public void givenConstructor_whenConstructing2Relations_maxBounds2() throws Exception {
		logger.debug("testing givenConstructor_whenConstructing2Relations_maxBounds2");
        EdgeTable[] sampleTables = new EdgeTable[2];
        EdgeField[] sampleFields = new EdgeField[4];
        for(int i = 0; i < 4; i++) {
            EdgeField testField = new EdgeField(i+"|field"+i);
            sampleFields[i] = testField;
        }
        EdgeTable testTable = new EdgeTable("1|table1");
        testTable.addNativeField(1);
        testTable.addNativeField(2);
        testTable.addNativeField(3);
        EdgeTable testTable2 = new EdgeTable("0|table0");
        testTable2.makeArrays();
        testTable.makeArrays();
        testTable.setRelatedField(1, 1);
        testTable.setRelatedField(2, 1);
        sampleTables[0] = testTable;
        sampleTables[1] = testTable2;
        EdgeConvertCreateDDL testConvertCreateDDL = new CreateDDLMySQL(sampleTables, sampleFields);
        assertEquals("maxBound is set to 2",2,testConvertCreateDDL.maxBound);
    }

    @Test
    public void givenConstructor_whenConstructingZeroedRelations_maxBounds2() throws Exception {
		logger.debug("testing givenConstructor_whenConstructingZeroedRelations_maxBounds2");
        EdgeTable[] sampleTables = new EdgeTable[2];
        EdgeField[] sampleFields = new EdgeField[4];
        for(int i = 0; i < 4; i++) {
            EdgeField testField = new EdgeField(i+"|field"+i);
            sampleFields[i] = testField;
        }
        EdgeTable testTable = new EdgeTable("1|table1");
        testTable.addNativeField(1);
        testTable.addNativeField(2);
        testTable.addNativeField(3);
        EdgeTable testTable2 = new EdgeTable("0|table0");
        testTable2.makeArrays();
        testTable.makeArrays();
        testTable.setRelatedField(0, 0);//testing this as zeroed
        testTable.setRelatedField(1, 1);
        testTable.setRelatedField(2, 1);
        // Orders swapped
        sampleTables[0] = testTable2;
        sampleTables[1] = testTable;
        EdgeConvertCreateDDL testConvertCreateDDL = new CreateDDLMySQL(sampleTables, sampleFields);
        assertEquals("maxBound is set to 2",2,testConvertCreateDDL.maxBound);
    }
    
    @Test(expected = Test.None.class /* no exception expected */)
    public void givenClass_whenAbstractMethods_classHasGetDatabaseName() throws Exception {
		logger.debug("testing givenClass_whenAbstractMethods_classHasGetDatabaseName");
        EdgeConvertCreateDDL.class.getMethod("getDatabaseName", (Class<?>[]) null);
    }
    @Test(expected = Test.None.class /* no exception expected */)
    public void givenClass_whenAbstractMethods_classHasGetProductName() throws Exception {
		logger.debug("testing givenClass_whenAbstractMethods_classHasGetProductName");
        EdgeConvertCreateDDL.class.getMethod("getProductName", (Class<?>[]) null);
    }
    @Test(expected = Test.None.class /* no exception expected */)
    public void givenClass_whenAbstractMethods_classHasGetSQLString() throws Exception {
		logger.debug("testing givenClass_whenAbstractMethods_classHasGetSQLString");
        EdgeConvertCreateDDL.class.getMethod("getSQLString", (Class<?>[]) null);
    }
    @Test(expected = Test.None.class /* no exception expected */)
    public void givenClass_whenAbstractMethods_classHasCreateDDL() throws Exception {
		logger.debug("testing givenClass_whenAbstractMethods_classHasCreateDDL");
        EdgeConvertCreateDDL.class.getMethod("createDDL", (Class<?>[]) null);
    }
}
