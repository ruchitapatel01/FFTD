package com.project.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.VO.LoginVO;

public interface LoginDAO extends JpaRepository<LoginVO, Integer>{
	
}
