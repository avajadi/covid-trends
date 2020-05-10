package org.avajadi.opendata.covid;

public final class Version {

    private static final String VERSION = "${project.version}";
    private static final String TIME = "${build.number}";

    public static String getVersion() {
        return VERSION;
    }
    public static String getBuildTime() { return TIME; }
}
