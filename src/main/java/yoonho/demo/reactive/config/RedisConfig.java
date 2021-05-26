package yoonho.demo.reactive.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@EnableCaching
@Configuration
public class RedisConfig {
		
	@Bean(name = "redisCacheManager")
	public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
		RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
				.disableCachingNullValues() // null value 캐시안함
				.entryTtl(Duration.ofSeconds(3*60)) // 캐시의 기본 유효시간 설정
				.computePrefixWith(CacheKeyPrefix.simple())
				.serializeKeysWith(
						RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())); // redis 캐시 데이터 저장방식을StringSeriallizer로 지정
						
		// 캐시키별 default 유효시간 설정
		Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
//		cacheConfigurations.put(CacheKey.USER, RedisCacheConfiguration.defaultCacheConfig()
//				.entryTtl(Duration.ofSeconds(CacheKey.USER_EXPIRE_SEC)));
//
		return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory)
				.cacheDefaults(configuration)
				.withInitialCacheConfigurations(cacheConfigurations).build();

	}
}
