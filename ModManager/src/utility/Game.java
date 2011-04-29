package utility;

import business.ManagerOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.stream.FileImageInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Attributes and Methods related to the HoN
 * @author Usuário
 */
public class Game {

    private String path;
    private String version;
    private static Game instance = null;
    private static Logger logger = Logger.getLogger(Game.class.getPackage().getName());

    /**
     * Try to find HoN installation folder on different platforms.
     *
     * @return folder where HoN is installed or null if such folder cannot be found
     */
    public static String findHonFolder() {
        // First, see if we already found the HoN folder
        if (ManagerOptions.getInstance().getGamePath() != null && !ManagerOptions.getInstance().getGamePath().isEmpty()) {
            return ManagerOptions.getInstance().getGamePath();
        }
        // Try to find HoN folder in case we are on Windows
        if (OS.isWindows()) {
            // Try to find HoN in its usual location:
            String[] honFolder = {"C:\\Program Files\\Heroes of Newerth\\", "C:\\Program Files (x86)\\Heroes of Newerth\\"};
            for (int i = 0; i < honFolder.length; i++) {
                File f = new File(honFolder[i]);
                if (f.exists()) {
                    return f.getAbsolutePath();
                }
            }

            // Get the folder from uninstall info in windows registry saved by HoN
            String registryData = WindowsRegistry.getRecord("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\hon", "InstallLocation");
            if (registryData != null && !registryData.isEmpty()) {
                return registryData;
            }
            registryData = WindowsRegistry.getRecord("SOFTWARE\\Notausgang\\HoN_ModMan", "hondir");
            if (registryData != null && !registryData.isEmpty()) {
                return registryData;
            }
            registryData = WindowsRegistry.getRecord("SOFTWARE\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\hon", "InstallLocation");
            if (registryData != null && !registryData.isEmpty()) {
                return registryData;
            }
            // From Notausgang's ModManager
            registryData = WindowsRegistry.getRecord("SOFTWARE\\Notausgang\\HoN_ModMan", "hondir");
            if (registryData != null && !registryData.isEmpty()) {
                return registryData;
            }
        }
        // Try to find HoN folder in case we are on Linux
        if (OS.isLinux()) {
            // Try to find HoN in its usual location:
            String[] honFolder = {"~/Heroes of Newerth/", "~/HoN/"};
            for (int i = 0; i < honFolder.length; i++) {
                File f = new File(honFolder[i]);
                if (f.exists()) {
                    return f.getAbsolutePath();
                }
            }
        }
        // Try to find HoN folder in case we are on Mac
        if (OS.isMac()) {
            File a = new File("/Applications/Heroes of Newerth.app");
            if (a.exists()) {
                logger.info("GAME: Mac: " + a.getPath() + " exists");
                return a.getAbsolutePath();
            }
        }
        // Let the user guide us.
        return null;
    }

    /**
     * Try to find Mod folder on different platforms
     * 
     * @return folder of the mods or null
     */
    public static String findModFolder(String gameFolder) {
        // Try to find HoN folder in case we are on Windows
        if (OS.isWindows()) {
            if (gameFolder != null) {
                File folder = new File(gameFolder + File.separator + "game" + File.separator + "mods");
                if (folder.exists() && folder.isDirectory()) {
                    return folder.getAbsolutePath();
                }
            }
        }
        // Try to find HoN folder in case we are on Linux
        if (OS.isLinux()) {
            File folder = new File(System.getProperty("user.home") + File.separator + ".Heroes of Newerth" + File.separator + "mods");
                if (folder.exists() && folder.isDirectory()) {
                    return folder.getAbsolutePath();
                }
            if (gameFolder != null) {
                folder = new File(gameFolder + File.separator + "game" + File.separator + "mods");
                if (folder.exists() && folder.isDirectory()) {
                    return folder.getAbsolutePath();
                }
            }
        }
        // Try to find HoN folder in case we are on Mac
        if (OS.isMac()) {
            File a = new File(System.getProperty("user.home") + File.separator + "Library/Application Support/Heroes of Newerth/game/mods");
            logger.error("GAME: " + a.getPath());
            return a.exists() ? a.getPath() : null;
        }

        return null;
    }

    /**
     * @param path to the HoN folder.
     * @throws FileNotFoundException if HoN folder doesn't exist.
     * @throws IOException if happened some I/O exception.
     */
    private Game() {
        this.path = null;
        this.version = null;
    }

    /**
     * This method is used to get the only instance of this class that is running.
     * @return the instance.
     */
    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    /**
     * Before using this method, the setPath must be called, or it will throw an IllegalArgumentException. This returns a string with the game version. ('1.0.4.0' for example).
     * @return the version of HoN.
     * @throws FileNotFoundException if HoN folder doesn't exist. Possible values:
     * <br/>"Hon folder doesn't exist".
     * <br/>"Hon file wasn't found".
     * @throws IOException if happened some I/O exception.
     * @throws IllegalArgumentException if the attribute Game.path is null.
     */
    public String getVersion() throws IllegalArgumentException, FileNotFoundException, IOException {
        if (this.path == null) {
            this.path = findHonFolder();
            if (this.path == null) {
                throw new IllegalArgumentException("Attribute 'path' not set yet.");
            }
        }
        if (this.version == null) {
            setVersion(getVersion(getPath()));
            return this.version;
        } else {
            return this.version;
        }
    }

