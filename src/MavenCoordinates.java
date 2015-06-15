public class MavenCoordinates {
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String fileName;
    private final String pomFilePath;

    public MavenCoordinates(String groupId, String artifactId, String version, String fileName, String pomFilePath) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.fileName = fileName;
        this.pomFilePath = pomFilePath;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPomFilePath() {
        return pomFilePath;
    }

    @Override
    public String toString() {
        return "MavenCoordinates{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                ", fileName='" + fileName + '\'' +
                ", pomFilePath='" + pomFilePath + '\'' +
                '}';
    }

    public String getDependencyXml() {
        return "            <dependency>\n" +
                "                <groupId>" + groupId + "</groupId>\n" +
                "                <artifactId>" + artifactId + "</artifactId>\n" +
                "                <version>" + version + "</version>\n" +
                "                <scope>provided</scope>\n" +
                "            </dependency>";

    }
}
