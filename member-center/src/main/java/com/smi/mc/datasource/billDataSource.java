package com.smi.mc.datasource;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 计费数据源
 * @author yanghailong
 *
 */
@Configuration
@MapperScan(basePackages = billDataSource.PACKAGE, sqlSessionFactoryRef = "adsSqlSessionFactory")
public class billDataSource {
	//扫描计费dao接口包名
    static final String PACKAGE = "com.smi.mc.dao.billing";

    @Value("${bill.datasource.url}")
    private String dbUrl;
    @Value("${bill.datasource.username}")
    private String dbUser;
    @Value("${bill.datasource.password}")
    private String dbPassword;

    @Bean(name = "adsDataSource")
    public DataSource adsDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUser);
        dataSource.setPassword(dbPassword);
        return dataSource;
    }

    @Bean(name = "adsTransactionManager")
    public DataSourceTransactionManager adsTransactionManager(@Qualifier("adsDataSource") DataSource adsDataSource) {
        return new DataSourceTransactionManager(adsDataSource);
    }

    @Bean(name = "adsSqlSessionFactory")
    public SqlSessionFactory adsSqlSessionFactory(@Qualifier("adsDataSource") DataSource adsDataSource) throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(adsDataSource);
        //绑定计费mybatis映射文件路径
        sessionFactory.setMapperLocations(
				new PathMatchingResourcePatternResolver().getResources("classpath:/mybatis-mapper/bill/*.xml"));
        return sessionFactory.getObject();
    }
}