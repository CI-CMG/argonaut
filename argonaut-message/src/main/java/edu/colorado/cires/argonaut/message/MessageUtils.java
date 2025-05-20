package edu.colorado.cires.argonaut.message;

import java.util.ArrayList;
import java.util.List;

final class MessageUtils {

  static <T> List<T> emptyOrCopy(List<T> source) {
    return source == null ? new ArrayList<>(0) : new ArrayList<>(source);
  }
}
