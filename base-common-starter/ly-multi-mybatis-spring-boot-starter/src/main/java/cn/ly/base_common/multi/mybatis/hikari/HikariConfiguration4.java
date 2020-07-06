package cn.ly.base_common.multi.mybatis.hikari;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Created by liaomengge on 2018/12/19.
 */
@ConditionalOnClass(HikariDataSource.class)
@ConditionalOnProperty(name = "ly.datasource.type", havingValue = "com.zaxxer.hikari.HikariDataSource")
@Configuration
public class HikariConfiguration4 {

    @Bean(name = "fourMasterDataSource", destroyMethod = "close")
    @Primary
    @ConfigurationProperties("ly.mybatis.four.hikari.master")
    public HikariDataSource masterDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name = "fourSlaveDataSource", destroyMethod = "close")
    @ConfigurationProperties("ly.mybatis.four.hikari.slave")
    public HikariDataSource slaveDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }
}
