package de.fraunhofer.iese.cognac.ads.ads_platform.exception;

public class ConflictingResourceException extends Exception {

  public ConflictingResourceException(String message) {
    super(message);
  }

  public ConflictingResourceException() {
    super();
  }
}
