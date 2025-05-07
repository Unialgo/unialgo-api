package com.ua.unialgo.list.repository;
import org.springframework.data.repository.CrudRepository;

import com.ua.unialgo.list.entity.List;

public interface ListRepository extends CrudRepository<List, Long> {}
