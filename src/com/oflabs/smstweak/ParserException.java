package com.oflabs.smstweak;



// Exception class for parser errors.
public class ParserException extends Exception {
  String errStr; // describes the error

  public ParserException(String str) {
    errStr = str;
  }

  public String toString() {
    return errStr;
  }
}