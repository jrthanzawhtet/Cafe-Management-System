package com.jdc.cafe.POJO;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


import javax.persistence.*;
import java.io.Serializable;

@Data
@DynamicInsert
@DynamicUpdate
@Table(name = "bill")
public class Bill implements Serializable {

    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "contactnumber")
    private String contactNumber;

    @Column(name = "paymentmethod")
    private String paymentMethod;

    @Column(name = "column")
    private Integer total;

    @Column(name = "productdetails",columnDefinition = "json")
    private String productDetail;

    @Column(name = "createdby")
    private String createdBy;



}
