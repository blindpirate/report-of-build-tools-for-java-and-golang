import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

@groovy.transform.ToString
class Tool {
    String name
    List<String> identityFiles

    boolean match(File dir) {
        boolean existInProjectRoot = identityFiles.any { new File(dir, it).exists() }
        if (existInProjectRoot) {
            return true
        } else {
            return findRecursively(dir)
        }
    }

    boolean findRecursively(File dir) {
        boolean findIt = false
        Files.walkFileTree(dir.toPath(),
                [] as Set,
                100,
                new SimpleFileVisitor<Path>() {
                    @Override
                    FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) throws IOException {
                        if (identityFiles.any { Files.exists(path.resolve(it), LinkOption.NOFOLLOW_LINKS) }) {
                            findIt = true
                            return FileVisitResult.TERMINATE
                        }
                        return FileVisitResult.CONTINUE
                    }
                })
        return findIt
    }
}
