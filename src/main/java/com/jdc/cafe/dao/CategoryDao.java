package com.jdc.cafe.dao;

import com.jdc.cafe.POJO.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryDao extends JpaRepository<Category, Integer> {

    List<Category> getAllCategory();


}
