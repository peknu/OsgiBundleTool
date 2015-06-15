import java.util.List;

public class BundleXmlInfo {
    private final ManifestInfo manifestInfo;
    private final List<MavenCoordinates> mavenCoordinates;

    public BundleXmlInfo(ManifestInfo manifestInfo, List<MavenCoordinates> mavenCoordinates) {
        this.manifestInfo = manifestInfo;
        this.mavenCoordinates = mavenCoordinates;
    }

    public ManifestInfo getManifestInfo() {
        return manifestInfo;
    }

    public List<MavenCoordinates> getMavenCoordinates() {
        return mavenCoordinates;
    }

    public MavenCoordinates getMainMavenCoordinates() {
        if (mavenCoordinates.size() > 1) {
            String filename = manifestInfo.getBundleJarFile();
            String prefix = filename.substring(0, filename.lastIndexOf('-'));
            for (MavenCoordinates mavenCoordinate : mavenCoordinates) {
                if (mavenCoordinate.getArtifactId().equals(prefix)) {
                    return mavenCoordinate;
                }
            }
        } else if (mavenCoordinates.size() == 1) {
            return mavenCoordinates.get(0);
        }
        return null;
    }

    public String getPomXml() {
        StringBuilder result = new StringBuilder();
        result.append(manifestInfo.getManifestXml()).append("\n");
        MavenCoordinates mavenCoordinate = getMainMavenCoordinates();
        if (mavenCoordinate != null) {
            result.append(mavenCoordinate.getDependencyXml()).append("\n");
        } else {
            throw new IllegalStateException("No main pom information forund in file " + manifestInfo.getBundleJarFile());
        }
        return result.toString();
    }
}
