package telran.forum.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.forum.configuration.AccountConfiguration;
import telran.forum.dao.UserAccountRepository;
import telran.forum.dto.UserEditDto;
import telran.forum.dto.UserProfileDto;
import telran.forum.dto.UserRegisterDto;
import telran.forum.exceptions.ForbiddenException;
import telran.forum.exceptions.RolesSetIsEmptyException;
import telran.forum.exceptions.UserAuthenticationException;
import telran.forum.exceptions.UserExistsException;
import telran.forum.exceptions.UserNotExitsException;
import telran.forum.model.UserAccount;

@Service
public class UserAccountServiceImpl implements UserAccountService {
	
	@Autowired
	UserAccountRepository accountRepository;
	
	@Autowired
	AccountConfiguration accountConfiguration;


	@Override
	public UserProfileDto register(UserRegisterDto userRegisterDto) {
		if (accountRepository.existsById(userRegisterDto.getLogin())) {
			throw new UserExistsException();
		}
		String hashPassword = BCrypt.hashpw(userRegisterDto.getPassword(), BCrypt.gensalt());
		UserAccount userAccount = UserAccount.builder()
									.login(userRegisterDto.getLogin())
									.password(hashPassword)
									.firstName(userRegisterDto.getFirstName())
									.lastName(userRegisterDto.getLastName())
									.role("User")
									.expDate(LocalDateTime.now().plusDays(accountConfiguration.getExpPeriod()))
									.build();
		accountRepository.save(userAccount);
		return userAccountToUserProfileDto(userAccount);
	}
	
	private UserProfileDto userAccountToUserProfileDto(UserAccount userAccount) {
		return UserProfileDto.builder()
				.login(userAccount.getLogin())
				.firstName(userAccount.getFirstName())
				.lastName(userAccount.getLastName())
				.roles(userAccount.getRoles())
				.build();
	}

	@Override
	public UserProfileDto login(String token) {
		UserAccountCredentials userAccountCredentials = 
				accountConfiguration.tokenDecode(token);
		UserAccount userAccount = 
				accountRepository.findById(userAccountCredentials.getLogin())
				.orElseThrow(UserAuthenticationException::new);
		if (!BCrypt.checkpw(userAccountCredentials.getPassword(), userAccount.getPassword())) {
			throw new ForbiddenException();
		}
		
		return userAccountToUserProfileDto(userAccount);
	}

	@Override
	public UserProfileDto editUser(String token, UserEditDto userEditDto) {
		UserAccountCredentials userAccountCredentials = 
				accountConfiguration.tokenDecode(token);
		UserAccount userAccount = 
				accountRepository.findById(userAccountCredentials.getLogin())
				.orElseThrow(UserAuthenticationException::new);
		if (!BCrypt.checkpw(userAccountCredentials.getPassword(), userAccount.getPassword())) {
			throw new ForbiddenException();
		}
		
			if(userEditDto.getFirstName()!=null)
			{
				userAccount.setFirstName(userEditDto.getFirstName());
			}
			if(userEditDto.getLastName()!=null)
			{
				userAccount.setLastName(userEditDto.getLastName());
			}
		
		accountRepository.save(userAccount);
		return userAccountToUserProfileDto(userAccount);
	}

	@Override
	public UserProfileDto removeUser(String token) {
		UserAccountCredentials userAccountCredentials = 
				accountConfiguration.tokenDecode(token);
		UserAccount userAccount = 
				accountRepository.findById(userAccountCredentials.getLogin())
				.orElseThrow(UserAuthenticationException::new);
		if (!BCrypt.checkpw(userAccountCredentials.getPassword(), userAccount.getPassword())) {
			throw new ForbiddenException();
		}
	
		if(userAccount.getRoles().contains("Administator"))
		{
			accountRepository.delete(userAccount);
			return userAccountToUserProfileDto(userAccount);
		}
		return null;
	}

	@Override
	public void changePassword(String token, String password) {
		UserAccountCredentials userAccountCredentials = 
				accountConfiguration.tokenDecode(token);
		UserAccount userAccount = 
				accountRepository.findById(userAccountCredentials.getLogin())
				.orElseThrow(UserAuthenticationException::new);
		if (!BCrypt.checkpw(userAccountCredentials.getPassword(), userAccount.getPassword())) {
			throw new ForbiddenException();
		}
		
			userAccount.setPassword(password);
			accountRepository.save(userAccount);
		
		
	}

	@Override
	public Set<String> addRole(String login, String role, String token) {
		UserAccountCredentials userAccountCredentials = 
				accountConfiguration.tokenDecode(token);
		UserAccount userAccount = 
				accountRepository.findById(userAccountCredentials.getLogin())
				.orElseThrow(UserAuthenticationException::new);
		if (!BCrypt.checkpw(userAccountCredentials.getPassword(), userAccount.getPassword())) {
			throw new ForbiddenException();
		}
		if(userAccount.getRoles().contains("Administator"))
		{
			UserAccount acc=accountRepository.findById(login).orElse(null);
			if(acc == null)
			{
				throw new UserNotExitsException();
			}
			Set<String>roleSet=acc.getRoles();
			if(roleSet==null)
			{
				roleSet=new HashSet<String>();
			}
			roleSet.add(role);
			acc.setRoles(roleSet);
			accountRepository.save(acc);
			return acc.getRoles();
		}
		
	 return null;
	}

	@Override
	public Set<String> removeRole(String login, String role, String token) {
		UserAccountCredentials userAccountCredentials = 
				accountConfiguration.tokenDecode(token);
		UserAccount userAccount = 
				accountRepository.findById(userAccountCredentials.getLogin())
				.orElseThrow(UserAuthenticationException::new);
		if (!BCrypt.checkpw(userAccountCredentials.getPassword(), userAccount.getPassword())) {
			throw new ForbiddenException();
		}
		if(userAccount.getRoles().contains("Administator"))
		{
			UserAccount acc=accountRepository.findById(login).orElse(null);
			if(acc == null)
			{
				throw new UserNotExitsException();
			}
			Set<String>roleSet=acc.getRoles();
			if(roleSet==null)
			{
				throw new RolesSetIsEmptyException();
			}
			roleSet.remove(role);
			acc.setRoles(roleSet);
			accountRepository.save(acc);
			return acc.getRoles();
		}
		return null;
	}

}
