package com.besaba.revonline.snippetide.shareservices;

public class JsonParserQuote {
  // from jdk.nashorn.internal.parser package
  public static String quote(String value) {
    StringBuilder product = new StringBuilder();
    product.append("\"");
    char[] var2 = value.toCharArray();
    int var3 = var2.length;

    for (char ch : var2) {
      switch (ch) {
        case '\b':
          product.append("\\b");
          break;
        case '\t':
          product.append("\\t");
          break;
        case '\n':
          product.append("\\n");
          break;
        case '\f':
          product.append("\\f");
          break;
        case '\r':
          product.append("\\r");
          break;
        case '\"':
          product.append("\\\"");
          break;
        case '\\':
          product.append("\\\\");
          break;
        default:
          if (ch < 32) {
            product.append(unicodeEscape(ch));
          } else {
            product.append(ch);
          }
      }
    }

    product.append("\"");
    return product.toString();
  }

  static String unicodeEscape(char ch) {
    StringBuilder sb = new StringBuilder();
    sb.append("\\u");
    String hex = Integer.toHexString(ch);

    for(int i = hex.length(); i < 4; ++i) {
      sb.append('0');
    }

    sb.append(hex);
    return sb.toString();
  }
}
