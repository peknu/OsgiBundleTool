package com.hm.cms.blueprints;

import org.apache.ivy.osgi.core.BundleInfo;
import org.apache.ivy.osgi.core.ExportPackage;
import org.apache.ivy.osgi.core.ManifestParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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

public class Main {

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, ParseException {
        File dir = new File("F:\\AEM61V2");
        BundleCrawler bundleCrawler = new BundleCrawler(dir);
        List<BundleXmlInfo> bundleXmlInfoList = new ArrayList<>();
        for (File baseBundleDir : bundleCrawler.getBundleBaseDirectories()) {
            BundleDir bundleDir = new BundleDir(baseBundleDir);

            File bundleFile = bundleDir.getBundleJarFile();
            if (bundleFile.exists()) {
                try {
                    //System.out.println("Processing bundle: " + bundleFile.getCanonicalPath());
                    String bundleFileName = bundleDir.getBundleJarFileName();
                    List<MavenCoordinates> mavenCoordinates = getMavenCoordinates(bundleFile, bundleFileName);
                    ManifestInfo manifestInfo = getManifestInfo(bundleFile, bundleFileName);
                    BundleXmlInfo bundleInfo = new BundleXmlInfo(bundleFile.getAbsolutePath(), manifestInfo, mavenCoordinates);
                    bundleXmlInfoList.add(bundleInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error processing bundle");
                }
            } else {
                System.out.println("No bundle file in directory: " + baseBundleDir.getName());
            }
        }
        PomProducer pomProducer = new PomProducer("com.hm.cms.cqblueprints", "cqdependencies", "6.1.0", bundleXmlInfoList);
        //System.out.println(pomProducer.getXmlContent());
        //validateBundleInfoList(bundleXmlInfoList);
        //dumpFile(pomProducer.getXmlContent());
        //installBundlesToLocalRepo(bundleXmlInfoList);
    }

    private static String getBundleJarFileName(File file) throws IOException {
        Path path = Paths.get(file.toURI());
        List<String> content = Files.readAllLines(path);
        String line = content.get(1);
        int index = Math.max(line.lastIndexOf('/'), line.lastIndexOf('\\'));
        return line.substring(index + 1);
    }

    private static void installBundlesToLocalRepo(List<BundleXmlInfo> bundleXmlInfoList) throws IOException {
        StringBuilder installCommands = new StringBuilder();
        for (BundleXmlInfo bundleXmlInfo : bundleXmlInfoList) {
            String command = installBundleToLocalRepo(bundleXmlInfo);
            if (command != null) {
                installCommands.append(command).append("\n");
            }
            //break;
        }
        System.out.println(installCommands.toString());
        Files.write(Paths.get("F:\\AEM61V2", "installLocal.sh"), installCommands.toString().getBytes());
    }

    private static String installBundleToLocalRepo(BundleXmlInfo bundleXmlInfo) throws IOException {
        String bundleJarFile = bundleXmlInfo.getAbsoluteBundleFilePath();
        File file = new File(bundleJarFile);
        if (bundleXmlInfo.getMainMavenCoordinates() != null) {
            System.out.println("Installing bundle: " + bundleJarFile + " with pom.xml: " + bundleXmlInfo.getMainMavenCoordinates().getPomFilePath());

            //String folderName = jarFile.substring(0, jarFile.lastIndexOf('.'));
            //Path targetDir = Paths.get("F:\\AEM61\\" + folderName);
            /*if (!Files.exists(targetDir)) {
                System.out.println("Creating directory: " + targetDir);
                Files.createDirectory(targetDir);
                Files.copy(Paths.get("F:\\AEM61\\" + jarFile), Paths.get(targetDir + "\\" + jarFile), StandardCopyOption.REPLACE_EXISTING);
            }*/
            JarFile jarBundleFile = new JarFile(bundleJarFile);
            ZipEntry entry = jarBundleFile.getEntry(bundleXmlInfo.getMainMavenCoordinates().getPomFilePath());
            try(InputStream pomXmlInputStream = jarBundleFile.getInputStream(entry)) {
                Files.copy(pomXmlInputStream, Paths.get(file.getParent() + "\\pom.xml"), StandardCopyOption.REPLACE_EXISTING);
            }
            //return "cmd /c \"mvn install:install-file -Dfile=" + file.getParent() + "\\bundle.jar"  + " -DpomFile=" + file.getParent() + "\\pom.xml -Dpackaging=jar\"";
            return "mvn install:install-file -Dfile=" + getParentPath(file) + "/bundle.jar"  + " -DpomFile=" + getParentPath(file) + "/pom.xml -Dpackaging=jar";
        }
        return null;
    }

    private static String getParentPath(File file) {
        return file.getParent().substring(11).replace('\\','/');
    }

    private static void validateBundleInfoList(List<BundleXmlInfo> bundleXmlInfoList) {
        for (BundleXmlInfo bundleXmlInfo : bundleXmlInfoList) {
            validateBundleXmlInfo(bundleXmlInfo);
        }
    }

    private static void validateBundleXmlInfo(BundleXmlInfo bundleXmlInfo) {
        if (bundleXmlInfo.getMavenCoordinates().size() > 1) {
            System.out.println("File: " + bundleXmlInfo.getManifestInfo().getBundleJarFileWithVersion());
            for (MavenCoordinates mavenCoordinates : bundleXmlInfo.getMavenCoordinates()) {
                System.out.println(mavenCoordinates.toString());
            }
        }
    }

    private static void dumpFile(String content) throws IOException {
        Files.write(Paths.get("F:\\AEM61V2", "pom.xml"), content.getBytes());
    }

    private static String getDependencies(List<MavenCoordinates> mavenCoordinates) {
        StringBuilder result = new StringBuilder();
        for (MavenCoordinates mavenCoordinate : mavenCoordinates) {
            result.append(mavenCoordinate.getDependencyXml()).append("\n");
        }
        return result.toString();
    }

    private static List<MavenCoordinates> getMavenCoordinates(File file, String fileName) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
        JarFile jarFile = new JarFile(file);
        //System.out.println(jarFile.getName());
        Enumeration<JarEntry> enumeration = jarFile.entries();
        List<MavenCoordinates> result = new ArrayList<>();
        while (enumeration.hasMoreElements()) {
            JarEntry pomFileEntry = enumeration.nextElement();
            String entryName = pomFileEntry.getName();
            //if (entryName.startsWith("META-INF/maven/") && entryName.endsWith("pom.xml")) {
            if (entryName.startsWith("META-INF/maven/") && entryName.endsWith("pom.properties")) {
                //System.out.println("Entry: " + pomFileEntry.getName());
                //result.add(getMavenCoordinates(jarFile, pomFileEntry));
                String pomFile = entryName.substring(0, entryName.lastIndexOf('.')) + ".xml";
                result.add(getMavenCoordinatesFromPomProperties(jarFile, pomFileEntry, pomFile, fileName));
            }
        }
        return result;
    }

    private static MavenCoordinates getMavenCoordinatesFromPomProperties(JarFile jarFile, JarEntry pomPropertiesFileEntry, String pomFile, String fileName) throws IOException {
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

    private static ManifestInfo getManifestInfo(File file, String bundleFileName) throws IOException, ParseException {
        JarFile jarFile = new JarFile(file);
        Manifest manifest = jarFile.getManifest();
        try {
            BundleInfo bundleInfo = ManifestParser.parseManifest(manifest);
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
