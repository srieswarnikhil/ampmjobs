package com.quantum.ampmjobs.api.utility;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailUtility {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private TemplateEngine templateEngine;

	@Autowired
	private MailProperties mailProperties;

	@Async
	public void emailWithTemplate(final String to, final String subject, final String templateName,
			final Context context) {

		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
		try {
			helper.setFrom(mailProperties.getUsername());
			helper.setTo(to);
			helper.setSubject(subject);
			String htmlContent = templateEngine.process(templateName, context);
			helper.setText(htmlContent, true);
			mailSender.send(mimeMessage);
			System.out.println("sent email from mailCheck successfully");
		} catch (Exception e) {
			System.out.println("failed to sent email from mailCheck");
			e.printStackTrace();
		}
	}
}
