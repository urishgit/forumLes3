package telran.forum.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import telran.forum.dto.UserEditDto;
import telran.forum.dto.UserProfileDto;
import telran.forum.dto.UserRegisterDto;
import telran.forum.service.UserAccountService;

@RestController
@RequestMapping("/account")
public class UserAccountController {
	@Autowired
	UserAccountService userAccountService;
	
	@PostMapping("/user")
	public UserProfileDto register(@RequestBody UserRegisterDto userRegisterDto) {
		return userAccountService.register(userRegisterDto);
	}
	
	@PostMapping("/login")
	public UserProfileDto login(@RequestHeader("Authorization") String token) {
		return userAccountService.login(token);
	}
@PostMapping("/edit")
UserProfileDto editUser( @RequestHeader("Authorization") String token, @RequestBody  UserEditDto userEditDto)
{
	return userAccountService.editUser(token, userEditDto);
}

	@DeleteMapping("/delete")
	UserProfileDto removeUser( @RequestHeader("Authorization")  String token)
	{
		return userAccountService.removeUser(token);
	}
	@PostMapping("/change/password")
	void changePassword( @RequestHeader("Authorization")  String token,@RequestParam String password)
	{
		userAccountService.changePassword(token, password);
	}
	@PutMapping("/add/role/{role}")
	Set<String> addRole(@RequestParam String login,@PathVariable String role,  @RequestHeader("Authorization")  String token)
	{
		return userAccountService.addRole(login, role, token);
	}
	
	@DeleteMapping("/remove/role/{login}")
	Set<String> removeRole(@RequestParam String login,@PathVariable String role,   @RequestHeader("Authorization") String token)
	{
		return userAccountService.removeRole(login, role, token);
	}
	
	
}
