package com.jdc.cafe.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Objects;

public interface BillService {


    ResponseEntity<String> generateReport(Map<String, Object> requestMap);
}