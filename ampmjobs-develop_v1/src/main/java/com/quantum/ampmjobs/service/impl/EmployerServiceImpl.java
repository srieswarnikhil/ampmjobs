package com.quantum.ampmjobs.service.impl;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quantum.ampmjobs.api.utility.SMSUtility;
import com.quantum.ampmjobs.dao.UserRepository;
import com.quantum.ampmjobs.dto.Applicants;
import com.quantum.ampmjobs.dto.JobDetails;
import com.quantum.ampmjobs.dto.JobFilledData;
import com.quantum.ampmjobs.dto.JobTitles;
import com.quantum.ampmjobs.dto.MyJsonData;
import com.quantum.ampmjobs.dto.PostedJobs;
import com.quantum.ampmjobs.dto.ShortlistStudent;
import com.quantum.ampmjobs.entities.AuthorizedUser;
import com.quantum.ampmjobs.json.dto.EmployerPostedJobs;
import com.quantum.ampmjobs.json.dto.IdAndJobid;
import com.quantum.ampmjobs.json.dto.JobApply;
import com.quantum.ampmjobs.json.dto.UserIdAndFlag;
import com.quantum.ampmjobs.service.EmployerService;
import com.quantum.ampmjobs.utility.ActivityUtilities;

@Service
public class EmployerServiceImpl implements EmployerService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private SMSUtility smsUtility;

	@Autowired
	@Qualifier("db.props")
	private Properties messageSource;

	@Override
	public List<MyJsonData> getJobTitles(final String email, final long phone) {

		String message = messageSource.getProperty("udfun_get_employer_job_titles");
		String sql = MessageFormat.format(message, "'" + email + "'", String.valueOf(phone));

		String res = jdbcTemplate.queryForObject(sql, String.class);
		return res == null ? new ArrayList<>() : ActivityUtilities.convertJsonIntoObject(res, new ArrayList<>());
	}

	@Override
	public int updateJobDetails(final JobDetails jobDetails, final AuthorizedUser user) {

		int res = 0;
		try {

			if (jobDetails.getJob_visible_date() == null || jobDetails.getJob_visible_date().trim() == "") {
				Date date = new Date();
				String visibleDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
				jobDetails.setJob_visible_date(visibleDate);
			}
			jobDetails.setEmployer_id(user.getUserId());

			if (jobDetails.getEmployer_job_detail_id() == 0) {
				jobDetails.setUnique_job_code(getUniqueJobId());
			}

			String sql = "CALL public.usp_proc_employer_job_post(?::jsonb,?)";
			String json = ActivityUtilities.convertObjectIntoJson(jobDetails);
			String dbupdate = jdbcTemplate.queryForObject(sql, String.class, json, "");
			res = "1".equals(dbupdate) ? 1 : 0;
			System.out.println("post job status from DB function : " + dbupdate);
		} catch (Exception e) {
			res = 0;
			e.printStackTrace();
		}
		return res;

	}

	private String getUniqueJobId() {
		String uniqueCode = ActivityUtilities.generateUniqueJobId();

		String message = messageSource.getProperty("check_job_unique_code_available");
		String sql = MessageFormat.format(message, "'" + uniqueCode + "'");
		String dbCode = userRepo.getUniqueResult(sql);
		return "0".equals(dbCode) ? uniqueCode : getUniqueJobId();

	}

	@Override
	public List<Applicants> getApplicants(final AuthorizedUser user) {

		String message = messageSource.getProperty("get_employer_applicants");
		String jobsQuery = MessageFormat.format(message, String.valueOf(user.getUserId()));

		String dbResponse = jdbcTemplate.queryForObject(jobsQuery, String.class);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		List<Applicants> applicants = new ArrayList<>();
		if (dbResponse != null) {
			try {

				applicants = objectMapper.readValue(dbResponse, new TypeReference<List<Applicants>>() {
				});

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return applicants;
	}

	@Override
	public int deleteApplicant(final int appliedJobId) {
		String sql = messageSource.getProperty("update_job_status_by_employer");
		return jdbcTemplate.update(sql, "Rejected", appliedJobId);
	}

	@Override
	public int shortListApplicant(final int appliedJobId) {
		int res = 0;
		try {
			String sql = messageSource.getProperty("update_job_status_by_employer");
			res = jdbcTemplate.update(sql, "Short-Listed", appliedJobId);
			if (res > 0) {

				String message = messageSource.getProperty("sms_required_info");
				String query = MessageFormat.format(message, String.valueOf(appliedJobId));

				Object[] shortListedInfo = userRepo.getShortListedInfo(query);
				String companyName = (String) shortListedInfo[1];
				long studentMobile = Long.parseLong((String) shortListedInfo[0]);

				smsUtility.sendSMStoShortlistedStudent(companyName, studentMobile);
			}
		} catch (Exception e) {
			res = 0;
			e.printStackTrace();
		}
		return res;

	}

	@Override
	public Map<String, List<Applicants>> mappedApplicants(final AuthorizedUser user) {

		List<JobTitles> preferredJobTitles = getPreferredJobTitles(user);
		List<Applicants> preferredApplicants = getApplicants(user);
		Map<String, List<Applicants>> res = new HashMap<>();

		preferredJobTitles.stream().forEach(title -> {
			res.put(title.getJobtype_name(),
					preferredApplicants.stream().filter(applicant -> applicant.getJobtype_id() == title.getJobtype_id())
							.collect(Collectors.toList()));
		});

		return res;

	}

	@Override
	public List<JobTitles> getPreferredJobTitles(final AuthorizedUser user) {

		UserIdAndFlag in = new UserIdAndFlag(user.getUserId(), "employer");
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
	public List<Applicants> getStudentsByJobType(final int jobTypeId, final long userId) {

		IdAndJobid in = new IdAndJobid(userId, jobTypeId);
		String jobsQuery = "select * from public.udfun_get_employer_dashboard_jobmatch_st_dtls(?::jsonb)";
		String json = ActivityUtilities.convertObjectIntoJson(in);
		String dbResponse = jdbcTemplate.queryForObject(jobsQuery, String.class, json);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		List<Applicants> students = new ArrayList<>();
		if (dbResponse != null) {
			try {
				students = objectMapper.readValue(dbResponse, new TypeReference<List<Applicants>>() {
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return students;

	}

	@Override
	public List<PostedJobs> getPostedJobs(final AuthorizedUser user) {

		EmployerPostedJobs inDto = new EmployerPostedJobs(null, user.getUserId(), 1, 10);

		String jobsQuery = "select * from public.udfun_get_employer_posted_jobdetails_search(?::jsonb)";
		String json = ActivityUtilities.convertObjectIntoJson(inDto);
		String dbResponse = jdbcTemplate.queryForObject(jobsQuery, String.class, json);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		List<PostedJobs> jobTitlesDtoList = new ArrayList<>();
		if (dbResponse != null) {
			try {
				jobTitlesDtoList = objectMapper.readValue(dbResponse, new TypeReference<List<PostedJobs>>() {
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return jobTitlesDtoList;

	}

	@Override
	public JobDetails getJobDetailsById(final int jobId) {

		String message = messageSource.getProperty("get_employer_job_details");
		String jobsQuery = MessageFormat.format(message, String.valueOf(jobId));

		String dbResponse = jdbcTemplate.queryForObject(jobsQuery, String.class);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		JobDetails job = new JobDetails();
		if (dbResponse != null) {
			try {
				List<JobDetails> jobs = objectMapper.readValue(dbResponse, new TypeReference<List<JobDetails>>() {
				});
				job = jobs.get(0);
				job.setJobStartTimePart1(job.getJob_start_time().substring(0, 5));
				job.setJobStartTimePart2(job.getJob_start_time().substring(5));
				job.setJobEndTimePart1(job.getJob_end_time().substring(0, 5));
				job.setJobEndTimePart2(job.getJob_end_time().substring(5));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return job;

	}

	@Override
	public int modifyFilledPositions(final JobFilledData data, final AuthorizedUser user) {
		String sql = messageSource.getProperty("update_fill_count");
		int update = jdbcTemplate.update(sql, data.getFilledPositions(), data.getJobId());
		System.out.println("Update Filled Positions DB status: " + update);
		return update;
	}

	@Override
	public boolean checkIsStudentSelected(final ShortlistStudent dto) {

		String message = messageSource.getProperty("is_student_selected");
		String sql = MessageFormat.format(message, dto.getJobtype_id(), dto.getStudent_id());
		String isStudentSelected = userRepo.getUniqueResult(sql);
		return isStudentSelected == null ? false : Integer.valueOf(isStudentSelected) > 0 ? true : false;

	}

	@Override
	public int updateStudentShortlist(final ShortlistStudent dto) {

		int res = 0;
		try {
			JobApply dtoIn = new JobApply(dto.getJobtype_id(), dto.getStudent_id(), true, true, dto.getJob_status());
			String sql = "CALL public.usp_proc_job_application_request(?::jsonb,?)";
			String json = ActivityUtilities.convertObjectIntoJson(dtoIn);
			String dbupdate = jdbcTemplate.queryForObject(sql, String.class, json, "");
			res = Integer.parseInt(dbupdate);
			if (res == 1) {

				String sMessage = messageSource.getProperty("get_student_phone");
				String sQuery = MessageFormat.format(sMessage, String.valueOf(dto.getStudent_id()));
				String studentMobile = userRepo.getUniqueResult(sQuery);

				String eMessage = messageSource.getProperty("get_company_name");
				String eQuery = MessageFormat.format(eMessage, "'company'", String.valueOf(dto.getEmployer_id()));
				String companyName = userRepo.getUniqueResult(eQuery);

				smsUtility.sendSMStoShortlistedStudent(companyName, Long.parseLong(studentMobile));
			}
			System.out.println("job apply by student, status from DB : " + dbupdate);
		} catch (Exception e) {
			res = 0;
			e.printStackTrace();
		}
		return res;

	}

	@Override
	public int updateIsViewed(final int jobId) {
		String sql = messageSource.getProperty("update_is_viewed");
		return jdbcTemplate.update(sql, true, jobId);
	}

	@Override
	public List<JobDetails> getJobUniqueCodes(final int jobTypeId, final Long userId) {

		String jobsQuery = "select * from public.udfun_get_employer_unique_job_codes(?::jsonb)";
		IdAndJobid inDto = new IdAndJobid(userId, jobTypeId);
		String json = ActivityUtilities.convertObjectIntoJson(inDto);
		String dbResponse = jdbcTemplate.queryForObject(jobsQuery, String.class, json);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		if (dbResponse != null) {
			try {
				return objectMapper.readValue(dbResponse, new TypeReference<List<JobDetails>>() {
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;

	}

}
