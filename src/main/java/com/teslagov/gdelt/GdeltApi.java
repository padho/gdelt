package com.teslagov.gdelt;

import com.teslagov.gdelt.csv.CsvProcessor;
import com.teslagov.gdelt.csv.GDELTReturnResult;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;

/**
 * @author Kevin Chen
 */
public class GdeltApi
{
	private final HttpClient httpClient;

	private final GdeltLastUpdateFetcher gdeltLastUpdateFetcher;

	private final GdeltLastUpdateDownloader gdeltLastUpdateDownloader;

	private final GdeltConfiguration gdeltConfiguration;

	private final CsvProcessor csvProcessor;

	public GdeltApi()
	{
		this( HttpClientBuilder.create().build() );
	}

	public GdeltApi( HttpClient httpClient )
	{
		this.httpClient = httpClient;
		this.gdeltLastUpdateFetcher = new GdeltLastUpdateFetcher();
		this.gdeltLastUpdateDownloader = new GdeltLastUpdateDownloader();
		this.gdeltConfiguration = new GdeltConfiguration()
		{
			@Override
			public String getV2ServerURL()
			{
				return "http://data.gdeltproject.org/gdeltv2/lastupdate.txt";
			}
		};
		this.csvProcessor = new CsvProcessor();
	}

	public String downloadLastUpdate( File destinationDir )
	{
		// Step 1: get csv url
		String lastUpdateUrl = gdeltLastUpdateFetcher.getGDELTLastUpdate( httpClient, gdeltConfiguration );

		// Step 2: download zipped csv and unzip
		GdeltLastUpdateDownloader lastUpdateDownloader = new GdeltLastUpdateDownloader();
		return lastUpdateDownloader.downloadGDELTFile( httpClient, destinationDir, lastUpdateUrl, false );
	}

	public GDELTReturnResult parseCsv( File file )
	{
		return csvProcessor.processCSV( file );
	}
}
