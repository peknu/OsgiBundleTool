package com.hm.cms.blueprints;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class BundleCrawler {
    private final File baseDir;

    public BundleCrawler(File baseDir) {
        this.baseDir = baseDir;
    }

    public File[] getBundleBaseDirectories() {
        File[] bundleDirectories = baseDir.listFiles(pathname -> pathname.getName().startsWith("bundle") && pathname.isDirectory());
        Arrays.sort(bundleDirectories, new FileNameComparator());
        return bundleDirectories;
    }

    private static class FileNameComparator implements Comparator<File> {
        @Override
        public int compare(File o1, File o2) {
            return getBundleNumber(o1) - getBundleNumber(o2);
        }

        private int getBundleNumber(File file) {
            return Integer.parseInt(file.getName().substring(6));
        }
    }
}
