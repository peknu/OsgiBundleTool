package com.hm.cms.blueprints;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

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
            if (bundleFile.extractPomFile()) {
                installCommand.append(bundleFile.getRemoteMvnInstallWindowsCommand()).append("\n");
            }
        }
        return installCommand.toString();
    }
}
