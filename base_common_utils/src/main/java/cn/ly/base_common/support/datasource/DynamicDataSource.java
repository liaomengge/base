package cn.ly.base_common.support.datasource;

/**
 * Created by liaomengge on 16/4/11.
 */

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return DBContext.getDBKey();
    }
}