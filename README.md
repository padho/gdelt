# gdelt-java-sdk
An easy way to download GDELT CSV files. By default, files are downloaded to `~/gdelt`; however, a different parent directory can be specified.
Underneath the parent directory, `.CSV` and `.CSV.zip` files are stored under the directory structure `YEAR/MONTH/DAY_OF_MONTH`.
So if the url we're trying to download is `http://data.gdeltproject.org/gdeltv2/20161201040000.export.CSV.zip`, then the files will appear under `~/gdelt/2016/12/01`, assuming we're using the default parent directory (`~/gdelt`)

### `GdeltApi.java`
##### `downloadLastUpdate` - fetches the latest CSV file
##### `download` - fetches a CSV file corresponding to a given date
##### `parseCsv(File)` - parses a CSV file into a POJO

### Examples
```java
public class Test {
  public static void main(String[] args) {
    GdeltApi gdeltApi = new GdeltApi();
      
    // fluent api for download csv files
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