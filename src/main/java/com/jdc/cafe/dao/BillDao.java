package com.jdc.cafe.dao;

import com.jdc.cafe.POJO.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillDao extends JpaRepository<Bill, Integer> {

}
