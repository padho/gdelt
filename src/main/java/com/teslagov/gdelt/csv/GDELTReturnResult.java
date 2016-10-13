package com.teslagov.gdelt.csv;

import com.teslagov.gdelt.models.GdeltDailyDownloadResource;
import com.teslagov.gdelt.models.GdeltEventResource;

import java.util.List;

/**
 * @author Kevin Chen
 */
public class GDELTReturnResult
{
	private GdeltDailyDownloadResource downloadResult;

	private List<GdeltEventResource> gdeltEventList;

	public GdeltDailyDownloadResource getDownloadResult()
	{
		return downloadResult;
	}

	public void setDownloadResult( GdeltDailyDownloadResource downloadResult )
	{
		this.downloadResult = downloadResult;
	}

	public List<GdeltEventResource> getGdeltEventList()
	{
		return gdeltEventList;
	}

	public void setGdeltEventList( List<GdeltEventResource> gdeltEventList )
	{
		this.gdeltEventList = gdeltEventList;
	}
}
