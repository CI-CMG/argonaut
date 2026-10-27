package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.file.core.FileStore;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSubmissionProcessedCleanupProcessor implements SubmissionProcessedCleanupProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSubmissionProcessedCleanupProcessor.class);
  private static final Pattern DATEPATTERN = Pattern.compile("[0-9]{4}\\-[0-9]{2}\\-[0-9]{2}T[0-9]{2}\\:[0-9]{2}:[0-9]{2}.*Z");

  private final Map<String, ReentrantLock> perDacLock = new HashMap<>();
  private FileStore submissionStore;
  private boolean cleanRejects = false;
  private boolean enabled = true;
  private Supplier<Instant> nowSupplier = Instant::now;

  public void setNowSupplier(Supplier<Instant> nowSupplier) {
    this.nowSupplier = nowSupplier;
  }

  public void setSubmissionStore(FileStore submissionStore) {
    this.submissionStore = submissionStore;
  }

  public void setCleanRejects(boolean cleanRejects) {
    this.cleanRejects = cleanRejects;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  public void cleanup(String dac, int daysOlderThan) {
    if (enabled) {
      final ReentrantLock lock;
      synchronized (perDacLock) {
        ReentrantLock maybeLock = perDacLock.get(dac);
        if (maybeLock == null) {
          maybeLock = new ReentrantLock();
          perDacLock.put(dac, maybeLock);
        }
        lock = maybeLock;
      }
      if (lock.tryLock()){
        try {
          Instant now = nowSupplier.get();

          String processedPath = submissionStore.appendToPath(submissionStore.getRoot(), "dac", dac, "processed");
          List<String> submissionTimestamps = submissionStore.listFileNamesInDirectory(processedPath, true);
          for (String submissionTimestamp : submissionTimestamps) {
            Matcher matcher = DATEPATTERN.matcher(submissionTimestamp);
            if (matcher.matches()) {
              Instant submitTime;
              try {
                submitTime = Instant.parse(submissionTimestamp);
              } catch (Exception e) {
                LOGGER.warn("Processed submission directory unable to be parsed, skipping: {}", submissionTimestamp, e);
                continue;
              }
              if (daysOlderThan <= 0 || submitTime.isBefore(now.minus(daysOlderThan, ChronoUnit.DAYS))) {
                String timestampPath = submissionStore.appendToPath(processedPath, submissionTimestamp);
                if (cleanRejects) {
                  timestampPath = submissionStore.appendToPath(timestampPath, "reject");
                }
                List<String> processedFiles = submissionStore.listFileNamesInDirectory(timestampPath);
                for (String processedFile : processedFiles) {
                  String path = submissionStore.appendToPath(timestampPath, processedFile);
                  try {
                    submissionStore.delete(path);
                    LOGGER.info("Cleaned up {} submission file: {}", cleanRejects ? "rejected" : "processed", path);
                  } catch (Exception e) {
                    LOGGER.warn("Unable to delete processed submission file, skipping: {}", path, e);
                  }
                }
              }
            }

          }
        } finally {
          lock.unlock();
        }
      } else {
        LOGGER.info("Processed cleanup already in progress for {}, skipping.", dac);
      }
    }
  }

}
