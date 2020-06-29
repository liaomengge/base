package cn.ly.base_common.support.datasource;

/**
 * Created by liaomengge on 16/4/11.
 * see {@link cn.ly.base_common.support.datasource.DynamicDataSource}
 */

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Deprecated
public class RoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return DBContext.getDBKey();
    }
}