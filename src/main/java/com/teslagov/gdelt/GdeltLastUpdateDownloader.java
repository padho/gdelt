package com.teslagov.gdelt;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Kevin Chen
 */
public class GdeltLastUpdateDownloader
{
	private static final Logger logger = LoggerFactory.getLogger( GdeltLastUpdateDownloader.class );

	private void ensureDirectoryExists( File directory )
	{
		// if the output directory doesn't exist, create it
		if ( !directory.exists() )
		{
			directory.mkdirs();
		}
	}

	public String downloadGDELTFile( HttpClient httpClient, File directory, String csvLocation, boolean deleteZipFileAfterDownload )
	{
		ensureDirectoryExists( directory );

		String fileDestination = directory.getName();
		logger.debug( "file destination: {}", fileDestination );

		String zipFilename = null;

		if ( UrlValidator.isValid( csvLocation ) )
		{
			zipFilename = csvLocation.substring( csvLocation.lastIndexOf( "/" ) + 1 );
		}

		if ( zipFilename == null )
		{
			return null;
		}

		String unzipFilename = zipFilename.substring( 0, zipFilename.lastIndexOf( "." ) );

		logger.debug( "dest dir abs path: {}", directory.getAbsolutePath() );

		File zipFile = new File( directory.getAbsolutePath() + File.separator + zipFilename );

		boolean downloadStatus = downloadFile( httpClient, csvLocation, zipFile );

		if ( downloadStatus )
		{
			File fileTemp = new File( fileDestination + zipFilename );

			String fullPath = fileDestination + zipFilename;

			try
			{
				Boolean unzipStatus;

				unzipStatus = FileUnzipper.unzipFile( fileDestination, fullPath );

				if ( unzipStatus )
				{
					return fileDestination + unzipFilename;
				}
			}
			catch ( Exception ex )
			{
				logger.error( "Exception while unzipping file: \"{}\"", fullPath, ex );
			}
			finally
			{
				if ( deleteZipFileAfterDownload && fileTemp.exists() )
				{
					fileTemp.delete();
				}
			}
		}

		return null;
	}

	boolean downloadFile( HttpClient httpClient, String fileURL, File zipFile )
	{
		HttpGet httpGet = HttpGetter.get( fileURL );
		HttpResponse response = null;
		try
		{
			response = httpClient.execute( httpGet );
		}
		catch ( IOException e )
		{
			throw new GdeltException( "Could not execute request", e );
		}

		if ( response.getStatusLine().getStatusCode() == 200 )
		{
			try ( FileOutputStream fos = new FileOutputStream( zipFile ) )
			{
				response.getEntity().writeTo( fos );
				return true;
			}
			catch ( IOException e )
			{
				throw new GdeltException( "Could not get response", e );
			}
		}
		return false;
	}
}
