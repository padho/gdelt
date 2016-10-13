package com.teslagov.gdelt;

import com.teslagov.gdelt.csv.CsvProcessor;
import com.teslagov.gdelt.models.GdeltEventResource;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * @author Kevin Chen
 */
public class Test
{
	private static final Logger logger = LoggerFactory.getLogger( Test.class );

	enum GdeltCameoDownloadCodes
	{
		EngageInMaterialCooperation( "06" ),
		ProvideAid( "07" ),
		Threaten( "13" ),
		Protest( "14" ),
		Coerce( "17" ),
		Assault( "18" ),
		Fight( "19" ),
		EngageInUnconventionalMassViolence( "20" );

		public String getRootCameoCode()
		{
			return rootCameoCode;
		}

		GdeltCameoDownloadCodes( String cameoCode )
		{
			this.rootCameoCode = cameoCode;
		}

		public static boolean containsCameo( String eventRootCode )
		{
			return eventRootCode.equals( EngageInMaterialCooperation.getRootCameoCode() ) ||
				eventRootCode.equals( ProvideAid.getRootCameoCode() ) ||
				eventRootCode.equals( Threaten.getRootCameoCode() ) ||
				eventRootCode.equals( Protest.getRootCameoCode() ) ||
				eventRootCode.equals( Coerce.getRootCameoCode() ) ||
				eventRootCode.equals( Assault.getRootCameoCode() ) ||
				eventRootCode.equals( Fight.getRootCameoCode() ) ||
				eventRootCode.equals( EngageInUnconventionalMassViolence.getRootCameoCode() );
		}

		private final String rootCameoCode;
	}

	public static void main( String[] args )
	{
		GdeltLastUpdateFetcher gdeltLastUpdateFetcher = new GdeltLastUpdateFetcher();
		GdeltConfiguration gdeltConfiguration = new GdeltConfiguration()
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
//		GdeltLastUpdateDownloader lastUpdateDownloader = new GdeltLastUpdateDownloader();
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

		List<GdeltEventResource> gdeltEvents = gdeltReturnResult.getGdeltEventList();
		long count = gdeltEvents.stream().filter( event -> GdeltCameoDownloadCodes.containsCameo( event.getEventRootCode() ) ).count();
		logger.debug( "Loaded {} events", count );
	}
}
