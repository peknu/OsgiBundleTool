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
        System.out.println(mavenRepoInstaller.getInstallLocalScript(true));

        //Files.write(Paths.get("F:\\AEM61V2", "cqdependencies-6.1.0.pom"), pomProducer.getXmlContent().getBytes());
        //Files.write(Paths.get("F:\\AEM61V2", "installLocal.bat"), mavenRepoInstaller.getInstallLocalScript(true).getBytes());
    }
}
