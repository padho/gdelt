package com.teslagov.gdelt.models;

import java.io.Serializable;

/**
 * @author Kevin Chen
 */
public class GdeltDownloadResultResource implements Serializable
{
    private boolean downloadedSuccessfully;

    private int recordsLoaded;

    private int recordsFailed;

    public boolean getDownloadedSuccessfully()
    {
        return downloadedSuccessfully;
    }

    public void setDownloadedSuccessfully(boolean downloadedSuccessfully)
    {
        this.downloadedSuccessfully = downloadedSuccessfully;
    }

    public int getRecordsLoaded()
    {
        return recordsLoaded;
    }

    public void setRecordsLoaded(int recordsLoaded)
    {
        this.recordsLoaded = recordsLoaded;
    }

    public int getRecordsFailed()
    {
        return recordsFailed;
    }

    public void setRecordsFailed(int recordsFailed)
    {
        this.recordsFailed = recordsFailed;
    }
}
