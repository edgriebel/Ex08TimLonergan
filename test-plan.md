# Test Plans
EdgeConvertCreateDLL test - Nick Giancursio
- Empty constructor does not set any fields
- Regular constructor sets tables and fields provided
- Regular constructor runs initializes and sets numBoundTables to correct size given 0 EdgeTables
- Regular constructor runs initializes and sets numBoundTables to correct size given 1 EdgeTable
- Regular constructor runs initializes and sets numBoundTables to correct size given 2 EdgeTables
- Regular constructor runs initializes and runs though tables, setting maxBound to correct number the highest set of bound tables, when larger table is specified last
- Regular constructor runs initializes and runs though tables, setting maxBound to correct number the highest set of bound tables, when larger table is specified first
- Running getTable with a numFigure in-bounds returns the matching EdgeTable
- Running getTable with a numFigure out-of-bounds returns null
- Running getField with a numFigure in-bounds returns the matching EdgeField
- Running getField with a numFigure out-of-bounds returns null
- Abstract method getDatabaseName exists
- Abstract method getProductName exists
- Abstract method getSQLString exists
- Abstract method createDDL exists

- EdgeTable test - Tim Lonergan
    - Table constructor is passed through and the following variables are read/set...
        - numFigure (int)
        - name (String)
        - alRelatedTables (ArrayList)
        - alNativeTables (ArrayList)
        - relatedTables (int array)
        - relatedFields (int array)
        - nativeFields (int array)

    - moveFieldUp() recieves specified nativeField via an int array pointer to execute a move one place UP

    - moveFieldDown() recieves specified nativeField via an int array pointer to execute a move one place DOWN

    toString() recieves string via constructor and returns TableName

- EdgeField test - Tim Lonergan
    - Table constructor is passed through and the following variables are read/set...
        - numFigure (int)
        - name (String)
        - tableID (int)
        - tableBound (int)
        - disallowNull (boolean)
        - isPrimaryKey (boolean)
        - defaultValue (String --> set as empty)
        - varcharValue (final)
        - dataType (int array pointer)
    
    - getStrDataType() recieves string via constructor and returns its value ("Varchar", "Boolean", "Integer", "Double")

    - toString() recieves string via constructor and returns FieldName




  
EdgeConvertFileParser test - Matej Malesevic
 - given the constructor is empty, when a new instance is created, then this.constructorFile should be null 
 - given the courses.edg file is used, when a new instance is created, then this.constructorFile should reflect this
 - given a .sav file is used, when a new instance is created, then this.constructorFile should reflect this 
 - given any other file type is used (e.g. CoursesEdgeDiagram.pdf), when a new instance is created, then this.constructorFile should reflect this
 - given this.constructorFile contains a .edg file, when running openFile(), then this.fileType should equal "edge"
 - given this.constructorFile contains a .sav file, when running openFile(), then this.fileType should equal "save"
 - given this.constructorFile contains any other file type, when running openFile(), then this.fileType should equal null
 - given this.constructorFile contains a .edg file and parseEdgeFile() is run, when running getEdgeFields(), then the return should not be null
 - given this.constructorFile contains a .sav file and parseSaveFile() is run, when running getEdgeFields(), then the return should not be null
 - given this.constructorFile contains a .edg file and parseEdgeFile() is run, when running getEdgeTables(), then the return should not be null
 - given this.constructorFile contains a .sav file and parseSaveFile() is run, when running getEdgeTables(), then the return should not be null


# Running Tests
- Project is gradle wrapped, so run within the root of this project directory `./gradlew test`
- Test results will be here: `/build/reports/tests/test/packages/default-package.html`