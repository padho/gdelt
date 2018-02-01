package com.teslagov.gdelt;

import java.io.File;

/**
 * @author Kevin Chen
 */
public class GdeltLastUpdateDownloadConfiguration
{
    private GdeltApi gdeltApi;
    private File directory = GdeltDefaultDirectoryFileFactory.getDefaultDirectory();
    private boolean unzip = true;
    private boolean deleteZipFile = false;

    public GdeltLastUpdateDownloadConfiguration(GdeltApi gdeltApi)
    {
        this.gdeltApi = gdeltApi;
    }

    public GdeltLastUpdateDownloadConfiguration toDirectory(File directory)
    {
        this.directory = directory;
        return this;
    }

    public GdeltLastUpdateDownloadConfiguration unzip(boolean unzip)
    {
        this.unzip = unzip;
        return this;
    }

    public GdeltLastUpdateDownloadConfiguration deleteZipFile(boolean deleteZipFile)
    {
        this.deleteZipFile = deleteZipFile;
        return this;
    }

    public File execute()
    {
        return gdeltApi.downloadLastUpdate(directory, unzip, deleteZipFile);
    }
}
