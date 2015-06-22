package com.hm.cms.blueprints;

import java.util.List;

public class PomProducer {
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final List<BundleInfo> bundleInfoList;

    public PomProducer(String groupId, String artifactId, String version, List<BundleInfo> bundleInfoList) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.bundleInfoList = bundleInfoList;
    }

    public String getXmlContent() {
        return getHeading() + "\n\n" +
                getCoordinatesSection() + "\n\n" +
                getDecription() + "\n\n" +
                getDependencyManagementSection() + "\n" +
                "</project>";
    }

    private String getHeading() {
         return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                 "\n" +
                 "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                 "\n" +
                 "    <modelVersion>4.0.0</modelVersion>";
    }

    private String getCoordinatesSection() {
        return "    <groupId>" + groupId + "</groupId>\n" +
                "    <artifactId>" + artifactId + "</artifactId>\n" +
                "    <version>" + version + "</version>\n" +
                "    <packaging>pom</packaging>";
    }

    private String getDecription() {
        return "    <description>\n" +
                "    <![CDATA[\n" +
                "        Declares all of the artifacts (and their versions) that are available in an out-of-the-box CQ environment. This\n" +
                "        list can then be used by other projects to ensure that they are using the correct version of libraries that are\n" +
                "        available in a CQ environment.\n" +
                "\n" +
                "        To use this list, add this to your project:\n" +
                "\n" +
                "            <dependencyManagement>\n" +
                "                <dependencies>\n" +
                "                    <dependency>\n" +
                "                        <groupId>${project.groupId}</groupId>\n" +
                "                        <artifactId>${project.artifactId}</artifactId>\n" +
                "                        <version>${project.version}</version>\n" +
                "                        <type>pom</type>\n" +
                "                        <scope>import</scope>\n" +
                "                    </dependency>\n" +
                "\n" +
                "                    ...\n" +
                "\n" +
                "                </dependencies>\n" +
                "            </dependencyManagement>\n" +
                "\n" +
                "        then, when you need to use a library provided by CQ, in your dependency section you would simply add:\n" +
                "\n" +
                "            <dependencies>\n" +
                "                <dependency>\n" +
                "                    <groupId>com.day.cq</groupId>\n" +
                "                    <artifactId>cq-commons</artifactId>\n" +
                "                </dependency>\n" +
                "\n" +
                "                ...\n" +
                "\n" +
                "            </dependencies>\n" +
                "\n" +
                "        you do not need to declare a version, or that the scope of the dependency is \"provided\", both of these settings\n" +
                "        are taken from the values in this pom.\n" +
                "    ]]>\n" +
                "    </description>";
    }

    private String getDependencyManagementSection() {
        return "    <dependencyManagement>\n" +
                "        <dependencies>\n" +
                getManualDependencies() + "\n\n" +
                getDependencies() +
                "        </dependencies>\n" +
                "    </dependencyManagement>";
    }

    private String getManualDependencies() {
        return "             <!--________________-->\n" +
                "            <!-- MANUALLY ADDED -->\n" +
                "            <!--________________-->\n" +
                "            <dependency>\n" +
                "                <groupId>javax.servlet.jsp</groupId>\n" +
                "                <artifactId>jsp-api</artifactId>\n" +
                "                <version>2.2</version>\n" +
                "                <scope>provided</scope>\n" +
                "            </dependency>\n" +
                "            <!--\n" +
                "                bundle jar:\n" +
                "                    org.osgi.compendium-4.1.0.jar\n" +
                "                exported packages:\n" +
                "                    info.dmtree\n" +
                "                    info.dmtree.notification\n" +
                "                    info.dmtree.notification.spi\n" +
                "                    info.dmtree.registry\n" +
                "                    info.dmtree.security\n" +
                "                    info.dmtree.spi\n" +
                "                    org.osgi.application\n" +
                "                    org.osgi.service.application\n" +
                "                    org.osgi.service.cm\n" +
                "                    org.osgi.service.component\n" +
                "                    org.osgi.service.deploymentadmin\n" +
                "                    org.osgi.service.deploymentadmin.spi\n" +
                "                    org.osgi.service.device\n" +
                "                    org.osgi.service.event\n" +
                "                    org.osgi.service.http\n" +
                "                    org.osgi.service.io\n" +
                "                    org.osgi.service.log\n" +
                "                    org.osgi.service.metatype\n" +
                "                    org.osgi.service.monitor\n" +
                "                    org.osgi.service.prefs\n" +
                "                    org.osgi.service.provisioning\n" +
                "                    org.osgi.service.upnp\n" +
                "                    org.osgi.service.useradmin\n" +
                "                    org.osgi.service.wireadmin\n" +
                "                    org.osgi.util.gsm\n" +
                "                    org.osgi.util.measurement\n" +
                "                    org.osgi.util.mobile\n" +
                "                    org.osgi.util.position\n" +
                "                    org.osgi.util.tracker\n" +
                "                    org.osgi.util.xml\n" +
                "                embedded pom.xml path:\n" +
                "                    no pom.xml found\n" +
                "                error messages:\n" +
                "                    could not find any embedded pom files\n" +
                "            -->\n" +
                "            <dependency>\n" +
                "                <groupId>org.osgi</groupId>\n" +
                "                <artifactId>org.osgi.compendium</artifactId>\n" +
                "                <version>4.2.0</version>\n" +
                "                <scope>provided</scope>\n" +
                "            </dependency>\n" +
                "\n" +
                "            <!--\n" +
                "                bundle jar:\n" +
                "                    org.osgi.core-4.1.0.jar\n" +
                "                exported packages:\n" +
                "                    org.osgi.framework\n" +
                "                    org.osgi.service.condpermadmin\n" +
                "                    org.osgi.service.packageadmin\n" +
                "                    org.osgi.service.permissionadmin\n" +
                "                    org.osgi.service.startlevel\n" +
                "                    org.osgi.service.url\n" +
                "                embedded pom.xml path:\n" +
                "                    no pom.xml found\n" +
                "                error messages:\n" +
                "                    could not find any embedded pom files\n" +
                "            -->\n" +
                "            <dependency>\n" +
                "                <groupId>org.osgi</groupId>\n" +
                "                <artifactId>org.osgi.core</artifactId>\n" +
                "                <version>4.2.0</version>\n" +
                "                <scope>provided</scope>\n" +
                "            </dependency>\n" +
                "\n" +
                "            <!--\n" +
                "                bundle jar:\n" +
                "                    servlet-api-2.5.jar\n" +
                "                exported packages:\n" +
                "                    none\n" +
                "                embedded pom.xml path:\n" +
                "                    no pom.xml found\n" +
                "                error messages:\n" +
                "                    could not find any embedded pom files\n" +
                "                    could not find Export-Package attribute in Manifest file\n" +
                "            -->\n" +
                "            <dependency>\n" +
                "                <groupId>javax.servlet</groupId>\n" +
                "                <artifactId>servlet-api</artifactId>\n" +
                "                <version>2.5</version>\n" +
                "                <scope>provided</scope>\n" +
                "            </dependency>\n" +
                "            <dependency>\n" +
                "                <groupId>javax.jcr</groupId>\n" +
                "                <artifactId>jcr</artifactId>\n" +
                "                <version>2.0</version>\n" +
                "                <scope>provided</scope>\n" +
                "            </dependency>\n" +
                "            <!--____________________-->\n" +
                "            <!-- END MANUALLY ADDED -->\n" +
                "            <!--____________________-->";
    }

    private String getDependencies() {
        StringBuilder result = new StringBuilder();
        for (BundleInfo bundleInfo : bundleInfoList) {
            if (bundleInfo.getMavenCoordinates().size() > 0) {
                boolean excludedDependency = isExcludedDependency(bundleInfo);
                result.append(bundleInfo.getPomXml(excludedDependency)).append("\n");
            }
        }
        return result.toString();
    }

    private boolean isExcludedDependency(BundleInfo bundleInfo) {
        MavenCoordinates mavenCoordinates = bundleInfo.getMainMavenCoordinates();
        if (mavenCoordinates != null) {
            String groupId = mavenCoordinates.getGroupId();
            String artifactId = mavenCoordinates.getArtifactId();
            if ("com.adobe.xmp.worker".equals(groupId) && "files.native.fragment".equals(artifactId)) {
                return true;
            }
        }
        return false;
    }
}
