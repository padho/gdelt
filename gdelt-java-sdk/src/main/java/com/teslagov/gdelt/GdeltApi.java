package com.teslagov.gdelt;

import com.teslagov.gdelt.csv.CsvProcessor;
import com.teslagov.gdelt.csv.GDELTReturnResult;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.InputStream;

/**
 * @author Kevin Chen
 */
public class GdeltApi
{
	private final HttpClient httpClient;

	private final GdeltConfiguration gdeltConfiguration;

	private final GdeltLastUpdateFetcher gdeltLastUpdateFetcher;

	private final GdeltLastUpdateDownloader gdeltLastUpdateDownloader;

	private final GdeltLastUpdateUnzipper gdeltLastUpdateUnzipper;

	private final CsvProcessor csvProcessor;

	public GdeltApi()
	{
		this( HttpClientBuilder.create().build() );
	}

	public GdeltApi( HttpClient httpClient )
	{
		this.httpClient = httpClient;
		this.gdeltConfiguration = new GdeltConfiguration()
		{
			@Override
			public String getV2ServerURL()
			{
				return "http://data.gdeltproject.org/gdeltv2/lastupdate.txt";
			}
		};
		this.gdeltLastUpdateFetcher = new GdeltLastUpdateFetcher();
		this.gdeltLastUpdateDownloader = new GdeltLastUpdateDownloader();
		this.gdeltLastUpdateUnzipper = new GdeltLastUpdateUnzipper();
		this.csvProcessor = new CsvProcessor();
	}

	/**
	 * Downloads a GDELT CSV file (unzipped), and does not delete the zip file.
	 *
	 * @param destinationDir The directory to download files to.
	 * @return a GDELT CSV file
	 */
	public File downloadLastUpdate( File destinationDir )
	{
		return downloadLastUpdate( destinationDir, true, false );
	}

	/**
	 * Downloads a GDELT CSV file (unzipped) to {@code ~/gdelt}. This method does not delete the zip file afterward.
	 *
	 * @return a GDELT CSV file.
	 */
	public File downloadLastUpdate()
	{
		File destinationDir = new File( System.getProperty( "user.home" ) + File.separator + "gdelt" );
		return downloadLastUpdate( destinationDir, true, false );
	}

	/**
	 * Downloads a GDELT CSV file (zipped or unzipped).
	 *
	 * @param destinationDir The directory to download files to.
	 * @param unzip          If you choose to unzip the CSV file, this method will not delete the zip file.
	 * @return a GDELT CSV file
	 */
	public File downloadLastUpdate( File destinationDir, boolean unzip )
	{
		return downloadLastUpdate( destinationDir, unzip, false );
	}

	/**
	 * Returns the GDELT last updates CSV file (zipped or unzipped).
	 *
	 * @param destinationDir The directory to download files to.
	 * @param unzip          Whether the downloaded file will be unzipped.
	 * @param deleteZip      If you choose to unzip the CSV file, this method will not delete the zip file.
	 * @return a GDELT CSV file
	 */
	public File downloadLastUpdate( File destinationDir, boolean unzip, boolean deleteZip )
	{
		// Step 1: get csv url
		String lastUpdateUrl = gdeltLastUpdateFetcher.getGDELTLastUpdate( httpClient, gdeltConfiguration );

		// Step 2: download zipped csv
		File zippedCsvFile = gdeltLastUpdateDownloader.downloadGDELTZipFile( httpClient, destinationDir, lastUpdateUrl );

		if ( unzip )
		{
			return unzipCsv( zippedCsvFile, deleteZip );
		}

		return zippedCsvFile;
	}

	/**
	 * Unzips a GDELT CSV file (to the same directory as the given zip file).
	 *
	 * @param zippedCsvFile The file to unzip
	 * @param deleteZip     Whether to delete the zip file afterwards
	 * @return an unzipped CSV file.
	 */
	public File unzipCsv( File zippedCsvFile, boolean deleteZip )
	{
		return gdeltLastUpdateUnzipper.unzip( zippedCsvFile, deleteZip );
	}

	/**
	 * Parses a GDELT CSV file.
	 *
	 * @param file The CSV file.
	 * @return the GDELT CSV records as POJOs.
	 */
	public GDELTReturnResult parseCsv( File file )
	{
		return csvProcessor.processCSV( file );
	}

	// TODO make public once the test passes
	GDELTReturnResult parseCsv( InputStream inputStream )
	{
		return csvProcessor.processCSV( inputStream );
	}
}
