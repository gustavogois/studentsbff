package com.studentsbff.service;

import com.studentsbff.dto.EmailMessage;
import com.studentsbff.dto.GmailSyncResponse;
import com.studentsbff.dto.ParsedSchoolEvent;
import com.studentsbff.model.Student;
import com.studentsbff.model.Subject;
import com.studentsbff.model.User;
import com.studentsbff.repository.SchoolEventRepository;
import com.studentsbff.repository.SubjectRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GmailSyncService {

    private static final Logger log = LoggerFactory.getLogger(GmailSyncService.class);

    private final GmailService gmailService;
    private final EmailParsingService emailParsingService;
    private final SchoolEventService schoolEventService;
    private final SchoolEventRepository schoolEventRepository;
    private final SubjectRepository subjectRepository;

    public GmailSyncService(
            GmailService gmailService,
            EmailParsingService emailParsingService,
            SchoolEventService schoolEventService,
            SchoolEventRepository schoolEventRepository,
            SubjectRepository subjectRepository) {
        this.gmailService = gmailService;
        this.emailParsingService = emailParsingService;
        this.schoolEventService = schoolEventService;
        this.schoolEventRepository = schoolEventRepository;
        this.subjectRepository = subjectRepository;
    }

    @Transactional
    public GmailSyncResponse sync(User user, Student student, int daysBack) {
        log.info("Starting Gmail sync for user {} with daysBack={}", user.getEmail(), daysBack);

        List<EmailMessage> allEmails = gmailService.fetchRecentEmails(user, daysBack);
        int totalEmails = allEmails.size();

        List<EmailMessage> newEmails =
                allEmails.stream()
                        .filter(
                                email ->
                                        !schoolEventRepository.existsBySourceEmailId(
                                                email.messageId()))
                        .toList();

        int skippedDuplicates = totalEmails - newEmails.size();

        if (newEmails.isEmpty()) {
            log.info("No new emails to process (skipped {} duplicates)", skippedDuplicates);
            return new GmailSyncResponse(0, skippedDuplicates, totalEmails);
        }

        List<Subject> subjects = subjectRepository.findAllByStudentId(student.getId());
        List<String> subjectNames = subjects.stream().map(Subject::getName).toList();

        List<ParsedSchoolEvent> parsedEvents =
                emailParsingService.parseEmails(newEmails, subjectNames);

        schoolEventService.saveFromParsed(parsedEvents, student, subjects);

        int newEventsCount = parsedEvents.size();
        log.info(
                "Gmail sync complete: {} new events, {} skipped, {} total emails",
                newEventsCount,
                skippedDuplicates,
                totalEmails);

        return new GmailSyncResponse(newEventsCount, skippedDuplicates, totalEmails);
    }
}
