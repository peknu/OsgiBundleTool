import java.util.List;

public class PomProducer {
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final List<BundleXmlInfo> bundleXmlInfoList;

    public PomProducer(String groupId, String artifactId, String version, List<BundleXmlInfo> bundleXmlInfoList) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.bundleXmlInfoList = bundleXmlInfoList;
    }

    public String getXmlContent() {
        StringBuilder result = new StringBuilder();
        result.append(getHeading()).append("\n\n").
                append(getCoordinatesSection()).append("\n\n").
                append(getDecription()).append("\n\n").
                append(getDependencyManagementSection()).append("\n").
                append("</project>");
        return result.toString();
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
                getDependencies() +
                "        </dependencies>\n" +
                "    </dependencyManagement>";
    }

    private String getDependencies() {
        StringBuilder result = new StringBuilder();
        for (BundleXmlInfo bundleXmlInfo : bundleXmlInfoList) {
            if (bundleXmlInfo.getMavenCoordinates().size() > 0) {
                result.append(bundleXmlInfo.getPomXml()).append("\n");
            }
        }
        return result.toString();
    }
}
