package cn.ly.base_common.multi.mybatis.hikari;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Created by liaomengge on 2018/12/19.
 */
@ConditionalOnClass(HikariDataSource.class)
@ConditionalOnProperty(name = "ly.datasource.type", havingValue = "com.zaxxer.hikari.HikariDataSource")
@Configuration
public class HikariConfiguration3 {

    @Bean(name = "threeMasterDataSource", destroyMethod = "close")
    @Primary
    @ConfigurationProperties("ly.mybatis.three.hikari.master")
    public HikariDataSource masterDataSource() {
        return (HikariDataSource) DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name = "threeSlaveDataSource", destroyMethod = "close")
    @ConfigurationProperties("ly.mybatis.three.hikari.slave")
    public HikariDataSource slaveDataSource() {
        return (HikariDataSource) DataSourceBuilder.create().type(HikariDataSource.class).build();
    }
}