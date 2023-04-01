package com.example.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.config.AppConfig;
import com.example.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.val;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
@Transactional
class HibernateCacheTest {

  @Autowired
  private CacheManager cacheManager;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private SessionFactory sessionFactory;

  @Test
  void testCache() {

    val statistics = sessionFactory.getStatistics();
    statistics.setStatisticsEnabled(true);

    val user1 = userRepository.findById(1L);
    assertTrue(user1.isPresent());
    assertEquals("John Doe", user1.get().getName());

    val cache = (EhCacheCache) cacheManager.getCache("myCache");

    assertNotNull(cache);
    assertEquals(1, cache.getNativeCache().getSize());
  }

}

