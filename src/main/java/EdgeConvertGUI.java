import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.lang.reflect.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EdgeConvertGUI {
   public static Logger logger = LogManager.getLogger(EdgeConvertGUI.class.getName());
   public static final int HORIZ_SIZE = 635;
   public static final int VERT_SIZE = 400;
   public static final int HORIZ_LOC = 100;
   public static final int VERT_LOC = 100;
   public static final String DEFINE_TABLES = "Define Tables";
   public static final String DEFINE_RELATIONS = "Define Relations";
   public static final String CANCELLED = "CANCELLED";
   private static JFileChooser jfcEdge, jfcGetClass, jfcOutputDir;
   private static ExampleFileFilter effEdge, effSave, effClass;
   private File parseFile, saveFile, outputFile, outputDir, outputDirOld;
   private String truncatedFilename;
   private String sqlString;
   private String databaseName;
   EdgeMenuListener menuListener;
   EdgeRadioButtonListener radioListener;
   EdgeWindowListener edgeWindowListener;
   CreateDDLButtonListener createDDLListener;
   private EdgeConvertFile ecfp;
   private EdgeConvertCreateDDL eccd;
   private static PrintWriter pw;
   private EdgeTable[] tables; // master copy of EdgeTable objects
   private EdgeField[] fields; // master copy of EdgeField objects
   private EdgeTable currentDTTable, currentDRTable1, currentDRTable2; // pointers to currently selected table(s) on
                                                                       // Define Tables (DT) and Define Relations (DR)
                                                                       // screens
   private EdgeField currentDTField, currentDRField1, currentDRField2; // pointers to currently selected field(s) on
                                                                       // Define Tables (DT) and Define Relations (DR)
                                                                       // screens
   private static boolean readSuccess = true; // this tells GUI whether to populate JList components or not
   private boolean dataSaved = true;
   private ArrayList alSubclasses, alProductNames;
   private String[] productNames;
   private Object[] objSubclasses;

   // Define Tables screen objects
   static JFrame jfDT;
   static JPanel jpDTBottom, jpDTCenter, jpDTCenter1, jpDTCenter2, jpDTCenterRight, jpDTCenterRight1, jpDTCenterRight2,
         jpDTMove;
   static JButton jbDTCreateDDL, jbDTDefineRelations, jbDTVarchar, jbDTDefaultValue, jbDTMoveUp, jbDTMoveDown;
   static ButtonGroup bgDTDataType;
   static JRadioButton[] jrbDataType;
   static String[] strDataType;
   static JCheckBox jcheckDTDisallowNull, jcheckDTPrimaryKey;
   static JTextField jtfDTVarchar, jtfDTDefaultValue;
   static JLabel jlabDTTables, jlabDTFields;
   static JScrollPane jspDTTablesAll, jspDTFieldsTablesAll;
   static JList jlDTTablesAll, jlDTFieldsTablesAll;
   static DefaultListModel dlmDTTablesAll, dlmDTFieldsTablesAll;
   static JMenuBar jmbDTMenuBar;
   static JMenu jmDTFile, jmDTOptions, jmDTHelp;
   static JMenuItem jmiDTOpenEdge, jmiDTSave, jmiDTSaveAs, jmiDTExit, jmiDTOptionsOutputLocation,
         jmiDTOptionsShowProducts, jmiDTHelpAbout;

   // Define Relations screen objects
   static JFrame jfDR;
   static JPanel jpDRBottom, jpDRCenter, jpDRCenter1, jpDRCenter2, jpDRCenter3, jpDRCenter4;
   static JButton jbDRCreateDDL, jbDRDefineTables, jbDRBindRelation;
   static JList jlDRTablesRelations, jlDRTablesRelatedTo, jlDRFieldsTablesRelations, jlDRFieldsTablesRelatedTo;
   static DefaultListModel dlmDRTablesRelations, dlmDRTablesRelatedTo, dlmDRFieldsTablesRelations,
         dlmDRFieldsTablesRelatedTo;
   static JLabel jlabDRTablesRelations, jlabDRTablesRelatedTo, jlabDRFieldsTablesRelations, jlabDRFieldsTablesRelatedTo;
   static JScrollPane jspDRTablesRelations, jspDRTablesRelatedTo, jspDRFieldsTablesRelations,
         jspDRFieldsTablesRelatedTo;
   static JMenuBar jmbDRMenuBar;
   static JMenu jmDRFile, jmDROptions, jmDRHelp;
   static JMenuItem jmiDROpenEdge, jmiDROpenSave, jmiDRSave, jmiDRSaveAs, jmiDRExit, jmiDROptionsOutputLocation,
         jmiDROptionsShowProducts, jmiDRHelpAbout;

   public EdgeConvertGUI() {
      logger.debug("Creating GUI");
      menuListener = new EdgeMenuListener();
      radioListener = new EdgeRadioButtonListener();
      edgeWindowListener = new EdgeWindowListener();
      createDDLListener = new CreateDDLButtonListener();
      this.showGUI();
   } // EdgeConvertGUI.EdgeConvertGUI()

   public void showGUI() {
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // use the OS native LAF, as opposed to
                                                                              // default Java LAF
      } catch (Exception e) {
         System.out.println("Error setting native LAF: " + e);
      }
      createDTScreen();
      createDRScreen();
      logger.info("Showing GUI");
   } // showGUI()

   public void createDTScreen() {// create Define Tables screen
      logger.trace("createDTScreen start");
      jfDT = new JFrame(DEFINE_TABLES);
      jfDT.setLocation(HORIZ_LOC, VERT_LOC);
      Container cp = jfDT.getContentPane();
      jfDT.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      jfDT.addWindowListener(edgeWindowListener);
      jfDT.getContentPane().setLayout(new BorderLayout());
      jfDT.setVisible(true);
      jfDT.setSize(HORIZ_SIZE + 150, VERT_SIZE);

      // setup menubars and menus
      jmbDTMenuBar = new JMenuBar();
      jfDT.setJMenuBar(jmbDTMenuBar);

      jmDTFile = new JMenu("File");
      jmDTFile.setMnemonic(KeyEvent.VK_F);
      jmbDTMenuBar.add(jmDTFile);
      jmiDTOpenEdge = new JMenuItem("Open Edge/Save File");
      jmiDTOpenEdge.setMnemonic(KeyEvent.VK_E);
      jmiDTOpenEdge.addActionListener(menuListener);
      jmiDTSave = new JMenuItem("Save");
      jmiDTSave.setMnemonic(KeyEvent.VK_S);
      jmiDTSave.setEnabled(false);
      jmiDTSave.addActionListener(menuListener);
      jmiDTSaveAs = new JMenuItem("Save As...");
      jmiDTSaveAs.setMnemonic(KeyEvent.VK_A);
      jmiDTSaveAs.setEnabled(false);
      jmiDTSaveAs.addActionListener(menuListener);
      jmiDTExit = new JMenuItem("Exit");
      jmiDTExit.setMnemonic(KeyEvent.VK_X);
      jmiDTExit.addActionListener(menuListener);
      jmDTFile.add(jmiDTOpenEdge);
      jmDTFile.add(jmiDTSave);
      jmDTFile.add(jmiDTSaveAs);
      jmDTFile.add(jmiDTExit);

      jmDTOptions = new JMenu("Options");
      jmDTOptions.setMnemonic(KeyEvent.VK_O);
      jmbDTMenuBar.add(jmDTOptions);
      jmiDTOptionsOutputLocation = new JMenuItem("Set Output File Definition Location");
      jmiDTOptionsOutputLocation.setMnemonic(KeyEvent.VK_S);
      jmiDTOptionsOutputLocation.addActionListener(menuListener);
      jmiDTOptionsShowProducts = new JMenuItem("Show Database Products Available");
      jmiDTOptionsShowProducts.setMnemonic(KeyEvent.VK_H);
      jmiDTOptionsShowProducts.setEnabled(false);
      jmiDTOptionsShowProducts.addActionListener(menuListener);
      jmDTOptions.add(jmiDTOptionsOutputLocation);
      jmDTOptions.add(jmiDTOptionsShowProducts);

      jmDTHelp = new JMenu("Help");
      jmDTHelp.setMnemonic(KeyEvent.VK_H);
      jmbDTMenuBar.add(jmDTHelp);
      jmiDTHelpAbout = new JMenuItem("About");
      jmiDTHelpAbout.setMnemonic(KeyEvent.VK_A);
      jmiDTHelpAbout.addActionListener(menuListener);
      jmDTHelp.add(jmiDTHelpAbout);

      jfcEdge = new JFileChooser(".");
      jfcOutputDir = new JFileChooser("..");
      effEdge = new ExampleFileFilter("edg", "Edge Diagrammer Files");
      effSave = new ExampleFileFilter("sav", "Edge Convert Save Files");
      jfcOutputDir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

      jpDTBottom = new JPanel(new GridLayout(1, 2));

      jbDTCreateDDL = new JButton("Create DDL");
      jbDTCreateDDL.setEnabled(false);
      jbDTCreateDDL.addActionListener(createDDLListener);

      jbDTDefineRelations = new JButton(DEFINE_RELATIONS);
      jbDTDefineRelations.setEnabled(false);
      jbDTDefineRelations.addActionListener(
            new ActionListener() {
               public void actionPerformed(ActionEvent ae) {
                  logger.info("Switching to Define Relations screen");
                  jfDT.setVisible(false);
                  jfDR.setVisible(true); // show the Define Relations screen
                  clearDTControls();
                  dlmDTFieldsTablesAll.removeAllElements();
               }
            });

      jpDTBottom.add(jbDTDefineRelations);
      jpDTBottom.add(jbDTCreateDDL);
      jfDT.getContentPane().add(jpDTBottom, BorderLayout.SOUTH);

      jpDTCenter = new JPanel(new GridLayout(1, 3));
      jpDTCenterRight = new JPanel(new GridLayout(1, 2));
      dlmDTTablesAll = new DefaultListModel();
      jlDTTablesAll = new JList(dlmDTTablesAll);
      jlDTTablesAll.addListSelectionListener(
            new ListSelectionListener() {
               public void valueChanged(ListSelectionEvent lse) {
                  int selIndex = jlDTTablesAll.getSelectedIndex();
                  if (selIndex >= 0) {
                     String selText = dlmDTTablesAll.getElementAt(selIndex).toString();
                     setCurrentDTTable(selText); // set pointer to the selected table
                     int[] currentNativeFields = currentDTTable.getNativeFieldsArray();
                     jlDTFieldsTablesAll.clearSelection();
                     dlmDTFieldsTablesAll.removeAllElements();
                     jbDTMoveUp.setEnabled(false);
                     jbDTMoveDown.setEnabled(false);
                     logger.debug("Item selection in DTTables changed to " + selText + " (" + selIndex + ")");
                     for (int fIndex = 0; fIndex < currentNativeFields.length; fIndex++) {
                        dlmDTFieldsTablesAll.addElement(getFieldName(currentNativeFields[fIndex]));
                     }
                  }
                  disableControls();
               }
            });

      dlmDTFieldsTablesAll = new DefaultListModel();
      jlDTFieldsTablesAll = new JList(dlmDTFieldsTablesAll);
      jlDTFieldsTablesAll.addListSelectionListener(
            new ListSelectionListener() {
               public void valueChanged(ListSelectionEvent lse) {
                  int selIndex = jlDTFieldsTablesAll.getSelectedIndex();
                  if (selIndex >= 0) {
                     if (selIndex == 0) {
                        jbDTMoveUp.setEnabled(false);
                     } else {
                        jbDTMoveUp.setEnabled(true);
                     }
                     if (selIndex == (dlmDTFieldsTablesAll.getSize() - 1)) {
                        jbDTMoveDown.setEnabled(false);
                     } else {
                        jbDTMoveDown.setEnabled(true);
                     }
                     String selText = dlmDTFieldsTablesAll.getElementAt(selIndex).toString();
                     setCurrentDTField(selText); // set pointer to the selected field
                     enableControls();
                     jrbDataType[currentDTField.getDataType()].setSelected(true); // select the appropriate radio
                                                                                  // button, based on value of dataType
                     if (jrbDataType[0].isSelected()) { // this is the Varchar radio button
                        jbDTVarchar.setEnabled(true); // enable the Varchar button
                        jtfDTVarchar.setText(Integer.toString(currentDTField.getVarcharValue())); // fill text field
                                                                                                  // with varcharValue
                     } else { // some radio button other than Varchar is selected
                        jtfDTVarchar.setText(""); // clear the text field
                        jbDTVarchar.setEnabled(false); // disable the button
                     }
                     logger.trace("Item selection in DTFields changed to " + selText + " (" + selIndex + ")");
                     jcheckDTPrimaryKey.setSelected(currentDTField.getIsPrimaryKey()); // clear or set Primary Key
                                                                                       // checkbox
                     jcheckDTDisallowNull.setSelected(currentDTField.getDisallowNull()); // clear or set Disallow Null
                                                                                         // checkbox
                     jtfDTDefaultValue.setText(currentDTField.getDefaultValue()); // fill text field with defaultValue
                  }
               }
            });

      jpDTMove = new JPanel(new GridLayout(2, 1));
      jbDTMoveUp = new JButton("^");
      jbDTMoveUp.setEnabled(false);
      jbDTMoveUp.addActionListener(
            new ActionListener() {
               public void actionPerformed(ActionEvent ae) {
                  int selection = jlDTFieldsTablesAll.getSelectedIndex();
                  currentDTTable.moveFieldUp(selection);
                  // repopulate Fields List
                  int[] currentNativeFields = currentDTTable.getNativeFieldsArray();
                  jlDTFieldsTablesAll.clearSelection();
                  dlmDTFieldsTablesAll.removeAllElements();
                  for (int fIndex = 0; fIndex < currentNativeFields.length; fIndex++) {
                     dlmDTFieldsTablesAll.addElement(getFieldName(currentNativeFields[fIndex]));
                  }
                  jlDTFieldsTablesAll.setSelectedIndex(selection - 1);
                  logger.debug("Moving selected item up in list");
                  dataSaved = false;
               }
            });
      jbDTMoveDown = new JButton("v");
      jbDTMoveDown.setEnabled(false);
      jbDTMoveDown.addActionListener(
            new ActionListener() {
               public void actionPerformed(ActionEvent ae) {
                  int selection = jlDTFieldsTablesAll.getSelectedIndex(); // the original selected index
                  currentDTTable.moveFieldDown(selection);
                  // repopulate Fields List
                  int[] currentNativeFields = currentDTTable.getNativeFieldsArray();
                  jlDTFieldsTablesAll.clearSelection();
                  dlmDTFieldsTablesAll.removeAllElements();
                  for (int fIndex = 0; fIndex < currentNativeFields.length; fIndex++) {
                     dlmDTFieldsTablesAll.addElement(getFieldName(currentNativeFields[fIndex]));
                  }
                  jlDTFieldsTablesAll.setSelectedIndex(selection + 1);
                  logger.debug("Moving selected item down in list");
                  dataSaved = false;
               }
            });
      jpDTMove.add(jbDTMoveUp);
      jpDTMove.add(jbDTMoveDown);

      jspDTTablesAll = new JScrollPane(jlDTTablesAll);
      jspDTFieldsTablesAll = new JScrollPane(jlDTFieldsTablesAll);
      jpDTCenter1 = new JPanel(new BorderLayout());
      jpDTCenter2 = new JPanel(new BorderLayout());
      jlabDTTables = new JLabel("All Tables", SwingConstants.CENTER);
      jlabDTFields = new JLabel("Fields List", SwingConstants.CENTER);
      jpDTCenter1.add(jlabDTTables, BorderLayout.NORTH);
      jpDTCenter2.add(jlabDTFields, BorderLayout.NORTH);
      jpDTCenter1.add(jspDTTablesAll, BorderLayout.CENTER);
      jpDTCenter2.add(jspDTFieldsTablesAll, BorderLayout.CENTER);
      jpDTCenter2.add(jpDTMove, BorderLayout.EAST);
      jpDTCenter.add(jpDTCenter1);
      jpDTCenter.add(jpDTCenter2);
      jpDTCenter.add(jpDTCenterRight);

      strDataType = EdgeField.getStrDataType(); // get the list of currently supported data types
      jrbDataType = new JRadioButton[strDataType.length]; // create array of JRadioButtons, one for each supported data
                                                          // type
      bgDTDataType = new ButtonGroup();
      jpDTCenterRight1 = new JPanel(new GridLayout(strDataType.length, 1));
      for (int i = 0; i < strDataType.length; i++) {
         jrbDataType[i] = new JRadioButton(strDataType[i]); // assign label for radio button from String array
         logger.trace(String.format("Adding data type option %s", strDataType[i]));
         jrbDataType[i].setEnabled(false);
         jrbDataType[i].addActionListener(radioListener);
         bgDTDataType.add(jrbDataType[i]);
         jpDTCenterRight1.add(jrbDataType[i]);
      }
      jpDTCenterRight.add(jpDTCenterRight1);

      jcheckDTDisallowNull = new JCheckBox("Disallow Null");
      jcheckDTDisallowNull.setEnabled(false);
      jcheckDTDisallowNull.addItemListener(
            new ItemListener() {
               public void itemStateChanged(ItemEvent ie) {
                  currentDTField.setDisallowNull(jcheckDTDisallowNull.isSelected());
                  logger.debug("Disallow null for selection set to " + jcheckDTDisallowNull.isSelected());
                  dataSaved = false;
               }
            });

      jcheckDTPrimaryKey = new JCheckBox("Primary Key");
      jcheckDTPrimaryKey.setEnabled(false);
      jcheckDTPrimaryKey.addItemListener(
            new ItemListener() {
               public void itemStateChanged(ItemEvent ie) {
                  currentDTField.setIsPrimaryKey(jcheckDTPrimaryKey.isSelected());
                  logger.debug("Primary key for selection set to " + jcheckDTPrimaryKey.isSelected());
                  dataSaved = false;
               }
            });

      jbDTDefaultValue = new JButton("Set Default Value");
      jbDTDefaultValue.setEnabled(false);
      jbDTDefaultValue.addActionListener(
            new ActionListener() {
               public void actionPerformed(ActionEvent ae) {
                  logger.debug("Performing action Set Default Value");
                  String prev = jtfDTDefaultValue.getText();
                  boolean goodData = false;
                  int i = currentDTField.getDataType();
                  do {
                     logger.trace("start of new do of do while for setting default value");
                     String result = (String) JOptionPane.showInputDialog(
                           null,
                           "Enter the default value:",
                           "Default Value",
                           JOptionPane.PLAIN_MESSAGE,
                           null,
                           null,
                           prev);

                     if ((result == null)) {
                        jtfDTDefaultValue.setText(prev);
                        logger.warn(
                              "No default value specified, value is null. Setting back to previous value " + prev);
                        return;
                     }
                     switch (i) {
                        case 0: // varchar
                           if (result.length() <= Integer.parseInt(jtfDTVarchar.getText())) {
                              jtfDTDefaultValue.setText(result);
                              logger.trace("Default varchar for selection set to " + result);
                              goodData = true;
                           } else {
                              JOptionPane.showMessageDialog(null,
                                    "The length of this value must be less than or equal to the Varchar length specified.");
                              logger.warn("Default value for selection was not set due to invalid Varchar length ("
                                    + result.length() + ") exceeding field length of ("
                                    + Integer.parseInt(jtfDTVarchar.getText()) + ")");
                           }
                           break;
                        case 1: // boolean
                           String newResult = result.toLowerCase();
                           if (newResult.equals("true") || newResult.equals("false")) {
                              jtfDTDefaultValue.setText(newResult);
                              logger.trace("Default boolean for selection set to " + newResult);
                              goodData = true;
                           } else {
                              JOptionPane.showMessageDialog(null,
                                    "You must input a valid boolean value (\"true\" or \"false\").");
                              logger.warn("Default value for selection set to invalid boolean value was given ("
                                    + newResult + ") must be true or false");
                           }
                           break;
                        case 2: // Integer
                           try {
                              int intResult = Integer.parseInt(result);
                              jtfDTDefaultValue.setText(result);
                              logger.trace("Default int for selection set to " + result);
                              goodData = true;
                           } catch (NumberFormatException nfe) {
                              JOptionPane.showMessageDialog(null, "\"" + result
                                    + "\" is not an integer or is outside the bounds of valid integer values.");
                              logger.warn(
                                    "NumberFormatException - Default value for selection set to invalid integer, could not parse "
                                          + result + " as integer");
                           }
                           break;
                        case 3: // Double
                           try {
                              double doubleResult = Double.parseDouble(result);
                              jtfDTDefaultValue.setText(result);
                              logger.trace("Default double for selection set to " + result);
                              goodData = true;
                           } catch (NumberFormatException nfe) {
                              JOptionPane.showMessageDialog(null, "\"" + result
                                    + "\" is not a double or is outside the bounds of valid double values.");
                              logger.warn(
                                    "NumberFormatException - Default value for selection set to invalid integer, could not parse "
                                          + result + " as integer");
                           }
                           break;
                        case 4: // Timestamp
                           try {
                              jtfDTDefaultValue.setText(result);
                              logger.trace("Default timestamp for selection set to " + result);
                              goodData = true;
                           } catch (Exception e) {
                              logger.warn(
                                    "Default value for selection exception occured when setting text result " + result);
                           }
                           break;
                     }
                  } while (!goodData);
                  logger.trace("end of do while for setting default value");
                  logger.trace("default value is set to " + jtfDTDefaultValue.getText());
                  int selIndex = jlDTFieldsTablesAll.getSelectedIndex();
                  if (selIndex >= 0) {
                     String selText = dlmDTFieldsTablesAll.getElementAt(selIndex).toString();
                     setCurrentDTField(selText);
                     currentDTField.setDefaultValue(jtfDTDefaultValue.getText());
                  }
                  dataSaved = false;
               }
            }); // jbDTDefaultValue.addActionListener
      jtfDTDefaultValue = new JTextField();
      jtfDTDefaultValue.setEditable(false);

      jbDTVarchar = new JButton("Set Varchar Length");
      jbDTVarchar.setEnabled(false);
      jbDTVarchar.addActionListener(
            new ActionListener() {
               public void actionPerformed(ActionEvent ae) {
                  logger.debug("Action performed to set varchar length");
                  String prev = jtfDTVarchar.getText();
                  String result = (String) JOptionPane.showInputDialog(
                        null,
                        "Enter the varchar length:",
                        "Varchar Length",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        prev);
                  if ((result == null)) {
                     jtfDTVarchar.setText(prev);
                     return;
                  }
                  int selIndex = jlDTFieldsTablesAll.getSelectedIndex();
                  int varchar;
                  try {
                     if (result.length() > 5) {
                        JOptionPane.showMessageDialog(null,
                              "Varchar length must be greater than 0 and less than or equal to 65535.");
                        jtfDTVarchar.setText(Integer.toString(EdgeField.VARCHAR_DEFAULT_LENGTH));
                        logger.warn("Varchar length specified (" + result + ") is too large");
                        return;
                     }
                     varchar = Integer.parseInt(result);
                     if (varchar > 0 && varchar <= 65535) { // max length of varchar is 255 before v5.0.3
                        jtfDTVarchar.setText(Integer.toString(varchar));
                        currentDTField.setVarcharValue(varchar);
                        logger.debug("Varchar length set to " + varchar);
                     } else {
                        JOptionPane.showMessageDialog(null,
                              "Varchar length must be greater than 0 and less than or equal to 65535.");
                        jtfDTVarchar.setText(Integer.toString(EdgeField.VARCHAR_DEFAULT_LENGTH));
                        logger.warn("Varchar length specified (" + result + ") is too large");
                        return;
                     }
                  } catch (NumberFormatException nfe) {
                     JOptionPane.showMessageDialog(null, "\"" + result + "\" is not a number");
                     jtfDTVarchar.setText(Integer.toString(EdgeField.VARCHAR_DEFAULT_LENGTH));
                     logger.warn("NumberFormatException - Varchar length specified (" + result + ") is not a number");
                     return;
                  }
                  dataSaved = false;
               }
            });
      jtfDTVarchar = new JTextField();
      jtfDTVarchar.setEditable(false);

      jpDTCenterRight2 = new JPanel(new GridLayout(6, 1));
      jpDTCenterRight2.add(jbDTVarchar);
      jpDTCenterRight2.add(jtfDTVarchar);
      jpDTCenterRight2.add(jcheckDTPrimaryKey);
      jpDTCenterRight2.add(jcheckDTDisallowNull);
      jpDTCenterRight2.add(jbDTDefaultValue);
      jpDTCenterRight2.add(jtfDTDefaultValue);
      jpDTCenterRight.add(jpDTCenterRight1);
      jpDTCenterRight.add(jpDTCenterRight2);
      jpDTCenter.add(jpDTCenterRight);
      jfDT.getContentPane().add(jpDTCenter, BorderLayout.CENTER);
      jfDT.validate();
      logger.trace("createDTScreen end");
   } // createDTScreen

   public void createDRScreen() {
      // create Define Relations screen
      jfDR = new JFrame(DEFINE_RELATIONS);
      jfDR.setSize(HORIZ_SIZE, VERT_SIZE);
      jfDR.setLocation(HORIZ_LOC, VERT_LOC);
      jfDR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      jfDR.addWindowListener(edgeWindowListener);
      jfDR.getContentPane().setLayout(new BorderLayout());

      // setup menubars and menus
      jmbDRMenuBar = new JMenuBar();
      jfDR.setJMenuBar(jmbDRMenuBar);
      jmDRFile = new JMenu("File");
      jmDRFile.setMnemonic(KeyEvent.VK_F);
      jmbDRMenuBar.add(jmDRFile);
      jmiDROpenEdge = new JMenuItem("Open Edge File");
      jmiDROpenEdge.setMnemonic(KeyEvent.VK_E);
      jmiDROpenEdge.addActionListener(menuListener);
      jmiDROpenSave = new JMenuItem("Open Save File");
      jmiDROpenSave.setMnemonic(KeyEvent.VK_V);
      jmiDROpenSave.addActionListener(menuListener);
      jmiDRSave = new JMenuItem("Save");
      jmiDRSave.setMnemonic(KeyEvent.VK_S);
      jmiDRSave.setEnabled(false);
      jmiDRSave.addActionListener(menuListener);
      jmiDRSaveAs = new JMenuItem("Save As...");
      jmiDRSaveAs.setMnemonic(KeyEvent.VK_A);
      jmiDRSaveAs.setEnabled(false);
      jmiDRSaveAs.addActionListener(menuListener);
      jmiDRExit = new JMenuItem("Exit");
      jmiDRExit.setMnemonic(KeyEvent.VK_X);
      jmiDRExit.addActionListener(menuListener);
      jmDRFile.add(jmiDROpenEdge);
      jmDRFile.add(jmiDROpenSave);
      jmDRFile.add(jmiDRSave);
      jmDRFile.add(jmiDRSaveAs);
      jmDRFile.add(jmiDRExit);

      jmDROptions = new JMenu("Options");
      jmDROptions.setMnemonic(KeyEvent.VK_O);
      jmbDRMenuBar.add(jmDROptions);
      jmiDROptionsOutputLocation = new JMenuItem("Set Output File Definition Location");
      jmiDROptionsOutputLocation.setMnemonic(KeyEvent.VK_S);
      jmiDROptionsOutputLocation.addActionListener(menuListener);
      jmiDROptionsShowProducts = new JMenuItem("Show Database Products Available");
      jmiDROptionsShowProducts.setMnemonic(KeyEvent.VK_H);
      jmiDROptionsShowProducts.setEnabled(false);
      jmiDROptionsShowProducts.addActionListener(menuListener);
      jmDROptions.add(jmiDROptionsOutputLocation);
      jmDROptions.add(jmiDROptionsShowProducts);

      jmDRHelp = new JMenu("Help");
      jmDRHelp.setMnemonic(KeyEvent.VK_H);
      jmbDRMenuBar.add(jmDRHelp);
      jmiDRHelpAbout = new JMenuItem("About");
      jmiDRHelpAbout.setMnemonic(KeyEvent.VK_A);
      jmiDRHelpAbout.addActionListener(menuListener);
      jmDRHelp.add(jmiDRHelpAbout);

      jpDRCenter = new JPanel(new GridLayout(2, 2));
      jpDRCenter1 = new JPanel(new BorderLayout());
      jpDRCenter2 = new JPanel(new BorderLayout());
      jpDRCenter3 = new JPanel(new BorderLayout());
      jpDRCenter4 = new JPanel(new BorderLayout());

      dlmDRTablesRelations = new DefaultListModel();
      jlDRTablesRelations = new JList(dlmDRTablesRelations);
      jlDRTablesRelations.addListSelectionListener(
            new ListSelectionListener() {
               public void valueChanged(ListSelectionEvent lse) {
                  int selIndex = jlDRTablesRelations.getSelectedIndex();
                  if (selIndex >= 0) {
                     String selText = dlmDRTablesRelations.getElementAt(selIndex).toString();
                     setCurrentDRTable1(selText);
                     logger.trace("DRTable1 selection set to " + selText);
                     int[] currentNativeFields, currentRelatedTables, currentRelatedFields;
                     currentNativeFields = currentDRTable1.getNativeFieldsArray();
                     currentRelatedTables = currentDRTable1.getRelatedTablesArray();
                     jlDRFieldsTablesRelations.clearSelection();
                     jlDRTablesRelatedTo.clearSelection();
                     jlDRFieldsTablesRelatedTo.clearSelection();
                     dlmDRFieldsTablesRelations.removeAllElements();
                     dlmDRTablesRelatedTo.removeAllElements();
                     dlmDRFieldsTablesRelatedTo.removeAllElements();
                     for (int fIndex = 0; fIndex < currentNativeFields.length; fIndex++) {
                        dlmDRFieldsTablesRelations.addElement(getFieldName(currentNativeFields[fIndex]));
                     }
                     for (int rIndex = 0; rIndex < currentRelatedTables.length; rIndex++) {
                        dlmDRTablesRelatedTo.addElement(getTableName(currentRelatedTables[rIndex]));
                     }
                  }
               }
            });

      dlmDRFieldsTablesRelations = new DefaultListModel();
      jlDRFieldsTablesRelations = new JList(dlmDRFieldsTablesRelations);
      jlDRFieldsTablesRelations.addListSelectionListener(
            new ListSelectionListener() {
               public void valueChanged(ListSelectionEvent lse) {
                  int selIndex = jlDRFieldsTablesRelations.getSelectedIndex();
                  if (selIndex >= 0) {
                     String selText = dlmDRFieldsTablesRelations.getElementAt(selIndex).toString();
                     setCurrentDRField1(selText);
                     logger.trace("DRField1 selection set to " + selText);
                     if (currentDRField1.getFieldBound() == 0) {
                        jlDRTablesRelatedTo.clearSelection();
                        jlDRFieldsTablesRelatedTo.clearSelection();
                        dlmDRFieldsTablesRelatedTo.removeAllElements();
                     } else {
                        logger.trace("DRField1 selection cleared");
                        jlDRTablesRelatedTo.setSelectedValue(getTableName(currentDRField1.getTableBound()), true);
                        jlDRFieldsTablesRelatedTo.setSelectedValue(getFieldName(currentDRField1.getFieldBound()), true);
                     }
                  }
               }
            });

      dlmDRTablesRelatedTo = new DefaultListModel();
      jlDRTablesRelatedTo = new JList(dlmDRTablesRelatedTo);
      jlDRTablesRelatedTo.addListSelectionListener(
            new ListSelectionListener() {
               public void valueChanged(ListSelectionEvent lse) {
                  int selIndex = jlDRTablesRelatedTo.getSelectedIndex();
                  if (selIndex >= 0) {
                     String selText = dlmDRTablesRelatedTo.getElementAt(selIndex).toString();
                     setCurrentDRTable2(selText);
                     logger.trace("DRTable2 selection set to " + selText);
                     int[] currentNativeFields = currentDRTable2.getNativeFieldsArray();
                     dlmDRFieldsTablesRelatedTo.removeAllElements();
                     for (int fIndex = 0; fIndex < currentNativeFields.length; fIndex++) {
                        dlmDRFieldsTablesRelatedTo.addElement(getFieldName(currentNativeFields[fIndex]));
                     }
                  }
               }
            });

      dlmDRFieldsTablesRelatedTo = new DefaultListModel();
      jlDRFieldsTablesRelatedTo = new JList(dlmDRFieldsTablesRelatedTo);
      jlDRFieldsTablesRelatedTo.addListSelectionListener(
            new ListSelectionListener() {
               public void valueChanged(ListSelectionEvent lse) {
                  int selIndex = jlDRFieldsTablesRelatedTo.getSelectedIndex();
                  if (selIndex >= 0) {
                     String selText = dlmDRFieldsTablesRelatedTo.getElementAt(selIndex).toString();
                     setCurrentDRField2(selText);
                     logger.trace("DRField2 selection set to " + selText);
                     jbDRBindRelation.setEnabled(true);
                  } else {
                     logger.trace("DRField2 selection cleared");
                     jbDRBindRelation.setEnabled(false);
                  }
               }
            });

      jspDRTablesRelations = new JScrollPane(jlDRTablesRelations);
      jspDRFieldsTablesRelations = new JScrollPane(jlDRFieldsTablesRelations);
      jspDRTablesRelatedTo = new JScrollPane(jlDRTablesRelatedTo);
      jspDRFieldsTablesRelatedTo = new JScrollPane(jlDRFieldsTablesRelatedTo);
      jlabDRTablesRelations = new JLabel("Tables With Relations", SwingConstants.CENTER);
      jlabDRFieldsTablesRelations = new JLabel("Fields in Tables with Relations", SwingConstants.CENTER);
      jlabDRTablesRelatedTo = new JLabel("Related Tables", SwingConstants.CENTER);
      jlabDRFieldsTablesRelatedTo = new JLabel("Fields in Related Tables", SwingConstants.CENTER);
      jpDRCenter1.add(jlabDRTablesRelations, BorderLayout.NORTH);
      jpDRCenter2.add(jlabDRFieldsTablesRelations, BorderLayout.NORTH);
      jpDRCenter3.add(jlabDRTablesRelatedTo, BorderLayout.NORTH);
      jpDRCenter4.add(jlabDRFieldsTablesRelatedTo, BorderLayout.NORTH);
      jpDRCenter1.add(jspDRTablesRelations, BorderLayout.CENTER);
      jpDRCenter2.add(jspDRFieldsTablesRelations, BorderLayout.CENTER);
      jpDRCenter3.add(jspDRTablesRelatedTo, BorderLayout.CENTER);
      jpDRCenter4.add(jspDRFieldsTablesRelatedTo, BorderLayout.CENTER);
      jpDRCenter.add(jpDRCenter1);
      jpDRCenter.add(jpDRCenter2);
      jpDRCenter.add(jpDRCenter3);
      jpDRCenter.add(jpDRCenter4);
      jfDR.getContentPane().add(jpDRCenter, BorderLayout.CENTER);
      jpDRBottom = new JPanel(new GridLayout(1, 3));

      jbDRDefineTables = new JButton(DEFINE_TABLES);
      jbDRDefineTables.addActionListener(
            new ActionListener() {
               public void actionPerformed(ActionEvent ae) {
                  logger.info("Switching to Define Tables screen");
                  jfDT.setVisible(true); // show the Define Tables screen
                  jfDR.setVisible(false);
                  clearDRControls();
                  depopulateLists();
                  populateLists();
               }
            });

      jbDRBindRelation = new JButton("Bind/Unbind Relation");
      jbDRBindRelation.setEnabled(false);
      jbDRBindRelation.addActionListener(
            new ActionListener() {
               public void actionPerformed(ActionEvent ae) {
                  logger.trace("Bind/Unbind Relation event");
                  int nativeIndex = jlDRFieldsTablesRelations.getSelectedIndex();
                  int relatedField = currentDRField2.getNumFigure();
                  if (currentDRField1.getFieldBound() == relatedField) { // the selected fields are already bound to
                                                                         // each other
                     logger.debug("Showing confirmation to unbind");
                     int answer = JOptionPane.showConfirmDialog(null, "Do you wish to unbind the relation on field " +
                           currentDRField1.getName() + "?",
                           "Are you sure?", JOptionPane.YES_NO_OPTION);
                     if (answer == JOptionPane.YES_OPTION) {
                        logger.debug("Unbind accepted");
                        currentDRTable1.setRelatedField(nativeIndex, 0); // clear the related field
                        currentDRField1.setTableBound(0); // clear the bound table
                        currentDRField1.setFieldBound(0); // clear the bound field
                        jlDRFieldsTablesRelatedTo.clearSelection(); // clear the listbox selection
                     }
                     logger.trace("End Bind/Unbind Event");
                     return;
                  }
                  if (currentDRField1.getFieldBound() != 0) { // field is already bound to a different field
                     logger.debug("Showing confirmation to overwrite bind");
                     int answer = JOptionPane.showConfirmDialog(null, "There is already a relation defined on field " +
                           currentDRField1.getName() + ", do you wish to overwrite it?",
                           "Are you sure?", JOptionPane.YES_NO_OPTION);
                     if (answer == JOptionPane.NO_OPTION || answer == JOptionPane.CLOSED_OPTION) {
                        logger.debug("Cancel, revert to original settings");
                        jlDRTablesRelatedTo.setSelectedValue(getTableName(currentDRField1.getTableBound()), true); // revert
                                                                                                                   // selections
                                                                                                                   // to
                                                                                                                   // saved
                                                                                                                   // settings
                        jlDRFieldsTablesRelatedTo.setSelectedValue(getFieldName(currentDRField1.getFieldBound()), true); // revert
                                                                                                                         // selections
                                                                                                                         // to
                                                                                                                         // saved
                                                                                                                         // settings
                        logger.trace("End Bind/Unbind Event");
                        return;
                     }
                  }
                  if (currentDRField1.getDataType() != currentDRField2.getDataType()) {
                     logger.warn("Cancelling, The datatypes of " + currentDRTable1.getName() + "." +
                           currentDRField1.getName() + " and " + currentDRTable2.getName() +
                           "." + currentDRField2.getName() + " do not match.  Unable to bind this relation.");
                     JOptionPane.showMessageDialog(null, "The datatypes of " + currentDRTable1.getName() + "." +
                           currentDRField1.getName() + " and " + currentDRTable2.getName() +
                           "." + currentDRField2.getName() + " do not match.  Unable to bind this relation.");
                     logger.trace("End Bind/Unbind Event");

                     return;
                  }
                  if ((currentDRField1.getDataType() == 0) && (currentDRField2.getDataType() == 0)) {
                     if (currentDRField1.getVarcharValue() != currentDRField2.getVarcharValue()) {
                        logger.warn("Cancelling, The varchar lengths of " + currentDRTable1.getName() + "." +
                              currentDRField1.getName() + " and " + currentDRTable2.getName() +
                              "." + currentDRField2.getName() + " do not match.  Unable to bind this relation.");
                        JOptionPane.showMessageDialog(null,
                              "The varchar lengths of " + currentDRTable1.getName() + "." +
                                    currentDRField1.getName() + " and " + currentDRTable2.getName() +
                                    "." + currentDRField2.getName() + " do not match.  Unable to bind this relation.");
                        logger.trace("End Bind/Unbind Event");

                        return;
                     }
                  }
                  logger.trace("Starting bind");
                  currentDRTable1.setRelatedField(nativeIndex, relatedField);
                  currentDRField1.setTableBound(currentDRTable2.getNumFigure());
                  currentDRField1.setFieldBound(currentDRField2.getNumFigure());
                  JOptionPane.showMessageDialog(null, "Table " + currentDRTable1.getName() + ": native field " +
                        currentDRField1.getName() + " bound to table " + currentDRTable2.getName() +
                        " on field " + currentDRField2.getName());
                  logger.info("Table " + currentDRTable1.getName() + ": native field " +
                        currentDRField1.getName() + " bound to table " + currentDRTable2.getName() +
                        " on field " + currentDRField2.getName());
                  dataSaved = false;
                  logger.trace("End Bind/Unbind Event");
               }
            });

      jbDRCreateDDL = new JButton("Create DDL");
      jbDRCreateDDL.setEnabled(false);
      jbDRCreateDDL.addActionListener(createDDLListener);

      jpDRBottom.add(jbDRDefineTables);
      jpDRBottom.add(jbDRBindRelation);
      jpDRBottom.add(jbDRCreateDDL);
      jfDR.getContentPane().add(jpDRBottom, BorderLayout.SOUTH);
   } // createDRScreen

   public static void setReadSuccess(boolean value) {
      readSuccess = value;
   }

   public static boolean getReadSuccess() {
      return readSuccess;
   }

   private void setCurrentDTTable(String selText) {
      for (int tIndex = 0; tIndex < tables.length; tIndex++) {
         if (selText.equals(tables[tIndex].getName())) {
            logger.trace(String.format("Setting current dt table to %s", selText));
            currentDTTable = tables[tIndex];
            return;
         }
      }
   }

   private void setCurrentDTField(String selText) {
      for (int fIndex = 0; fIndex < fields.length; fIndex++) {
         if (selText.equals(fields[fIndex].getName()) && fields[fIndex].getTableID() == currentDTTable.getNumFigure()) {
            logger.trace(String.format("Setting current dt field to %s", selText));
            currentDTField = fields[fIndex];
            return;
         }
      }
   }

   private void setCurrentDRTable1(String selText) {
      for (int tIndex = 0; tIndex < tables.length; tIndex++) {
         if (selText.equals(tables[tIndex].getName())) {
            logger.trace(String.format("Setting current table1 to %s", selText));
            currentDRTable1 = tables[tIndex];
            return;
         }
      }
      logger.warn(String.format("Could not find %s in availalble tables", selText));
   }

   private void setCurrentDRTable2(String selText) {
      for (int tIndex = 0; tIndex < tables.length; tIndex++) {
         if (selText.equals(tables[tIndex].getName())) {
            logger.trace(String.format("Setting current table2 to %s", selText));
            currentDRTable2 = tables[tIndex];
            return;
         }
      }
   }

   private void setCurrentDRField1(String selText) {
      for (int fIndex = 0; fIndex < fields.length; fIndex++) {
         if (selText.equals(fields[fIndex].getName()) &&
               fields[fIndex].getTableID() == currentDRTable1.getNumFigure()) {
            logger.trace(String.format("Setting current field1 to %s", selText));
            currentDRField1 = fields[fIndex];
            return;
         }
      }
   }

   private void setCurrentDRField2(String selText) {
      for (int fIndex = 0; fIndex < fields.length; fIndex++) {
         if (selText.equals(fields[fIndex].getName()) &&
               fields[fIndex].getTableID() == currentDRTable2.getNumFigure()) {
            logger.trace(String.format("Setting current field2 to %s", selText));
            currentDRField2 = fields[fIndex];
            return;
         }
      }
   }

   private String getTableName(int numFigure) {
      for (int tIndex = 0; tIndex < tables.length; tIndex++) {
         if (tables[tIndex].getNumFigure() == numFigure) {
            logger.trace(String.format("Getting field name %b sucess, return %s", numFigure, tables[tIndex].getName()));
            return tables[tIndex].getName();
         }
      }
      return "";
   }

   private String getFieldName(int numFigure) {
      for (int fIndex = 0; fIndex < fields.length; fIndex++) {
         if (fields[fIndex].getNumFigure() == numFigure) {
            logger.trace(String.format("Getting field name %b sucess, return %s", numFigure, fields[fIndex].getName()));
            return fields[fIndex].getName();
         }
      }
      return "";
   }

   private void enableControls() {
      logger.trace("Enabling controls");
      for (int i = 0; i < strDataType.length; i++) {
         jrbDataType[i].setEnabled(true);
      }
      jcheckDTPrimaryKey.setEnabled(true);
      jcheckDTDisallowNull.setEnabled(true);
      jbDTVarchar.setEnabled(true);
      jbDTDefaultValue.setEnabled(true);
   }

   private void disableControls() {
      logger.trace("Disabling controls");
      for (int i = 0; i < strDataType.length; i++) {
         jrbDataType[i].setEnabled(false);
      }
      jcheckDTPrimaryKey.setEnabled(false);
      jcheckDTDisallowNull.setEnabled(false);
      jbDTDefaultValue.setEnabled(false);
      jtfDTVarchar.setText("");
      jtfDTDefaultValue.setText("");
   }

   private void clearDTControls() {
      logger.trace("Clearing DT controls");
      jlDTTablesAll.clearSelection();
      jlDTFieldsTablesAll.clearSelection();
   }

   private void clearDRControls() {
      logger.trace("Clearing DR controls");
      jlDRTablesRelations.clearSelection();
      jlDRTablesRelatedTo.clearSelection();
      jlDRFieldsTablesRelations.clearSelection();
      jlDRFieldsTablesRelatedTo.clearSelection();
   }

   private void depopulateLists() {
      logger.trace("Depopulating lists");
      dlmDTTablesAll.clear();
      dlmDTFieldsTablesAll.clear();
      dlmDRTablesRelations.clear();
      dlmDRFieldsTablesRelations.clear();
      dlmDRTablesRelatedTo.clear();
      dlmDRFieldsTablesRelatedTo.clear();
   }

   private void populateLists() {
      if (readSuccess) {
         jfDT.setVisible(true);
         jfDR.setVisible(false);
         disableControls();
         depopulateLists();
         for (int tIndex = 0; tIndex < tables.length; tIndex++) {
            String tempName = tables[tIndex].getName();
            dlmDTTablesAll.addElement(tempName);
            int[] relatedTables = tables[tIndex].getRelatedTablesArray();
            if (relatedTables.length > 0) {
               dlmDRTablesRelations.addElement(tempName);
            }
         }
      }
      readSuccess = true;
   }

   private void saveAs() {
      logger.trace("Starting saveAs");
      int returnVal;
      jfcEdge.addChoosableFileFilter(effSave);
      returnVal = jfcEdge.showSaveDialog(null);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
         saveFile = jfcEdge.getSelectedFile();
         logger.trace("Save file selected " + saveFile.getName());
         if (saveFile.exists()) {
            logger.debug("Showing overwrite prompt");
            int response = JOptionPane.showConfirmDialog(null, "Overwrite existing file?", "Confirm Overwrite",
                  JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.CANCEL_OPTION) {
               logger.debug("Aborting overwrite");
               return;
            }
         }
         if (!saveFile.getName().endsWith("sav")) {
            logger.debug("Filename specified does not end in sav, appending");
            String temp = saveFile.getAbsolutePath() + ".sav";
            saveFile = new File(temp);
         }
         jmiDTSave.setEnabled(true);
         truncatedFilename = saveFile.getName().substring(saveFile.getName().lastIndexOf(File.separator) + 1);
         jfDT.setTitle(DEFINE_TABLES + " - " + truncatedFilename);
         jfDR.setTitle(DEFINE_RELATIONS + " - " + truncatedFilename);
         logger.debug("Filename set to " + truncatedFilename);
      } else {
         logger.debug("Abort saveAs");
         return;
      }
      logger.trace("End saveAs");
      writeSave();
   }

   private void writeSave() {
      logger.trace("Starting writeSave");
      if (saveFile != null) {
         try {
            logger.trace("Starting PrintWriter");
            pw = new PrintWriter(new BufferedWriter(new FileWriter(saveFile, false)));
            // write the identification line
            pw.println(EdgeConvertFile.SAVE_ID);
            // write the tables
            logger.trace("Writing tables qty: " + tables.length);
            pw.println("#Tables#");
            for (int i = 0; i < tables.length; i++) {
               pw.println(tables[i]);
            }
            // write the fields
            logger.trace("Writing fields qty: " + fields.length);
            pw.println("#Fields#");
            for (int i = 0; i < fields.length; i++) {
               pw.println(fields[i]);
            }
            // close the file
            pw.close();
            logger.trace("File closed. Save completed");
         } catch (IOException ioe) {
            logger.error("IOException inside writeSave");
            logger.error(ioe);
            System.out.println(ioe);
         }
         dataSaved = true;
      } else {
         logger.error("No saveFile specified, no data has been saved");
      }
   }

   private void setOutputDir() {
      logger.trace("set output dir starting");
      int returnVal;
      outputDirOld = outputDir;
      alSubclasses = new ArrayList();
      alProductNames = new ArrayList();

      logger.trace("Opening file picker");
      returnVal = jfcOutputDir.showOpenDialog(null);

      if (returnVal == JFileChooser.CANCEL_OPTION) {
         logger.trace("Cancelling set output dir");
         return;
      }

      if (returnVal == JFileChooser.APPROVE_OPTION) {
         outputDir = jfcOutputDir.getSelectedFile();
         logger.debug("Setting outputDir as " + outputDir.getName());
      }

      getOutputClasses();

      if (alProductNames.size() == 0) {
         logger.warn(
               "No valid output definitions found in " + outputDir.getName() + ". Restoring output dir to previous");
         JOptionPane.showMessageDialog(null,
               "The path:\n" + outputDir + "\ncontains no valid output definition files.");
         outputDir = outputDirOld;
         return;
      }

      if ((parseFile != null || saveFile != null) && outputDir != null) {
         logger.trace("Enabling create DLL");
         jbDTCreateDDL.setEnabled(true);
         jbDRCreateDDL.setEnabled(true);
      }

      JOptionPane.showMessageDialog(null,
            "The available products to create DDL statements are:\n" + displayProductNames());
      logger.trace("Presenting DLL products");
      logger.info("DLL products available: " + displayProductNames());
      jmiDTOptionsShowProducts.setEnabled(true);
      jmiDROptionsShowProducts.setEnabled(true);
   }

   private String displayProductNames() {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < productNames.length; i++) {
         sb.append(productNames[i] + "\n");
      }
      return sb.toString();
   }

   private void getOutputClasses() {
      logger.trace("getOutputClasses starting");
      File[] resultFiles = {};
      Class resultClass = null;
      Class[] paramTypes = { EdgeTable[].class, EdgeField[].class };
      Class[] paramTypesNull = {};
      Constructor conResultClass;
      Object[] args = { tables, fields };
      Object objOutput = null;

      String classLocation = EdgeConvertGUI.class.getResource("EdgeConvertGUI.class").toString();
      if (classLocation.startsWith("jar:")) {
         logger.trace("Jar detected, converting to files");
         String jarfilename = classLocation.replaceFirst("^.*:", "").replaceFirst("!.*$", "");
         System.out.println("Jarfile: " + jarfilename);
         try (JarFile jarfile = new JarFile(jarfilename)) {
            ArrayList<File> filenames = new ArrayList<>();
            for (JarEntry e : Collections.list(jarfile.entries())) {
               filenames.add(new File(e.getName()));
            }
            resultFiles = filenames.toArray(new File[0]);
         } catch (IOException ioe) {
            logger.error("IOException in getOutputClasses");
            logger.error(ioe);
            throw new RuntimeException(ioe);
         }
      } else {
         resultFiles = outputDir.listFiles();
      }
      alProductNames.clear();
      alSubclasses.clear();
      try {
         for (int i = 0; i < resultFiles.length; i++) {
            logger.trace("Checking " + resultFiles[i].getName());
            if (!resultFiles[i].getName().endsWith(".class")) {
               logger.trace("Not a class file, ignoring");
               continue; // ignore all files that are not .class files
            }
            resultClass = Class
                  .forName(resultFiles[i].getName().substring(0, resultFiles[i].getName().lastIndexOf(".")));
            logger.trace("Class file found. " + resultClass.getName());
            if (resultClass.getSuperclass().getName().equals("EdgeConvertCreateDDL")) { // only interested in classes
                                                                                        // that extend
                                                                                        // EdgeConvertCreateDDL
               logger.trace("Class file extends EdgeConvertDLL.");
               if (parseFile == null && saveFile == null) {
                  conResultClass = resultClass.getConstructor(paramTypesNull);
                  objOutput = conResultClass.newInstance(null);
               } else {
                  conResultClass = resultClass.getConstructor(paramTypes);
                  objOutput = conResultClass.newInstance(args);
               }
               alSubclasses.add(objOutput);
               Method getProductName = resultClass.getMethod("getProductName", null);
               String productName = (String) getProductName.invoke(objOutput, null);
               logger.trace("Adding " + productName + " as an available product");
               alProductNames.add(productName);
            }
         }
      } catch (InstantiationException ie) {
         logger.error("InstantiationException in getOutputClasses");
         logger.error(ie);
         ie.printStackTrace();
      } catch (ClassNotFoundException cnfe) {
         logger.error("ClassNotFoundException in getOutputClasses");
         logger.error(cnfe);
         cnfe.printStackTrace();
      } catch (IllegalAccessException iae) {
         logger.error("IllegalAccessException in getOutputClasses");
         logger.error(iae);
         iae.printStackTrace();
      } catch (NoSuchMethodException nsme) {
         logger.error("NoSuchMethodException in getOutputClasses");
         logger.error(nsme);
         nsme.printStackTrace();
      } catch (InvocationTargetException ite) {
         logger.error("InvocationTargetException in getOutputClasses");
         logger.error(ite);
         ite.printStackTrace();
      }
      if (alProductNames.size() > 0 && alSubclasses.size() > 0) { // do not recreate productName and objSubClasses
                                                                  // arrays if the new path is empty of valid files
         productNames = (String[]) alProductNames.toArray(new String[alProductNames.size()]);
         objSubclasses = (Object[]) alSubclasses.toArray(new Object[alSubclasses.size()]);
      }
      logger.trace("End getOutputClasses");
   }

   private String getSQLStatements() {
      logger.trace("Start getSQLStatements");
      String strSQLString = "";
      String response = (String) JOptionPane.showInputDialog(
            null,
            "Select a product:",
            "Create DDL",
            JOptionPane.PLAIN_MESSAGE,
            null,
            productNames,
            null);

      if (response == null) {
         logger.warn("No response. Cancelling getSQLStatements");
         return EdgeConvertGUI.CANCELLED;
      }

      int selected;
      for (selected = 0; selected < productNames.length; selected++) {
         if (response.equals(productNames[selected])) {
            break;
         }
      }
      logger.trace("Trying to invoke class to getSQLString and database name");
      try {
         Class selectedSubclass = objSubclasses[selected].getClass();
         Method getSQLString = selectedSubclass.getMethod("getSQLString", null);
         Method getDatabaseName = selectedSubclass.getMethod("getDatabaseName", null);
         strSQLString = (String) getSQLString.invoke(objSubclasses[selected], null);
         databaseName = (String) getDatabaseName.invoke(objSubclasses[selected], null);
      } catch (IllegalAccessException iae) {
         logger.error("IllegalAccessException in getSQLStatements");
         logger.error(iae);
         iae.printStackTrace();
      } catch (NoSuchMethodException nsme) {
         logger.error("NoSuchMethodException in getSQLStatements");
         logger.error(nsme);
         nsme.printStackTrace();
      } catch (InvocationTargetException ite) {
         logger.error("InvocationTargetException in getSQLStatements");
         logger.error(ite);
         ite.printStackTrace();
      }
      logger.debug("getSQLStatements returned with length " + strSQLString.length());
      logger.trace("getSQLStatements returned with \n" + strSQLString);
      return strSQLString;
   }

   private void writeSQL(String output) {
      logger.trace("starting writeSQL with output " + output);
      jfcEdge.resetChoosableFileFilters();
      String str;
      if (parseFile != null) {
         logger.debug("Using parseFile path as outputFile");
         outputFile = new File(
               parseFile.getAbsolutePath().substring(0, (parseFile.getAbsolutePath().lastIndexOf(File.separator) + 1))
                     + databaseName + ".sql");
      } else {
         logger.debug("Using saveFile path as outputFile");
         outputFile = new File(
               saveFile.getAbsolutePath().substring(0, (saveFile.getAbsolutePath().lastIndexOf(File.separator) + 1))
                     + databaseName + ".sql");
      }
      logger.trace("outputFile set as " + outputFile.toString());
      if (databaseName.equals("")) {
         logger.error("writeSQL cancelled, no database name specified");
         return;
      }
      jfcEdge.setSelectedFile(outputFile);
      int returnVal = jfcEdge.showSaveDialog(null);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
         outputFile = jfcEdge.getSelectedFile();
         if (outputFile.exists()) {
            logger.warn("Existing file " + outputFile.toString() + ", prompting user if they wish to overwrite");
            int response = JOptionPane.showConfirmDialog(null, "Overwrite existing file?", "Confirm Overwrite",
                  JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.CANCEL_OPTION) {
               logger.info("User cancelled. Stopping writeSQL operation without writing.");
               return;
            }
         }
         try {
            logger.trace("starting PrintWriter in writeSQL");
            pw = new PrintWriter(new BufferedWriter(new FileWriter(outputFile, false)));
            // write the SQL statements
            pw.println(output);
            // close the file
            pw.close();
            logger.trace("closing PrintWriter in writeSQL");
         } catch (IOException ioe) {
            logger.error("IOException in writing SQL");
            System.out.println(ioe);
         }
      }
      logger.trace("end writeSQL");
   }

   class EdgeRadioButtonListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         for (int i = 0; i < jrbDataType.length; i++) {
            if (jrbDataType[i].isSelected()) {
               currentDTField.setDataType(i);
               break;
            }
         }
         if (jrbDataType[0].isSelected()) {
            jtfDTVarchar.setText(Integer.toString(EdgeField.VARCHAR_DEFAULT_LENGTH));
            jbDTVarchar.setEnabled(true);
         } else {
            jtfDTVarchar.setText("");
            jbDTVarchar.setEnabled(false);
         }
         jtfDTDefaultValue.setText("");
         currentDTField.setDefaultValue("");
         dataSaved = false;
      }
   }

   class EdgeWindowListener implements WindowListener {
      public void windowActivated(WindowEvent we) {
      }

      public void windowClosed(WindowEvent we) {
      }

      public void windowDeactivated(WindowEvent we) {
      }

      public void windowDeiconified(WindowEvent we) {
      }

      public void windowIconified(WindowEvent we) {
      }

      public void windowOpened(WindowEvent we) {
      }

      public void windowClosing(WindowEvent we) {
         logger.trace("Window closing event");
         if (!dataSaved) {
            logger.debug("Unsaved data prompt showing");
            int answer = JOptionPane.showOptionDialog(null,
                  "You currently have unsaved data. Would you like to save?",
                  "Are you sure?",
                  JOptionPane.YES_NO_CANCEL_OPTION,
                  JOptionPane.QUESTION_MESSAGE,
                  null, null, null);
            if (answer == JOptionPane.YES_OPTION) {
               if (saveFile == null) {
                  logger.debug("User accepted save file");
                  saveAs();
               }
               writeSave();
            }
            if ((answer == JOptionPane.CANCEL_OPTION) || (answer == JOptionPane.CLOSED_OPTION)) {
               logger.debug("User canceled exit");
               if (we.getSource() == jfDT) {
                  jfDT.setVisible(true);
               }
               if (we.getSource() == jfDR) {
                  jfDR.setVisible(true);
               }
               return;
            }
         }
         logger.info("Exiting program via window close option");
         System.exit(0); // No was selected
      }
   }

   class CreateDDLButtonListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         logger.trace("DLL Button action performed");
         while (outputDir == null) {
            JOptionPane.showMessageDialog(null,
                  "You have not selected a path that contains valid output definition files yet.\nPlease select a path now.");
            logger.warn("No outputDir specified, prompting user to select path");
            setOutputDir();
         }
         logger.trace("outputDir = " + outputDir.toString());
         getOutputClasses(); // in case outputDir was set before a file was loaded and EdgeTable/EdgeField
                             // objects created
         sqlString = getSQLStatements();
         if (sqlString.equals(EdgeConvertGUI.CANCELLED)) {
            logger.warn("sqlString is cancelled. stopping create DLL");
            return;
         }
         writeSQL(sqlString);
      }
   }

   class EdgeMenuListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         int returnVal;
         if ((ae.getSource() == jmiDTOpenEdge) || (ae.getSource() == jmiDROpenEdge)) {
            logger.trace("Menu action = OpenEdge");
            if (!dataSaved) {
               logger.debug("Unsaved data warning prompt opening");
               int answer = JOptionPane.showConfirmDialog(null, "You currently have unsaved data. Continue?",
                     "Are you sure?", JOptionPane.YES_NO_OPTION);
               if (answer != JOptionPane.YES_OPTION) {
                  logger.debug("Canceled operation");
                  return;
               }
               logger.warn("Continuing open edge, discard prior data");
            }
            logger.trace("Opening file picker");
            jfcEdge.addChoosableFileFilter(effEdge);
            jfcEdge.addChoosableFileFilter(effSave);
            returnVal = jfcEdge.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
               try {
                  parseFile = jfcEdge.getSelectedFile();
                  logger.trace("File selected " + parseFile.getName());
                  ecfp = EdgeConvertFile.parseFile(parseFile);
                  setReadSuccess(ecfp.getParseResult());
                  tables = ecfp.getEdgeTables();
                  for (int i = 0; i < tables.length; i++) {
                     tables[i].makeArrays();
                  }
                  fields = ecfp.getEdgeFields();
                  ecfp = null;
                  populateLists();
                  saveFile = null;
                  jmiDTSave.setEnabled(false);
                  jmiDRSave.setEnabled(false);
                  jmiDTSaveAs.setEnabled(true);
                  jmiDRSaveAs.setEnabled(true);
                  jbDTDefineRelations.setEnabled(true);

                  jbDTCreateDDL.setEnabled(true);
                  jbDRCreateDDL.setEnabled(true);

                  truncatedFilename = parseFile.getName()
                        .substring(parseFile.getName().lastIndexOf(File.separator) + 1);
                  jfDT.setTitle(DEFINE_TABLES + " - " + truncatedFilename);
                  jfDR.setTitle(DEFINE_RELATIONS + " - " + truncatedFilename);
               } catch (Exception e) {
                  System.err.println(e);
                  JOptionPane.showMessageDialog(null, "Could not parse file, please try again");
               }
            } else {
               logger.warn("Cancelling OpenEdge, user selected non-confirmation option");
               return;
            }
            dataSaved = true;
         }

         if ((ae.getSource() == jmiDTSaveAs) || (ae.getSource() == jmiDRSaveAs) || (ae.getSource() == jmiDTSave)
               || (ae.getSource() == jmiDRSave)) {
            if ((ae.getSource() == jmiDTSaveAs) || (ae.getSource() == jmiDRSaveAs)) {
               logger.trace("Menu action = SaveAs");
               saveAs();
            } else {
               logger.trace("Menu action = Save");
               writeSave();
            }
         }

         if ((ae.getSource() == jmiDTExit) || (ae.getSource() == jmiDRExit)) {
            logger.trace("Menu action = Exit");
            if (!dataSaved) {
               logger.debug("Unsaved data prompt showing");
               int answer = JOptionPane.showOptionDialog(null,
                     "You currently have unsaved data. Would you like to save?",
                     "Are you sure?",
                     JOptionPane.YES_NO_CANCEL_OPTION,
                     JOptionPane.QUESTION_MESSAGE,
                     null, null, null);
               if (answer == JOptionPane.YES_OPTION) {
                  logger.debug("User accepted save file");
                  if (saveFile == null) {
                     saveAs();
                  }
               }
               if ((answer == JOptionPane.CANCEL_OPTION) || (answer == JOptionPane.CLOSED_OPTION)) {
                  logger.debug("User canceled exit");
                  return;
               }
            }
            logger.info("Exiting program via Exit menu option");
            System.exit(0); // No was selected
         }

         if ((ae.getSource() == jmiDTOptionsOutputLocation) || (ae.getSource() == jmiDROptionsOutputLocation)) {
            logger.trace("Menu action = OutputLocation");
            setOutputDir();
         }

         if ((ae.getSource() == jmiDTOptionsShowProducts) || (ae.getSource() == jmiDROptionsShowProducts)) {
            logger.trace("Menu action = ShowProducts");
            JOptionPane.showMessageDialog(null,
                  "The available products to create DDL statements are:\n" + displayProductNames());
         }

         if ((ae.getSource() == jmiDTHelpAbout) || (ae.getSource() == jmiDRHelpAbout)) {
            logger.trace("Menu action = Help/About");
            JOptionPane.showMessageDialog(null, "EdgeConvert ERD To DDL Conversion Tool\n" +
                  "by Stephen A. Capperell\n" +
                  " 2007-2008");
         }
      } // EdgeMenuListener.actionPerformed()
   } // EdgeMenuListener
} // EdgeConvertGUI
