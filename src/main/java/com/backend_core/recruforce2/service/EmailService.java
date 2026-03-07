package com.backend_core.recruforce2.service;

import com.backend_core.recruforce2.domain.entities.Interview;
import com.backend_core.recruforce2.domain.entities.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.format.DateTimeFormatter;

/**
 * Service for sending email notifications to users.
 * All email operations are asynchronous to avoid blocking the main thread.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

  private final JavaMailSender mailSender;
  private final SpringTemplateEngine templateEngine;

  @Value("${spring.mail.username}")
  private String fromEmail;

  /**
   * Sends a simple text email.
   *
   * @param to      recipient email
   * @param subject email subject
   * @param text    email body
   */
  @Async
  public void sendSimpleEmail(String to, String subject, String text) {
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(fromEmail);
      message.setTo(to);
      message.setSubject(subject);
      message.setText(text);

      mailSender.send(message);
      log.info("Simple email sent to: {}", to);
    } catch (Exception e) {
      log.error("Failed to send simple email to {}: {}", to, e.getMessage());
    }
  }

  /**
   * Sends an HTML email using a Thymeleaf template.
   *
   * @param to           recipient email
   * @param subject      email subject
   * @param templateName Thymeleaf template name (without .html extension)
   * @param context      template variables
   */
  @Async
  public void sendHtmlEmail(String to, String subject, String templateName, Context context) {
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

      helper.setFrom(fromEmail);
      helper.setTo(to);
      helper.setSubject(subject);

      String htmlContent = templateEngine.process(templateName, context);
      helper.setText(htmlContent, true);

      mailSender.send(mimeMessage);
      log.info("HTML email sent to: {}", to);
    } catch (MessagingException e) {
      log.error("Failed to send HTML email to {}: {}", to, e.getMessage());
    }
  }

  /**
   * Sends an application acknowledgment email to a candidate.
   *
   * @param candidateEmail candidate's email
   * @param candidateName  candidate's full name
   * @param jobOfferTitle  title of the job offer applied to
   */
  @Async
  public void sendApplicationAcknowledgment(String candidateEmail, String candidateName, String jobOfferTitle) {
    String subject = "Application Received - " + jobOfferTitle;
    String text = String.format(
      "Dear %s,\n\n" +
        "Thank you for applying to the position of %s at RecruForce2.\n\n" +
        "We have received your application and will review it shortly. " +
        "You will be notified of the next steps in the recruitment process.\n\n" +
        "Best regards,\n" +
        "RecruForce2 Team",
      candidateName, jobOfferTitle
    );

    sendSimpleEmail(candidateEmail, subject, text);
  }

  /**
   * Sends an interview invitation email to a candidate.
   *
   * @param interview the scheduled interview
   */
  @Async
  public void sendInterviewInvitation(Interview interview) {
    String candidateEmail = interview.getApplication().getCandidate().getEmail();
    String candidateName = interview.getApplication().getCandidate().getFullName();
    String interviewType = interview.getType().name();
    String dateTime = interview.getDateTime().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' HH:mm"));

    String subject = "Interview Scheduled - " + interview.getApplication().getJobOffer().getTitle();
    String text = String.format(
      "Dear %s,\n\n" +
        "We are pleased to invite you to a %s interview for the position of %s.\n\n" +
        "Date and Time: %s\n" +
        "Duration: %d minutes\n" +
        "Location: %s\n" +
        "%s\n\n" +
        "Please confirm your attendance by clicking the link below:\n" +
        "%s\n\n" +
        "We look forward to meeting you!\n\n" +
        "Best regards,\n" +
        "RecruForce2 Team",
      candidateName,
      interviewType,
      interview.getApplication().getJobOffer().getTitle(),
      dateTime,
      interview.getDurationMinutes(),
      interview.getLocation() != null ? interview.getLocation() : "Remote",
      interview.getVideoLink() != null ? "Video Link: " + interview.getVideoLink() : "",
      "https://recruforce2.com/confirm/" + interview.getInvitationToken()
    );

    sendSimpleEmail(candidateEmail, subject, text);
  }

  /**
   * Sends an interview reminder email.
   *
   * @param interview the upcoming interview
   */
  @Async
  public void sendInterviewReminder(Interview interview) {
    String candidateEmail = interview.getApplication().getCandidate().getEmail();
    String candidateName = interview.getApplication().getCandidate().getFullName();
    String dateTime = interview.getDateTime().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' HH:mm"));

    String subject = "Interview Reminder - Tomorrow";
    String text = String.format(
      "Dear %s,\n\n" +
        "This is a friendly reminder about your upcoming interview for %s.\n\n" +
        "Date and Time: %s\n" +
        "Location: %s\n" +
        "%s\n\n" +
        "Please ensure you are ready and on time.\n\n" +
        "Best regards,\n" +
        "RecruForce2 Team",
      candidateName,
      interview.getApplication().getJobOffer().getTitle(),
      dateTime,
      interview.getLocation() != null ? interview.getLocation() : "Remote",
      interview.getVideoLink() != null ? "Video Link: " + interview.getVideoLink() : ""
    );

    sendSimpleEmail(candidateEmail, subject, text);
  }

  /**
   * Sends a notification email to a recruiter.
   *
   * @param user    the recruiter
   * @param title   notification title
   * @param message notification message
   */
  @Async
  public void sendNotificationEmail(User user, String title, String message) {
    String subject = "RecruForce2 Notification - " + title;
    String text = String.format(
      "Hello %s,\n\n%s\n\nBest regards,\nRecruForce2 Team",
      user.getFullName(), message
    );

    sendSimpleEmail(user.getEmail(), subject, text);
  }
}
