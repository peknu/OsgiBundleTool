package com.hm.cms.blueprints;

import org.apache.ivy.osgi.core.ExportPackage;
import org.apache.ivy.osgi.core.ManifestParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

public class BundleFile {
    private final String bundleFileName;
    private final File bundleJarFile;
    private BundleInfo cachedBundleInfo;

    /**
     *
     * @param bundleFileName correct file name of the bundle.jar file, ex: org.apache.sling.commons.log-4.0.2.jar
     * @param bundleJarFile bundle.jar file, ex: bundle10\version0.0\bundle.jar
     */
    public BundleFile(String bundleFileName, File bundleJarFile) {
        this.bundleFileName = bundleFileName;
        this.bundleJarFile = bundleJarFile;
    }

    public BundleInfo getBundleInfo() throws SAXException, ParserConfigurationException, XPathExpressionException, IOException, ParseException {
        if (cachedBundleInfo != null) {
            return cachedBundleInfo;
        }
        List<MavenCoordinates> mavenCoordinates = getMavenCoordinates(bundleJarFile, bundleFileName);
        ManifestInfo manifestInfo = getManifestInfo(bundleJarFile, bundleFileName);
        cachedBundleInfo = new BundleInfo(bundleJarFile.getAbsolutePath(), manifestInfo, mavenCoordinates);
        return cachedBundleInfo;
    }

    /**
     * Extract pom.xml file from the bundle.jar embedded file
     * @return true if the pom.xml file could be extracted, indicating that the bundle is ok to install
     * (of if the bundle.jar has correct "main" maven coordinates)
     */
    public boolean extractPomFile() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, ParseException {
        JarFile jarBundleFile = new JarFile(bundleJarFile);
        BundleInfo bundleInfo = getBundleInfo();
        MavenCoordinates mainMavenCoordinates = bundleInfo.getMainMavenCoordinates();
        if (mainMavenCoordinates != null) {
            ZipEntry entry = jarBundleFile.getEntry(mainMavenCoordinates.getPomFilePath());
            try (InputStream pomXmlInputStream = jarBundleFile.getInputStream(entry)) {
                Files.copy(pomXmlInputStream, Paths.get(bundleJarFile.getParent() + "\\pom.xml"), StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        }
        return false;
    }

    public String getLocalMvnInstallWindowsCommand() {
        String relativeParentPath = getRelativeParentPath(bundleJarFile);
        return "cmd /c \"mvn install:install-file -Dfile=" + relativeParentPath + "\\bundle.jar"  + " -DpomFile=" + relativeParentPath + "\\pom.xml -Dpackaging=jar\"";
    }

    public String getLocaleMvnInstallMacOSCommand() {
        String relativeParentPath = getRelativeParentPathForMacOs(bundleJarFile);
        return "mvn install:install-file -Dfile=" + relativeParentPath + "/bundle.jar"  + " -DpomFile=" + relativeParentPath + "/pom.xml -Dpackaging=jar";
    }

    public String getRemoteMvnInstallWindowsCommand() {
        return "TODO"; //TODO
    }

    private String getRelativeParentPathForMacOs(File file) {
        return getRelativeParentPath(file).replace('\\', '/');
    }

    private String getRelativeParentPath(File file) {
        File parentPath = file.getParentFile();
        String root = parentPath.getParentFile().getParent();
        return parentPath.getPath().substring(root.length()+ 1);
    }

    private List<MavenCoordinates> getMavenCoordinates(File file, String fileName) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
        JarFile jarFile = new JarFile(file);
        Enumeration<JarEntry> enumeration = jarFile.entries();
        List<MavenCoordinates> result = new ArrayList<>();
        while (enumeration.hasMoreElements()) {
            JarEntry pomFileEntry = enumeration.nextElement();
            String entryName = pomFileEntry.getName();
            if (entryName.startsWith("META-INF/maven/") && entryName.endsWith("pom.properties")) {
                String pomFile = entryName.substring(0, entryName.lastIndexOf('.')) + ".xml";
                result.add(getMavenCoordinatesFromPomProperties(jarFile, pomFileEntry, pomFile, fileName));
            }
        }
        return result;
    }

    private MavenCoordinates getMavenCoordinatesFromPomProperties(JarFile jarFile, JarEntry pomPropertiesFileEntry, String pomFile, String fileName) throws IOException {
        try (InputStream pomPropertiesFileStream = jarFile.getInputStream(pomPropertiesFileEntry)) {
            Properties properties = new Properties();
            properties.load(pomPropertiesFileStream);
            String groupId = properties.getProperty("groupId");
            String artifactId = properties.getProperty("artifactId");
            String version = properties.getProperty("version");
            if (artifactId.contains("twitter4j")) {
                System.out.println(new MavenCoordinates(groupId, artifactId, version, fileName, pomFile) + " -> " + jarFile.getName());
            }
            return new MavenCoordinates(groupId, artifactId, version, fileName, pomFile);
        }
    }

    private ManifestInfo getManifestInfo(File file, String bundleFileName) throws IOException, ParseException {
        JarFile jarFile = new JarFile(file);
        Manifest manifest = jarFile.getManifest();
        try {
            org.apache.ivy.osgi.core.BundleInfo bundleInfo = ManifestParser.parseManifest(manifest);
            Set<ExportPackage> exports = bundleInfo.getExports();
            List<String> exportedPackages = new ArrayList<>();
            for (ExportPackage exportPackage : exports) {
                exportedPackages.add(exportPackage.getName());
            }
            return new ManifestInfo(bundleFileName, file.getName(), exportedPackages);
        } catch (ParseException e) {
            List<String> exportedPackages = new ArrayList<>();
            exportedPackages.add("No information available");
            System.out.println("Error getting Manifest info for bundle " + file.getCanonicalPath());
            return new ManifestInfo(bundleFileName, file.getName(), exportedPackages);
        }
    }
}
