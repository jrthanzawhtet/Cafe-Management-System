package com.jdc.cafe.dao;

import com.jdc.cafe.POJO.Product;
import com.jdc.cafe.wrapper.ProductWrapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductDao extends JpaRepository<Product, Integer> {

    List<ProductWrapper> getAllProduct();
}
