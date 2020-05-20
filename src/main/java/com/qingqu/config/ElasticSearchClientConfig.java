package com.qingqu.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * ElasticSearchClient配置文件
 * </p>
 *
 * @author chen
 * @since 2020/5/8
 */
    // spring两步骤
    // 1、找对象
    // 2、放到spring中待用！
    // 3、如果是spring boot 就分析源码
    // xxxxAutoConfiguration   xxxProperties
@Configuration
public class ElasticSearchClientConfig {

    @Bean
    public RestHighLevelClient restHighLevelClient(){
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));

        return client;
    }
}
