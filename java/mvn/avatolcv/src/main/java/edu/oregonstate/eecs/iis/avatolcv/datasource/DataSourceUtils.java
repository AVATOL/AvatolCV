package edu.oregonstate.eecs.iis.avatolcv.datasource;

public class DataSourceUtils {
	public static DataSource getDataSourceForRun(String dataSourceName){
        DataSource ds = null;
        if ("bisque".equals(dataSourceName)){
            ds = new BisqueDataSource(); 
        } 
        else if ("morphobank".equals(dataSourceName)){
            ds = new MorphobankDataSource();
        }
        else {
            ds = new FileSystemDataSource();
        }
        return ds;
    }
}
