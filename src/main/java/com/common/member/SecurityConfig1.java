package com.common.member;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig1 extends WebSecurityConfigurerAdapter {

	@Resource
	private MemberServiceImpl3 memberServiceImpl;
	@Resource
	private DataSource dataSource;
	@Resource
	MemberDAO5 memberDAO;

	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/assets/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// HttpServletRequest ???? URL?? ???? ???? ?????? ????
		http.authorizeRequests()
			.antMatchers("/views/*").permitAll()
			.antMatchers("/views/**").hasRole("ADMIN")
			.antMatchers("/**").permitAll();

		// ?????? ????
		http.formLogin()
			.loginPage("/login")
			.loginProcessingUrl("/login_exe.run")
			.usernameParameter("user_id")
			.passwordParameter("user_pass")
			.successHandler(thisSuccessHandler())
			.failureHandler(thisFailureHandler())
			.permitAll();

		// ???????? ????
		http.logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			.deleteCookies("JSESSIONID","rememberMeCookie")
			.invalidateHttpSession(true)
			.permitAll()
			.logoutSuccessUrl("/");

		// ?????? ????
		http.rememberMe().rememberMeServices(thisRememberMeServices());
		// persistent ???? ???????? "?????? ???? ?????? ???? ?????? ???????? ????????. ?????? ?????? ?????? ???????????? ???? ?????? ?? ????????."
		http.addFilterAfter(new ExceptionHandlerFilter(), SecurityContextHolderAwareRequestFilter.class);

		//???????? ????
		http.authorizeRequests().accessDecisionManager(accessDecisionManager());

		// X-Frame-Options header ????. iframe ????.
		http.headers().frameOptions().disable();
		// CORS(Cross-Origin Resource Sharing)
		http.cors().configurationSource(corsConfigurationSource());
		// cors?? ???????? ???? disable ?????? ????
		http.csrf().disable();
		// ajax post CSRF???? ?????? ????
		// http.csrf().ignoringAntMatchers("**/*do","**/*run");

		// 403 ???????? ??????
		http.exceptionHandling().accessDeniedPage("/error/denied");
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new MemberPasswordEncoder1();
	}

	@Bean // ?????? ???? ?????? ???? Handler
	public MemberSuccessHandler1 thisSuccessHandler() {
		return new MemberSuccessHandler1("/index");
	}

	@Bean // ?????? ???? ?????? ???? Handler
	public MemberFailureHandler1 thisFailureHandler() {	return new MemberFailureHandler1("/login"); }

	@Bean // ???????? ?? URL?? ????????
	public RoleHierarchy roleHierarchy() {
		RoleHierarchyImpl roleHierarchyImpl = new RoleHierarchyImpl();
		roleHierarchyImpl.setHierarchy("ROLE_MASTER > ROLE_ADMIN > ROLE_USER");

		return roleHierarchyImpl;
	}

	@Bean
	public AccessDecisionManager accessDecisionManager() {

		WebExpressionVoter webExpressionVoter = new WebExpressionVoter();
		DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
		expressionHandler.setRoleHierarchy(roleHierarchy());
		webExpressionVoter.setExpressionHandler(expressionHandler);

		List<AccessDecisionVoter<? extends Object>> decisionVoters = Arrays.asList(
			webExpressionVoter,
			new RoleVoter(),
			new AuthenticatedVoter(),
			new URLVoter1(memberDAO)
		);

		return new UnanimousBased(decisionVoters);
	}

	// ?????????? ????
	@Bean
	public JdbcTokenRepositoryImpl jdbcTokenRepositoryImpl() {
		JdbcTokenRepositoryImpl repository = new JdbcTokenRepositoryImpl();
		repository.setCreateTableOnStartup(false); // ?????????? ????. ???? true?? ???????? ?? ???????? false ????.
		repository.setDataSource(dataSource);
		return repository;
	}

	@Bean
	public PersistentTokenBasedRememberMeServices thisRememberMeServices() {
		PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices = new PersistentTokenBasedRememberMeServices("rememberMeCookie", memberServiceImpl, jdbcTokenRepositoryImpl());
		persistentTokenBasedRememberMeServices.setTokenValiditySeconds(60*60*24*30);//30???????? ????
		persistentTokenBasedRememberMeServices.setCookieName("rememberMeCookie");
		persistentTokenBasedRememberMeServices.setParameter("ingCheck");

		return persistentTokenBasedRememberMeServices;
	}

	// CORS ????
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(Arrays.asList("http://webadmin.amoretto.co.kr","https://webadmin.amoretto.co.kr","https://reconciliation.petitelin.co.kr")); //CORS ?????? ?????? ??????
		configuration.setAllowedHeaders(Arrays.asList("*")); //???? ?????? ???? ???????? CORS ?????? ?????? ????
		configuration.setAllowedMethods(Arrays.asList("POST","GET")); //CORS ?????? ?????? Http  Method??
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}