package com.wade.wtra.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;

public class StardogService {

    public static final String ENDPOINT = "http://ec2-54-154-190-8.eu-west-1.compute.amazonaws.com:5820/wtra/query";
    public static final String JSON_LD_CONTENT_TYPE = "application/sparql-results+json";
    private static final HttpClient client = new DefaultHttpClient();


    public static HttpResponse execute(String query) throws Exception {
        return execute(query, "true");
    }

    public static HttpResponse execute(String query, String reasoning) throws Exception{
        URIBuilder builder = new URIBuilder(ENDPOINT);
        builder.addParameter("query", query);
        builder.addParameter("reasoning", reasoning);
        HttpGet getRequest = new HttpGet(builder.build().toURL().toString());
        getRequest.addHeader("Authorization", "Basic YWRtaW46YWRtaW4=");
        getRequest.addHeader("Accept", JSON_LD_CONTENT_TYPE);
        return client.execute(getRequest);
    }

}
