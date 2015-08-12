package com.hm.cms.blueprints;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    /**
     * Program to extract and install OSGi bundle information.
     * Operates on a directory structure directly from crx-quickstart\launchpad\felix\ or a copy of this structure.
     * Should be a "clean" version of AEM 6.1.0 with no extra bundles installed
     *
     * The base dir below should point to the root folder of the structure.
     * For example:
     * F:\AEM61V2
     *   bundle0
     *   bundle1
     *   ...
     *   bundle442
     *
     *   The program is capable of:
     *   1) Generate a pom.xml file that can be used in the dependencyManagement section of the parent pom file:
     *      <dependency>
     *        <groupId>com.hm.cms.cqblueprints</groupId>
     *        <artifactId>cqdependencies</artifactId>
     *        <version>6.1.0</version>
     *        <type>pom</type>
     *        <scope>import</scope>
     *      </dependency>
     *
     *   2) Extract pom.xml files from installed bundles and generate local or remote install script for Maven repo
     *
     *   Additional:
     *     com.day.cq.parent version 42 must be installed as a copy of version 40
     *     com.adobe.granite.parent version 33, 50, 52 and 54 must be installed as a copy of version 32
     *   The reason is that these versions are not available in any public repositories and can not be extracted from
     *   information in the installed OSGi bundles.
     */
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, ParseException {
        File dir = new File("F:\\AEM61V2");
        BundleCrawler bundleCrawler = new BundleCrawler(dir);
        List<BundleInfo> bundleInfoList = new ArrayList<>();
        List<BundleFile> bundleFileList = new ArrayList<>();
        for (File baseBundleDir : bundleCrawler.getBundleBaseDirectories()) {
            BundleDir bundleDir = new BundleDir(baseBundleDir);
            if (bundleDir.existBundleFile()) {
                try {
                    BundleFile bundleFile = bundleDir.getBundleFile();
                    bundleFileList.add(bundleFile);

                    BundleInfo bundleInfo = bundleFile.getBundleInfo();
                    bundleInfoList.add(bundleInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error processing bundle");
                }
            } else {
                System.out.println("No bundle file in directory: " + baseBundleDir.getName());
            }
        }
        PomProducer pomProducer = new PomProducer("com.hm.cms.cqblueprints", "cqdependencies", "6.1.0", bundleInfoList);
        MavenRepoInstaller mavenRepoInstaller = new MavenRepoInstaller(bundleFileList);

        System.out.println(pomProducer.getXmlContent());
        //System.out.println(mavenRepoInstaller.getInstallLocalScript(true));
        //Files.write(Paths.get("F:\\AEM61V2", "cqdependencies-6.1.0.pom"), pomProducer.getXmlContent().getBytes());
        //Files.write(Paths.get("F:\\AEM61V2", "installLocal.bat"), mavenRepoInstaller.getInstallLocalScript(true).getBytes());
    }
}
