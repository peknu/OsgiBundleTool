package com.hm.cms.blueprints;

import java.util.List;

public class BundleXmlInfo {
    private final String absoluteBundleFilePath;
    private final ManifestInfo manifestInfo;
    private final List<MavenCoordinates> mavenCoordinates;

    public BundleXmlInfo(String absoluteBundleFilePath, ManifestInfo manifestInfo, List<MavenCoordinates> mavenCoordinates) {
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

    public String getPomXml(boolean excluded) {
        StringBuilder result = new StringBuilder();
        result.append(manifestInfo.getManifestXml()).append("\n");
        MavenCoordinates mavenCoordinate = getMainMavenCoordinates();
        if (mavenCoordinate != null) {
            if (excluded) {
                result.append("<!--\n");
            }
            result.append(mavenCoordinate.getDependencyXml()).append("\n");
            if (excluded) {
                result.append("\n-->");
            }
        } else {
            throw new IllegalStateException("No main pom information found in file " + manifestInfo.getBundleJarFileWithVersion());
        }
        return result.toString();
    }
}
