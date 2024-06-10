package com.quantum.ampmjobs.service;

import java.util.List;
import java.util.Map;

import com.quantum.ampmjobs.dto.JobDetails;
import com.quantum.ampmjobs.dto.JobTitles;
import com.quantum.ampmjobs.entities.AuthorizedUser;

public interface StudentService {

	int getCountryId(String username);

	List<JobDetails> getPreferredJobs(AuthorizedUser user);

	List<JobTitles> getPreferredJobTitles(AuthorizedUser user);

	Map<String, List<JobDetails>> getMappedJobs(AuthorizedUser user);

	int applyJob(int jobId, AuthorizedUser user);

	List<JobDetails> getAppliedJobs(AuthorizedUser user);

	List<JobDetails> getAllJobsBySearch(AuthorizedUser user);

}
