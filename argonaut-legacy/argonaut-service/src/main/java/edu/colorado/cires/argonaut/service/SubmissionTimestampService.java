package edu.colorado.cires.argonaut.service;

import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class SubmissionTimestampService {

  public String generateTimestamp() {
    return Instant.now().toString();
  }

}
