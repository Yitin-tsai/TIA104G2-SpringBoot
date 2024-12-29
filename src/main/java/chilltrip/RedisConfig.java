package chilltrip;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {

    @Bean
    public JedisPool jedisPool() {
        // Redis 連線池的配置
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10); // 設定連線池中最大連線數
        poolConfig.setMaxIdle(5); // 設定最大空閒連線數
        poolConfig.setMinIdle(1); // 設定最小空閒連線數
        poolConfig.setTestOnBorrow(true); // 設定借用連線時檢查是否有效
        
        // 設定 Redis 伺服器的主機和端口
        String redisHost = "localhost";
        int redisPort = 6379;
        
        return new JedisPool(poolConfig, redisHost, redisPort);
    }
}
