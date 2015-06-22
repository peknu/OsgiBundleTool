package com.hm.cms.blueprints;

import java.util.List;

public class BundleInfo {
    private final String absoluteBundleFilePath;
    private final ManifestInfo manifestInfo;
    private final List<MavenCoordinates> mavenCoordinates;

    public BundleInfo(String absoluteBundleFilePath, ManifestInfo manifestInfo, List<MavenCoordinates> mavenCoordinates) {
        this.absoluteBundleFilePath = absoluteBundleFilePath;
        this.manifestInfo = manifestInfo;
        this.mavenCoordinates = mavenCoordinates;
    }

    public String getAbsoluteBundleFilePath() {
        return absoluteBundleFilePath;
    }

    public ManifestInfo getManifestInfo() {
        return manifestInfo;
    }

    public List<MavenCoordinates> getMavenCoordinates() {
        return mavenCoordinates;
    }

    /**
     * Get the maven coordinate of the "main" bundle. Some jundle.jar files may contain multiple entries/directories
     * below the META-INF\maven\ directory. In that case, the artifactId + "-" + version should math the correct
     * bundle file name from the bundle.info file.
     *
     * @return The main maven coordinates from the bundle.jar file
     */
    public MavenCoordinates getMainMavenCoordinates() {
        if (mavenCoordinates.size() > 1) {
            String filename = manifestInfo.getBundleJarFileWithVersion();
            for (MavenCoordinates mavenCoordinate : mavenCoordinates) {
                if (filename.equals(mavenCoordinate.getArtifactId() + "-" + mavenCoordinate.getVersion() + ".jar")) {
                    return mavenCoordinate;
                }
            }
        } else if (mavenCoordinates.size() == 1) {
            return mavenCoordinates.get(0);
        }
        return null;
    }

    /**
     * Get the import pom.xml file content for the bundle
     * @param excluded comment out this <dependency> section tn the pom file
     * @return
     */
    public String getPomXml(boolean excluded) {
        StringBuilder result = new StringBuilder();
        result.append(manifestInfo.getManifestXml()).append("\n");
        MavenCoordinates mavenCoordinate = getMainMavenCoordinates();
        if (mavenCoordinate != null) {
            if (excluded) {
                result.append("            <!--\n");
            }
            result.append(mavenCoordinate.getDependencyXml()).append("\n");
            if (excluded) {
                result.append("            -->\n");
            }
        } else {
            throw new IllegalStateException("No main pom information found in file " + manifestInfo.getBundleJarFileWithVersion());
        }
        return result.toString();
    }
}
