package client;

import com.google.gson.Gson;
import lombok.Setter;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.Arrays;
import java.util.List;

//@Log4j
public class Session {
    private CloseableHttpClient client;
    private String host;
    private Gson gson = new Gson();
    @Setter
    private String basePath;

    public Session(String host) {
        this.host = host;
//        client = HttpClients.custom().setDefaultHeaders();
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

    public <T> T get(List<NameValuePair> queryParam, Class<T> clazz) throws URISyntaxException, IOException {
        URI uri = new URIBuilder(host).addParameters(queryParam).build();
        HttpEntity entity = client.execute(new HttpGet(uri)).getEntity();

        return toObject(body(entity), clazz);
    }

    public <T> T get(Class<T> clazz) throws URISyntaxException, IOException {
        URIBuilder builder = new URIBuilder(host.concat(basePath));
        HttpEntity entity = client.execute(new HttpGet(builder.build())).getEntity();

        return toObject(body(entity), clazz);
    }

    public <T> T post(Object request, Class<T> clazz) throws URISyntaxException, IOException {
        URIBuilder builder = new URIBuilder(host);
        HttpPost post = new HttpPost(builder.build());
        post.setEntity(stringEntity(request));
        HttpEntity entity = client.execute(post).getEntity();

        return toObject(body(entity), clazz);
    }

    public <T> T post(List<BasicNameValuePair> formParams, Class<T> clazz) throws URISyntaxException, IOException {
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
        URIBuilder builder = new URIBuilder(host);
        HttpPost post = new HttpPost(builder.build());
        post.setEntity(entity);
        HttpEntity response = client.execute(post).getEntity();
        System.out.println(body(post.getEntity()));
        return toObject(body(response), clazz);
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        Session session = new Session("http://jsonplaceholder.typicode.com");
        session.setBasePath("/posts/1");
        List<NameValuePair> params = Arrays.<NameValuePair>asList(new BasicNameValuePair("postId", "1"));

        System.out.println(session.get(params, String.class));
    }
}