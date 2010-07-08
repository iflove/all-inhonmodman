package manager;

import business.ManagerOptions;
import business.Mod;
import business.actions.*;

import utility.OS;
import utility.XML;
import utility.ZIP;
import utility.WindowsRegistry;
import utility.exception.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Observable;
import java.util.Random;
import java.util.prefs.Preferences;

import com.mallardsoft.tuple.*;

import java.security.InvalidParameterException;

import org.apache.log4j.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import utility.Game;

/**
 * Implementation of the core functionality of HoN modification manager. This class is
 * the 'model' part of the MVC framework used for creating manager GUI. After any updates
 * that should result in UI changes (such as new mod added) it should call updateNotify
 * method which will notify observers to refresh. This class should never directly call
 * view or controller classes.
 *
 * @author Shirkit
 */
public class Manager extends Observable {

    private static Manager instance = null;
    private ArrayList<Mod> mods;
    private ArrayList<ArrayList<Pair<String, String>>> deps;
    private ArrayList<ArrayList<Pair<String, String>>> cons;
    private Set<Mod> applied;
    private ManagerOptions options;
    private static String MANAGER_FOLDER = "/Users/penn/Documents/Development/HoNMoDMan/Manager/manager";
    private static String MODS_FOLDER = "/Users/penn/Library/Application Support/Heroes of Newerth/game/mods";
    private static String HON_FOLDER = "/Applications/Heroes of Newerth"; // We need this
    private static String OPTIONS_PATH = ""; // Stores the absolute path to the option file
    private static String HOMEPAGE = "http://sourceforge.net/projects/all-inhonmodman";
    private static String VERSION = "0.1 BETA";     // Version string shown in About dialog
    private static Logger logger = Logger.getLogger(Manager.class.getPackage().getName());
    // Preferences constants - use these constants instead of strings to reduce typo errors
    public static final String PREFS_LOCALE = "locale";
    public static final String PREFS_LAF = "laf";
    public static final String PREFS_CLARGUMENTS = "clarguments";
    public static final String PREFS_HONFOLDER = "honfolder";

    private Manager() {
        mods = new ArrayList<Mod>();
        deps = new ArrayList<ArrayList<Pair<String, String>>>();
        cons = new ArrayList<ArrayList<Pair<String, String>>>();
        options = new ManagerOptions();
        try {
            // Load options
            options.loadOptions(new File(OPTIONS_PATH));
            applied = options.getAppliedMods();
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
            applied = new HashSet<Mod>();
        }
        // Find HoN folder
        String folder = findHonFolder();
        if (folder != null) {
            logger.info("Setting HoN folder to: " + folder);
            Preferences prefs = Preferences.userNodeForPackage(Manager.class);
            prefs.put(Manager.PREFS_HONFOLDER, folder);
        }
    }

    /**
     * Try to find HoN installation folder on different platforms.
     *
     * @return folder where HoN is installed or null if such folder cannot be found
     */
    private String findHonFolder() {
        // Try to find HoN folder in case we are on Windows
        if (OS.isWindows()) {
            // Get the folder from uninstall info in windows registry saved by HoN
            String registryData = WindowsRegistry.getRecord("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\hon", "InstallLocation");
            if (!registryData.isEmpty()) {
                return registryData;
            }
            // We didnt find HoN folder in the registry, try something else?
            return null;
        }
        // Try to find HoN folder in case we are on Linux
        if (OS.isLinux()) {
        }
        // Try to find HoN folder in case we are on Mac
        if (OS.isMac()) {
        }
        return null;
    }

    /**
     * This method is used to get the running instance of the Manager class.
     * @return the instance.
     * @see get()
     */
    public static Manager getInstance() {
        if (instance == null) {
            instance = new Manager();
        }
        return instance;
    }

    /*
     * Various getters and setters
     */
    public void setModPath(String p) {
        MODS_FOLDER = p;
    }

    public void setGamePath(String p) {
        HON_FOLDER = p;
    }

    public void setManagerPath(String p) {
        MANAGER_FOLDER = p;
    }

    public void setOptionsPath(String p) {
        OPTIONS_PATH = p;
    }

    public String getModPath() {
        return MODS_FOLDER;
    }

    public String getGamePath() {
        return HON_FOLDER;
    }

    public String getManagerPath() {
        return MANAGER_FOLDER;
    }

    public String getOptionsPath() {
        return OPTIONS_PATH;
    }

    public String getHomepage() {
        return HOMEPAGE;
    }

    public String getVersion() {
        return VERSION;
    }

    /**
     * This function is just for testing purpose
     * @param name
     */
    public void setAppliedMod(String name) {
        if (applied.add(getMod(name))) {
            System.out.println("Setting mod " + name + " to applied successfully");
        } else {
            System.out.println("Setting mod " + name + " to applied NOT successfully");
        }

    }

