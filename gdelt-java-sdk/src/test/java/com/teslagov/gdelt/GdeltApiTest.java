package com.teslagov.gdelt;

import com.teslagov.gdelt.csv.GDELTReturnResult;
import com.teslagov.gdelt.models.GdeltEventResource;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Kevin Chen
 */
public class GdeltApiTest
{
	private static final Logger logger = LoggerFactory.getLogger( GdeltApiTest.class );

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

	private GdeltApi gdeltApi;

	@Before
	public void init()
	{
		gdeltApi = new GdeltApi();
	}

	@Test
	public void testDownload()
	{
		// Download LastUpdate CSV
		File csvFile = gdeltApi.downloadLastUpdate();
		assertNotNull( csvFile );
		logger.info( "Download a GDELT CSV file to: {}", csvFile.getAbsolutePath() );
	}

	@Test
	public void testParseCsvInputStream() throws IOException
	{
		// TODO csv processing works with file but not with InputStream
		InputStream inputStream = GdeltApiTest.class.getClassLoader().getResourceAsStream( "20161013151500.export.CSV" );
		logger.info( "Input Stream: {}", IOUtils.toString( inputStream, StandardCharsets.UTF_8 ) );
		GDELTReturnResult gdeltReturnResult = gdeltApi.parseCsv( inputStream );
		assertCsv( gdeltReturnResult );
	}

	@Test
	public void testParseCsvFile()
	{
		File file = new File( GdeltApiTest.class.getClassLoader().getResource( "20161013151500.export.CSV" ).getFile() );
		GDELTReturnResult gdeltReturnResult = gdeltApi.parseCsv( file );
		assertCsv( gdeltReturnResult );
	}

	private void assertCsv( GDELTReturnResult gdeltReturnResult )
	{
		long start, end;
		gdeltReturnResult.getGdeltEventList().forEach( e -> logger.trace( e.toString() ) );

		List<GdeltEventResource> gdeltEvents = gdeltReturnResult.getGdeltEventList();
		start = System.currentTimeMillis();
		long count = gdeltEvents.stream().filter( event -> GdeltCameoDownloadCodes.containsCameo( event.getEventRootCode() ) ).count();
		end = System.currentTimeMillis();
		logger.debug( "Took {} ms to filter", end - start );
		logger.debug( "Loaded {} events", count );
		assertEquals( 653, count );
	}
}