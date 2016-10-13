package com.teslagov.gdelt.models;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hanymorcos
 */
@Data
public class GDELTDailyDownloadDB implements Serializable
{
	Boolean downloadedSuccessfully;

	int recordsLoaded = 0;

	int recordsFailed = 0;

	Date modifiedDate = new Date();
}
