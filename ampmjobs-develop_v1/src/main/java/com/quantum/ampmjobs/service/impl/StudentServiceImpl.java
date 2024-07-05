package com.quantum.ampmjobs.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quantum.ampmjobs.dao.UserRepository;
import com.quantum.ampmjobs.dto.JobDetails;
import com.quantum.ampmjobs.dto.JobTitles;
import com.quantum.ampmjobs.entities.AuthorizedUser;
import com.quantum.ampmjobs.json.dto.JobApply;
import com.quantum.ampmjobs.json.dto.SearchJob;
import com.quantum.ampmjobs.json.dto.UserIdAndFlag;
import com.quantum.ampmjobs.service.StudentService;
import com.quantum.ampmjobs.utility.ActivityUtilities;

@Service
public class StudentServiceImpl implements StudentService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public int getCountryId(final String email) {
		String sql = "select country_id from public.student where email = '" + email + "' limit 1";
		String uniqueResult = userRepo.getUniqueResult(sql);
		return Integer.parseInt(uniqueResult);
	}

	@Override
	public List<JobDetails> getPreferredJobs(final AuthorizedUser user) {
		String sql = "select id from public.student where email = '" + user.getUsername() + "' and phone = "
				+ user.getPhone() + " limit 1";
		String studentId = userRepo.getUniqueResult(sql);
		String jobsQuery = "select * from public.udfun_get_student_dashboard_jobdetails(" + studentId + ")";
		String dbResponse = jdbcTemplate.queryForObject(jobsQuery, String.class);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		List<JobDetails> jobDetails = new ArrayList<>();
		if (dbResponse != null) {
			try {
				jobDetails = objectMapper.readValue(dbResponse, new TypeReference<List<JobDetails>>() {
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return jobDetails;

	}

	@Override
	public List<JobTitles> getPreferredJobTitles(final AuthorizedUser user) {
		UserIdAndFlag in = new UserIdAndFlag(user.getUserId(), "student");
		String jobsQuery = "select * from public.udfun_get_preferred_job_titles(?::jsonb)";
		String json = ActivityUtilities.convertObjectIntoJson(in);
		String dbResponse = jdbcTemplate.queryForObject(jobsQuery, String.class, json);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		List<JobTitles> jobTitlesDtoList = new ArrayList<>();
		if (dbResponse != null) {
			try {
				jobTitlesDtoList = objectMapper.readValue(dbResponse, new TypeReference<List<JobTitles>>() {
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return jobTitlesDtoList;
	}

	@Override
	public Map<String, List<JobDetails>> getMappedJobs(final AuthorizedUser user) {
		List<JobTitles> preferredJobTitles = getPreferredJobTitles(user);
		List<JobDetails> preferredJobs = getPreferredJobs(user);
		if (preferredJobs != null) {
			Map<String, List<JobDetails>> map = preferredJobTitles.stream().collect(Collectors.toMap(
					JobTitles::getJobtype_name,
					jobTitle -> preferredJobs.stream().filter(job -> job.getJobtype_id() == jobTitle.getJobtype_id())
							.collect(Collectors.toList()),
					(jobDetailsList1, jobDetailsList2) -> jobDetailsList1, LinkedHashMap::new));
			return map;
		} else {
			Map<String, List<JobDetails>> map = new HashMap<>();
			preferredJobTitles.stream().forEach(title -> {
				map.put(title.getJobtype_name(), new ArrayList<>());
			});
			return map;
		}
	}

	@Override
	public int applyJob(final int jobId, final AuthorizedUser user) {
		int res = 0;
		try {
			JobApply dto = new JobApply(jobId, user.getUserId(), false, false, "Applied");
			String sql = "CALL public.usp_proc_job_application_request(?::jsonb,?)";
			String json = ActivityUtilities.convertObjectIntoJson(dto);
			String dbupdate = jdbcTemplate.queryForObject(sql, String.class, json, "");
			res = Integer.parseInt(dbupdate);
			System.out.println("job apply by student, status from DB : " + dbupdate);
		} catch (Exception e) {
			res = 0;
			e.printStackTrace();
		}
		return res;

	}

	@Override
	public List<JobDetails> getAppliedJobs(final AuthorizedUser user) {
		List<JobDetails> jobs = new ArrayList<>();
		try {
			SearchJob dto = new SearchJob(user.getUserId(), 1, 1000);

			String jobsQuery = "select * from public.udfun_get_student_job_applied(?::jsonb)";
			String json = ActivityUtilities.convertObjectIntoJson(dto);
			String dbResponse = jdbcTemplate.queryForObject(jobsQuery, String.class, json);

			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			if (dbResponse != null) {
				jobs = objectMapper.readValue(dbResponse, new TypeReference<List<JobDetails>>() {
				});
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jobs;
	}

	@Override
	public List<JobDetails> getAllJobsBySearch(final AuthorizedUser user) {

		List<JobDetails> jobs = new ArrayList<>();
		try {
			SearchJob dto = new SearchJob(user.getUserId(), 1, 500);

			String jobsQuery = "select * from public.udfun_get_student_dashboard_jobdetails_search(?::jsonb)";
			String json = ActivityUtilities.convertObjectIntoJson(dto);
			String dbResponse = jdbcTemplate.queryForObject(jobsQuery, String.class, json);

			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			if (dbResponse != null) {
				jobs = objectMapper.readValue(dbResponse, new TypeReference<List<JobDetails>>() {
				});
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jobs;

	}

}
