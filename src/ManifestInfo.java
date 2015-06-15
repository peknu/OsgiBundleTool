import java.util.List;

public class ManifestInfo {
    /*
    <!--
                bundle jar:
                    mongo-java-driver-2.9.1.jar
                exported packages:
                    com.mongodb
                    com.mongodb.gridfs
                    com.mongodb.io
                    com.mongodb.util
                    org.bson
                    org.bson.io
                    org.bson.types
                    org.bson.util
                    org.bson.util.annotations
                embedded pom.xml path:
                    no pom.xml found
                error messages:
                    could not find any embedded pom files
            -->
     */
    private final String bundleJarFile;
    private final List<String> exportedPackages;

    public ManifestInfo(String bundleJarFile, List<String> exportedPackages) {
        this.bundleJarFile = bundleJarFile;
        this.exportedPackages = exportedPackages;
    }

    public String getBundleJarFile() {
        return bundleJarFile;
    }

    public List<String> getExportedPackages() {
        return exportedPackages;
    }

    @Override
    public String toString() {
        return "ManifestInfo{" +
                "bundleJarFile='" + bundleJarFile + '\'' +
                ", exportedPackages=" + exportedPackages +
                '}';
    }

    public String getManifestXml() {
        return "            <!--\n" +
                "                bundle jar:\n" +
                "                    " + bundleJarFile + "\n" +
                "                exported packages:\n" +
                getExportPackageString() +
                "            -->";
    }

    private String getExportPackageString() {
        StringBuilder result = new StringBuilder();
        for (String packageName : exportedPackages) {
            result.append("                    " + packageName + "\n");
        }
        return result.toString();
    }
}
