package de.fraunhofer.iese.cognac.ads.ads_platform.service;

import de.fraunhofer.iese.cognac.ads.ads_platform.configuration.AdsPlatformConfigurationProperties;
import de.fraunhofer.iese.cognac.ads.ads_platform.exception.DoesNotExistException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Scanner;

class FileStorageServiceImplTest {

  public static final String TEST_FILES_DIR = "testfiles";
  private static final Path fileStorageLocation = Paths.get(TEST_FILES_DIR).toAbsolutePath().normalize();
  private FileStorageService testSubject;

  @BeforeEach
  void setUp() throws FileStorageException, IOException {
    Files.createDirectories(fileStorageLocation);

    final AdsPlatformConfigurationProperties propertiesMock = Mockito.mock(AdsPlatformConfigurationProperties.class);
    Mockito.when(propertiesMock.getFilesDir()).thenReturn(TEST_FILES_DIR);

    testSubject = new FileStorageServiceImpl(propertiesMock);
  }

  @AfterEach
  void tearDown() throws IOException {
    FileSystemUtils.deleteRecursively(fileStorageLocation);
  }

  @Test
  void whenStoreFileWithValidPathThenFileShouldBeStored() throws FileStorageException, IOException {
    final Path storedFilePath = fileStorageLocation.resolve("foo/test.txt");
    Assertions.assertFalse(storedFilePath.toFile().exists());
    testSubject.storeFile("foo/test.txt", new ClassPathResource("test.txt"));
    Assertions.assertTrue(storedFilePath.toFile().exists());
    try (final Scanner sc = new Scanner(storedFilePath)) {
      Assertions.assertEquals("Hello World", sc.nextLine());
    }
  }

  @Test
  void whenStoreFileWithBlankPathThenThrowsException() {
    Assertions.assertThrows(FileStorageException.class, () -> testSubject.storeFile("", new ClassPathResource("test.txt")));
  }

  @Test
  void whenStoreFileWithUncleanPathThenThrowsException() {
    Assertions.assertThrows(FileStorageException.class, () -> testSubject.storeFile("test123/./bar.txt", new ClassPathResource("test.txt")));
  }

  @Test
  void whenStoreFileWithTwoDotsInPathThenThrowsException() {
    Assertions.assertThrows(FileStorageException.class, () -> testSubject.storeFile("../bar.txt", new ClassPathResource("test.txt")));
  }

  @Test
  void whenStoreFileWithAbsolutPathThenThrowsException() {
    Assertions.assertThrows(FileStorageException.class, () -> testSubject.storeFile("/foo/bar.txt", new ClassPathResource("test.txt")));
  }

  @Test
  void whenFileNotPresentThenLoadFileReturnsEmptyOptional() throws FileStorageException {
    final Path filePath = fileStorageLocation.resolve("foo/bar.zip");
    Assertions.assertFalse(filePath.toFile().exists());
    Assertions.assertTrue(testSubject.loadFile("foo/bar.zip").isEmpty());
  }

  @Test
  void whenFilePresentThenLoadFileReturnsNonEmptyOptional() throws FileStorageException, IOException {
    final Path filePath = fileStorageLocation.resolve("foo/bar.zip");
    Assertions.assertFalse(filePath.toFile().exists());
    Files.createDirectory(filePath.getParent());
    Files.copy(Paths.get(new ClassPathResource("test.txt").getFile().getAbsolutePath()), filePath);
    Assertions.assertTrue(filePath.toFile().exists());
    final Optional<Resource> resourceOptional = testSubject.loadFile("foo/bar.zip");
    Assertions.assertTrue(resourceOptional.isPresent());
    try (final Scanner sc = new Scanner(resourceOptional.get().getInputStream())) {
      Assertions.assertEquals("Hello World", sc.nextLine());
    }
  }

  @Test
  void whenDeleteFileForNonExistingPathThenThrowsException() {
    final Path filePath = fileStorageLocation.resolve("foo/bar.zip");
    Assertions.assertFalse(filePath.toFile().exists());
    Assertions.assertThrows(DoesNotExistException.class, () -> testSubject.deleteFile("foo/bar.zip"));
  }

  @Test
  void whenDeleteFileForExistingPathThenNoException() throws IOException {
    final Path filePath = fileStorageLocation.resolve("foo/bar.zip");
    Assertions.assertFalse(filePath.toFile().exists());
    Files.createDirectory(filePath.getParent());
    Files.copy(Paths.get(new ClassPathResource("test.txt").getFile().getAbsolutePath()), filePath);
    Assertions.assertTrue(filePath.toFile().exists());
    Assertions.assertDoesNotThrow(() -> testSubject.deleteFile("foo/bar.zip"));
    Assertions.assertFalse(filePath.toFile().exists());
  }

  @Test
  void whenDeleteDirectoryWithNonExistingPathThenThrowsException() {
    final Path filePath = fileStorageLocation.resolve("foo/bar.zip");
    Assertions.assertFalse(filePath.toFile().exists());
    Assertions.assertFalse(filePath.getParent().toFile().exists());
    Assertions.assertThrows(DoesNotExistException.class, () -> testSubject.deleteDirectory("foo"));
  }

  @Test
  void whenDeleteDirectoryWithExistingPathThenNoException() throws IOException {
    final Path filePath = fileStorageLocation.resolve("foo/bar.zip");
    Assertions.assertFalse(filePath.toFile().exists());
    Files.createDirectory(filePath.getParent());
    Files.copy(Paths.get(new ClassPathResource("test.txt").getFile().getAbsolutePath()), filePath);
    Assertions.assertTrue(filePath.toFile().exists());
    Assertions.assertDoesNotThrow(() -> testSubject.deleteDirectory("foo"));
    Assertions.assertFalse(filePath.toFile().exists());
    Assertions.assertFalse(filePath.getParent().toFile().exists());
  }


}