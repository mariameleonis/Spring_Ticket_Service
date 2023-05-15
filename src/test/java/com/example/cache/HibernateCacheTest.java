package com.example.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.entity.User;
import com.example.repository.AbstractRepositoryTest;
import com.example.repository.UserRepository;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;

class HibernateCacheTest extends AbstractRepositoryTest {

  @Autowired
  private CacheManager cacheManager;

  @Autowired
  private UserRepository userRepository;

  @Test
  void testCache() {
    long userId = 1L;
    val user1 = userRepository.findById(userId);

    assertTrue(user1.isPresent());
    assertEquals("John Doe", user1.get().getName());

    val cache = (CaffeineCache) cacheManager.getCache("myCache");

    assertNotNull(cache);
    assertEquals(1, cache.getNativeCache().asMap().size());
    assertEquals(user1.get(), cache.get(userId, User.class));
  }

}

