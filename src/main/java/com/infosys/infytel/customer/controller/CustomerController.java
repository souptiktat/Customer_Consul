package com.infosys.infytel.customer.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.infosys.infytel.customer.dto.CustomerDTO;
import com.infosys.infytel.customer.dto.LoginDTO;
import com.infosys.infytel.customer.dto.PlanDTO;
import com.infosys.infytel.customer.service.CustCircuitBreakerService;
import com.infosys.infytel.customer.service.CustomerService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.vavr.concurrent.Future;

@RestController
@EnableAutoConfiguration
@CrossOrigin
//@LoadBalancerClient(name="MyloadBalancer", configuration=LoadBalancerConfig.class)
public class CustomerController {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	CustomerService custService;
	
	//@Autowired
	//RestTemplate template;
	
	@Autowired
	DiscoveryClient client;
	
	@Autowired
	CustCircuitBreakerService custCircuitBreakerService;
	
	//@Value("${friend.uri}")
	String friendUri;
	
	//@Value("${plan.uri}")
	String planUri;
	
	@RequestMapping(value = "/customers", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void createCustomer(@RequestBody CustomerDTO custDTO) {
		logger.info("Creation request for customer {}" , custDTO);
		custService.createCustomer(custDTO);
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public boolean login(@RequestBody LoginDTO loginDTO) {
		logger.info("Login request for Customer {} with password {}", loginDTO.getPhoneNo(),loginDTO.getPassword());
		return custService.login(loginDTO);
	}

	/*@RequestMapping(value = "/customers/{phoneNo}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public CustomerDTO getCustomerProfile(@PathVariable Long phoneNo) {
		logger.info("Profile request for customer {}" , phoneNo);
		CustomerDTO custDTO = custService.getCustomerProfile(phoneNo);
		PlanDTO planDTO = new RestTemplate().getForObject("http://localhost:8400/plans/"+custDTO.getCurrentPlan().getPlanId(), PlanDTO.class);
		custDTO.setCurrentPlan(planDTO);
		List<Long> friends = new RestTemplate().getForObject("http://localhost:8300/customers/"+ phoneNo +"/friends", List.class);
		custDTO.setFriendAndFamily(friends);
		return custDTO;
	}*/
	
	//@CircuitBreaker(name="customerService" , fallbackMethod="getCustomerProfileFallback")
	@RequestMapping(value = "/customers/{phoneNo}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public CustomerDTO getCustomerProfile(@PathVariable Long phoneNo) {
		long overAllStart = System.currentTimeMillis();
		logger.info("Profile request for customer {}", phoneNo);
		
		CustomerDTO custDTO=custService.getCustomerProfile(phoneNo);
		
		/*List<ServiceInstance> listOfPlanInstances = client.getInstances("PlanMS");
		if(listOfPlanInstances!=null && !listOfPlanInstances.isEmpty())
			planUri=listOfPlanInstances.get(0).getUri().toString();
		System.out.println("planUri :: " + planUri);*/
		
		//PlanDTO planDTO=new RestTemplate().getForObject(planUri+"/plans/"+custDTO.getCurrentPlan().getPlanId(), PlanDTO.class);
		
		//PlanDTO planDTO=template.getForObject("http://PlanMS/plans/"+custDTO.getCurrentPlan().getPlanId(), PlanDTO.class);
		
		long planStart = System.currentTimeMillis();
		Future<PlanDTO> planDTOFuture= custCircuitBreakerService.getSpecificPlan(custDTO.getCurrentPlan().getPlanId());
		long planStop = System.currentTimeMillis();
		
		long friendStart = System.currentTimeMillis();
		//@SuppressWarnings("unchecked")
		//List<Long> friends=template.getForObject("http://MyloadBalancer/customers/"+phoneNo+"/friends", List.class);
		
		/*List<ServiceInstance> listOfFriendInstances = client.getInstances("FriendFamilyMS");
		if(listOfFriendInstances!=null && !listOfFriendInstances.isEmpty())
			friendUri=listOfFriendInstances.get(0).getUri().toString();*/
		
		//List<Long> friends=new RestTemplate().getForObject(friendUri+"/customers/"+phoneNo+"/friends", List.class);
		
		//List<Long> friends=template.getForObject("http://FriendFamilyMS/customers/"+phoneNo+"/friends", List.class);
		
		Future<List<Long>> friendsFuture= custCircuitBreakerService.getFriendFamily(phoneNo);
		
		long friendStop = System.currentTimeMillis();
		
		long overAllStop = System.currentTimeMillis();
		
		custDTO.setCurrentPlan(planDTOFuture.get());
		custDTO.setFriendAndFamily(friendsFuture.get());
		
		logger.info("Total Time for Plan : {}" , (planStop-planStart));
		logger.info("Total time for friend : {}" , (friendStop-friendStart));
		logger.info("Total overall time for request : {}" , (overAllStop-overAllStart));
		return custDTO;
	}
	
	public CustomerDTO getCustomerProfileFallback(Long phoneNo, Throwable throwable) {
		logger.info("==========In Fallback===============");
		return new CustomerDTO();
	}
}
