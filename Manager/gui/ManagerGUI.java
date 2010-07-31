package gui;

import java.net.MalformedURLException;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import gui.l10n.L10n;
import manager.Manager;
import business.ManagerOptions;
import business.Mod;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.prefs.Preferences;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import org.apache.log4j.Logger;
import javax.swing.UIManager;
import business.actions.Action;
import business.actions.ActionRequirement;
import java.io.File;
import java.util.Iterator;
import javax.swing.DefaultListModel;
import utility.JarFileLoader;

/**
 * Main form of the ModManager. This class is the 'view' part of the MVC framework
 *
 * @author Shirkit, Kovo
 */
public class ManagerGUI extends javax.swing.JFrame implements Observer {
    // Model for this View (part of MVC pattern)

    private Manager controller;
    private ManagerOptions model;
    private static Logger logger = Logger.getLogger(ManagerGUI.class.getPackage().getName());
    private Preferences prefs;
    // Column names of the mod list table
    private String[] columnNames = new String[]{
        "",
        L10n.getString("table.modname"),
        L10n.getString("table.modauthor"),
        L10n.getString("table.modversion"),
        L10n.getString("table.modstatus")
    };
    private Object[][] tableData;

    /**
     * Creates the main form
     * @param model model patr of the MVC framework
     */
    public ManagerGUI(Manager controller) {
        this.model = ManagerOptions.getInstance();
        this.controller = controller;
        this.model.addObserver(this);
        // Set Look and feel (based on preferences)
        prefs = Preferences.userNodeForPackage(Manager.class);
        //String lafClass = prefs.get(model.PREFS_LAF, "DUMMY_DEFAULT");
        String lafClass = ManagerOptions.getInstance().getLaf();
        try {
            if (lafClass.isEmpty()) {
                // No LaF set in preferences, set default
                logger.info("Setting default look and feel");
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } else {
                logger.info("Setting look and feel to: " + lafClass);
                UIManager.setLookAndFeel(lafClass);
            }
        } catch (Exception e) {
            logger.warn("Cannot change L&F: " + e.getMessage());
        }

        // Registration for Synthetica Look and Feel
        String[] li = {"Licensee=Pedro Torres", "LicenseRegistrationNumber=NCPT200729", "Product=Synthetica", "LicenseType=Non Commercial", "ExpireDate=--.--.----", "MaxVersion=2.999.999"};
        UIManager.put("Synthetica.license.info", li);
        UIManager.put("Synthetica.license.key", "644E94EB-97019D70-E7B56201-11EE0820-82B6C8DC");

        initComponents();
        // Set application icon
        try {
            URL urlImage = this.getClass().getResource("resources/icon.png");
            this.setIconImage(Toolkit.getDefaultToolkit().getImage(urlImage));
            dialogOptions.setIconImage(Toolkit.getDefaultToolkit().getImage(urlImage));
        } catch (Exception e) {
            logger.warn("Cannot find application icon");
        }
        // Set model of the language combo box. This will not be localized
        comboBoxChooseLanguage.addItem(new Language("English", "en"));
        comboBoxChooseLanguage.addItem(new Language("Slovak", "sk"));
        // Set model of the LaF combobox. This will not be localized
        comboBoxLafs.addItem(new LaF("Default", "default"));
        comboBoxLafs.addItem(new LaF("JGoodies", "com.jgoodies.looks.plastic.PlasticXPLookAndFeel"));
        comboBoxLafs.addItem(new LaF("Napkin", "net.sourceforge.napkinlaf.NapkinLookAndFeel"));
        comboBoxLafs.addItem(new LaF("Tonic", "com.digitprop.tonic.TonicLookAndFeel"));
        comboBoxLafs.addItem(new LaF("Synthetica Standard", "de.javasoft.plaf.synthetica.SyntheticaStandardLookAndFeel"));
        comboBoxLafs.addItem(new LaF("Synthetica Blue Steel", "de.javasoft.plaf.synthetica.SyntheticaBlueSteelLookAndFeel"));
        URL urls[] = {};
        JarFileLoader newLafs = new JarFileLoader(urls);
        try {
            newLafs.addFile("C:/Users/Shirkit/Documents/NetBeansProjects/Manager/laf/syntheticaBlackEye.jar");
            try {
                if (new File("C:\\Users\\Shirkit\\Documents\\NetBeansProjects\\Manager\\laf\\syntheticaBlackEye.jar").exists()) {
                    Class.forName("de.javasoft.plaf.synthetica.SyntheticaBlackEyeLookAndFeel");
                }
            } catch (ClassNotFoundException ex) {
                System.err.println("fail");
            }
        } catch (MalformedURLException ex) {
            java.util.logging.Logger.getLogger(ManagerGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        comboBoxLafs.addItem(new LaF("Synthetica Black Eye", "de.javasoft.plaf.synthetica.SyntheticaBlackEyeLookAndFeel"));
        // Components on the Mod details panel are not visible by default
        setDetailsVisible(false);
        // This thing here is working along with formComponentShown to solve the fucking bug of not showing the correct size when running the app
        this.setResizable(false);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dialogOptions = new javax.swing.JDialog();
        labelHonFolder = new javax.swing.JLabel();
        textFieldHonFolder = new javax.swing.JTextField();
        buttonHonFolder = new javax.swing.JButton();
        labelSelectLaf = new javax.swing.JLabel();
        comboBoxLafs = new javax.swing.JComboBox();
        buttonApplyLaf = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();
        buttonOk = new javax.swing.JButton();
        labelChooseLanguage = new javax.swing.JLabel();
        comboBoxChooseLanguage = new javax.swing.JComboBox();
        labelCLArguments = new javax.swing.JLabel();
        textFieldCLArguments = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        panelModList = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableModList = new javax.swing.JTable();
        buttonApplyMods = new javax.swing.JButton();
        buttonAddMod = new javax.swing.JButton();
        panelModDetails = new javax.swing.JPanel();
        labelModIcon = new javax.swing.JLabel();
        labelModName = new javax.swing.JLabel();
        labelModAuthor = new javax.swing.JLabel();
        panelModDescription = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        areaModDesc = new javax.swing.JTextArea();
        labelRequirements = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        listRequirements = new javax.swing.JList();
        labelVisitWebsite = new javax.swing.JLabel();
        labelModStatus = new javax.swing.JLabel();
        buttonEnableMod = new javax.swing.JButton();
        buttonUpdateMod = new javax.swing.JButton();
        mainMenu = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        itemApplyMods = new javax.swing.JMenuItem();
        itemApplyAndLaunch = new javax.swing.JMenuItem();
        itemUnapplyAllMods = new javax.swing.JMenuItem();
        itemOpenModFolder = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        itemExit = new javax.swing.JMenuItem();
        menuOptions = new javax.swing.JMenu();
        itemOpenPreferences = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        itemVisitForumThread = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        itemAbout = new javax.swing.JMenuItem();

        dialogOptions.setTitle(L10n.getString("prefs.dialog.title"));
        dialogOptions.setModal(true);
        dialogOptions.setName("dialogOptions"); // NOI18N

        labelHonFolder.setText(L10n.getString("prefs.label.honfolder"));
        labelHonFolder.setName("labelHonFolder"); // NOI18N

        textFieldHonFolder.setName("textFieldHonFolder"); // NOI18N

        buttonHonFolder.setText(L10n.getString("prefs.button.honfolder"));
        buttonHonFolder.setName("buttonHonFolder"); // NOI18N

        labelSelectLaf.setText(L10n.getString("prefs.label.lookandfeel"));
        labelSelectLaf.setName("labelSelectLaf"); // NOI18N

        comboBoxLafs.setName("comboBoxLafs"); // NOI18N

        buttonApplyLaf.setText(L10n.getString("prefs.button.lookandfeel"));
        buttonApplyLaf.setName("buttonApplyLaf"); // NOI18N

        buttonCancel.setText(L10n.getString("button.cancel"));
        buttonCancel.setName("buttonCancel"); // NOI18N

        buttonOk.setText(L10n.getString("button.ok"));
        buttonOk.setName("buttonOk"); // NOI18N

        labelChooseLanguage.setText(L10n.getString("prefs.label.chooselanguage"));
        labelChooseLanguage.setName("labelChooseLanguage"); // NOI18N

        comboBoxChooseLanguage.setName("comboBoxChooseLanguage"); // NOI18N

        labelCLArguments.setText(L10n.getString("prefs.label.clarguments"));
        labelCLArguments.setName("labelCLArguments"); // NOI18N

        textFieldCLArguments.setName("textFieldCLArguments"); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel1.setText(L10n.getString("prefs.label.languagechanges"));
        jLabel1.setName("jLabel1"); // NOI18N

        javax.swing.GroupLayout dialogOptionsLayout = new javax.swing.GroupLayout(dialogOptions.getContentPane());
        dialogOptions.getContentPane().setLayout(dialogOptionsLayout);
        dialogOptionsLayout.setHorizontalGroup(
            dialogOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogOptionsLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(dialogOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogOptionsLayout.createSequentialGroup()
                        .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogOptionsLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(dialogOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogOptionsLayout.createSequentialGroup()
                                .addGroup(dialogOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelSelectLaf)
                                    .addComponent(labelCLArguments)
                                    .addComponent(labelChooseLanguage)
                                    .addComponent(labelHonFolder))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(dialogOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(comboBoxChooseLanguage, javax.swing.GroupLayout.Alignment.LEADING, 0, 207, Short.MAX_VALUE)
                                    .addComponent(comboBoxLafs, javax.swing.GroupLayout.Alignment.LEADING, 0, 207, Short.MAX_VALUE)
                                    .addComponent(textFieldCLArguments, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                                    .addComponent(textFieldHonFolder, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(dialogOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(buttonApplyLaf, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(buttonHonFolder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(dialogOptionsLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        dialogOptionsLayout.setVerticalGroup(
            dialogOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogOptionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dialogOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelHonFolder)
                    .addComponent(textFieldHonFolder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonHonFolder))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dialogOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelCLArguments)
                    .addComponent(textFieldCLArguments, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dialogOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelSelectLaf)
                    .addComponent(comboBoxLafs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonApplyLaf))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dialogOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelChooseLanguage)
                    .addComponent(comboBoxChooseLanguage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addGroup(dialogOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonCancel)
                    .addComponent(buttonOk))
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(L10n.getString("application.title"));
        setMinimumSize(new java.awt.Dimension(700, 450));
        setName("Form"); // NOI18N
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        panelModList.setMinimumSize(new java.awt.Dimension(400, 250));
        panelModList.setName("panelModList"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tableModList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Name", "Author", "Version", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableModList.setName("tableModList"); // NOI18N
        jScrollPane1.setViewportView(tableModList);

        buttonApplyMods.setText(L10n.getString("button.applymods"));
        buttonApplyMods.setName("buttonApplyMods"); // NOI18N

        buttonAddMod.setText(L10n.getString("button.addmod"));
        buttonAddMod.setName("buttonAddMod"); // NOI18N

        panelModDetails.setBorder(javax.swing.BorderFactory.createTitledBorder(" "+L10n.getString("panel.details.label")+" "));
        panelModDetails.setMinimumSize(new java.awt.Dimension(0, 250));
        panelModDetails.setName("panelModDetails"); // NOI18N
        panelModDetails.setPreferredSize(new java.awt.Dimension(255, 420));

        labelModIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/resources/icon.png"))); // NOI18N
        labelModIcon.setName("labelModIcon"); // NOI18N

        labelModName.setFont(labelModName.getFont().deriveFont(labelModName.getFont().getStyle() | java.awt.Font.BOLD, labelModName.getFont().getSize()+3));
        labelModName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelModName.setText("mod name");
        labelModName.setName("labelModName"); // NOI18N

        labelModAuthor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelModAuthor.setText("mod author");
        labelModAuthor.setName("labelModAuthor"); // NOI18N

        panelModDescription.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        panelModDescription.setName("panelModDescription"); // NOI18N
        panelModDescription.setOpaque(false);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jScrollPane2.setName("jScrollPane2"); // NOI18N

        areaModDesc.setBackground(new java.awt.Color(212, 208, 200));
        areaModDesc.setColumns(20);
        areaModDesc.setEditable(false);
        areaModDesc.setFont(buttonAddMod.getFont());
        areaModDesc.setLineWrap(true);
        areaModDesc.setRows(5);
        areaModDesc.setText("mod desc");
        areaModDesc.setWrapStyleWord(true);
        areaModDesc.setAutoscrolls(false);
        areaModDesc.setMargin(new java.awt.Insets(5, 5, 5, 5));
        areaModDesc.setName("areaModDesc"); // NOI18N
        jScrollPane2.setViewportView(areaModDesc);

        labelRequirements.setFont(labelRequirements.getFont().deriveFont(labelRequirements.getFont().getStyle() | java.awt.Font.BOLD));
        labelRequirements.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelRequirements.setText(L10n.getString("label.requires"));
        labelRequirements.setName("labelRequirements"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        listRequirements.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        listRequirements.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listRequirements.setName("listRequirements"); // NOI18N
        listRequirements.setSelectionBackground(new java.awt.Color(212, 208, 200));
        listRequirements.setSelectionForeground(new java.awt.Color(51, 51, 51));
        jScrollPane3.setViewportView(listRequirements);

        javax.swing.GroupLayout panelModDescriptionLayout = new javax.swing.GroupLayout(panelModDescription);
        panelModDescription.setLayout(panelModDescriptionLayout);
        panelModDescriptionLayout.setHorizontalGroup(
            panelModDescriptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
            .addComponent(labelRequirements, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        panelModDescriptionLayout.setVerticalGroup(
            panelModDescriptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelModDescriptionLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelRequirements)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        labelVisitWebsite.setFont(labelVisitWebsite.getFont().deriveFont(labelVisitWebsite.getFont().getStyle() | java.awt.Font.BOLD, labelVisitWebsite.getFont().getSize()+1));
        labelVisitWebsite.setForeground(new java.awt.Color(51, 102, 255));
        labelVisitWebsite.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelVisitWebsite.setText(L10n.getString("label.visitwebsite"));
        labelVisitWebsite.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelVisitWebsite.setName("labelVisitWebsite"); // NOI18N

        labelModStatus.setFont(labelModStatus.getFont().deriveFont(labelModStatus.getFont().getStyle() | java.awt.Font.BOLD));
        labelModStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelModStatus.setText("mod status");
        labelModStatus.setName("labelModStatus"); // NOI18N

        buttonEnableMod.setText(L10n.getString("button.enablemod"));
        buttonEnableMod.setName("buttonEnableMod"); // NOI18N

        buttonUpdateMod.setText(L10n.getString("button.updatemod"));
        buttonUpdateMod.setName("buttonUpdateMod"); // NOI18N

        javax.swing.GroupLayout panelModDetailsLayout = new javax.swing.GroupLayout(panelModDetails);
        panelModDetails.setLayout(panelModDetailsLayout);
        panelModDetailsLayout.setHorizontalGroup(
            panelModDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelModDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelModDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelModDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelModDetailsLayout.createSequentialGroup()
                        .addComponent(labelModIcon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelModDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelModAuthor, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelModName, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelModDetailsLayout.createSequentialGroup()
                        .addComponent(buttonEnableMod, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonUpdateMod, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(labelVisitWebsite, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelModStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        panelModDetailsLayout.setVerticalGroup(
            panelModDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelModDetailsLayout.createSequentialGroup()
                .addGroup(panelModDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelModIcon)
                    .addGroup(panelModDetailsLayout.createSequentialGroup()
                        .addComponent(labelModName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelModAuthor)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelModDescription, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(11, 11, 11)
                .addComponent(labelVisitWebsite)
                .addGap(2, 2, 2)
                .addComponent(labelModStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelModDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonEnableMod)
                    .addComponent(buttonUpdateMod))
                .addContainerGap())
        );

        javax.swing.GroupLayout panelModListLayout = new javax.swing.GroupLayout(panelModList);
        panelModList.setLayout(panelModListLayout);
        panelModListLayout.setHorizontalGroup(
            panelModListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelModListLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelModListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelModListLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelModDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelModListLayout.createSequentialGroup()
                        .addComponent(buttonApplyMods, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonAddMod, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelModListLayout.setVerticalGroup(
            panelModListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelModListLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelModListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelModDetails, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelModListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonApplyMods)
                    .addComponent(buttonAddMod))
                .addContainerGap())
        );

        panelModDetails.getAccessibleContext().setAccessibleParent(panelModList);

        mainMenu.setName("mainMenu"); // NOI18N

        menuFile.setMnemonic(L10n.getMnemonic("menu.file"));
        menuFile.setText(L10n.getString("menu.file"));
        menuFile.setName("menuFile"); // NOI18N

        itemApplyMods.setMnemonic(L10n.getMnemonic("menu.file.applymods"));
        itemApplyMods.setText(L10n.getString("menu.file.applymods"));
        itemApplyMods.setName("itemApplyMods"); // NOI18N
        menuFile.add(itemApplyMods);

        itemApplyAndLaunch.setMnemonic(L10n.getMnemonic("menu.file.applyandlaunch"));
        itemApplyAndLaunch.setText(L10n.getString("menu.file.applyandlaunch"));
        itemApplyAndLaunch.setName("itemApplyAndLaunch"); // NOI18N
        menuFile.add(itemApplyAndLaunch);

        itemUnapplyAllMods.setMnemonic(L10n.getMnemonic("menu.file.unapplymods"));
        itemUnapplyAllMods.setText(L10n.getString("menu.file.unapplymods"));
        itemUnapplyAllMods.setName("itemUnapplyAllMods"); // NOI18N
        menuFile.add(itemUnapplyAllMods);

        itemOpenModFolder.setMnemonic(L10n.getMnemonic("menu.file.openfolder"));
        itemOpenModFolder.setText(L10n.getString("menu.file.openfolder"));
        itemOpenModFolder.setName("itemOpenModFolder"); // NOI18N
        menuFile.add(itemOpenModFolder);

        jSeparator1.setName("jSeparator1"); // NOI18N
        menuFile.add(jSeparator1);

        itemExit.setMnemonic(L10n.getMnemonic("menu.file.exit"));
        itemExit.setText(L10n.getString("menu.file.exit"));
        itemExit.setName("itemExit"); // NOI18N
        menuFile.add(itemExit);

        mainMenu.add(menuFile);

        menuOptions.setMnemonic(L10n.getMnemonic("menu.options"));
        menuOptions.setText(L10n.getString("menu.options"));
        menuOptions.setName("menuOptions"); // NOI18N

        itemOpenPreferences.setText(L10n.getString("menu.options.preferences"));
        itemOpenPreferences.setName("itemOpenPreferences"); // NOI18N
        itemOpenPreferences.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemOpenPreferencesActionPerformed(evt);
            }
        });
        menuOptions.add(itemOpenPreferences);

        mainMenu.add(menuOptions);

        menuHelp.setMnemonic(L10n.getMnemonic("menu.help"));
        menuHelp.setText(L10n.getString("menu.help"));
        menuHelp.setName("menuHelp"); // NOI18N

        itemVisitForumThread.setMnemonic(L10n.getMnemonic("menu.help.website"));
        itemVisitForumThread.setText(L10n.getString("menu.help.website"));
        itemVisitForumThread.setName("itemVisitForumThread"); // NOI18N
        menuHelp.add(itemVisitForumThread);

        jSeparator4.setName("jSeparator4"); // NOI18N
        menuHelp.add(jSeparator4);

        itemAbout.setMnemonic(L10n.getMnemonic("menu.help.about"));
        itemAbout.setText(L10n.getString("menu.help.about"));
        itemAbout.setName("itemAbout"); // NOI18N
        itemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemAboutActionPerformed(evt);
            }
        });
        menuHelp.add(itemAbout);

        mainMenu.add(menuHelp);

        setJMenuBar(mainMenu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelModList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelModList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-616)/2, (screenSize.height-523)/2, 616, 523);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Open Preferences dialog
     */
    private void itemOpenPreferencesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemOpenPreferencesActionPerformed
        // Set values in the options dialog
        //prefs = Preferences.userNodeForPackage(L10n.class);
        // Get selected language
        //String lang = prefs.get(model.PREFS_LOCALE, "DUMMY_DEFAULT");
        String lang = ManagerOptions.getInstance().getLanguage();
        if (lang.isEmpty()) {
            comboBoxChooseLanguage.setSelectedIndex(0);
        } else {
            comboBoxChooseLanguage.setSelectedItem(new Language("Language", lang));
        }
        // Get selected Laf
        //prefs = Preferences.userNodeForPackage(Manager.class);
        //String laf = prefs.get(model.PREFS_LAF, "DUMMY_DEFAULT");
        String laf = ManagerOptions.getInstance().getLaf();
        if (laf.isEmpty()) {
            comboBoxLafs.setSelectedIndex(0);
        } else {
            comboBoxLafs.setSelectedItem(new LaF("LaF", laf));
        }
        // Get CL arguments
        //String clArgs = prefs.get(model.PREFS_CLARGUMENTS, "DUMMY_DEFAULT");
        String clArgs = ManagerOptions.getInstance().getCLArgs();
        textFieldCLArguments.setText("");
        if (clArgs.isEmpty()) {
        } else {
            textFieldCLArguments.setText(clArgs);
        }
        // Get HoN folder
        //String honFolder = prefs.get(model.PREFS_HONFOLDER, "DUMMY_DEFAULT");
        String honFolder = ManagerOptions.getInstance().getGamePath();
        if (honFolder.isEmpty()) {
            textFieldHonFolder.setText("");
        } else {
            textFieldHonFolder.setText(honFolder);
        }
        dialogOptions.setSize(500, 300);
        dialogOptions.setLocationRelativeTo(this);
        dialogOptions.setVisible(true);
    }//GEN-LAST:event_itemOpenPreferencesActionPerformed

    /**
     * Open About dialog
     */
    private void itemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemAboutActionPerformed
        ManagerAboutBox about = new ManagerAboutBox(this, ManagerOptions.getInstance());
        about.setLocation(this.getX() + 20, this.getY() + 20);
        about.setVisible(true);
    }//GEN-LAST:event_itemAboutActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        this.setResizable(true);
    }//GEN-LAST:event_formComponentShown

    /**
     * Display specified message to the user using JOptionPane
     * @param message message to be displayed
     * @param title title of the message dialog
     * @param type type of the mesage, see JOptionPane for list of types
     */
    public void showMessage(String message, String title, int type) {
        JOptionPane.showMessageDialog(this, message, title, type);
    }

    /**
     * Custom table model of the mod list table
     */
    private class ModTableModel extends DefaultTableModel {

        Class[] types = new Class[]{
            java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
        };
        boolean[] canEdit = new boolean[]{
            true, false, false, false, false
        };

        public ModTableModel(Object[][] data, Object[] columnNames) {
            super(data, columnNames);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (getRowCount() > 0 && getValueAt(0, columnIndex) != null) {
                return getValueAt(0, columnIndex).getClass();
            }
            return super.getColumnClass(columnIndex);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit[columnIndex];
        }
    }

    /**
     * Update table with the list of mods
     *
     * @param mods list of mods to display
     */
    private void updateModTable(ArrayList<Mod> mods) {
        // Save current selected row
        int selectedRow = tableModList.getSelectedRow();
        if (selectedRow == -1) {
            selectedRow = 0;
        }
        this.tableData = new Object[mods.size()][];
        // Display all mods
        logger.error("UPDATE: " + mods.size());
        for (int i = 0; i < mods.size(); i++) {
            this.tableData[i] = new Object[5];
            if (ManagerOptions.getInstance().getAppliedMods().contains(mods.get(i))) {
                mods.get(i).enable();
            }
            this.tableData[i][0] = (Boolean) mods.get(i).isEnabled();
            this.tableData[i][1] = (String) mods.get(i).getName();
            this.tableData[i][2] = (String) mods.get(i).getAuthor();
            this.tableData[i][3] = (String) mods.get(i).getVersion();
            if (mods.get(i).isEnabled()) {
                this.tableData[i][4] = (String) L10n.getString("table.modstatus.enabled");
            } else {
                this.tableData[i][4] = (String) L10n.getString("table.modstatus.disabled");
            }
        }
        // Update table model
        DefaultTableModel dtm = (DefaultTableModel) tableModList.getModel();
        dtm.setDataVector(this.tableData, this.columnNames);
        // Restore selected row
        tableModList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableModList.getSelectionModel().setSelectionInterval(0, selectedRow);
        // Display details of selected mod
        displayModDetail();
    }

    /**
     * Highlight the mods that is required to enable before or disable before the current select mod do
     * TODO: next release, right now don't bother
     */
    public void highlightMods() {
        int selectedRow = tableModList.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

    }

    /**
     * Display details of the selected mod in the right panel
     */
    public void displayModDetail() {
        // Make sure that items in the panel are visible
        setDetailsVisible(true);
        Mod mod = null;
        int selectedRow = tableModList.getSelectedRow();
        try {
            mod = Manager.getInstance().getMod(selectedRow);
        } catch (IndexOutOfBoundsException e) {
            logger.error("Cannot display mod at index " + selectedRow);
            return;
        }
        labelModName.setText(mod.getName());
        labelModAuthor.setText(mod.getAuthor());
        areaModDesc.setText(mod.getDescription());
        labelVisitWebsite.setToolTipText(mod.getWebLink());
        labelModIcon.setIcon(mod.getIcon());
        buttonUpdateMod.setActionCommand(mod.getName());
        buttonEnableMod.setActionCommand(mod.getName());
        if (mod.isEnabled()) {
            buttonEnableMod.setText(L10n.getString("button.disablemod"));
            labelModStatus.setText(L10n.getString("label.modstatus.enabled"));
        } else {
            buttonEnableMod.setText(L10n.getString("button.enablemod"));
            labelModStatus.setText(L10n.getString("label.modstatus.disabled"));
        }
        // Display mod incompatibility
        ArrayList<Action> reqs = new ArrayList<Action>();
        reqs.addAll(mod.getActions(Action.REQUIREMENT));
        DefaultListModel dlm = new DefaultListModel();
        String elem;
        for (Iterator actIter = reqs.iterator(); actIter.hasNext();) {
            Action act = (Action) actIter.next();
            if (act.getClass() == ActionRequirement.class) {
                elem = ((ActionRequirement) act).getName();
                if (((ActionRequirement) act).getVersion() != null) {
                    elem += " (ver. " + ((ActionRequirement) act).getVersion() + ")";
                }
                dlm.addElement(elem);
            }
        }
        listRequirements.setModel(dlm);
    }

    /**
     *  Method used for updating the view (called when the model has changed and
     * notifyObservers() was called)
     */
    public void update(Observable obs, Object obj) {
        logger.info("List of mods has changed, updating...");
        updateModTable(ManagerOptions.getInstance().getMods());
        highlightMods();
    }

    /**
     * Change visibility of components on the mod details panel
     * @param visible true to make them visible, false to make them invisible
     */
    private void setDetailsVisible(boolean visible) {
        labelModIcon.setVisible(visible);
        labelModName.setVisible(visible);
        labelModAuthor.setVisible(visible);
        areaModDesc.setVisible(visible);
        labelVisitWebsite.setVisible(visible);
        labelModStatus.setVisible(visible);
        labelRequirements.setVisible(visible);
        listRequirements.setVisible(visible);
        buttonUpdateMod.setVisible(visible);
        buttonEnableMod.setVisible(visible);
    }

    /*
     * The following methods add listeners to the UI components
     */
    public void buttonAddModAddActionListener(ActionListener al) {
        buttonAddMod.addActionListener(al);
    }

    public void buttonEnableModAddActionListener(ActionListener al) {
        buttonEnableMod.addActionListener(al);
    }

    public void labelVisitWebsiteAddMouseListener(MouseListener ml) {
        labelVisitWebsite.addMouseListener(ml);
    }

    public void tableRemoveListSelectionListener(ListSelectionListener sl) {
        tableModList.getSelectionModel().removeListSelectionListener(sl);
        tableModList.getColumnModel().getSelectionModel().removeListSelectionListener(sl);
    }

    public void tableAddListSelectionListener(ListSelectionListener sl) {
        tableModList.getSelectionModel().addListSelectionListener(sl);
        tableModList.getColumnModel().getSelectionModel().addListSelectionListener(sl);
    }

    public void tableAddModelListener(TableModelListener tml) {
        tableModList.getModel().addTableModelListener(tml);
    }

    public void itemApplyModsAddActionListener(ActionListener al) {
        itemApplyMods.addActionListener(al);
        buttonApplyMods.addActionListener(al);
    }

    public void itemApplyAndLaunchAddActionListener(ActionListener al) {
        itemApplyAndLaunch.addActionListener(al);
    }

    public void itemUnapplyAllModsAddActionListener(ActionListener al) {
        itemUnapplyAllMods.addActionListener(al);
    }

    public void buttonUpdateModActionListener(ActionListener al) {
        buttonUpdateMod.addActionListener(al);
    }

    public void itemOpenModFolderAddActionListener(ActionListener al) {
        itemOpenModFolder.addActionListener(al);
    }

    public void itemVisitForumThreadAddActionListener(ActionListener al) {
        itemVisitForumThread.addActionListener(al);
    }

    public void itemExitAddActionListener(ActionListener al) {
        itemExit.addActionListener(al);
    }

    public void buttonApplyLafAddActionListener(ActionListener al) {
        buttonApplyLaf.addActionListener(al);
    }

    public void buttonOkAddActionListener(ActionListener al) {
        buttonOk.addActionListener(al);
    }

    public void buttonCancelAddActionListener(ActionListener al) {
        buttonCancel.addActionListener(al);
    }

    public void buttonHonFolderAddActionListener(ActionListener al) {
        buttonHonFolder.addActionListener(al);
    }

    /*
     * Various getters and setters
     */
    public String getTextFieldHonFolder() {
        return textFieldHonFolder.getText();
    }

    public void setTextFieldHonFolder(String txt) {
        textFieldHonFolder.setText(txt);
    }

    public String getSelectedLafClass() {
        return ((LaF) comboBoxLafs.getSelectedItem()).getLafClass();
    }

    public String getSelectedLanguage() {
        return ((Language) comboBoxChooseLanguage.getSelectedItem()).getCode();
    }

    public JDialog getPrefsDialog() {
        return dialogOptions;
    }

    public JTable getModListTable() {
        return this.tableModList;
    }

    public int getSelectedMod() {
        return tableModList.getSelectedRow();
    }

    public String getSelectedHonFolder() {
        return textFieldHonFolder.getText();
    }

    public String getCLArguments() {
        return textFieldCLArguments.getText();
    }

    /**
     * Class of items in the Select LaF combo box on preferences dialog
     */
    private class LaF {

        private String name;
        private String lafClass;

        public LaF(String _name, String _lafClass) {
            name = _name;
            lafClass = _lafClass;
        }

        @Override
        public String toString() {
            return name;
        }

        public String getLafClass() {
            return lafClass;
        }

        @Override
        public boolean equals(Object laf) {
            if (lafClass.equals(((LaF) laf).lafClass)) {
                return true;
            }
            return false;
        }
    }

    /**
     * Class of items in the Select language combo box on preferences dialog
     */
    private class Language {

        private String name;
        private String code;

        public Language(String _name, String _code) {
            name = _name;
            code = _code;
        }

        @Override
        public String toString() {
            return name;
        }

        public String getCode() {
            return code;
        }

        @Override
        public boolean equals(Object lang) {
            if (code.equals(((Language) lang).code)) {
                return true;
            }
            return false;
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea areaModDesc;
    private javax.swing.JButton buttonAddMod;
    private javax.swing.JButton buttonApplyLaf;
    private javax.swing.JButton buttonApplyMods;
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonEnableMod;
    private javax.swing.JButton buttonHonFolder;
    private javax.swing.JButton buttonOk;
    private javax.swing.JButton buttonUpdateMod;
    private javax.swing.JComboBox comboBoxChooseLanguage;
    private javax.swing.JComboBox comboBoxLafs;
    private javax.swing.JDialog dialogOptions;
    private javax.swing.JMenuItem itemAbout;
    private javax.swing.JMenuItem itemApplyAndLaunch;
    private javax.swing.JMenuItem itemApplyMods;
    private javax.swing.JMenuItem itemExit;
    private javax.swing.JMenuItem itemOpenModFolder;
    private javax.swing.JMenuItem itemOpenPreferences;
    private javax.swing.JMenuItem itemUnapplyAllMods;
    private javax.swing.JMenuItem itemVisitForumThread;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JLabel labelCLArguments;
    private javax.swing.JLabel labelChooseLanguage;
    private javax.swing.JLabel labelHonFolder;
    private javax.swing.JLabel labelModAuthor;
    private javax.swing.JLabel labelModIcon;
    private javax.swing.JLabel labelModName;
    private javax.swing.JLabel labelModStatus;
    private javax.swing.JLabel labelRequirements;
    private javax.swing.JLabel labelSelectLaf;
    private javax.swing.JLabel labelVisitWebsite;
    private javax.swing.JList listRequirements;
    private javax.swing.JMenuBar mainMenu;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenu menuOptions;
    private javax.swing.JPanel panelModDescription;
    private javax.swing.JPanel panelModDetails;
    private javax.swing.JPanel panelModList;
    private javax.swing.JTable tableModList;
    private javax.swing.JTextField textFieldCLArguments;
    private javax.swing.JTextField textFieldHonFolder;
    // End of variables declaration//GEN-END:variables
}
