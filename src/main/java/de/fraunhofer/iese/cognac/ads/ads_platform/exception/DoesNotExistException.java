package de.fraunhofer.iese.cognac.ads.ads_platform.exception;

public class DoesNotExistException extends Exception {

  public DoesNotExistException(String message) {
    super(message);
  }

  public DoesNotExistException() {
    super();
  }
}
