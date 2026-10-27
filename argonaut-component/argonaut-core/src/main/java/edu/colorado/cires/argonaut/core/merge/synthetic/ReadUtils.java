package edu.colorado.cires.argonaut.core.merge.synthetic;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.jspecify.annotations.Nullable;
import ucar.ma2.Array;
import ucar.ma2.IndexIterator;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

final class ReadUtils {

  private ReadUtils() {
    /* This utility class should not be instantiated */
  }

  /**
   * Get NetCDF variable size
   * @param file NetCDF file
   * @param variableName NetCDF variable name
   * @return NetCDF variable size, optional is empty if variable does not exist
   */
  public static Optional<Long> getVariableSize(NetcdfFile file, String variableName) {
    return getVariable(file, variableName)
      .map(Variable::getSize);
  }

  @SafeVarargs
  public static List<String> getVariables(NetcdfFile file, Predicate<String>... predicate) {
    return file.getVariables().stream()
      .map(Variable::getFullName)
      .filter(variable -> {
        for (Predicate<String> filter : predicate) {
          if (!filter.test(variable)) {
            return false;
          }
        }

        return true;
      })
      .toList();
  }

  /**
   * Stream values from NetCDF 1D array variable
   * @param file NetCDF file
   * @param variableName NetCDF variable name
   * @return List of object, optional is empty if variable does not exist. NetCDF values which
   * equal the _FillVariable attribute for the specified variable are inserted into the resulting list as 'null'
   */
  public static <T> Stream<@Nullable T> stream1DVariable(NetcdfFile file, String variableName,
    Class<T> clazz) {
    Optional<Variable> maybeVariable = getVariable(file, variableName);
    if (maybeVariable.isEmpty()) {
      return Stream.empty();
    }

    Variable variable = maybeVariable.get();
    Optional<Serializable> fillValue = Optional.ofNullable(variable.attributes().findAttribute("_FillValue"))
      .map(attribute -> variable.getDataType().getPrimitiveClassType() == String.class ? attribute.getStringValue() : attribute.getNumericValue());

    Array array;
    try {
      array = variable.read();
    } catch (IOException e) {
      throw new RuntimeException("failed to read array variable %s".formatted(variableName), e);
    }

    return IndexSpliterator.stream(array.getIndexIterator())
      .map(object -> fillValue
        .filter(fv -> fv.equals(object))
        .map(ignored -> null)
        .orElse(object))
      .map(clazz::cast);
  }

  /**
   * Get NetCDF variable
   * @param file NetCDF file
   * @param variableName NetCDF variable name
   * @return NetCDF variable, optional is empty if variable does not exist
   */
  private static Optional<Variable> getVariable(NetcdfFile file, String variableName) {
    return Optional.ofNullable(file.findVariable(variableName));
  }

  /**
   *
   * @param iterator
   */
  private record IndexSpliterator(IndexIterator iterator) implements Iterator<Object> {

    public static Stream<Object> stream(IndexIterator iterator) {
      return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(new IndexSpliterator(iterator), Spliterator.ORDERED),
        false
      );
    }

    @Override
    public boolean hasNext() {
      return iterator.hasNext();
    }

    @Override
    public Object next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      return iterator.next();
    }
  }

}
