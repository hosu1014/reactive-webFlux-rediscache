package yoonho.demo.reactive.util;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebClientUtil {
	private static WebClient webClient;
	
	@Component
	static class Init {
		@Autowired
		void init(WebClient webClient) throws Exception {
			WebClientUtil.webClient = webClient;
		}
	}
	
	public static <T> WebClient.ResponseSpec callPost(String baseUrl, String uri, Object[] params, T requestObject) {
		return webClient.mutate()
		.baseUrl(baseUrl)
		.build()
		.post()
		.uri(uri, params)
		.accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestObject)
        .retrieve()
        .onStatus(status -> status.is4xxClientError() 
                || status.is5xxServerError()
                , clientResponse ->
                 clientResponse.bodyToMono(String.class)
                 .map(body -> new RuntimeException(body)));
	}
	
	public static <T> WebClient.ResponseSpec callGet(HttpHeaders httpHeaders, String baseUrl, String uri, MultiValueMap<String, String> queryParam) {
		return webClient.mutate()
		.baseUrl(baseUrl)
		.build()
		.get()
        .uri(uriBuilder -> uriBuilder.path(uri).queryParams(queryParam).build())
        .headers(headers -> {
            if (ObjectUtils.isEmpty(httpHeaders) == false) {
                headers.setAll(httpHeaders.toSingleValueMap());
            }
        })
        .accept(MediaType.APPLICATION_JSON, APPLICATION_FORM_URLENCODED)
        .retrieve()
        .onStatus(status -> status.is4xxClientError() 
                || status.is5xxServerError()
                , clientResponse ->
                 clientResponse.bodyToMono(String.class)
                 .map(body -> new RuntimeException(body)));
	}
	
	
}
