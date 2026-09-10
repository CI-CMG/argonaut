package edu.colorado.cires.argonaut.core.merge.multiprof;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoMultiProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Reader;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ProfileCopyUtils;
import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MultiProfileIterator implements Iterator<ArgoProfileV31>, Closeable {

  private static final Logger LOGGER = LoggerFactory.getLogger(MultiProfileIterator.class);

  private final List<LocalPathSupplier> inputFileSuppliers;
  private LocalPathSupplier next = null;
  private LinkedList<ArgoProfileV31> nextProfiles = new LinkedList<>();
  private int index = 0;
  private int profileIndex = 0;
  private final boolean readOnlyFirstProfileInFile;

  MultiProfileIterator(List<LocalPathSupplier> inputFileSuppliers, boolean readOnlyFirstProfileInFile) {
    this.inputFileSuppliers = inputFileSuppliers;
    this.readOnlyFirstProfileInFile = readOnlyFirstProfileInFile;
  }


  @Override
  public boolean hasNext() {
    return !nextProfiles.isEmpty() || index < inputFileSuppliers.size();
  }

  @Override
  public ArgoProfileV31 next() {
    if (nextProfiles.isEmpty()) {
      if (next != null) {
        try {
          next.cleanUp();
        } catch (Exception e) {
          LOGGER.warn("An error occurred when cleaning up profile merge source  " + next.getFileName(), e);
        }
      }
      next = inputFileSuppliers.get(index++);
      next.prepare();
      try (
          ArgoProfileV31Reader reader = new ArgoProfileV31Reader(next.getLocalPath());
      ) {
        ArgoMultiProfileV31 multiProfile = reader.getMultiProfile();
        List<ArgoProfileV31> profiles = multiProfile.getProfiles();
        if (readOnlyFirstProfileInFile) {
          profiles = profiles.subList(0, 1);
        }
        nextProfiles = new LinkedList<>();
        for (ArgoProfileV31 profile : profiles) {
          nextProfiles.add(ProfileCopyUtils.copyProfileToMemory(profile, profileIndex++));
        }
      } catch (IOException e) {
        throw new RuntimeException("Unable to parse profile: " + next.getFileName(), e);
      }
    }
    return nextProfiles.removeFirst();
  }

  @Override
  public void close() throws IOException {
    if (next != null) {
      try {
        next.cleanUp();
      } catch (Exception e) {
        LOGGER.warn("An error occurred when cleaning up profile merge source  " + next.getFileName(), e);
      }
    }
  }
}
