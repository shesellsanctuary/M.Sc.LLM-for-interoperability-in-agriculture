package de.fraunhofer.iese.cognac.ads.ads_platform.service;

public class FileStorageException extends Exception {
  public FileStorageException(final String message) {
    super(message);
  }

  public FileStorageException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
