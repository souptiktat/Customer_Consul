package com.infosys.infytel.customer.dto;

import java.util.List;

import com.infosys.infytel.customer.entity.Customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {

	long phoneNo;
	String name;
	Integer age;
	String address;
	String password;
	char gender;
	PlanDTO currentPlan;
	List<Long> friendAndFamily;
	
	public static CustomerDTO valueOf(Customer cust) {
		CustomerDTO custDTO = new CustomerDTO();
		custDTO.setAge(cust.getAge());
		custDTO.setPhoneNo(cust.getPhoneNo());
		custDTO.setName(cust.getName());
		custDTO.setAddress(cust.getAddress());
		custDTO.setPassword(cust.getPassword());
		custDTO.setGender(cust.getGender());
		PlanDTO planDTO = new PlanDTO();
		planDTO.setPlanId(cust.getPlanId());
		custDTO.setCurrentPlan(planDTO);
		
		return custDTO;
	}
	
	public Customer createEntity() {
		Customer cust = new Customer();
		cust.setAge(this.age);
		cust.setPhoneNo(this.phoneNo);
		cust.setName(this.name);
		cust.setAddress(this.address);
		cust.setPassword(this.password);
		cust.setGender(this.gender);
		cust.setPlanId(this.getCurrentPlan().planId);
		return cust;
	}
}
