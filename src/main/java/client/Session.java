package client;

import com.google.gson.Gson;
import lombok.Setter;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.Arrays;
import java.util.List;

public class Session {
    private CloseableHttpClient client;
    private URIBuilder uriBuilder;
    private Gson gson = new Gson();
    @Setter
    private String basePath;

    public Session(String host) throws URISyntaxException {
        uriBuilder = new URIBuilder(host);
        Header header = new BasicHeader("Project-Id", "BlaBla");
        Header header1 = new BasicHeader(HttpHeaders.CONTENT_TYPE, "hyi");
        client = HttpClients.custom().setDefaultHeaders(Arrays.asList(header, header1)).build();
    }

    // region helpers
    private <T> T toObject(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    private StringEntity stringEntity(Object clazz) {
        return new StringEntity(gson.toJson(clazz), ContentType.APPLICATION_JSON);
    }

    private String body(HttpEntity entity) throws IOException {
        return EntityUtils.toString(entity);
    }
    //endregion helpers

    //region http
    public <T> T get(List<NameValuePair> queryParam, Class<T> clazz) throws URISyntaxException, IOException {
        URI uri = uriBuilder.setPath(basePath).addParameters(queryParam).build();
        HttpEntity entity = client.execute(new HttpGet(uri)).getEntity();

        return toObject(body(entity), clazz);
    }

    public <T> T get(Class<T> clazz) throws URISyntaxException, IOException {
        URI uri = uriBuilder.setPath(basePath).build();
        System.out.println(uri.toString());
        HttpEntity entity = client.execute(new HttpGet(uri)).getEntity();

        return toObject(body(entity), clazz);
    }

    public <T> T post(Object request, Class<T> clazz) throws URISyntaxException, IOException {
        HttpPost post = new HttpPost(uriBuilder.build());
        post.setEntity(stringEntity(request));
        HttpEntity entity = client.execute(post).getEntity();

        return toObject(body(entity), clazz);
    }

    public <T> T post(List<NameValuePair> formParams, Class<T> clazz) throws URISyntaxException, IOException {
        HttpPost post = new HttpPost(uriBuilder.build());
        post.setEntity(new UrlEncodedFormEntity(formParams, Consts.UTF_8));
        HttpEntity response = client.execute(post).getEntity();

        return toObject(body(response), clazz);
    }
    //endregion
}