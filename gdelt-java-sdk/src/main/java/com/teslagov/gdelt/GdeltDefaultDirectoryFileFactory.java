package com.teslagov.gdelt;

import java.io.File;

/**
 * @author Kevin Chen
 */
public class GdeltDefaultDirectoryFileFactory {
	static File getDefaultDirectory() {
		return new File(System.getProperty("user.home") + File.separator + "gdelt");
	}
}