    public void saveOptions() throws IOException {
        options.saveOptions(new File(OPTIONS_PATH));
    }

    /**
     * This functions above are just for testing
     */
    /**
     * This will return the arraylist of applied mods
     * @return
     */
    public Set<Mod> getAppliedMods() {
        return applied;
    }

    /**
     * This should be called after adding all the honmod files to build and initialize the arrays
     * @throws IOException 
     */
    public void buildGraphs() throws IOException {
        // First reset all new added mods from startup
        for (int i = 0; i < mods.size(); i++) {
            mods.get(i).disable();
        }

        // Now building the graph
        for (int i = 0; i < mods.size(); i++) {
            int length = mods.get(i).getActions().size();
            for (int j = 0; j < length; j++) {
                if (mods.get(i).getActions().get(j).getClass() == ActionApplyAfter.class) {
                    // do something
                } else if (mods.get(i).getActions().get(j).getClass() == ActionApplyBefore.class) {
                    // do something
                } else if (mods.get(i).getActions().get(j).getClass() == ActionIncompatibility.class) {
                    Pair<String, String> mod = Tuple.from(((ActionIncompatibility) mods.get(i).getActions().get(j)).getName(), ((ActionIncompatibility) mods.get(i).getActions().get(j)).getVersion());
                    if (cons.get(i) == null) {
                        ArrayList<Pair<String, String>> temp = new ArrayList<Pair<String, String>>();
                        temp.add(mod);
                        cons.add(i, temp);
                    } else {
                        cons.get(i).add(mod);
                    }
                } else if (mods.get(i).getActions().get(j).getClass() == ActionRequirement.class) {
                    Pair<String, String> mod = Tuple.from(((ActionRequirement) mods.get(i).getActions().get(j)).getName(), ((ActionRequirement) mods.get(i).getActions().get(j)).getVersion());
                    if (deps.get(i) == null) {
                        ArrayList<Pair<String, String>> temp = new ArrayList<Pair<String, String>>();
                        temp.add(mod);
                        deps.add(i, temp);
                    } else {
                        deps.get(i).add(mod);
                    }
                }
            }
        }
    }

    /**
     * Adds a Mod to the list of mods. This list is the real thing that the Manager uses.
     * @param Mod to be added.
     */
    private void addMod(Mod mod) {
        mods.add(mod);
        deps.add(null);
        cons.add(null);
    }

    /**
     * Set status of the MVC model to changed and notify all registered observers.
     * Call this method when the data model changed and UI has to be updated.
     */
    public void updateNotify() {
        // Notify observers that model has been updated
        setChanged();
        notifyObservers();
    }

    /**
     * Load all mods from the mods folder and put them into the array.
     *
     * @throws IOException
     */
    public void loadMods() throws IOException {
        File modsFolder = new File(MODS_FOLDER);
        // Get mod files from the directory
        FileFilter fileFilter = new FileFilter() {

            public boolean accept(File file) {
                String fileName = file.getName();
                if ((!file.isDirectory())
                        && /* Filter out directories */ (!fileName.startsWith("."))
                        && /* Filter out hidden files and current dir */ (fileName.endsWith(".honmod"))) /* Filter only .honmod files */ {
                    return true;
                } else {
                    return false;
                }
            }
        };
        File[] files = modsFolder.listFiles(fileFilter);
        if (files == null || files.length == 0) {
            return;
        }
        // Go through all the mods and load them
        for (int i = 0; i < files.length; i++) {
            addHonmod(files[i], false);
        }
    }

    /**
     * This function is used internally from the GUI itself automatically when launch to initiate existing mods.
     * @param honmod is the file (.honmod) to be add.
     * @param copy flag to indicate whether to copy the file to mods folder
     */
    public void addHonmod(File honmod, boolean copy) throws FileNotFoundException, IOException {
        if (!honmod.exists()) {
            throw new FileNotFoundException();
        }
        String xml = new String(ZIP.getFile(honmod, "mod.xml"));
        Mod m = XML.xmlToMod(xml);
        Icon icon = new ImageIcon(ZIP.getFile(honmod, "icon.png"));
        m.setIcon(icon);
        logger.info("Mod file opened. Mod name: " + m.getName());
        m.setPath(honmod.getAbsolutePath());
        m.setId(0);
        if (copy) {
            // Copy the honmod file to mods directory
            copyFile(honmod, new File(MODS_FOLDER + File.separator + honmod.getName()));
            logger.info("Mod file copied to mods older");
        }
        addMod(m);
    }

