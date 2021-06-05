package main;

import ar.com.hjg.pngj.ImageLineInt;
import ar.com.hjg.pngj.PngReader;
import ar.com.hjg.pngj.PngWriter;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.png.PngMetadataReader;
import com.drew.metadata.Tag;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;

public class Main {

  public static void main(String[] args) throws ImageProcessingException, IOException {

    var path = Path.of(System.getProperty("user.dir"), "Maltese.png");
    showMetadata(path);
    var dest = path.resolveSibling("new_" + path.getFileName());
    mirrorPng(path, dest);
    showMetadata(dest);
  }

  static void mirrorPng(Path orig, Path dest) throws IOException, ImageProcessingException {

    System.out.println("-".repeat(80));
    System.out.println("mirrorPng");
    System.out.println("-".repeat(80));
    PngReader pngr = new PngReader(orig.toFile());
    PngWriter pngw = new PngWriter(dest.toFile(), pngr.imgInfo, true);
    pngw.copyChunksFrom(pngr.getChunksList());
    var lmt = Files.getLastModifiedTime(orig).toInstant();
    var ldt = LocalDateTime.ofInstant(lmt, ZoneId.systemDefault());
    pngw.getMetadata().setTimeYMDHMS(
            ldt.getYear(), ldt.getMonthValue(), ldt.getDayOfMonth(),
            ldt.getHour(), ldt.getMinute(), ldt.getSecond());
    System.out.println(pngw.getMetadata().getTimeAsString());
    for (int row = 0; row < pngr.imgInfo.rows; row++) {
      ImageLineInt line = (ImageLineInt) pngr.readRow();
      pngw.writeRow(line);
    }
    pngr.end();
    pngw.end();

    var metadata = ImageMetadataReader.readMetadata(dest.toFile());
    var dirs = metadata.getDirectories();
    dirs.forEach(dir -> {
      System.out.println("Directory: " + dir.getClass().getSimpleName());
      dir.getTags().stream()
              .sorted(Comparator.comparing(Tag::getTagType))
              .forEach(tag -> System.out.printf("(%d) %s\n", tag.getTagType(), tag));
    });
//    var pngMetadataReader = PngMetadataReader.readMetadata(dest.toFile());
//    var pngDirs = pngMetadataReader.getDirectories();
//    pngDirs.forEach(dir -> {
//      System.out.println("PNG Directory: " + dir.getClass().getSimpleName());
//      dir.getTags().stream()
//              .sorted(Comparator.comparing(Tag::getTagType))
//              .forEach(tag -> System.out.printf("(%d) %s\n", tag.getTagType(), tag));
//    });
  }

  public static void showMetadata(Path path) throws ImageProcessingException, IOException {
    System.out.println("-".repeat(80));
    System.out.println("showMetadata: " + path);
    System.out.println("-".repeat(80));
    var metadata = ImageMetadataReader.readMetadata(path.toFile());
    var dirs = metadata.getDirectories().iterator();
    while (dirs.hasNext()) {
      var dir = dirs.next();
      System.out.println("Directory = " + dir.getClass().getSimpleName());
      dir.getTags().stream()
              .sorted(Comparator.comparing(Tag::getTagType))
              .forEach(tag -> System.out.printf("(%d) %s\n", tag.getTagType(), tag));
    }
    var date_file_system = Files.getLastModifiedTime(path).toInstant();
    var date_file_system2 = Files.getFileAttributeView(path, BasicFileAttributeView.class
    )
            .readAttributes().creationTime().toInstant();

    System.out.println("Modified Time (win10 file system) : "
            + LocalDateTime.ofInstant(date_file_system, ZoneId.systemDefault()));
    System.out.println("Creation Time (win10 file system) : "
            + LocalDateTime.ofInstant(date_file_system2, ZoneId.systemDefault()));

    PngReader pngr = new PngReader(path.toFile());
    System.out.println("PngReader getTime() : "
            + pngr.getMetadata().getTime());
  }
}
