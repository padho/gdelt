package com.midas.gdelt.csv;

import com.midas.gdelt.models.GdeltDownloadResultResource;
import com.midas.gdelt.models.GdeltEventResource;

import java.util.List;

/**
 * @author Kevin Chen
 */
public class GDELTReturnResult
{
    private GdeltDownloadResultResource downloadResult;

    private List<GdeltEventResource> gdeltEventList;

    public GdeltDownloadResultResource getDownloadResult()
    {
        return downloadResult;
    }

    public void setDownloadResult(GdeltDownloadResultResource downloadResult)
    {
        this.downloadResult = downloadResult;
    }

    public List<GdeltEventResource> getGdeltEventList()
    {
        return gdeltEventList;
    }

    public void setGdeltEventList(List<GdeltEventResource> gdeltEventList)
    {
        this.gdeltEventList = gdeltEventList;
    }
}
