package com.teslagov.gdelt.example;

import com.teslagov.gdelt.GdeltApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author Kevin Chen
 */
@Component
public class GdeltScheduledTask
{
	private final GdeltApi gdeltApi;

	@Autowired
	public GdeltScheduledTask( GdeltApi gdeltApi )
	{
		this.gdeltApi = gdeltApi;
	}

	@Scheduled( fixedDelay = 15 * 60 * 1000 )
	public void ingestGdeltRecords()
	{
		File destinationDir = new File( System.getProperty( "user.home" ) + File.separator + "gdelt" );
		File unzippedCsv = gdeltApi.downloadLastUpdate( destinationDir );
	}
}
