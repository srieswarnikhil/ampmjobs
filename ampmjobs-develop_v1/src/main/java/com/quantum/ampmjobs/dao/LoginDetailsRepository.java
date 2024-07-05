package com.quantum.ampmjobs.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.quantum.ampmjobs.entities.LoginDetails;

public interface LoginDetailsRepository extends JpaRepository<LoginDetails, Long> {

	@Query("SELECT u FROM LoginDetails u WHERE u.phone = :mobileNumber")
	LoginDetails findLoginDetailsByMobile(long mobileNumber);

	@Query("SELECT u FROM LoginDetails u WHERE u.email = :email")
	LoginDetails findLoginDetailsByEmail(String email);

	LoginDetails findByEmailAndPhone(String email, long phone);

	@Query("SELECT ld FROM LoginDetails ld " + "WHERE CAST(ld.phone AS string) LIKE concat('%', :searchText, '%') "
			+ "OR lower(ld.email) LIKE lower(concat('%', :searchText, '%'))")
	List<LoginDetails> findByPhoneOrEmailContainingIgnoreCase(@Param("searchText") String searchText,
			Pageable pageable);

}