package com.spring.controller;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.spring.Exception.EmailExistsException;
import com.spring.model.AddUserModel;
import com.spring.model.PasswordResetToken;
import com.spring.model.RegistrationFlow;
import com.spring.model.RegistrationFlowCompleteEvent;
import com.spring.model.SecurityQuestionAnswer;
import com.spring.model.SecurityRelatedQuestions;
import com.spring.model.VerificationToken;
import com.spring.security.SpringSecurityUserDetailsService;
import com.spring.service.InterfRegistrationSaveService;
import com.spring.service.InterfSaveService;
import com.spring.service.InterfSecurityQuestionAnswerService;
import com.spring.service.InterfSecurityRelatedQuestionService;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.common.collect.ImmutableMap;

@Controller
public class HelloController {

@Autowired	
private InterfSaveService interfSaveService;

@Autowired
private InterfRegistrationSaveService interfRegistrationSaveService;

@Autowired
private ApplicationEventPublisher eventPublisher;

@Autowired
private JavaMailSender mailSender;

@Autowired
private Environment environment;

@Autowired
private InterfSecurityRelatedQuestionService interfSecurityRelatedQuestionService;

@Autowired
private SpringSecurityUserDetailsService springSecurityUserDetailsService;

@Autowired
private InterfSecurityQuestionAnswerService interfSecurityQuestionAnswerService;
 

	@GetMapping("/")
	public String userTable(Model model) {
		List<AddUserModel> displayUserModel = interfSaveService.getUserDetails();
		model.addAttribute("displayUserModel", displayUserModel);
		return "SpringSecurityTable";
	}
	
	@GetMapping("/addUser")
	public String createNewUser(Model model) {
		model.addAttribute("addUserModel", new AddUserModel());
		return "addUser";
	}
	
	@PostMapping("/save")
	public String saveNewUser(@Valid AddUserModel addUserModel ,BindingResult bindingResult , Model model) {
		interfSaveService.saveModel(addUserModel);
		List<AddUserModel> displayUserModel = interfSaveService.getUserDetails();
		
		model.addAttribute("displayUserModel",displayUserModel );
		
		return "SpringSecurityTable";
	}
	
   @GetMapping("/delete")
   public String deleteUser(@RequestParam("id") Long id,Model model) {
	   
	   interfSaveService.deleteUser(id);
	    List<AddUserModel> displayUserModel = interfSaveService.getUserDetails();
		model.addAttribute("displayUserModel",displayUserModel );
	   return "SpringSecurityTable";
   }
   
   @GetMapping("/edit")
   public String editUser(@RequestParam("id") Long id,Model model) {
	   List<AddUserModel> displayUserModel = interfSaveService.getUserDetails();
	    Long editId = id;
		model.addAttribute("displayUserModel",displayUserModel );
		model.addAttribute("editId",editId);
		 return "SpringSecurityTable";
   }
   
   @GetMapping("/saveModifiedData")
   public String updateData(@RequestParam("id") Long id,@Valid AddUserModel addUserModel ,BindingResult bindingResult , Model model) {
 
	   interfSaveService.saveModel(addUserModel);
	   if(bindingResult.hasErrors()) {
		   return "SpringSecurityTable";
	   }
	   List<AddUserModel> displayUserModel = interfSaveService.getUserDetails();
	   model.addAttribute("displayUserModel",displayUserModel );
	   return "SpringSecurityTable";
   }
   
	
	  @GetMapping("/login") 
	  public String loginPage() { 
		  return "loginPage"; 
  }
	 
   
   @GetMapping("/signup")
   public ModelAndView signup(Model model) {
	   model.addAttribute("questions", interfSecurityRelatedQuestionService.findAllTheQuestions());
	   return new ModelAndView("registration","registrationData",new RegistrationFlow());
   }
   
   @PostMapping("/saveRegistration")
   public ModelAndView saveRegistration(@ModelAttribute("registrationData") @Valid final RegistrationFlow registrationFlow, 
   BindingResult result ,Model model ,@RequestParam("securityQuestion") Long securityQuestionId, @RequestParam("securityAnswer") String securityAnswer , 
   final HttpServletRequest request, final RedirectAttributes redirectAttributes)  {
	   model.addAttribute("registrationData", registrationFlow);
	   if(result.hasErrors()) {
		   return new ModelAndView("registration","registrationData",registrationFlow);
	   }
	   try {
		   registrationFlow.setEnabled(false);
		   final RegistrationFlow registered = interfRegistrationSaveService.save(registrationFlow);
		   final SecurityRelatedQuestions securityQuestions = interfSecurityRelatedQuestionService.findById(securityQuestionId);
		   interfSecurityQuestionAnswerService.save(registrationFlow, securityQuestions, securityAnswer);
		   final String appUrl = "http://"+request.getServerName()+":"+request.getServerPort() + request.getContextPath();
		   eventPublisher.publishEvent(new RegistrationFlowCompleteEvent(registered,appUrl));
		   redirectAttributes.addFlashAttribute("message","Your account created successfully. Please verify your email by clicking the link");
	   } catch(EmailExistsException e) {
		   result.addError(new FieldError("registration","email",e.getMessage()));
		   return new ModelAndView("registration","registrationData",registrationFlow);
	   }
	  
	   
	   return new ModelAndView("redirect:/login");
   }
   
