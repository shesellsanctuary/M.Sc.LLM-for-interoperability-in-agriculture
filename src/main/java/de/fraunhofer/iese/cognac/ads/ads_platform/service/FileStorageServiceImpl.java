package de.fraunhofer.iese.cognac.ads.ads_platform.service;

import de.fraunhofer.iese.cognac.ads.ads_platform.configuration.AdsPlatformConfigurationProperties;
import de.fraunhofer.iese.cognac.ads.ads_platform.exception.DoesNotExistException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
public class FileStorageServiceImpl implements FileStorageService {
  private static final Logger logger = LoggerFactory.getLogger(FileStorageServiceImpl.class);
  private static final String TWO_DOTS = "..";
  private static final String PATH_SEPARATOR = "/";

  private final Path fileStorageLocation;

  @Autowired
  public FileStorageServiceImpl(final AdsPlatformConfigurationProperties configurationProperties) throws FileStorageException {
    this.fileStorageLocation = Paths.get(configurationProperties.getFilesDir()).toAbsolutePath().normalize();
    try {
      Files.createDirectories(this.fileStorageLocation);
    } catch (Exception ex) {
      throw new FileStorageException("Could not create the directory where the files will be stored.", ex);
    }
  }

  private void validatePath(final String path) throws FileStorageException {
    if (org.apache.commons.lang3.StringUtils.isBlank(path)) {
      throw new FileStorageException("blank path");
    }
    if (!path.equals(StringUtils.cleanPath(path))) {
      throw new FileStorageException("unclean path " + path);
    }
    if (path.contains(TWO_DOTS)) {
      throw new FileStorageException("Path contains invalid path sequence " + path);
    }
    if (path.startsWith(PATH_SEPARATOR)) {
      throw new FileStorageException("non-relative path " + path);
    }
  }

  @Override
  public void storeFile(final String path, final Resource resource) throws FileStorageException {
    validatePath(path);
    final Path targetLocation = this.fileStorageLocation.resolve(path);
    logger.debug("Going to store file at: {}", targetLocation);
    try {
      Files.createDirectories(targetLocation.getParent());
      Files.copy(resource.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
      logger.debug("Stored file at: {}", targetLocation);
    } catch (IOException ex) {
      throw new FileStorageException("Could not store file " + path, ex);
    }
  }

  @Override
  public Optional<Resource> loadFile(final String path) throws FileStorageException {
    validatePath(path);
    final Path filePath = this.fileStorageLocation.resolve(path);
    logger.debug("Going to read file from: {}", filePath);
    final Resource resource = new PathResource(filePath);
    if (resource.exists()) {
      return Optional.of(resource);
    } else {
      return Optional.empty();
    }
  }

  @Override
  public void deleteFile(final String path) throws DoesNotExistException, FileStorageException {
    validatePath(path);
    final Path filePath = this.fileStorageLocation.resolve(path);
    logger.debug("Going to delete file: {}", filePath);
    if (Files.exists(filePath)) {
      try {
        Files.delete(filePath);
      } catch (IOException e) {
        throw new FileStorageException("Could not delete file " + path, e);
      }
    } else {
      throw new DoesNotExistException();
    }
  }

  @Override
  public void deleteDirectory(final String path) throws FileStorageException, DoesNotExistException {
    validatePath(path);
    final Path directoryPath = this.fileStorageLocation.resolve(path);
    logger.debug("Going to delete directory: {}", directoryPath);
    if (Files.exists(directoryPath) && Files.isDirectory(directoryPath)) {
      final boolean deleted;
      try {
        deleted = FileSystemUtils.deleteRecursively(directoryPath);
      } catch (IOException e) {
        throw new FileStorageException("Could not delete directory " + path);
      }
      if (!deleted) {
        throw new FileStorageException("Could not delete directory " + path);
      }
    } else {
      throw new DoesNotExistException();
    }
  }

}
