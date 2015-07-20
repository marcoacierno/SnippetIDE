package com.besaba.revonline.snippetide.configuration;

import java.util.Arrays;

class JsonConfigurationUtils {
  public static <T> String[] transformAnyArrayToStringArray(final T value) {
    if (!value.getClass().isArray()) {
      throw new IllegalArgumentException(value + " is not an array");
    }

    final Class<?> componentType = value.getClass().getComponentType();
    String[] result = null;

    if (componentType.isPrimitive()) {
      if (boolean.class.isAssignableFrom(componentType)) {
        final boolean[] arr = (boolean[]) value;
        result = new String[arr.length];

        for (int i = 0; i < arr.length; i++) {
          result[i] = String.valueOf(arr[i]);
        }
      } else if (byte.class.isAssignableFrom(componentType)) {
        final byte[] arr = (byte[]) value;
        result = new String[arr.length];

        for (int i = 0; i < arr.length; i++) {
          result[i] = String.valueOf(arr[i]);
        }
      } else if (short.class.isAssignableFrom(componentType)) {
        final short[] arr = (short[]) value;
        result = new String[arr.length];

        for (int i = 0; i < arr.length; i++) {
          result[i] = String.valueOf(arr[i]);
        }
      } else if (char.class.isAssignableFrom(componentType)) {
        final char[] arr = (char[]) value;
        result = new String[arr.length];

        for (int i = 0; i < arr.length; i++) {
          result[i] = String.valueOf(arr[i]);
        }
      } else if (int.class.isAssignableFrom(componentType)) {
        final int[] arr = (int[]) value;
        result = new String[arr.length];

        for (int i = 0; i < arr.length; i++) {
          result[i] = String.valueOf(arr[i]);
        }
      } else if (long.class.isAssignableFrom(componentType)) {
        final long[] arr = (long[]) value;
        result = new String[arr.length];

        for (int i = 0; i < arr.length; i++) {
          result[i] = String.valueOf(arr[i]);
        }
      } else if (float.class.isAssignableFrom(componentType)) {
        final float[] arr = (float[]) value;
        result = new String[arr.length];

        for (int i = 0; i < arr.length; i++) {
          result[i] = String.valueOf(arr[i]);
        }
      } else if (double.class.isAssignableFrom(componentType)) {
        final double[] arr = (double[]) value;
        result = new String[arr.length];

        for (int i = 0; i < arr.length; i++) {
          result[i] = String.valueOf(arr[i]);
        }
      }
    } else {
      result = Arrays.stream((Object[]) value)
          .map(Object::toString)
          .toArray(String[]::new);
    }

    return result;
  }
}
