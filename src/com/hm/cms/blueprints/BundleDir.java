package com.hm.cms.blueprints;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class BundleDir {
    private final File bundleDir;

    /**
     *
     * @param bundleDir Base bundle directory, for example:
     *                  D:\hmonline\servers\author\crx-quickstart\launchpad\felix\bundle436
     *
     */
    public BundleDir(File bundleDir) {
        this.bundleDir = bundleDir;
    }

    /**
     * Get the jar file name from the second line in the bundle.info file
     * @return The correct filename of the bundle.jar file
     * @throws IOException
     */
    public String getBundleJarFileName() throws IOException {
        File bundleInfoFile = new File(bundleDir, "bundle.info");
        Path path = Paths.get(bundleInfoFile.toURI());
        List<String> content = Files.readAllLines(path);
        String line = content.get(1);
        int index = Math.max(line.lastIndexOf('/'), line.lastIndexOf('\\'));
        return line.substring(index + 1);
    }

    public File getBundleJarFile() {
        return new File(bundleDir, "version0.0\\bundle.jar");
    }

    public boolean existBundleFile() {
        return getBundleJarFile().exists();
    }

    public BundleFile getBundleFile() throws IOException {
        return new BundleFile(getBundleJarFileName(), getBundleJarFile());
    }
}
