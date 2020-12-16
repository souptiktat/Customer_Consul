package com.infosys.infytel.customer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.infytel.customer.dto.CustomerDTO;
import com.infosys.infytel.customer.dto.LoginDTO;
import com.infosys.infytel.customer.entity.Customer;
import com.infosys.infytel.customer.repository.CustomerRepository;

@Service
public class CustomerService {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	CustomerRepository custRepo;
	
	public void createCustomer(CustomerDTO custDTO) {
		logger.info("Creation request for customer {}" , custDTO);
		Customer cust = custDTO.createEntity();
		custRepo.save(cust);
	}
	
	public boolean login(LoginDTO loginDTO) {
		logger.info("Loginrequest for customer {} with password {}" , loginDTO.getPhoneNo(), loginDTO.getPassword());
		Customer cust = custRepo.findByPhoneNo(loginDTO.getPhoneNo());
		if(cust!=null && cust.getPassword().equals(loginDTO.getPassword())) {
			return true;
		}
		return false;
	}
	
	public CustomerDTO getCustomerProfile(Long phoneNo) {
		logger.info("Profile request for customer {}" , phoneNo);
		Customer cust = custRepo.findByPhoneNo(phoneNo);
		CustomerDTO custDTO = CustomerDTO.valueOf(cust);
		
		logger.info("Profile for customer : {}" , custDTO);
		return custDTO;
	}
}