    /**
     * @param path is a String with the path to the HoN folder.
     * @return the game version. This method checks for the path of the game, and from there he tries to get it's version.
     */
    private String getVersion(String path) throws FileNotFoundException, IOException {
        // Different folder for diffrent OS
        File folder = new File(path);
        File honWindows = new File(folder.getAbsolutePath() + File.separator + "hon.exe");
        File honLinux = new File(folder.getAbsolutePath() + File.separator + "manifest.xml");
        File honMac = new File(folder.getAbsolutePath() + File.separator + "manifest.xml");
        String gameVersion = "";

        if (!folder.exists()) {
            throw new FileNotFoundException("HoN folder doesn't exist. " + path);
        } else {
            if (OS.isWindows()) {
                gameVersion = getGameVersionWindows(honWindows);
            } else if (OS.isLinux()) {
                gameVersion = getGameVersionLinux(honLinux);
            } else if (OS.isMac()) {
                gameVersion = getGameVersionMac(honMac);
            } else {
                throw new FileNotFoundException("HoN file wasn't found. " + path);
            }
        }
        return gameVersion;

    }

    /**
     *
     * @param file of the 'hon.exe'. This algorithm was taken from the HoN ModManager.
     * @return a String with the game version.
     * @throws FileNotFoundException if the @param File hon was not found.
     * @throws IOException if occurred some I/O exception while reading/writing.
     */
    private String getGameVersionWindows(File hon) throws FileNotFoundException, IOException {
        try {
            FileImageInputStream fos = new FileImageInputStream(hon);
            byte[] buffer = new byte[(int) hon.length()];
            fos.read(buffer, 0, buffer.length);
            fos.close();
            int i = FindInByteStream(buffer, new byte[]{0x43, 0, 0x55, 0, 0x52, 0, 0x45, 0, 0x20, 0, 0x43, 0, 0x52, 0, 0x54, 0, 0x5D, 0, 0, 0});
            String gameVersion = "";
            if (i >= 0) {
                i += 20;
                int j;
                do {
                    j = buffer[i] + 256 * buffer[i + 1];
                    if (j > 0) {
                        gameVersion += Character.toString((char) j);
                    }
                    i += 2;
                } while ((j != 0) && (gameVersion.length() < 10));
            }
            return gameVersion;
        } catch (FileNotFoundException e1) {
            throw new FileNotFoundException("HoN file wasn't found. " + path);
        }
    }

    /**
     *
     * @param file of the 'hon.exe'. This algorithm was taken from the HoN ModManager.
     * @return a String with the game version.
     * @throws FileNotFoundException if the @param File hon was not found.
     * @throws IOException if occurred some I/O exception while reading/writing.
     */
    private String getGameVersionLinux(File manifest) throws FileNotFoundException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document dom = null;

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(manifest);
            // TODO: Handle exceptions
        } catch (ParserConfigurationException pce) {
        } catch (SAXException se) {
        } catch (IOException ioe) {
        }

        Element docEle = dom.getDocumentElement();
        NodeList nl = docEle.getElementsByTagName("file");

        if (nl != null && nl.getLength() > 0) {
            for (int i = nl.getLength(); i >= 0; i--) {
                Element el = (Element) nl.item(i);

                if (el != null && el.getAttribute("path").equalsIgnoreCase("hon-x86")) {

                    String gameVersion = el.getAttribute("version");
                    logger.error("GAME: gameversion: " + gameVersion);

                    return gameVersion;
                }
            }
        }

        return "*";
    }

    private String getGameVersionMac(File manifest) throws FileNotFoundException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document dom = null;

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(manifest);
            // TODO: Handle exceptions
        } catch (ParserConfigurationException pce) {
        } catch (SAXException se) {
        } catch (IOException ioe) {
        }

        Element docEle = dom.getDocumentElement();
        NodeList nl = docEle.getElementsByTagName("file");

        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                Element el = (Element) nl.item(i);

                if (el.getAttribute("path").equalsIgnoreCase("HoN")) {

                    String gameVersion = el.getAttribute("version");
                    logger.error("GAME: gameversion: " + gameVersion);

                    return gameVersion;
                }
            }
        }

        return "*";
    }

    /**
     *
     * @param buffer is the search place.
     * @param needle is the thing that is you are looking for.
     * @return
     * <br/><b>-1</b> if nothing was found.
     * <br/>A integer of the position found.
     */
    private int FindInByteStream(byte[] buffer, byte[] needle) {
        for (int i = 0; i < (buffer.length - needle.length); i++) {
            int j;
            for (j = 0; j < needle.length; j++) {
                if (buffer[i + j] != needle[j]) {
                    break;
                }
            }
            if (j >= needle.length) {
                return i;
            }
        }
        return -1;
    }

    /**
     * This method is required to be used before using the other methods in this class.
     * @param path to the HoN folder.
     */
    public void setPath(File path) {
        this.path = path.getAbsolutePath();
    }

    /**
     * @return Absoluth path to the HoN folder
     */
    public String getPath() {
        return path;
    }

    /**
     * @param version of the HoN.
     */
    private void setVersion(String version) {
        this.version = version;
    }

    public File getHonExecutable() throws FileNotFoundException {
        if (path == null) {
            path = findHonFolder();
            if (path == null) {
                throw new FileNotFoundException();
            }
        }
        File honWindows = new File(path + File.separator + "hon.exe");
        File honLinux = new File(path + File.separator + "hon-x86");
        File honLinux64 = new File(path + File.separator + "hon-x86_64");
        File honMac = new File(path);
        if (OS.isWindows()) {
            if (honWindows.exists()) {
                return honWindows;
            }
        } else if (OS.isLinux()) {
            if (honLinux64.exists()) {
                return honLinux64;
            } else if (honLinux.exists()) {
                return honLinux;
            }
        } else if (OS.isMac()) {
            if (honMac.exists()) {
                return honMac;
            }
        }
        return null;
    }
}
