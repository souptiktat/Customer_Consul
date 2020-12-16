package com.infosys.infytel.customer.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "customer")
@Data
public class Customer {

	@Id
	@Column(name = "phone_no", nullable = false)
	Long phoneNo;
	
	@Column(nullable = false, length = 50)
	String name;
	
	@Column(nullable = false)
	Integer age;
	
	@Column(nullable = false, length = 50 )
	String address;
	
	@Column(nullable = false, length = 50)
	String password;
	
	@Column(nullable = false, length = 1)
	char gender;
	
	@Column(name = "plan_id", nullable = false)
	Integer planId;
}
