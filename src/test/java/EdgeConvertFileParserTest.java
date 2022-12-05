import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import java.io.File;

public class EdgeConvertFileParserTest {
 File edgeFile;
 File saveFile;
 File randomFile;
 EdgeConvertFileParser fileParserTester;


    @Before
public void setup(){
    this.edgeFile=new File("../../../Courses.edg");
    this.randomFile=new File("../../../CoursesEdgeDiagram.pdf");
    this.saveFile=new File("../../../courses.sav");
}
@Test
public void givenEdgeFileWhenRunningOpenFileFiletypeEdge(){
        this.fileParserTester=new EdgeConvertFileParser(this.edgeFile);
        Assert.assertEquals("fileType should be edge","edge", this.fileParserTester.openFile(this.edgeFile));
    }
    @Test
    public void givenSaveFileWhenRunningOpenFileFiletypeSave(){
        this.fileParserTester=new EdgeConvertFileParser(this.saveFile);
        Assert.assertEquals("filetype should be save","save",this.fileParserTester.openFile(this.saveFile));
    }

    @Test
    public void givenRandomFileWhenRunningOpenFileFiletypeNull(){
        this.fileParserTester=new EdgeConvertFileParser(this.randomFile);
        Assert.assertNull("filetype should be null",this.fileParserTester.openFile(this.randomFile));
    }

    @Test
    public void givenEdgeFileWhenRunningGetEdgeFieldsReturnNotNull() throws Exception{
        this.fileParserTester=new EdgeConvertFileParser(this.edgeFile);
        this.fileParserTester.parseEdgeFile();
        Assert.assertNotNull(this.fileParserTester.getEdgeFields());
    }

    @Test
    public void givenSaveFileWhenRunningGetEdgeFieldsReturnNotNull() throws Exception{
        this.fileParserTester=new EdgeConvertFileParser(this.saveFile);
        this.fileParserTester.parseSaveFile();
        Assert.assertNotNull(this.fileParserTester.getEdgeFields());
    }

    @Test
    public void givenEdgeFileWhenRunningGetEdgeTablesReturnNotNull() throws Exception{
        this.fileParserTester=new EdgeConvertFileParser(this.edgeFile);
        this.fileParserTester.parseEdgeFile();
        Assert.assertNotNull(this.fileParserTester.getEdgeTables());
    }

    @Test
    public void givenSaveFileWhenRunningGetEdgeTablesReturnNotNull() throws Exception{
        this.fileParserTester=new EdgeConvertFileParser(this.edgeFile);
        this.fileParserTester.parseEdgeFile();
        Assert.assertNotNull(this.fileParserTester.getEdgeFields());
    }


}
