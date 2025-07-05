package com.hmall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.rest.uris}")
    private String uris;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        // 解析ES地址
        String[] uriArray = uris.split(",");
        HttpHost[] httpHosts = new HttpHost[uriArray.length];
        for (int i = 0; i < uriArray.length; i++) {
            String uri = uriArray[i];
            // 去除 http:// 或 https://
            uri = uri.replace("http://", "").replace("https://", "");
            String[] split = uri.split(":");
            String host = split[0];
            int port = split.length > 1 ? Integer.parseInt(split[1]) : 9200;
            httpHosts[i] = new HttpHost(host, port, "http");
        }
        return new RestHighLevelClient(RestClient.builder(httpHosts));
    }
}

