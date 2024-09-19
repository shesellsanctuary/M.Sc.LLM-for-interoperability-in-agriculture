package de.fraunhofer.iese.cognac.ads.ads_platform.service;

import de.fraunhofer.iese.cognac.ads.ads_platform.exception.DoesNotExistException;

import org.springframework.core.io.Resource;

import java.util.Optional;

public interface FileStorageService {

  void storeFile(String path, Resource resource) throws FileStorageException;

  Optional<Resource> loadFile(String path) throws FileStorageException;

  void deleteFile(String path) throws DoesNotExistException, FileStorageException;

  void deleteDirectory(String path) throws FileStorageException, DoesNotExistException;
}
