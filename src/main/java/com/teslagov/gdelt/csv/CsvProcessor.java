package com.teslagov.gdelt.csv;

import com.teslagov.gdelt.GDELTException;
import com.teslagov.gdelt.models.GDELTDailyDownloadDB;
import com.teslagov.gdelt.models.GDELTEventDB;
import com.teslagov.gdelt.models.GDELTURLDB;
import lombok.Data;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kevin Chen
 */
public class CsvProcessor
{
	private static final Logger logger = LoggerFactory.getLogger( CsvProcessor.class );

	@Data
	public class GDELTReturnResult
	{
		GDELTDailyDownloadDB downloadResult;

		List<GDELTEventDB> gdeltEventList;
	}

	private CSVParser createCvsParser( Reader reader, CSVFormat csvFileFormat )
	{
		try
		{
			return new CSVParser( reader, csvFileFormat );
		}
		catch ( IOException e )
		{
			throw new GDELTException( "Could not construct CSV parser", e );
		}
	}

	public GDELTReturnResult processCSV( File file )
	{
		// GDELT CSV files are tab delimited
		CSVFormat csvFormat = CSVFormat.newFormat( '\t' );

		logger.debug( "Creating csv parser..." );
		Reader reader = null;
		try
		{
			reader = new FileReader( file );
		}
		catch ( FileNotFoundException e )
		{
			throw new GDELTException( "File not found", e );
		}
		CSVParser csvParser = createCvsParser( reader, csvFormat );

		return processCSV( csvParser, csvFormat );
	}

	public GDELTReturnResult processCSV( InputStream inputStream )
	{
		// GDELT CSV files are tab delimited
		CSVFormat csvFormat = CSVFormat.newFormat( '\t' );

		logger.debug( "Creating csv parser..." );
		InputStreamReader inputStreamReader = new InputStreamReader( inputStream, StandardCharsets.UTF_8 );
		Reader reader = IOUtils.toBufferedReader( inputStreamReader );
		CSVParser csvParser = createCvsParser( reader, csvFormat );

		return processCSV( csvParser, csvFormat );
	}

	private GDELTReturnResult processCSV( CSVParser csvParser, CSVFormat csvFormat )
	{
		List<GDELTEventDB> gdeltEventList = new ArrayList<>();

		GDELTReturnResult gdeltResult = new GDELTReturnResult();

		GDELTDailyDownloadDB gdeltDownload = new GDELTDailyDownloadDB();
		gdeltResult.setGdeltEventList( new ArrayList<>() );

		gdeltResult.setDownloadResult( gdeltDownload );

		int recordsLoaded = 0;
		int recordsFailed = 0;

		logger.debug( "Retrieving csv records..." );
		List<CSVRecord> csvRecords = null;
		try
		{
			csvRecords = csvParser.getRecords();
		}
		catch ( IOException e )
		{
			throw new GDELTException( "Could not get records from csv", e );
		}

		if ( csvRecords.isEmpty() )
		{
			logger.debug( "Found 0 records" );
		}

		else
		{
			String[] values = null;

			CSVRecord record = csvRecords.get( 0 );

			int size = record.size();
			logger.debug( "Found {} headers", size );

			// TODO what's the point of having multiple header enums if we're hard-coding the headers below???
			if ( size == GDELToldColumnHeader.getSize() )
			{
				logger.debug( "using GDELToldColumnHeader headers" );
				values = GDELToldColumnHeader.getNames();
			}
			else if ( size == GDELT1_0ColumnHeader.getSize() )
			{
				logger.debug( "using GDELT1_0ColumnHeader headers" );
				values = GDELT1_0ColumnHeader.getNames();
			}
			else if ( size == GDELT2_0ColumnHeader.getSize() )
			{
				logger.debug( "using GDELT2_0ColumnHeader headers" );
				values = GDELT2_0ColumnHeader.getNames();
			}

			if ( values != null )
			{
				csvFormat.withHeader( values );

				Map<String, Integer> map = new HashMap<>();
				for ( int i = 0; i < values.length; i++ )
				{
					map.put( values[i], i );
				}

				logger.debug( "**********  Before process Events" );
				GDELTReturnResult result = processEvents( csvRecords, map );
				logger.debug( "***********  After process Events" );

				// add to the main result list
				gdeltEventList.addAll( result.getGdeltEventList() );

				// add to the main failed and loaded record count
				GDELTDailyDownloadDB d = result.getDownloadResult();

				recordsFailed += d.getRecordsFailed();
				recordsLoaded += d.getRecordsLoaded();
			}
		}

		gdeltDownload.setDownloadedSuccessfully( Boolean.TRUE );
		gdeltDownload.setRecordsFailed( recordsFailed );
		gdeltDownload.setRecordsLoaded( recordsLoaded );

		gdeltResult.setDownloadResult( gdeltDownload );

		gdeltResult.setGdeltEventList( gdeltEventList );
		return gdeltResult;
	}

	private GDELTReturnResult processEvents( List<CSVRecord> records, Map<String, Integer> values )
	{
		GDELTReturnResult gdeltResult = new GDELTReturnResult();
		List<GDELTEventDB> gdeltEventList = new ArrayList<>();
		int recordsLoaded = 0;
		int recordsFailed = 0;

		Map<String, GDELTURLDB> urlRunningCache = new HashMap<>();

		for ( int i = 0; i < records.size(); i++ )
		{
			logger.debug( "Parsing record {}", i );
			CSVRecord record = records.get( i );
			String eventRootCode = record.get( values.get( "EventRootCode" ) );


			GDELTEventDB gdeltEvent;
			try
			{
				gdeltEvent = GDELTEventFromCsvRecordFactory.create( record, values );
			}
			catch ( Exception e )
			{
				recordsFailed++;
				continue;
			}

			// TODO clean up
			if ( values.get( "SOURCEURL" ) != null )
			{
				String url = record.get( values.get( "SOURCEURL" ) );
				GDELTURLDB urlObj = urlRunningCache.get( url );

				if ( urlObj == null )
				{
					List<GDELTURLDB> urlList = new ArrayList<>();
//								session.createQuery( "FROM GDELTURLDB WHERE SOURCEURL = :sourceurl" ).setParameter( "sourceurl", url ).list();

					if ( urlList.size() > 0 )
					{
						for ( GDELTURLDB rec : urlList )
						{
							if ( url.equals( rec.getSOURCEURL() ) )
							{
								gdeltEvent.setSOURCEURL( rec );
								urlObj = rec;
							}
						}
					}
				}
				else // if object in running cache use it.
				{
					gdeltEvent.setSOURCEURL( urlObj );
				}

				if ( urlObj == null ) // not in cache or database create it
				{ // if created a new url object but in cache
					urlObj = new GDELTURLDB( url );
					// TODO
//							session.save( urlObj );
					gdeltEvent.setSOURCEURL( urlObj );
					urlRunningCache.put( url, urlObj );
				}
			}

			recordsLoaded = recordsLoaded + 1;
			gdeltEventList.add( gdeltEvent );
		}

		logger.debug( "Went through {} records", records.size() );

		GDELTDailyDownloadDB gdeltDownload = new GDELTDailyDownloadDB();

		gdeltDownload.setDownloadedSuccessfully( Boolean.TRUE );
		gdeltDownload.setRecordsFailed( recordsFailed );
		gdeltDownload.setRecordsLoaded( recordsLoaded );

		logger.debug( gdeltDownload.toString() );

		gdeltResult.setDownloadResult( gdeltDownload );

		gdeltResult.setGdeltEventList( gdeltEventList );

		return gdeltResult;
	}
}
