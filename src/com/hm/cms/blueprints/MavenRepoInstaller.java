package com.hm.cms.blueprints;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MavenRepoInstaller {
    private final List<BundleFile> bundleFiles;

    public MavenRepoInstaller(List<BundleFile> bundleFiles) {
        this.bundleFiles = bundleFiles;
    }

    public String getInstallLocalScript(boolean isWindows) throws SAXException, ParserConfigurationException, ParseException, XPathExpressionException, IOException {
        StringBuilder installCommand = new StringBuilder();
        for (BundleFile bundleFile : bundleFiles) {
            if (bundleFile.extractPomFile()) {
                installCommand.append(isWindows ? bundleFile.getLocalMvnInstallWindowsCommand() : bundleFile.getLocaleMvnInstallMacOSCommand()).append("\n");
            }
        }
        return installCommand.toString();
    }

    public String getInstallRemoteScript() throws SAXException, ParserConfigurationException, ParseException, XPathExpressionException, IOException {
        StringBuilder installCommand = new StringBuilder();
        for (BundleFile bundleFile : bundleFiles) {
            if (bundleFile.extractPomFile() && includeInRemoteInstallScript(bundleFile.getBundleInfo().getMainMavenCoordinates())) {
                installCommand.append(bundleFile.getRemoteMvnInstallWindowsCommand()).append("\n");
            }
        }
        return installCommand.toString();
    }

    private static final Set<String> GROUP_IDS = new HashSet<>(Arrays.asList(
            "com.adobe.granite", "com.day.cq.dam", "com.day.cq", "com.day.cq.wcm", "com.day.jcr.vault", "org.apache.felix", "com.adobe.xmp", "org.apache.sling", "com.day.commons"));

    private boolean includeInRemoteInstallScript(MavenCoordinates mavenCoordinates) {
        return mavenCoordinates != null && GROUP_IDS.contains(mavenCoordinates.getGroupId());
    }
}
