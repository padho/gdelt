package com.teslagov.gdelt.csv;

import com.teslagov.gdelt.models.GdeltDailyDownloadResource;
import com.teslagov.gdelt.models.GdeltEventResource;
import lombok.Data;

import java.util.List;

/**
 * @author Kevin Chen
 */
@Data
public class GDELTReturnResult
{
	GdeltDailyDownloadResource downloadResult;

	List<GdeltEventResource> gdeltEventList;
}
