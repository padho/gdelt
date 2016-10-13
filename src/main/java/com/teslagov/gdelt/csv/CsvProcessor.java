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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
		GDELTDailyDownloadDB downloadREsult;

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

		gdeltResult.setDownloadREsult( gdeltDownload );

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
			String[] names = null;

			CSVRecord record = csvRecords.get( 0 );

			int size = record.size();
			logger.debug( "Found {} headers", size );

			// TODO what's the point of having multiple header enums if we're hard-coding the headers below???
			if ( size == GDELToldColumnHeader.getSize() )
			{
				logger.debug( "using GDELToldColumnHeader headers" );
				names = GDELToldColumnHeader.getNames();
			}
			else if ( size == GDELT1_0ColumnHeader.getSize() )
			{
				logger.debug( "using GDELT1_0ColumnHeader headers" );
				names = GDELT1_0ColumnHeader.getNames();
			}
			else if ( size == GDELT2_0ColumnHeader.getSize() )
			{
				logger.debug( "using GDELT2_0ColumnHeader headers" );
				names = GDELT2_0ColumnHeader.getNames();
			}

			if ( names != null )
			{
				csvFormat.withHeader( names );

				Map<String, Integer> map = new HashMap<>();
				for ( int i = 0; i < names.length; i++ )
				{
					map.put( names[i], i );
				}

				logger.debug( "**********  Before process Events" );
				GDELTReturnResult result = processEvents( csvRecords, map );
				logger.debug( "***********  After process Events" );

				// add to the main result list
				gdeltEventList.addAll( result.getGdeltEventList() );

				// add to the main failed and loaded record count
				GDELTDailyDownloadDB d = result.getDownloadREsult();

				recordsFailed += d.getRecordsFailed();
				recordsLoaded += d.getRecordsLoaded();
			}
		}

		gdeltDownload.setDownloadedSuccessfully( Boolean.TRUE );
		gdeltDownload.setRecordsFailed( recordsFailed );
		gdeltDownload.setRecordsLoaded( recordsLoaded );

		gdeltResult.setDownloadREsult( gdeltDownload );

		gdeltResult.setGdeltEventList( gdeltEventList );
		return gdeltResult;
	}

	private GDELTReturnResult processEvents( List<CSVRecord> records, Map<String, Integer> names )
	{
		GDELTReturnResult gdeltResult = new GDELTReturnResult();
		List<GDELTEventDB> gdeltEventList = new ArrayList<>();
		int recordsLoaded = 0;
		int recordsFailed = 0;

		Map<String, GDELTURLDB> urlRunningCache = new HashMap<>();

		DateFormat format = new SimpleDateFormat( "yyyyMMdd", Locale.ENGLISH );

		for ( int i = 0; i < records.size(); i++ )
		{
			logger.debug( "Parsing record {}", i );
			CSVRecord record = records.get( i );
			try
			{
				GDELTEventDB gdeltEvent = new GDELTEventDB();

				String actionGeoCountryCode = record.get( names.get( "ActionGeo_CountryCode" ) );
				gdeltEvent.setActionGeo_CountryCode( actionGeoCountryCode );

				String actor1GeoCountryCode = record.get( names.get( "Actor1Geo_CountryCode" ) );
				gdeltEvent.setActor1Geo_CountryCode( actor1GeoCountryCode );

				String actor2GeoCountryCode = record.get( names.get( "Actor2Geo_CountryCode" ) );
				gdeltEvent.setActor2Geo_CountryCode( actor2GeoCountryCode );

				String eventRootCode = record.get( names.get( "EventRootCode" ) );

				// TODO filtering should not happen here; the caller should worry about that
				if ( !GDELTCameoDownloadCodes.containsCameo( eventRootCode ) )
				{
					logger.debug( "Not parsing record with cameo code: {}... not one of our GDELTCameoDownloadCodes of interest", eventRootCode );
				}
				else
				{
					logger.debug( "Parsing fields..." );
					gdeltEvent.setGLOBALEVENTID( Integer.parseInt( record.get( names.get( "GLOBALEVENTID" ) ) ) );

					String sqldate = record.get( names.get( "SQLDATE" ) );

					gdeltEvent.setEventDate( format.parse( sqldate ) ); // get the Date from Event Date.

					gdeltEvent.setActor1Code( record.get( names.get( "Actor1Code" ) ) );

					gdeltEvent.setActor1Name( record.get( names.get( "Actor1Name" ) ) );

					gdeltEvent.setActor1CountryCode( record.get( names.get( "Actor1CountryCode" ) ) );

					gdeltEvent.setActor1KnownGroupCode( record.get( names.get( "Actor1KnownGroupCode" ) ) );

					gdeltEvent.setActor1EthnicCode( record.get( names.get( "Actor1EthnicCode" ) ) );

					gdeltEvent.setActor1Religion1Code( record.get( names.get( "Actor1Religion1Code" ) ) );

					gdeltEvent.setActor1Religion2Code( record.get( names.get( "Actor1Religion2Code" ) ) );

					gdeltEvent.setActor1Type1Code( record.get( names.get( "Actor1Type1Code" ) ) );

					gdeltEvent.setActor1Type2Code( record.get( names.get( "Actor1Type2Code" ) ) );

					gdeltEvent.setActor1Type3Code( record.get( names.get( "Actor1Type3Code" ) ) );

					gdeltEvent.setActor2Code( record.get( names.get( "Actor2Code" ) ) );

					gdeltEvent.setActor2Name( record.get( names.get( "Actor2Name" ) ) );

					gdeltEvent.setActor2CountryCode( record.get( names.get( "Actor2CountryCode" ) ) );

					gdeltEvent.setActor2KnownGroupCode( record.get( names.get( "Actor2KnownGroupCode" ) ) );

					gdeltEvent.setActor2EthnicCode( record.get( names.get( "Actor2EthnicCode" ) ) );

					gdeltEvent.setActor2Religion1Code( record.get( names.get( "Actor2Religion1Code" ) ) );

					gdeltEvent.setActor2Religion2Code( record.get( names.get( "Actor2Religion2Code" ) ) );

					gdeltEvent.setActor2Type1Code( record.get( names.get( "Actor2Type1Code" ) ) );

					gdeltEvent.setActor2Type2Code( record.get( names.get( "Actor2Type2Code" ) ) );

					gdeltEvent.setActor2Type3Code( record.get( names.get( "Actor2Type3Code" ) ) );

					gdeltEvent.setIsRootEvent( Boolean.parseBoolean( record.get( names.get( "IsRootEvent" ) ) ) );

					gdeltEvent.setEventCode( record.get( names.get( "EventCode" ) ) );

					gdeltEvent.setEventBaseCode( record.get( names.get( "EventBaseCode" ) ) );

					gdeltEvent.setEventRootCode( record.get( names.get( "EventRootCode" ) ) );

					if ( record.get( names.get( "QuadClass" ) ).length() > 0 )
					{
						gdeltEvent.setQuadClass( Integer.parseInt( record.get( names.get( "QuadClass" ) ) ) );
					}

					if ( record.get( names.get( "GoldsteinScale" ) ).length() > 0 )
					{
						gdeltEvent.setGoldsteinScale(
							Double.parseDouble( record.get( names.get( "GoldsteinScale" ) ) ) );
					}

					if ( record.get( names.get( "NumMentions" ) ).length() > 0 )
					{
						gdeltEvent.setNumMentions( Integer.parseInt( record.get( names.get( "NumMentions" ) ) ) );
					}

					if ( record.get( names.get( "NumSources" ) ).length() > 0 )
					{
						gdeltEvent.setNumSources( Integer.parseInt( record.get( names.get( "NumSources" ) ) ) );
					}

					if ( record.get( names.get( "NumArticles" ) ).length() > 0 )
					{
						gdeltEvent.setNumArticles( Integer.parseInt( record.get( names.get( "NumArticles" ) ) ) );
					}

					if ( record.get( names.get( "AvgTone" ) ).length() > 0 )
					{
						gdeltEvent.setAvgTone( Double.parseDouble( record.get( names.get( "AvgTone" ) ) ) );
					}

					if ( record.get( names.get( "Actor1Geo_Type" ) ).length() > 0 )
					{
						gdeltEvent.setActor1Geo_Type( Integer.parseInt( record.get( names.get( "Actor1Geo_Type" ) ) ) );
					}

					gdeltEvent.setActor1Geo_FullName( record.get( names.get( "Actor1Geo_FullName" ) ) );

					gdeltEvent.setActor1Geo_ADM1Code( record.get( names.get( "Actor1Geo_ADM1Code" ) ) );

					if ( names.get( "Actor1Geo_ADM2Code" ) != null )
					{
						gdeltEvent.setActor1Geo_ADM2Code( record.get( names.get( "Actor1Geo_ADM2Code" ) ) );
					}

					Double latitude, longitude;

					if ( ( record.get( names.get( "Actor1Geo_Lat" ) ).length() > 0
						&& record.get( names.get( "Actor1Geo_Long" ) ).length() > 0 ) )
					{
						latitude = Double.parseDouble( record.get( names.get( "Actor1Geo_Lat" ) ) );

						longitude = Double.parseDouble( record.get( names.get( "Actor1Geo_Long" ) ) );

						gdeltEvent.setActor1Geo_Lat( latitude );
						gdeltEvent.setActor1Geo_Long( longitude );
					}

					gdeltEvent.setActor1Geo_FeatureID( record.get( names.get( "Actor1Geo_FeatureID" ) ) );

					if ( record.get( names.get( "Actor2Geo_Type" ) ).length() > 0 )
					{
						gdeltEvent.setActor2Geo_Type( Integer.parseInt( record.get( names.get( "Actor2Geo_Type" ) ) ) );
					}

					gdeltEvent.setActor2Geo_FullName( record.get( names.get( "Actor2Geo_FullName" ) ) );

					gdeltEvent.setActor2Geo_ADM1Code( record.get( names.get( "Actor2Geo_ADM1Code" ) ) );

					if ( names.get( "Actor2Geo_ADM2Code" ) != null )
					{
						gdeltEvent.setActor2Geo_ADM2Code( record.get( names.get( "Actor2Geo_ADM2Code" ) ) );
					}

					if ( record.get( names.get( "Actor2Geo_Lat" ) ).length() > 0
						&& record.get( names.get( "Actor2Geo_Long" ) ).length() > 0 )
					{
						latitude = Double.parseDouble( record.get( names.get( "Actor2Geo_Lat" ) ) );

						longitude = Double.parseDouble( record.get( names.get( "Actor2Geo_Long" ) ) );

						gdeltEvent.setActor2Geo_Lat( latitude );
						gdeltEvent.setActor2Geo_Long( longitude );
					}

					gdeltEvent.setActor2Geo_FeatureID( record.get( names.get( "Actor2Geo_FeatureID" ) ) );

					gdeltEvent.setActionGeo_Type( Integer.parseInt( record.get( names.get( "ActionGeo_Type" ) ) ) );

					gdeltEvent.setActionGeo_FullName( record.get( names.get( "ActionGeo_FullName" ) ) );

					gdeltEvent.setActionGeo_ADM1Code( record.get( names.get( "ActionGeo_ADM1Code" ) ) );

					if ( names.get( "ActionGeo_ADM2Code" ) != null )
					{
						gdeltEvent.setActionGeo_ADM2Code( record.get( names.get( "ActionGeo_ADM2Code" ) ) );
					}

					if ( record.get( names.get( "ActionGeo_Lat" ) ).length() > 0
						&& record.get( names.get( "ActionGeo_Long" ) ).length() > 0 )
					{
						latitude = Double.parseDouble( record.get( names.get( "ActionGeo_Lat" ) ) );

						longitude = Double.parseDouble( record.get( names.get( "ActionGeo_Long" ) ) );

						gdeltEvent.setActionGeo_Lat( latitude );
						gdeltEvent.setActionGeo_Long( longitude );
					}

					gdeltEvent.setActionGeo_FeatureID( record.get( names.get( "ActionGeo_FeatureID" ) ) );

					gdeltEvent.setDATEADDED( record.get( names.get( "DATEADDED" ) ) );

					if ( names.get( "SOURCEURL" ) != null )
					{
						String url = record.get( names.get( "SOURCEURL" ) );
						GDELTURLDB urlObj = urlRunningCache.get( url );

						if ( urlObj == null )
						{
							List<GDELTURLDB> urlList = new ArrayList<>();
							// TODO
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
			}
			catch ( ParseException ex )
			{
				recordsFailed = recordsFailed + 1;
				logger.error( "GDELT Record Ingestion failed.", ex );
			}
		}

		logger.debug( "Went through {} records", records.size() );

		GDELTDailyDownloadDB gdeltDownload = new GDELTDailyDownloadDB();

		gdeltDownload.setDownloadedSuccessfully( Boolean.TRUE );
		gdeltDownload.setRecordsFailed( recordsFailed );
		gdeltDownload.setRecordsLoaded( recordsLoaded );

		gdeltResult.setDownloadREsult( gdeltDownload );

		gdeltResult.setGdeltEventList( gdeltEventList );

		return gdeltResult;
	}
}
