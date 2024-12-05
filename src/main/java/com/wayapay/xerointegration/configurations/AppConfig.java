package com.wayapay.xerointegration.configurations;

import com.wayapay.xerointegration.service.Oath2Service;
import com.wayapay.xerointegration.service.XeroIntegrationService;
import com.wayapay.xerointegration.service.impl.XeroAuthorizationServiceImpl;
import com.wayapay.xerointegration.service.impl.XeroIntegrationServiceImpl;
import com.xero.api.ApiClient;
import com.xero.api.client.AccountingApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import javax.ws.rs.core.UriBuilder;

@Configuration
@Slf4j
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        //NO OP
        try{
            CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setConnectTimeout(10_000);
            requestFactory.setReadTimeout(10_000);
            requestFactory.setHttpClient(httpClient);
            restTemplate.setRequestFactory(requestFactory);

        }catch(Exception e){
            log.error("Exception calling template " + e.getMessage());
        }
        //Do Proxy
        return restTemplate;
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        YamlPropertiesFactoryBean bean = new YamlPropertiesFactoryBean();
        bean.setResources(new ClassPathResource("messages.yml"));
        messageSource.setCommonMessages(bean.getObject());
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(0);
        return messageSource;
    }

    // Swagger Configurations
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.wayapay.xerointegration.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }



    @Bean
    public HttpHeaders headers(){
        return new HttpHeaders();
    }


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Xero Integration Microservice API")
                .description("Complete REST Xero middleware microservice API consumable by web clients")
                .license("MIT License")
                .version("1.1.0")
                .licenseUrl("https://opensource.org/licenses/MIT")
                .build();
    }
}
