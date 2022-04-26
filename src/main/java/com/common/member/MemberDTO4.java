package com.common.member;

import com.common.common.Common;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class MemberDTO4 implements UserDetails {

	private String user_id;
	private String user_pass;
	private String pass_expire;
	private String check_use;
	private int login_try;

	private String id;
	private String user_name;
	private String user_birthday;
	private String user_yang;
	private String user_zip;
	private String user_addr1;
	private String user_addr2;
	private String user_tel;
	private String user_hp;
	private String user_email;
	private String user_marriage;
	private String user_marriday;
	private String com_root;
	private String com_dept;
	private String user_position;
	private String user_duty;
	private String photo_URL;
	private String user_sex;
	private String privacy;
	private String menu_auth;
	private String cdate;
	private String treatment;
	private String store_userid;
	private String user_brand;
	private String os_version;
	private String principal_test;


	@Override
	public String getUsername() {
		return this.user_id;
	}

	@Override
	public String getPassword() { return this.user_pass; }

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() { //계정이 갖고 있는 권한 목록
		return Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
	}

	@Override // 계정이 만료되지 않았는지
	public boolean isAccountNonExpired() { // 계정이 만료되지 않았는지
		return !"n".equals(this.check_use);
	}

	@Override // 계정이 잠기지 않았는지
	public boolean isAccountNonLocked() { return login_try < 5; }

	@Override // 비밀번호가 만료되지 않았는지
	public boolean isCredentialsNonExpired() { //계정의 패스워드가 만료되지 않았는지
		return Common.nowDate().compareTo(this.pass_expire) < 0;
	}

	@Override // 계정이 활성화(사용가능)인지
	public boolean isEnabled() { //계정이 사용가능한 계정인지
		return "y".equals(this.check_use) ;
	}



	Set<GrantedAuthority> authorities=null;
	public void setAuthorities(Set<GrantedAuthority> authorities) {
		this.authorities=authorities;
	}

	public String get_Id() {return id;}
	public void set_Id(String id) {this.id = id;}

	public String getUser_id() { return user_id; }
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_pass() {
		return user_pass;
	}
	public void setUser_pass(String user_pass) {
		this.user_pass = user_pass;
	}

	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_birthday() {
		return user_birthday;
	}
	public void setUser_birthday(String user_birthday) {
		this.user_birthday = user_birthday;
	}

	public String getUser_yang() {
		return user_yang;
	}
	public void setUser_yang(String user_yang) {
		this.user_yang = user_yang;
	}

	public String getUser_zip() {
		return user_zip;
	}
	public void setUser_zip(String user_zip) {
		this.user_zip = user_zip;
	}

	public String getUser_addr1() {
		return user_addr1;
	}
	public void setUser_addr1(String user_addr1) {
		this.user_addr1 = user_addr1;
	}

	public String getUser_addr2() {
		return user_addr2;
	}
	public void setUser_addr2(String user_addr2) {
		this.user_addr2 = user_addr2;
	}

	public String getUser_tel() {
		return user_tel;
	}
	public void setUser_tel(String user_tel) {
		this.user_tel = user_tel;
	}

	public String getUser_hp() {
		return user_hp;
	}
	public void setUser_hp(String user_hp) {
		this.user_hp = user_hp;
	}

	public String getUser_email() {
		return user_email;
	}
	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}

	public String getUser_marriage() {
		return user_marriage;
	}
	public void setUser_marriage(String user_marriage) {
		this.user_marriage = user_marriage;
	}

	public String getUser_marriday() {
		return user_marriday;
	}
	public void setUser_marriday(String user_marriday) {
		this.user_marriday = user_marriday;
	}

	public String getCom_root() {
		return com_root;
	}
	public void setCom_root(String com_root) {
		this.com_root = com_root;
	}

	public String getCom_dept() {
		return com_dept;
	}
	public void setCom_dept(String com_dept) {
		this.com_dept = com_dept;
	}

	public String getUser_position() {
		return user_position;
	}
	public void setUser_position(String user_position) {
		this.user_position = user_position;
	}

	public String getUser_duty() {
		return user_duty;
	}
	public void setUser_duty(String user_duty) {
		this.user_duty = user_duty;
	}

	public String getPhoto_URL() {
		return photo_URL;
	}
	public void setPhoto_URL(String photo_URL) {
		this.photo_URL = photo_URL;
	}

	public String getUser_sex() {
		return user_sex;
	}
	public void setUser_sex(String user_sex) {
		this.user_sex = user_sex;
	}

	public String getPrivacy() {
		return privacy;
	}
	public void setPrivacy(String privacy) {
		this.privacy = privacy;
	}

	public String getMenu_auth() {
		return menu_auth;
	}
	public void setMenu_auth(String menu_auth) {
		this.menu_auth = menu_auth;
	}

	public String getCdate() {
		return cdate;
	}
	public void setCdate(String cdate) {
		this.cdate = cdate;
	}

	public String getTreatment() {
		return treatment;
	}
	public void setTreatment(String treatment) {
		this.treatment = treatment;
	}

	public String getStore_userid() {
		return store_userid;
	}
	public void setStore_userid(String store_userid) {
		this.store_userid = store_userid;
	}

	public int getLogin_try() {
		return login_try;
	}
	public void setLogin_try(int login_try) {
		this.login_try = login_try;
	}

	public String getPass_expire() {
		return pass_expire;
	}
	public void setPass_expire(String pass_expire) {
		this.pass_expire = pass_expire;
	}

	public String getUser_brand() {
		return user_brand;
	}
	public void setUser_brand(String user_brand) {
		this.user_brand = user_brand;
	}

	public String getOs_version() {
		return os_version;
	}
	public void setOs_version(String os_version) {
		this.os_version = os_version;
	}

	public String getCheck_use() { return check_use; }
	public void setCheck_use(String check_use) { this.check_use = check_use; }
}