    private static void copyFile(File in, File out) throws IOException {
        FileInputStream fis = new FileInputStream(in);
        FileOutputStream fos = new FileOutputStream(out);
        try {
            byte[] buf = new byte[1024];
            int i = 0;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }

    /**
     * Open website of the selected mod
     *
     * @param modIndex index of the mod in the list of mods
     * @return 0 on success, -1 if opening websites is not supported on this platform
     * @throws IndexOutOfBoundsException in case index is not in the mod list
     */
    public int openModWebsite(int modIndex) throws IndexOutOfBoundsException {
        Mod mod = mods.get(modIndex);
        String url = mod.getWebLink();

        return openWebsite(url);
    }

    /**
     * Open specified website in the default browser. This method is using java
     * Desktop API and therefore requires Java 1.6. Also, this operation might not
     * be supported on all platforms.
     *
     * @param url url of the website to open
     * @return 0 on success, -1 in case the operation is not supported on this platform
     */
    public int openWebsite(String url) {
        if (!java.awt.Desktop.isDesktopSupported()) {
            logger.info("Opening websites is not supported");
            return -1;
        }
        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
        if (!desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
            logger.info("Opening websites is not supported");
            return -1;
        }

        try {
            java.net.URI uri = new java.net.URI(url);
            desktop.browse(uri);
        } catch (Exception e) {
            logger.error("Unable to open website: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    /**
     * Open folder containing mods. The folder is opened using OS specific explorer
     * and therefore might not be supported on all platforms. This operation uses java
     * Desktop API and requires Java 1.6
     *
     * @return 0 on succuess, -1 in case the operation is not supported on this platform
     */
    public int openModFolder() {
        if (!java.awt.Desktop.isDesktopSupported()) {
            logger.info("Opening local folders is not supported");
            return -1;
        }
        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

        try {
            desktop.open(new File(this.getModPath()));
        } catch (Exception e) {
            logger.error("Unable to open local folder: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    /**
     * This function retrives ALL the mods, including the disabled ones in the local computer. Use the method isEnabled() to check if Mod is enabled.
     * @return An ArrayList of Mods containing all mods in the local computer.
     * @see Mod.isEnabled()
     */
    public ArrayList<Mod> getMods() {
        return mods;
    }

    /**
     * Get mod at specified index
     *
     * @param index index of the mod in the list of mods
     * @return mod at the given index
     * @throws IndexOutOfBoundsException in case index does not exist in the list of mods
     */
    public Mod getMod(int index) throws IndexOutOfBoundsException {
        return (Mod) mods.get(index);
    }

    /**
     * This function returns the mod from the arraylist mods
     * @param name
     * @return Mod
     * @throws NoSuchElementException if the mod wasn't found
     */
    public Mod getMod(String name) throws NoSuchElementException {
        // get the enumration object for ArrayList mods
        Enumeration e = Collections.enumeration(mods);

        // enumerate through the ArrayList to find the mod
        while (e.hasMoreElements()) {
            Mod m = (Mod) e.nextElement();
            if (m.getName().equals(name)) {
                return m;
            }
        }

        throw new NoSuchElementException(name);
    }

    /**
     * This function returns the mod from the arraylist mods
     * @param name
     * @return Mod
     */
    public Mod getEnabledMod(String name) {
        Mod m = getMod(name);
        if (m != null && m.isEnabled()) {
            return m;
        }

        return null;
    }

    /**
     * This function checks to see if all dependencies are all satisfied    
     * @param m
     * @return True if all dependencies are satisfied else false
     */
    private boolean checkdeps(Mod m) throws ModNotEnabledException {
        // get a list of dependencies
        ArrayList<Pair<String, String>> list = deps.get(mods.indexOf(m));
        if (list == null || list.isEmpty()) {
            return true;
        } else {
            Enumeration e = Collections.enumeration(list);

            while (e.hasMoreElements()) {
                Pair<String, String> dep = (Pair<String, String>) e.nextElement();
                Mod d = getEnabledMod(Tuple.get1(dep));
                if (d == null) {
                    throw new ModNotEnabledException(Tuple.get1(dep), Tuple.get2(dep));
                }

                // compareModsVersion might not be working properly, ask him to fix it or something
                if (!compareModsVersions(d.getVersion(), Tuple.get2(dep))) {
                    throw new ModNotEnabledException(Tuple.get1(dep), Tuple.get2(dep));
                }

            }

            return true;
        }
    }

    /**
     * This function checks to see if there is any conflict
     * @param m
     * @return False if there isn't any conflict.
     */
    private boolean checkcons(Mod m) throws ModEnabledException {
        // get a list of conflicts
        ArrayList<Pair<String, String>> list = cons.get(mods.indexOf(m));
        if (list == null || list.isEmpty()) {
            return false;
        }
        Enumeration e = Collections.enumeration(list);
        while (e.hasMoreElements()) {
            Pair<String, String> check = (Pair<String, String>) e.nextElement();
            if (getEnabledMod(Tuple.get1(check)) != null) {
                throw new ModEnabledException(Tuple.get1(check), Tuple.get2(check));
            }
        }
        return false;
    }

    private boolean revcheckdeps(Mod m) {
        // get a list of dependencies on m
        ArrayList list = new ArrayList();
        Enumeration e = Collections.enumeration(deps);
        while (e.hasMoreElements()) {
            ArrayList<Pair<String, String>> temp = (ArrayList<Pair<String, String>>) e.nextElement();
            Enumeration te = Collections.enumeration(temp);
            while (te.hasMoreElements()) {
                if (Tuple.get1((Pair<String, String>) te.nextElement()).equals(m.getName())) {
                    return true;
                }
//	    			list.add(deps.indexOf(temp));
            }
        }

        return false;
    }

    /**
     * This function trys to enable the mod with the name given. Throws exceptions if didn't no success while enabling the mod. ignoreVersion should be always false, unless the user especifically says so.
     * @param name of the mod
     * @throws ModEnabledException if a mod was enabled and caused an incompatibility with the Mod that is being tryied to apply.
     * @throws ModNotEnabledException if a mod that was required by this mod wasn't enabled.
     * @throws NoSuchElementException if the mod doesn't exist
     * @throws ModVersionMissmatchException if the mod's version is imcompatible with the game version.
     * @throws NullPointerException if there is a problem with the game path (maybe the path was not set in the game class, or hon.exe wasn't found, or happened a random I/O error).
     */
    public void enableMod(String name, boolean ignoreVersion) throws ModEnabledException, ModNotEnabledException, NoSuchElementException, ModVersionMissmatchException, NullPointerException {
        Mod m = getMod(name);
        if (!ignoreVersion) {
            try {
                if (compareModsVersions(Game.getInstance().getVersion(), m.getVersion())) {
                    throw new ModVersionMissmatchException(name, name);
                }
            } catch (IllegalArgumentException ex) {
                throw new NullPointerException();
            } catch (FileNotFoundException ex) {
                throw new NullPointerException();
            } catch (IOException ex) {
                throw new NullPointerException();
            }
        }
        if (!m.isEnabled() && checkdeps(m) && !checkcons(m)) {
            mods.get(mods.indexOf(m)).enable();
        }
    }

    /**
     * This function is similar to enableMod in which it disable the mod instead
     * @param name
     * @return Whether the function disable the mod successfully
     */
    public boolean disableMod(String name) {
        Mod m = getMod(name);
        if (!m.isEnabled()) {
            System.out.println("Mod " + name + " already disabled");
            return true;
        }
        if (!revcheckdeps(m)) {
            // disable it
            mods.get(mods.indexOf(m)).disable();
            return true;
        }
        System.out.println("Can't disable mod " + name);
        return false;
    }

    private Stack<Mod> BFS(Stack<Mod> stack, ArrayList<Pair<String, String>> dep) {
        if (dep == null) {
            return stack;
        }
        if (dep.isEmpty()) {
            return stack;
        }

        LinkedList<Mod> queue = new LinkedList<Mod>();
        queue.offer(stack.peek());

        while (stack.size() != mods.size()) {
            Mod m = null;
            try {
                m = queue.remove();
            } catch (NoSuchElementException e) {
                break;
            }

            if (stack.contains(m)) {
                continue;
            } else {
                Enumeration d = Collections.enumeration(dep);

                while (d.hasMoreElements()) {
                    Mod tmp = getMod(Tuple.get1((Pair<String, String>) d.nextElement()));
                    if (!stack.contains(tmp) && tmp != null) {
                        System.out.println("gmm: " + tmp.getName());
                        stack.push(tmp);
                    }
                    if (!queue.contains(tmp) && tmp != null) {
                        queue.offer(tmp);
                    }
                }
            }
        }

        return stack;
    }

    public Stack<Mod> sortMods() {
        Stack<Mod> stack = new Stack<Mod>();

        Enumeration e = Collections.enumeration(mods);
        while (e.hasMoreElements()) {
            Mod m = (Mod) e.nextElement();
            if (!applied.contains(m) && m.isEnabled()) {
                if (!stack.contains(m)) {
                    stack.add(m);
                }
                stack = BFS(stack, deps.get(mods.indexOf(m)));
            }
        }

        return stack;
    }

    /**
     *
     * @throws IOException if a random I/O error happened.
     * @throws UnknowModActionException if a unkown Action was found. Actions that aren't know by the program can't be applied.
     * @throws NothingSelectedModActionException if a action tried to do a action that involves a string, but no string was selected.
     * @throws StringNotFoundModActionException if a search for a string was made, but that string wasn't found. Probally, imcompatibility or a error by the mod's author.
     * @throws InvalidModActionParameterException if a action had a invalid parameter. Only the position of actions 'insert' and 'find' can throw this exception.
     * @throws SecurityException if the Manager couldn't do a action because of security business.
     */
    public void applyMods() throws IOException, UnknowModActionException, NothingSelectedModActionException, StringNotFoundModActionException, InvalidModActionParameterException, ModActionConditionNotValidException, SecurityException {
        Stack<Mod> applyOrder = sortMods();
        File tempFolder = new File(System.getProperty("java.io.tmpdir") + File.separator + "HoN Mod Manager");
        // This generates a temp folder. If it isn't possible, generates a random folder inside the OS's temp folder.
        if (tempFolder.exists()) {
            if (!tempFolder.delete()) {
                Random r = new Random();
                tempFolder = new File(System.getProperty("java.io.tmpdir") + File.separator + r.nextInt());
                if (tempFolder.exists()) {
                    if (!tempFolder.delete()) {
                        throw new SecurityException();
                    }
                }
            }
        }
        tempFolder.mkdirs();
        while (!applyOrder.isEmpty()) {
            Mod mod = applyOrder.pop();
            for (int j = 0; j < mod.getActions().size(); j++) {
                Action action = mod.getActions().get(j);
                if (action.getClass().equals(ActionCopyFile.class)) {
                    ActionCopyFile copyfile = (ActionCopyFile) action;
                    if (!isValidCondition(action)) {
                        // condition isn't valid, can't apply
                        // No need to throw execption, since if condition isn't valid, this action won't be applied
                    } else {
                        // if path2 is not specified, path1 is copied
                        String toCopy;
                        if (copyfile.getSource().isEmpty() || copyfile.getSource().equals("") || copyfile.getSource() == null) {
                            toCopy = copyfile.getName();
                        } else {
                            // path2 is copied and renamed to path1
                            toCopy = copyfile.getSource();
                        }

                        if (copyfile.overwrite() == -1) {
                            throw new InvalidModActionParameterException(mod.getName(), mod.getVersion(), (Action) copyfile);
                        }

                        File temp = new File(tempFolder.getAbsolutePath() + File.separator + copyfile.getName());
                        if (temp.exists()) {
                            if (copyfile.overwrite() == 0) {
                                // Don't overwrite, do nothing
                            } else if (copyfile.overwrite() == 1) {
                                // Overwrite if newer
                                if (ZIP.getLastModified(new File(mod.getPath()), toCopy) > temp.lastModified()) {
                                    byte[] file = ZIP.getFile(new File(mod.getPath()), toCopy);
                                    FileOutputStream fos = new FileOutputStream(temp);
                                    if (temp.delete() && temp.createNewFile()) {
                                        ByteArrayInputStream bais = new ByteArrayInputStream(file);
                                        ZIP.copyInputStream(bais, fos);
                                        bais.close();
                                        fos.close();
                                    } else {
                                        throw new SecurityException();
                                    }
                                }
                            } else if (copyfile.overwrite() == 2) {
                                byte[] file = ZIP.getFile(new File(mod.getPath()), toCopy);
                                FileOutputStream fos = new FileOutputStream(temp);
                                if (temp.delete() && temp.createNewFile()) {
                                    ByteArrayInputStream bais = new ByteArrayInputStream(file);
                                    ZIP.copyInputStream(bais, fos);
                                    bais.close();
                                    fos.close();
                                } else {
                                    throw new SecurityException();
                                }
                            }
                        } else {
                            // if temporary file doesn't exists
                            if (copyfile.overwrite() == 1) {
                                if (ZIP.getLastModified(new File(mod.getPath()), toCopy) > temp.lastModified()) {
                                    byte[] file = ZIP.getFile(new File(mod.getPath()), toCopy);
                                    FileOutputStream fos = new FileOutputStream(temp);
                                    ByteArrayInputStream bais = new ByteArrayInputStream(file);
                                    ZIP.copyInputStream(bais, fos);
                                    bais.close();
                                    fos.close();
                                }
                            } else if (copyfile.overwrite() == 2) {
                                byte[] file = ZIP.getFile(new File(mod.getPath()), toCopy);
                                FileOutputStream fos = new FileOutputStream(temp);
                                ByteArrayInputStream bais = new ByteArrayInputStream(file);
                                ZIP.copyInputStream(bais, fos);
                                bais.close();
                                fos.close();
                            }
                        }
                        temp.setLastModified(ZIP.getLastModified(new File(mod.getPath()), toCopy));
                    }
                } else if (action.getClass().equals(ActionEditFile.class)) {
                    ActionEditFile editfile = (ActionEditFile) action;
                    if (!isValidCondition(action)) {
                        // condition isn't valid, can't apply
                        // No need to throw execption, since if condition isn't valid, this action won't be applied
                    } else {

                        // the selection is stored here
                        int cursor = 0;
                        int cursor2 = 0;

                        // check if something is selected
                        boolean isSelected = false;

                        // Check for file
                        File f = new File(tempFolder.getAbsolutePath() + File.separator + editfile.getName());
                        String toEdit = "";
                        if (f.exists()) {
                            // Load file from temp folder. If any other mod changes the file, it's actions won't be lost.
                            FileInputStream fin = new FileInputStream(f);
                            OutputStream os = new ByteArrayOutputStream();
                            ZIP.copyInputStream(fin, os);
                            toEdit = os.toString();
                        } else {
                            // Load file from resources0.s2z if no other mod edited this file
                            toEdit = new String(ZIP.getFile(new File(HON_FOLDER + File.separator + "game" + File.separator + "resources0.s2z"), editfile.getName()));
                        }
                        String afterEdit = new String(toEdit);
                        for (int k = 0; k < editfile.getActions().size(); k++) {
                            ActionEditFileActions editFileAction = editfile.getActions().get(k);

                            // Delete Action
                            if (editFileAction.getClass().equals(ActionEditFileDelete.class)) {
                                if (isSelected) {
                                    afterEdit = toEdit.substring(0, cursor) + toEdit.substring(cursor2);
                                    isSelected = false;
                                } else {
                                    ActionEditFileDelete delete = (ActionEditFileDelete) editFileAction;
                                    throw new NothingSelectedModActionException(mod.getName(), mod.getVersion(), (Action) delete);
                                }

                                // Find Action
                            } else if (editFileAction.getClass().equals(ActionEditFileFind.class)) {
                                ActionEditFileFind find = (ActionEditFileFind) editFileAction;
                                if (find.getPosition() != null) {
                                    if (find.isPositionAtStart()) {
                                        cursor = 0;
                                        cursor2 = 0;
                                        isSelected = true;
                                    } else if (find.isPositionAtEnd()) {
                                        cursor = toEdit.length();
                                        cursor2 = cursor;
                                        isSelected = true;
                                    } else {
                                        try {
                                            cursor = Integer.parseInt(find.getPosition());
                                            if (cursor < 0) {
                                                cursor = toEdit.length() - (cursor * (-1));
                                            }
                                            cursor2 = cursor;
                                            isSelected = true;
                                        } catch (NumberFormatException e) {
                                            // it isn't a valid number or word, can't apply
                                            throw new InvalidModActionParameterException(mod.getName(), mod.getVersion(), (Action) find);
                                        }
                                    }
                                } else {
                                    cursor = toEdit.indexOf(find.getContent());
                                    if (cursor == -1) {
                                        // couldn't find the string, can't apply
                                        throw new StringNotFoundModActionException(mod.getName(), mod.getVersion(), (Action) find, find.getContent());
                                    }
                                    cursor2 = cursor + find.getContent().length();
                                    isSelected = true;
                                }

                                // FindUp Action
                            } else if (editFileAction.getClass().equals(ActionEditFileFindUp.class)) {
                                ActionEditFileFindUp findup = (ActionEditFileFindUp) editFileAction;
                                cursor = toEdit.lastIndexOf(findup.getContent());
                                if (cursor == -1) {
                                    // couldn't find the string, can't apply
                                    throw new StringNotFoundModActionException(mod.getName(), mod.getVersion(), (Action) findup, findup.getContent());
                                }
                                cursor2 = cursor + findup.getContent().length();
                                isSelected = true;

                                // Insert Action
                            } else if (editFileAction.getClass().equals(ActionEditFileInsert.class)) {
                                ActionEditFileInsert insert = (ActionEditFileInsert) editFileAction;
                                if (isSelected) {
                                    if (insert.isPositionAfter()) {
                                        afterEdit = toEdit.substring(0, cursor2) + insert.getContent() + toEdit.substring(cursor2);
                                    } else if (insert.isPositionBefore()) {
                                        afterEdit = toEdit.substring(0, cursor) + insert.getContent() + toEdit.substring(cursor);
                                    } else {
                                        // position is invalid, can't apply
                                        throw new InvalidModActionParameterException(mod.getName(), mod.getVersion(), (Action) insert);
                                    }
                                } else {
                                    // the guy didn't select anything yet, can't apply
                                    throw new NothingSelectedModActionException(mod.getName(), mod.getVersion(), (Action) insert);
                                }

                                // Replace Action
                            } else if (editFileAction.getClass().equals(ActionEditFileReplace.class)) {
                                ActionEditFileReplace replace = (ActionEditFileReplace) editFileAction;
                                if (isSelected) {
                                    afterEdit = toEdit.replace(toEdit.substring(cursor, cursor2), replace.getContent());
                                    isSelected = false;
                                } else {
                                    throw new NothingSelectedModActionException(mod.getName(), mod.getVersion(), (Action) replace);
                                }
                            } else {
                                // Unknow action, can't apply
                                throw new UnknowModActionException(editFileAction.getClass().getName(), mod.getName());
                            }
                        }
                        File temp = new File(tempFolder.getAbsolutePath() + File.separator + editfile.getName());
                        File folder = new File(temp.getAbsolutePath().replace(temp.getName(), ""));
                        if (folder.isDirectory() && !folder.getAbsolutePath().equalsIgnoreCase(tempFolder.getAbsolutePath())) {
                            if (!folder.mkdirs()) {
                                throw new SecurityException();
                            }
                        }

                        // Write String afterEdit to a file
                        FileOutputStream fos = new FileOutputStream(temp);
                        ByteArrayInputStream in = new ByteArrayInputStream(afterEdit.getBytes("UTF-8"));
                        ZIP.copyInputStream(in, fos);

                        // Set the Last Modified field
                        temp.setLastModified(ZIP.getLastModified(new File(mod.getPath()), editfile.getName()));

                    }
                    // ApplyAfter, ApplyBefore, Incompatibility, Requirement Action
                } else if (action.getClass().equals(ActionApplyAfter.class) || action.getClass().equals(ActionApplyBefore.class) || action.getClass().equals(ActionIncompatibility.class) || action.getClass().equals(ActionRequirement.class)) {
                    // nothing to do
                } else {
                    // Unknow action, can't apply
                    throw new UnknowModActionException(action.getClass().getName(), mod.getName());
                }
            }
        }
        File targetZip = new File(HON_FOLDER + File.separator + "game" + File.separator + "resources999.s2z");
        if (targetZip.exists()) {
            if(!targetZip.delete()) {
                throw new SecurityException();
            }
        }
        ZIP.createZIP(tempFolder.getAbsolutePath(), targetZip.getAbsolutePath());
    }

    /**
     * Compares the singleVersion of a Mod and another expressionVersion.
     * @param singleVersion is the base version to be compared of. For example, a Mod's version go in here ('1.3', '3.2.57').
     * @param expressionVersion generally you put the ApplyAfter, ApplyBefore, ConditionVersion here ('1.35-*').
     * @return true if the mods have the same expressionVersion OR the singleVersion is in the range of the passed String expressionVersion. False otherwise.
     * @throws InvalidParameterException if can't compare the versions for some reason (out of format).
     */
    public boolean compareModsVersions(String singleVersion, String expressionVersion) throws InvalidParameterException {

        boolean result = false;

        if (expressionVersion == null) {
            expressionVersion = "*";
        }

        if ((expressionVersion.equals("*")) || (expressionVersion.equals(singleVersion)) || (expressionVersion.equals("*-*")) || (expressionVersion.equals(""))) {
            result = true;
        } else if (expressionVersion.contains("-")) {

            int check = 0;
            String vEx1 = expressionVersion.substring(0, expressionVersion.indexOf("-"));
            String vEx2 = expressionVersion.substring(expressionVersion.indexOf("-") + 1, expressionVersion.length());

            // singleVersion's expressionVersion but filled with '0' at the end, what causes a problem
            ArrayList<Integer> versionBase = new ArrayList<Integer>();
            String[] v1 = singleVersion.split("\\.");
            for (int i = 0; i < v1.length; i++) {
                if (!v1[i].equals("")) {
                    versionBase.add(new Integer(v1[i]));
                }
            }
            if (!vEx1.equals("*")) {
                ArrayList<Integer> versionExpression = new ArrayList<Integer>();
                String[] vExp = vEx1.split("\\.");
                for (int i = 0; i < vExp.length; i++) {
                    if (!vExp[i].equals("")) {
                        versionExpression.add(new Integer(vExp[i]));
                    }
                }
                for (int i = 0; i < versionExpression.size() && i < versionBase.size(); i++) {
                    if (versionExpression.get(i).compareTo(versionBase.get(i)) < 0) {
                        check++;
                        break;
                    }
                }
            } else {
                check++;
            }

            if (!vEx2.equals("*")) {
                ArrayList<Integer> versionExpression = new ArrayList<Integer>();
                String[] vExp = vEx2.split("\\.");
                for (int i = 0; i < vExp.length; i++) {
                    if (!vExp[i].equals("")) {
                        versionExpression.add(new Integer(vExp[i]));
                    }
                }
                for (int i = 0; i < versionExpression.size() && i < versionBase.size(); i++) {
                    if (versionExpression.get(i).compareTo(versionBase.get(i)) > 0) {
                        check++;
                        break;
                    }
                }
            } else {
                check++;
            }

            if (check >= 2) {
                result = true;
            }

        } else {
            throw new InvalidParameterException();
        }
        return result;
    }

    /**
     * This function should work now
     * @param action
     * @return
     */
    private boolean isValidCondition(Action action) {
        String condition = null;
        if (action.getClass().equals(ActionEditFile.class)) {
            ActionEditFile editfile = (ActionEditFile) action;
            condition = editfile.getCondition();
        } else if (action.getClass().equals(ActionCopyFile.class)) {
            ActionCopyFile copyfile = (ActionCopyFile) action;
            condition = copyfile.getCondition();
        }
        if (condition.isEmpty() || condition == null) {
            return true;
        }

        return isValidCondition(condition);
    }

    /**
     * findNextExpression returns the next expression from the given StringTokenizer
     * @param st
     * @return
     */
    private String findNextExpression(String previous, StringTokenizer st) {
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.equalsIgnoreCase("\'")) {
                String mod = token;
                do {
                    String next = st.nextToken();
                    if (next.equalsIgnoreCase("\'")) {
                        mod += next;
                        break;
                    }
                    mod += next;
                } while (st.hasMoreTokens());

                return mod;
            } else if (token.equalsIgnoreCase("(")) {
                String cond = "";
                boolean done = false;
                int level = 0;
                do {
                    String next = st.nextToken();
                    if (next.equalsIgnoreCase("(")) {
                        level++;
                    } else if (next.equalsIgnoreCase(")")) {
                        if (level == 0 && !done) {
                            break;
                        } else {
                            level--;
                        }
                    }

                    cond += next;
                } while (!done && st.hasMoreTokens());

                return cond;
            } else if (token.equalsIgnoreCase(" ")) {
                continue;
            } else {
                String ret = "";
                do {
                    ret += token;
                    try {
                        token = st.nextToken();
                    } catch (Exception e) {
                        break;
                    }
                } while (st.hasMoreTokens());
                return ret;
            }
        }

        return "";
    }

    /**
     * checkVersion checks to see if first argument <= second argument is true
     * @param lower
     * @param higher
     * @return
     */
    public boolean checkVersion(String lower, String higher) {
        boolean ret = false;

        StringTokenizer lowst = new StringTokenizer(lower, ".", false);
        StringTokenizer highst = new StringTokenizer(higher, ".", false);

        while (lowst.hasMoreTokens() && highst.hasMoreTokens()) {
            int first = Integer.parseInt(lowst.nextToken());
            int second = Integer.parseInt(highst.nextToken());

//    		System.out.println("first v: " + first + ", second v: " + second);

            ret = (first <= second);
        }

        return ret;
    }

    /**
     * validVesion checks to see if the version for m is within the condition set by version on the other parameter
     * in development
     * @param m
     * @param version
     * @return
     */
    public boolean validVersion(Mod m, String version) throws NumberFormatException {
        String target = m.getVersion().trim();

        if (version.contains("-")) {
            String low, high;
            low = version.substring(1, version.indexOf("-")).trim();
            high = version.substring(version.indexOf("-") + 1).trim();

            // checkVersion checks to see if first argument <= second argument is true
            return checkVersion(low, target) && checkVersion(target, high);

        } else {
            String compare = version.trim();
            return checkVersion(compare, target);
        }
    }

    /**
     * isValidCondition evaluates the condition string and return the result of it
     * Looks like it's working now, should work ;)
     * @param condition
     * @return
     */
    public boolean isValidCondition(String condition) {
        boolean valid = true;

        StringTokenizer st = new StringTokenizer(condition, "\' ()", true);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();

            if (token.equalsIgnoreCase("\'")) {
                String mod = "";
                do {
                    String next = st.nextToken();
                    if (next.equalsIgnoreCase("\'")) {
                        break;
                    }
                    mod += next;
                } while (st.hasMoreTokens());

                try {
                    String version = "";
                    if (mod.endsWith("]")) {
                        version = mod.substring(mod.indexOf('[') + 1, mod.length() - 1);
                        mod = mod.substring(0, mod.indexOf('['));
                    }

                    Mod m = getMod(mod);

                    try {
                        valid = (m.isEnabled() && validVersion(m, version));
                    } catch (Exception e) {
                        System.out.println("version is not valid: " + e.getMessage());
                        valid = false;
                    }
                } catch (NoSuchElementException e) {
                    System.out.println("mod not found exception: " + e.getMessage());
                    valid = false;
                }
            } else if (token.equalsIgnoreCase(" ")) {
                continue;
            } else if (token.equalsIgnoreCase("(")) {
                String cond = "";
                boolean done = false;
                int level = 0;
                do {
                    String next = st.nextToken();
                    if (next.equalsIgnoreCase("(")) {
                        level++;
                    } else if (next.equalsIgnoreCase(")")) {
                        if (level == 0 && !done) {
                            break;
                        } else {
                            level--;
                        }
                    }

                    cond += next;
                } while (!done && st.hasMoreTokens());

                return isValidCondition(cond);
            } else if (token.equalsIgnoreCase(")")) {
                return false;
            } else {
                String next = findNextExpression(token, st);

                if (token.equalsIgnoreCase("not")) {
                    valid = !isValidCondition(next);
                } else if (token.equalsIgnoreCase("and")) {
                    boolean compare = isValidCondition(next);
                    valid = (valid && compare);
                } else if (token.equalsIgnoreCase("or")) {
                    boolean compare = isValidCondition(next);
                    valid = (valid || compare);
                } else {
                    String mod = token + " " + next;
                }
            }
        }

        return valid;
    }
}
