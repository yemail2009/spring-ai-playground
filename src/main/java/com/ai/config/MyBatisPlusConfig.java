//package com.ai.config;
//
//import com.baomidou.mybatisplus.annotation.DbType;
//import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
//import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
//import com.baomidou.mybatisplus.core.injector.ISqlInjector;
//import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
//import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
//import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
//import org.apache.ibatis.reflection.MetaObject;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.Date;
//// MyBatisPlusConfig.java
//@Configuration
//@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
//public class MyBatisPlusConfig {
//
//    /**
//     * 分页插件
//     */
//    @Bean
//    public MybatisPlusInterceptor mybatisPlusInterceptor() {
//        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
//        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
//        return interceptor;
//    }
//
//    /**
//     * 自动填充插件
//     */
//    @Bean
//    public MetaObjectHandler metaObjectHandler() {
//        return new MetaObjectHandler() {
//            @Override
//            public void insertFill(MetaObject metaObject) {
//                this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
//                this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
//                this.strictInsertFill(metaObject, "isDeleted", Integer.class, 0);
//            }
//
//            @Override
//            public void updateFill(MetaObject metaObject) {
//                this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
//            }
//        };
//    }
//}
