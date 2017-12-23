package com.smi.mc.datasource;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 会员中心数据源
 * @author yanghailong
 *
 */
@Configuration
@MapperScan(basePackages = VipDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "rdsSqlSessionFactory")
public class VipDataSourceConfig {
	//扫描的会员中心dao接口包名
    static final String PACKAGE = "com.smi.mc.dao.cust";

    @Value("${vip.datasource.url}")
    private String dbUrl;
    @Value("${vip.datasource.username}")
    private String dbUser;
    @Value("${vip.datasource.password}")
    private String dbPassword;

    @Bean(name = "rdsDataSource")
    @Primary
    public DataSource rdsDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUser);
        dataSource.setPassword(dbPassword);
        return dataSource;
    }

    @Bean(name = "rdsTransactionManager")
    @Primary
    public DataSourceTransactionManager rdsTransactionManager() {
        return new DataSourceTransactionManager(rdsDataSource());
    }

    @Bean(name = "rdsSqlSessionFactory")
    @Primary
    public SqlSessionFactory rdsSqlSessionFactory(@Qualifier("rdsDataSource") DataSource rdsDataSource) throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(rdsDataSource);
        //绑定mybatis会员中心的映射文件路径
        sessionFactory.setMapperLocations(
				new PathMatchingResourcePatternResolver().getResources("classpath:/mybatis-mapper/cust/*.xml"));
        return sessionFactory.getObject();
    }
}