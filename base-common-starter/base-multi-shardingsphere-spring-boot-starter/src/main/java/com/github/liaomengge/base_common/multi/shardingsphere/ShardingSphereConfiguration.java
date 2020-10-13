package com.github.liaomengge.base_common.multi.shardingsphere;

import com.github.liaomengge.base_common.multi.shardingsphere.aspect.HintMasterAspect;
import com.github.liaomengge.base_common.multi.shardingsphere.configuration.*;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * Created by liaomengge on 2018/10/23.
 */
@Configuration
@ConditionalOnClass(SqlSessionFactoryBean.class)
@EnableTransactionManagement(proxyTargetClass = true)
@Import({ShardingDataSourceConfiguration.class, ShardingDataSourceConfiguration2.class,
        ShardingDataSourceConfiguration3.class, ShardingDataSourceConfiguration4.class,
        ShardingDataSourceConfiguration5.class})
public class ShardingSphereConfiguration {

    @Bean
    public HintMasterAspect hintMasterAspect() {
        return new HintMasterAspect();
    }
}
