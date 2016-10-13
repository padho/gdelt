package com.teslagov.gdelt.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @author hanymorcos
 */
@Data
@NoArgsConstructor

public class GDELTURLDB implements Serializable
{
	public GDELTURLDB( String SOURCEURL )
	{
		this.SOURCEURL = SOURCEURL;
	}

	private int urlID;

	private String SOURCEURL;

	private String gdeltSourceText;

	private byte[] nilsimsaHash;

	private int gdeltDownloadAttempts;

	private Boolean sourceDownloaded = false;

	private Boolean hideFromSearch = false;

	private java.util.Date lastmodified;
}