   @GetMapping("/registrationConfirmation")
   public ModelAndView confirmRegistration(final Model model,@RequestParam("token") final String token,final RedirectAttributes redirectAttributes) {
	   final VerificationToken verificationToken = interfRegistrationSaveService.getVerificationToken(token);
	   
	   if(verificationToken == null) {
		   redirectAttributes.addFlashAttribute("errorMessage", "Invalid account confirmation token");
		   return new ModelAndView("redirect:/login");
	   }
	   
	   final RegistrationFlow registrationFlow = verificationToken.getRegistrationFlow();
	   final Calendar calendar = Calendar.getInstance();
	   
	   if((verificationToken.getExpiryDate().getTime() - calendar.getTime().getTime()) <=0) {
		   redirectAttributes.addFlashAttribute("errorMessage","Your registration token has expired. Please try again.");
		   return new ModelAndView("redirect:/login");
	   }
	   registrationFlow.setEnabled(true);
	   interfRegistrationSaveService.saveRegisteredUser(registrationFlow);
	   redirectAttributes.addFlashAttribute("message", "Your account verified successfully");
	   return new ModelAndView("redirect:/login");
   }
   
   @GetMapping("/forgotPassword")
   public String getPassword() {
	   return "forgotPassword";
   }
   
   @PostMapping("/resetPassword")
   @ResponseBody
   public ModelAndView resetPassword(final HttpServletRequest request,@RequestParam("email") final String userEmail,
		   final RedirectAttributes redirectAttributes) {
	   final RegistrationFlow user = interfRegistrationSaveService.findUserByEmail(userEmail);
	   
	   if(user != null) {
		   final String token = UUID.randomUUID().toString();
		   interfRegistrationSaveService.createPasswordResetToken(user, token);
		   final String appUrl = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
		   final SimpleMailMessage email = constructResetTokenEmail(appUrl, token, user);
		   mailSender.send(email);
	   }
	   redirectAttributes.addFlashAttribute("message", "You should Receive Password Reset Email Shortly.");
	   return new ModelAndView("redirect:/login");
   }
   
   
   @GetMapping("/changePassword")
   public ModelAndView showChangePassword(Model model,@RequestParam("id") final Long id ,@RequestParam("token") final String token,
		   final RedirectAttributes redirectAttributes) {
	   PasswordResetToken passToken = interfRegistrationSaveService.getPasswordResetToken(token);
	   if(passToken == null){
		   redirectAttributes.addFlashAttribute("errorMessage","Invalid Password Reset Token ");
		   return new ModelAndView("redirect:/login");
	   }
	   final RegistrationFlow user = passToken.getUser();
	   if(user.getRegistrationId() != id) {
		   redirectAttributes.addFlashAttribute("errorMessage","Invalid Password Reset Token 2");
		   return new ModelAndView("redirect:/login");
	   }
	   final Calendar cal = Calendar.getInstance();
	   if((passToken.getExpiryDate().getTime() - cal.getTime().getTime())<=0) {
		   redirectAttributes.addFlashAttribute("errorMessage", "Your password reset token has expired");
           return new ModelAndView("redirect:/login");
	   }
	   
	   final Authentication authentication = new UsernamePasswordAuthenticationToken(user,null,springSecurityUserDetailsService.loadUserByUsername(user.getEmail()).getAuthorities());
	   SecurityContextHolder.getContext().setAuthentication(authentication);
		
	   model.addAttribute("securityQuestion",interfSecurityQuestionAnswerService.findByUserId(id));
	   return new ModelAndView("resetPassword");
   }
   
   @PostMapping("/changeSavePassword")
   @ResponseBody
   public ModelAndView savePassword(Model model,@RequestParam("password") final String password ,@RequestParam("passwordConfirmation") final String passwordConfirmation,
		  @RequestParam("securityAnswer") String securityAnswer, @RequestParam("userId") Long userId, final RedirectAttributes redirectAttributes) {
	   model.addAttribute("securityQuestion",interfSecurityQuestionAnswerService.findByUserId(userId));
	   SecurityQuestionAnswer securityQuestionAnswer =  interfSecurityQuestionAnswerService.findByUserId(userId);
	   String savedAnswer = securityQuestionAnswer.getAnswer();
	   if(securityAnswer ==null || securityAnswer =="") {
		   return new ModelAndView("resetPassword",ImmutableMap.of("errorMessage","Please enter Security Answer"));
	   }
	   
	   if( !securityAnswer.equalsIgnoreCase(savedAnswer)) {
		   return new ModelAndView("resetPassword",ImmutableMap.of("errorMessage","Incorrect Security Answer"));
	   }
	   
	   if(password ==null || password =="") {
		   return new ModelAndView("resetPassword",ImmutableMap.of("errorMessage","Please enter password"));
	   }
	   if(!password.equalsIgnoreCase(passwordConfirmation)) {
		   return new ModelAndView("resetPassword",ImmutableMap.of("errorMessage","Passwords do not match"));
	   }
	   final RegistrationFlow user = (RegistrationFlow)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	   interfRegistrationSaveService.changePassword(user, password);
	   redirectAttributes.addFlashAttribute("message","Password Reset Successfully");
	   return new ModelAndView("redirect:/login");
   }

private SimpleMailMessage constructResetTokenEmail(final String appUrl,final String token,final RegistrationFlow user) {
	final String url = appUrl + "/changePassword?id="+user.getRegistrationId()+"&token="+token;
	final SimpleMailMessage email = new SimpleMailMessage();
	email.setTo(user.getEmail());
	email.setSubject("Reset Password");
	email.setText("Please open the following url to reset your password: \r\n "+url);
	email.setFrom(environment.getProperty("support.email"));
	return email;
}

}
