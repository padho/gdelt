package com.teslagov.gdelt;

import com.teslagov.gdelt.csv.CsvProcessor;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Kevin Chen
 */
public class Test
{
	private static final Logger logger = LoggerFactory.getLogger( Test.class );

	public static void main( String[] args )
	{
		GDELTLastUpdateFetcher gdeltLastUpdateFetcher = new GDELTLastUpdateFetcher();
		GDELTConfiguration gdeltConfiguration = new GDELTConfiguration()
		{
			@Override
			public String getV2ServerURL()
			{
				return "http://data.gdeltproject.org/gdeltv2/lastupdate.txt";
			}
		};

		HttpClient httpClient = HttpClientBuilder.create().build();

		// Step 1: get csv url
//		String lastUpdateUrl = gdeltLastUpdateFetcher.getGDELTLastUpdate( httpClient, gdeltConfiguration );

		// Step 2: download zipped csv and unzip
//		GDELTLastUpdateDownloader lastUpdateDownloader = new GDELTLastUpdateDownloader();
//		lastUpdateDownloader.downloadGDELTFile( httpClient, new File( "gdelt" ), lastUpdateUrl, false );

		// Step 3: process csv
		CsvProcessor csvProcessor = new CsvProcessor();

//		InputStream inputStream = Test.class.getClassLoader().getResourceAsStream( "20161013151500.export.CSV" );
//		try
//		{
//			logger.info( "Input Stream: {}", IOUtils.toString( inputStream, StandardCharsets.UTF_8 ) );
//		}
//		catch ( IOException e )
//		{
//			e.printStackTrace();
//		}

		// TODO csv processing works with file but not with InputStream

		File file = new File( Test.class.getClassLoader().getResource( "20161013151500.export.CSV" ).getFile() );

		CsvProcessor.GDELTReturnResult gdeltReturnResult = csvProcessor.processCSV( file );
//		gdeltReturnResult.getGdeltEventList().forEach( e -> logger.info( e.toString() ) );
	}
}
