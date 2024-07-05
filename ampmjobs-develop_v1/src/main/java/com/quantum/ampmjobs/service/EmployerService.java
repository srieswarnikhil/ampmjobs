package com.quantum.ampmjobs.service;

import java.util.List;
import java.util.Map;

import com.quantum.ampmjobs.dto.Applicants;
import com.quantum.ampmjobs.dto.JobDetails;
import com.quantum.ampmjobs.dto.JobFilledData;
import com.quantum.ampmjobs.dto.JobTitles;
import com.quantum.ampmjobs.dto.MyJsonData;
import com.quantum.ampmjobs.dto.PostedJobs;
import com.quantum.ampmjobs.dto.ShortlistStudent;
import com.quantum.ampmjobs.entities.AuthorizedUser;

public interface EmployerService {

	List<MyJsonData> getJobTitles(String email, long phone);

	int updateJobDetails(JobDetails jobDetails, AuthorizedUser user);

	List<Applicants> getApplicants(AuthorizedUser user);

	int deleteApplicant(int appliedJobId);

	int shortListApplicant(int appliedJobId);

	Map<String, List<Applicants>> mappedApplicants(AuthorizedUser user);

	List<PostedJobs> getPostedJobs(AuthorizedUser user);

	JobDetails getJobDetailsById(int jobId);

	int modifyFilledPositions(JobFilledData data, AuthorizedUser user);

	List<JobTitles> getPreferredJobTitles(final AuthorizedUser user);

	List<Applicants> getStudentsByJobType(int jobTypeId, long userId);

	int updateStudentShortlist(ShortlistStudent dto);

	int updateIsViewed(int jobId);

	List<JobDetails> getJobUniqueCodes(int jobTypeId, Long userId);

	boolean checkIsStudentSelected(ShortlistStudent dto);
}
