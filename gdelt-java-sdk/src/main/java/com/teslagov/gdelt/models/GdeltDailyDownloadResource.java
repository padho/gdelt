package com.teslagov.gdelt.models;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Kevin Chen
 */
public class GdeltDailyDownloadResource implements Serializable {
	private Boolean downloadedSuccessfully;

	private int recordsLoaded;

	private int recordsFailed;

	private Date modifiedDate;

	public Boolean getDownloadedSuccessfully() {
		return downloadedSuccessfully;
	}

	public void setDownloadedSuccessfully(Boolean downloadedSuccessfully) {
		this.downloadedSuccessfully = downloadedSuccessfully;
	}

	public int getRecordsLoaded() {
		return recordsLoaded;
	}

	public void setRecordsLoaded(int recordsLoaded) {
		this.recordsLoaded = recordsLoaded;
	}

	public int getRecordsFailed() {
		return recordsFailed;
	}

	public void setRecordsFailed(int recordsFailed) {
		this.recordsFailed = recordsFailed;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
}
