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
    /*public static void main(String[] args) {
        File dir = new File("F:\\AEM61");
        File[] files = dir.listFiles(pathname -> pathname.getName().endsWith(".ser"));
        for (File file : files) {
            int index = file.getName().lastIndexOf("-");
            if (index > 0) {
                System.out.println(file.getName() + " -> " + file.getName().substring(0, index));
                file.renameTo(new File("F:\\AEM61\\" + file.getName().substring(0, index)));
            }
        }
    }*/

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, ParseException {
        File dir = new File("F:\\AEM61");
        File[] files = dir.listFiles(pathname -> pathname.getName().endsWith(".jar"));
        List<BundleXmlInfo> bundleXmlInfoList = new ArrayList<>();
        for (File file : files) {
            List<MavenCoordinates> mavenCoordinates = getMavenCoordinates(file);
            ManifestInfo manifestInfo = getManifestInfo(file);
            BundleXmlInfo bundleInfo = new BundleXmlInfo(manifestInfo, mavenCoordinates);
            bundleXmlInfoList.add(bundleInfo);
        }
        PomProducer pomProducer = new PomProducer("com.hm.cms.cqblueprints", "cqdependencies", "6.1.0", bundleXmlInfoList);
        //pomProducer.getXmlContent();
        //System.out.println(pomProducer.getXmlContent());
        //validateBundleInfoList(bundleXmlInfoList);
        dumpFile(pomProducer.getXmlContent());
        //installBundlesToLocalRepo(bundleXmlInfoList);
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
        Files.write(Paths.get("F:\\AEM61", "installLocal.bat"), installCommands.toString().getBytes());
    }

    private static String installBundleToLocalRepo(BundleXmlInfo bundleXmlInfo) throws IOException {
        String jarFile = bundleXmlInfo.getManifestInfo().getBundleJarFile();

        if (bundleXmlInfo.getMainMavenCoordinates() != null) {
            System.out.println("Installing bundle: " + jarFile + " with pom.xml: " + bundleXmlInfo.getMainMavenCoordinates().getPomFilePath());

            String folderName = jarFile.substring(0, jarFile.lastIndexOf('.'));
            Path targetDir = Paths.get("F:\\AEM61\\" + folderName);
            /*if (!Files.exists(targetDir)) {
                System.out.println("Creating directory: " + targetDir);
                Files.createDirectory(targetDir);
                Files.copy(Paths.get("F:\\AEM61\\" + jarFile), Paths.get(targetDir + "\\" + jarFile), StandardCopyOption.REPLACE_EXISTING);
            }
            JarFile jarBundleFile = new JarFile("F:\\AEM61\\" + jarFile);
            ZipEntry entry = jarBundleFile.getEntry(bundleXmlInfo.getMainMavenCoordinates().getPomFilePath());
            try(InputStream pomXmlInputStream = jarBundleFile.getInputStream(entry)) {
                Files.copy(pomXmlInputStream, Paths.get(targetDir + "\\pom.xml"), StandardCopyOption.REPLACE_EXISTING);
            }*/
            return "cmd /c \"mvn install:install-file -Dfile=" + targetDir + "\\" + jarFile + " -DpomFile=" + targetDir + "\\pom.xml -Dpackaging=jar\"";
        }
        return null;
    }

    private static void validateBundleInfoList(List<BundleXmlInfo> bundleXmlInfoList) {
        for (BundleXmlInfo bundleXmlInfo : bundleXmlInfoList) {
            validateBundleXmlInfo(bundleXmlInfo);
        }
    }

    private static void validateBundleXmlInfo(BundleXmlInfo bundleXmlInfo) {
        if (bundleXmlInfo.getMavenCoordinates().size() > 1) {
            System.out.println("File: " + bundleXmlInfo.getManifestInfo().getBundleJarFile());
            for (MavenCoordinates mavenCoordinates : bundleXmlInfo.getMavenCoordinates()) {
                System.out.println(mavenCoordinates.toString());
            }
        }
    }

    private static void dumpFile(String content) throws IOException {
        Files.write(Paths.get("F:\\AEM61", "pom.xml"), content.getBytes());
    }

    private static String getDependencies(List<MavenCoordinates> mavenCoordinates) {
        StringBuilder result = new StringBuilder();
        for (MavenCoordinates mavenCoordinate : mavenCoordinates) {
            result.append(mavenCoordinate.getDependencyXml()).append("\n");
        }
        return result.toString();
    }

    private static List<MavenCoordinates> getMavenCoordinates(File file) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
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
                result.add(getMavenCoordinatesFromPomProperties(jarFile, pomFileEntry, pomFile));
            }
        }
        return result;
    }

    private static MavenCoordinates getMavenCoordinatesFromPomProperties(JarFile jarFile, JarEntry pomPropertiesFileEntry, String pomFile) throws IOException {
        try (InputStream pomPropertiesFileStream = jarFile.getInputStream(pomPropertiesFileEntry)) {
            Properties properties = new Properties();
            properties.load(pomPropertiesFileStream);
            String groupId = properties.getProperty("groupId");
            String artifactId = properties.getProperty("artifactId");
            String version = properties.getProperty("version");
            return new MavenCoordinates(groupId, artifactId, version, jarFile.getName(), pomFile);
        }
    }

    private static ManifestInfo getManifestInfo(File file) throws IOException, ParseException {
        JarFile jarFile = new JarFile(file);
        Manifest manifest = jarFile.getManifest();
        BundleInfo bundleInfo = ManifestParser.parseManifest(manifest);
        Set<ExportPackage> exports = bundleInfo.getExports();
        List<String> exportedPackages = new ArrayList<>();
        for (ExportPackage exportPackage : exports) {
            exportedPackages.add(exportPackage.getName());
        }
        return new ManifestInfo(file.getName(), exportedPackages);
    }

    /*private static MavenCoordinates getMavenCoordinates(JarFile jarFile, JarEntry pomFileEntry) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try (InputStream pomFileStream = jarFile.getInputStream(pomFileEntry)) {
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            Document pomXmlDocument = documentBuilder.parse(pomFileStream);
            String groupId = getGroupId(pomXmlDocument);
            String artifactId = getArtifactId(pomXmlDocument);
            String version = getVersion(pomXmlDocument);
            return new MavenCoordinates(groupId, artifactId, version, jarFile.getName());
        }
    }

    private static String getGroupId(Document pomXmlDocument) throws XPathExpressionException {
        DeferredTextImpl groupIdNode = getXpathValueNode(pomXmlDocument, "/project/groupId[1]/text()");
        if (groupIdNode != null) {
            return groupIdNode.getTextContent();
        } else {
            return getXpathValue(pomXmlDocument, "/project/parent/groupId[1]/text()");
        }
    }

    private static String getArtifactId(Document pomXmlDocument) throws XPathExpressionException {
        String artifactId = getXpathValue(pomXmlDocument, "/project/artifactId[1]/text()");
        if (artifactId.contains("${pom.groupId")) {
            artifactId = artifactId.replace("${pom.groupId}", getGroupId(pomXmlDocument));
            //System.out.println("### " + artifactId);
        }
        if (artifactId.contains("${")) {
            Pattern pattern = Pattern.compile(".*\\$\\{(.*)\\}.*");
            Matcher matcher = pattern.matcher(artifactId);
            if (matcher.matches()) {
                String property = matcher.group(1);
                String propertyValue = getXpathValue(pomXmlDocument, "/project/properties/" + property + "[1]/text()");
                artifactId = artifactId.replace("${" + property + "}", propertyValue);
                //System.out.println("### " + artifactId);
            }
        }
        return artifactId;
    }

    private static String getVersion(Document pomXmlDocument) throws XPathExpressionException {
        DeferredTextImpl groupIdNode = getXpathValueNode(pomXmlDocument, "/project/version[1]/text()");
        if (groupIdNode != null) {
            return groupIdNode.getTextContent();
        } else {
            return getXpathValue(pomXmlDocument, "/project/parent/version[1]/text()");
        }
    }

    private static String getXpathValue(Document pomXmlDocument, String xpathExpr) throws XPathExpressionException {
        return getXpathValueNode(pomXmlDocument, xpathExpr).getTextContent();
    }

    private static DeferredTextImpl getXpathValueNode(Document pomXmlDocument, String xpathExpr) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile(xpathExpr);
        return (DeferredTextImpl) expr.evaluate(pomXmlDocument, XPathConstants.NODE);
    }*/
}
