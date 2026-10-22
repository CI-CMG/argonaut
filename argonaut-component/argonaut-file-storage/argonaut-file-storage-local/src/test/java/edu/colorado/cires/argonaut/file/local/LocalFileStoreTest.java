package edu.colorado.cires.argonaut.file.local;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LocalFileStoreTest {

  private final Path rootPath = Paths.get("target/files");
  private final Path otherPath = Paths.get("target/other");

  @BeforeEach
  public void setup() throws Exception {
    FileUtils.deleteQuietly(rootPath.toFile());
    Files.createDirectories(rootPath);
    FileUtils.deleteQuietly(otherPath.toFile());
    Files.createDirectories(otherPath);
  }

  @Test
  public void testDelete() throws Exception{
    LocalFileStore fileStore = new LocalFileStore();
    fileStore.setRootPath(rootPath);
    Path test1 = rootPath.resolve("foo/bar/cat/dog/woof.txt");
    Path test12 = rootPath.resolve("foo/bar/cat/dog/bark.txt");
    Path test2 = rootPath.resolve("foo/bar/cat/meow.txt");
    Path test3 = rootPath.resolve("a/b/c/d.txt");
    Path nogo = otherPath.resolve("1/2/3/4.txt");
    Files.createDirectories(test1.getParent());
    Files.createDirectories(test3.getParent());
    Files.createDirectories(nogo.getParent());
    Files.createFile(test1);
    Files.createFile(test12);
    Files.createFile(test2);
    Files.createFile(test3);
    Files.createFile(nogo);

    fileStore.delete(fileStore.appendToPath(fileStore.getRoot(), "foo", "bar", "cat", "dog", "woof.txt"));
    assertFalse(Files.exists(test1));
    assertTrue(Files.exists(test2));
    assertTrue(Files.exists(test3));
    assertTrue(Files.exists(nogo));
    assertTrue(Files.exists(rootPath.resolve("foo/bar/cat")));
    assertTrue(Files.exists(rootPath.resolve("foo/bar/cat/dog")));
    assertTrue(Files.exists(test12));
    assertTrue(Files.exists(rootPath.resolve("a")));

    fileStore.delete(fileStore.appendToPath(fileStore.getRoot(), "foo", "bar", "cat", "dog", "bark.txt"));
    assertFalse(Files.exists(test12));
    assertTrue(Files.exists(test2));
    assertTrue(Files.exists(test3));
    assertTrue(Files.exists(nogo));
    assertTrue(Files.exists(rootPath.resolve("foo/bar/cat")));
    assertFalse(Files.exists(rootPath.resolve("foo/bar/cat/dog")));
    assertTrue(Files.exists(rootPath.resolve("a")));

    fileStore.delete(fileStore.appendToPath(fileStore.getRoot(), "foo", "bar", "cat", "meow.txt"));
    assertFalse(Files.exists(test1));
    assertFalse(Files.exists(test2));
    assertTrue(Files.exists(test3));
    assertTrue(Files.exists(nogo));
    assertFalse(Files.exists(rootPath.resolve("foo")));
    assertTrue(Files.exists(rootPath.resolve("a")));
    assertTrue(Files.exists(rootPath));

    fileStore.delete(fileStore.appendToPath(fileStore.getRoot(), "a", "b", "c", "d.txt"));
    assertFalse(Files.exists(test1));
    assertFalse(Files.exists(test2));
    assertFalse(Files.exists(test3));
    assertTrue(Files.exists(nogo));
    assertFalse(Files.exists(rootPath.resolve("a")));
    assertTrue(Files.exists(rootPath));


    assertThrows(RuntimeException.class, () -> {
      fileStore.delete(fileStore.appendToPath(fileStore.getRoot(), "1", "2", "3", "4.txt"));
    });



  }


  @Test
  public void testMove() throws Exception{
    LocalFileStore fileStore = new LocalFileStore();
    fileStore.setRootPath(rootPath);
    Path test1 = rootPath.resolve("foo/bar/cat/dog/woof.txt");
    Path test12 = rootPath.resolve("foo/bar/cat/dog/bark.txt");
    Path test2 = rootPath.resolve("foo/bar/cat/meow.txt");
    Path test3 = rootPath.resolve("a/b/c/d.txt");
    Path nogo = otherPath.resolve("1/2/3/4.txt");
    Files.createDirectories(test1.getParent());
    Files.createDirectories(test3.getParent());
    Files.createDirectories(nogo.getParent());
    Files.createFile(test1);
    Files.createFile(test12);
    Files.createFile(test2);
    Files.createFile(test3);
    Files.createFile(nogo);

    Path moved = rootPath.resolve("moved");
    Path movedTest1 = moved.resolve("foo/bar/cat/dog/woof.txt");
    Path moveTest12 = moved.resolve("foo/bar/cat/dog/bark.txt");
    Path movedTest2 = moved.resolve("foo/bar/cat/meow.txt");
    Path movedTest3 = moved.resolve("a/b/c/d.txt");

    fileStore.move(
        fileStore.appendToPath(fileStore.getRoot(), "foo", "bar", "cat", "dog", "woof.txt"),
        fileStore.appendToPath(fileStore.getRoot(), "moved", "foo", "bar", "cat", "dog", "woof.txt")
    );
    assertFalse(Files.exists(test1));
    assertTrue(Files.exists(test2));
    assertTrue(Files.exists(test3));
    assertTrue(Files.exists(nogo));
    assertTrue(Files.exists(rootPath.resolve("foo/bar/cat")));
    assertTrue(Files.exists(rootPath.resolve("foo/bar/cat/dog")));
    assertTrue(Files.exists(test12));
    assertTrue(Files.exists(rootPath.resolve("a")));
    assertTrue(Files.exists(movedTest1));
    assertFalse(Files.exists(moveTest12));
    assertFalse(Files.exists(movedTest2));
    assertFalse(Files.exists(movedTest3));

    fileStore.move(
        fileStore.appendToPath(fileStore.getRoot(), "foo", "bar", "cat", "dog", "bark.txt"),
        fileStore.appendToPath(fileStore.getRoot(), "moved", "foo", "bar", "cat", "dog", "bark.txt")
    );
    assertFalse(Files.exists(test12));
    assertTrue(Files.exists(test2));
    assertTrue(Files.exists(test3));
    assertTrue(Files.exists(nogo));
    assertTrue(Files.exists(rootPath.resolve("foo/bar/cat")));
    assertFalse(Files.exists(rootPath.resolve("foo/bar/cat/dog")));
    assertTrue(Files.exists(rootPath.resolve("a")));
    assertTrue(Files.exists(movedTest1));
    assertTrue(Files.exists(moveTest12));
    assertFalse(Files.exists(movedTest2));
    assertFalse(Files.exists(movedTest3));

    fileStore.move(
        fileStore.appendToPath(fileStore.getRoot(), "foo", "bar", "cat", "meow.txt"),
        fileStore.appendToPath(fileStore.getRoot(), "moved", "foo", "bar", "cat", "meow.txt")
    );
    assertFalse(Files.exists(test1));
    assertFalse(Files.exists(test2));
    assertTrue(Files.exists(test3));
    assertTrue(Files.exists(nogo));
    assertFalse(Files.exists(rootPath.resolve("foo")));
    assertTrue(Files.exists(rootPath.resolve("a")));
    assertTrue(Files.exists(rootPath));
    assertTrue(Files.exists(movedTest1));
    assertTrue(Files.exists(moveTest12));
    assertTrue(Files.exists(movedTest2));
    assertFalse(Files.exists(movedTest3));

    fileStore.move(
        fileStore.appendToPath(fileStore.getRoot(), "a", "b", "c", "d.txt"),
        fileStore.appendToPath(fileStore.getRoot(), "moved", "a", "b", "c", "d.txt")
    );
    assertFalse(Files.exists(test1));
    assertFalse(Files.exists(test2));
    assertFalse(Files.exists(test3));
    assertTrue(Files.exists(nogo));
    assertFalse(Files.exists(rootPath.resolve("a")));
    assertTrue(Files.exists(rootPath));
    assertTrue(Files.exists(movedTest1));
    assertTrue(Files.exists(moveTest12));
    assertTrue(Files.exists(movedTest2));
    assertTrue(Files.exists(movedTest3));


    assertThrows(IllegalStateException.class, () -> {
      fileStore.move(
          fileStore.appendToPath(fileStore.getRoot(), "1", "2", "3", "4.txt"),
          fileStore.appendToPath(fileStore.getRoot(), "moved", "1", "2", "3", "4.txt")
      );
    });

    assertFalse(Files.exists(moved.resolve("1")));



  }
}