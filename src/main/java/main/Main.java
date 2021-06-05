package main;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.file.FileSystemDirectory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Main {

  public static void main(String[] args) throws ImageProcessingException, IOException {
    
    var path = Path.of(System.getProperty("user.dir"), "twhis.png");
    var metadata = ImageMetadataReader.readMetadata(path.toFile());
    var date_metadata_ext = metadata.getFirstDirectoryOfType(FileSystemDirectory.class)
            .getDate(FileSystemDirectory.TAG_FILE_MODIFIED_DATE).toInstant();
    var date_file_system = Files.getLastModifiedTime(path).toInstant();
    var date_file_system2 = Files.getFileAttributeView(path, BasicFileAttributeView.class)
            .readAttributes().creationTime().toInstant();
    System.out.println("Modified Date (metadata-extractor): "
            + LocalDateTime.ofInstant(date_metadata_ext, ZoneId.systemDefault()));
    System.out.println("Modified Time (win10 file system) : "
            + LocalDateTime.ofInstant(date_file_system, ZoneId.systemDefault()));
    System.out.println("Creation Time (win10 file system) : "
            + LocalDateTime.ofInstant(date_file_system2, ZoneId.systemDefault()));
  }
}
