package com.teslagov.gdelt;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Kevin Chen
 */
public class GdeltMultipleDownloadsConfiguration {
	private GdeltApi gdeltApi;
	private File directory = GdeltDefaultDirectoryFileFactory.getDefaultDirectory();
	private boolean unzip = true;
	private boolean deleteZipFile = false;
	private LocalDateTime since;
	private LocalDateTime until;

	public GdeltMultipleDownloadsConfiguration(GdeltApi gdeltApi, LocalDateTime since) {
		this.gdeltApi = gdeltApi;
		this.since = roundDown(removeSecondsAndNanos(since));
	}

	public GdeltMultipleDownloadsConfiguration toDirectory(File directory) {
		this.directory = directory;
		return this;
	}

	public GdeltMultipleDownloadsConfiguration unzip(boolean unzip) {
		this.unzip = unzip;
		return this;
	}

	public GdeltMultipleDownloadsConfiguration deleteZipFile(boolean deleteZipFile) {
		this.deleteZipFile = deleteZipFile;
		return this;
	}

	public GdeltMultipleDownloadsConfiguration until(LocalDateTime until) {
		this.until = until;
		return this;
	}

	public Observable<File> execute() {
		return Observable.create(observableEmitter -> {
			if (until == null) {
				// we cannot round up to next interval of 15, since it may not have been released yet
				until = roundDown(LocalDateTime.now());
			}

			LocalDateTime time = since;
			while (!time.isAfter(until)) {
				Optional<File> fileOptional = gdeltApi.tryDownloadUpdate(directory, unzip, deleteZipFile, time.getYear(), time.getMonth().getValue(), time.getDayOfMonth(), time.getHour(), time.getMinute());
				fileOptional.ifPresent(observableEmitter::onNext);
				time = time.plusMinutes(15);
			}

			observableEmitter.onComplete();
		});
	}

	private LocalDateTime roundDown(LocalDateTime time) {
		int minute = time.getMinute();
		return time.minusMinutes(minute % 15);
	}

	private LocalDateTime removeSecondsAndNanos(LocalDateTime time) {
		return time.minusSeconds(time.getSecond()).minusNanos(time.getNano());
	}
}
