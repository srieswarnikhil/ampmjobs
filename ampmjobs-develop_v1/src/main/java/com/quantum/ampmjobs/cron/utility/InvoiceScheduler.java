package com.quantum.ampmjobs.cron.utility;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InvoiceScheduler {

	@Scheduled(cron = "0 0 8 * * *") // Executes at 8 AM every day
	public void myTask() {

	}
}