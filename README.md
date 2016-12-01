# gdelt-java-sdk
This project provides a fluent API for downloading [GDELT](http://gdeltproject.org/) CSV files, which are released every 15 minutes.

Consumers can download the 
["last update"](http://data.gdeltproject.org/gdeltv2/lastupdate.txt), 
a specific GDELT CSV file given a time, or all of the CSV files given between a time interval.  

By default, files are downloaded into the `~/gdelt` directory; however, a different parent directory can be specified.
Beneath the parent directory, files are automatically stored under the directory structure `YEAR/MONTH/DAY_OF_MONTH`.

For instance, if we were to download 
[20161201040000.export.CSV.zip](http://data.gdeltproject.org/gdeltv2/20161201040000.export.CSV.zip), 
then the file would appear in `~/gdelt/2016/12/01`, by default.

Whenever files are downloaded, we first check the target directory. 
If we already have the `.CSV` file, we skip the download and return the file.
If we have the corresponding `.CSV.zip`, then we unzip it and return the unzipped file.

### `GdeltApi.java`
##### `downloadLastUpdate` - fetches the latest CSV file
##### `download` - fetches a CSV file corresponding to a given date
##### `parseCsv(File)` - parses a CSV file into a POJO

### Examples
```java
public class Test {
  public static void main(String[] args) {
    GdeltApi gdeltApi = new GdeltApi();
      
    // fluent api for downloading csv files.
    // only the default params are shown
    File csvFile = 
      gdeltApi.downloadLastUpdate()
        .toDirectory(new File("~/gdelt"))
        .deleteZipFile(false)
        .unzip(true)
        .execute();
      
    // this is equivalent to the previous
    // if the last update has been downloaded already, 
    // then we skip the download and return that file 
    File csvFile2 = 
      gdeltApi.downloadLastUpdate()
        .execute();
  }
}
```