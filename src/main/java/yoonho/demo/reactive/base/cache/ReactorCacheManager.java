package yoonho.demo.reactive.base.cache;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;
import reactor.cache.CacheFlux;
import reactor.cache.CacheMono;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;

@RequiredArgsConstructor
@Component
public class ReactorCacheManager {
	private final CacheManager cacheManager;

    public <T> Mono<T> findCachedMono(String cacheName, Object key, Supplier<Mono<T>> retriever, Class<T> classType) {
        Cache cache = cacheManager.getCache(cacheName);
        if (ObjectUtils.isEmpty(cache)) {
            return Mono.defer(retriever);
        }
        return CacheMono
                .lookup(k -> {
                    T result = cache.get(k, classType);
                    return Mono.justOrEmpty(result).map(Signal::next);
                }, key)
                .onCacheMissResume(Mono.defer(retriever))
                .andWriteWith((k, signal) -> Mono.fromRunnable(() -> {
                    if (!signal.isOnError()) {
                        cache.put(k, signal.get());
                    }
                }));
    }

    public <T> Flux<T> findCachedFlux(String cacheName, Object key, Supplier<Flux<T>> retriever) {
        Cache cache = cacheManager.getCache(cacheName);
        if (ObjectUtils.isEmpty(cache)) {
            return Flux.defer(retriever);
        }
        return CacheFlux
                .lookup(k -> {
                    List<T> result = cache.get(k, List.class);
                    return Mono.justOrEmpty(result)
                            .flatMap(list -> Flux.fromIterable(list).materialize().collectList());
                }, key)
                .onCacheMissResume(Flux.defer(retriever))
                .andWriteWith((k, signalList) -> Flux.fromIterable(signalList)
                        .dematerialize()
                        .collectList()
                        .doOnNext(list -> {
                            cache.put(k, list);
                        })
                        .then());
    }
}
