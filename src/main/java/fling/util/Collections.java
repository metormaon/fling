package fling.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import fling.sententials.Word;

public class Collections {
  @SafeVarargs public static <T> Set<T> set(T... items) {
    Set<T> $ = new LinkedHashSet<>();
    java.util.Collections.addAll($, items);
    return $;
  }
  public static <T> List<T> asList(Collection<T> collection) {
    return java.util.Collections.unmodifiableList(new ArrayList<>(collection));
  }
  public static <T> Word<T> asWord(Collection<T> collection) {
    return new Word<>(collection);
  }
  @SafeVarargs public static final <T> List<T> chainList(Collection<T>... collections) {
    List<T> list = new ArrayList<>();
    for (Collection<T> collection : collections)
      list.addAll(collection);
    return java.util.Collections.unmodifiableList(list);
  }
}