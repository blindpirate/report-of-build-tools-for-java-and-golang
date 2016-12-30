package com.github.blindpirate.gogradle.statistic

import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

class GithubGoProjectScanner {
    static Map tools = [godep            : [files: 'Godeps/Godeps.json', url: 'https://github.com/tools/godep'],
                        govendor         : [files: 'vendor/vendor.json', url: 'https://github.com/kardianos/govendor'],
                        gopm             : [files: '.gopmfile', url: 'https://github.com/gpmgo/gopm'],
                        gvt              : [files: 'vendor/manifest', url: 'https://github.com/FiloSottile/gvt'],
                        gvend            : [files: 'vendor.yml', url: 'https://github.com/govend/govend'],
                        glide            : [files: 'glide.yaml|glide.lock', url: 'https://github.com/Masterminds/glide'],
                        trash            : [files: 'vendor.conf', url: 'https://github.com/rancher/trash'],
                        gom              : [files: 'Gomfile', url: 'https://github.com/mattn/gom'],
                        bunch            : [files: 'bunchfile', url: 'https://github.com/dkulchenko/bunch'],
                        goop             : [files: 'Goopfile|Goopfile.lock', url: 'https://github.com/nitrous-io/goop'],
                        goat             : [files: '.go.yaml', url: 'https://github.com/mediocregopher/goat'],
                        glock            : [files: 'GLOCKFILE', url: 'https://github.com/robfig/glock'],
                        gobs             : [files: 'goproject.json', url: 'https://bitbucket.org/vegansk/gobs'],
                        gopack           : [files: 'gopack.config', url: 'https://github.com/d2fn/gopack'],
                        nut              : [files: 'Nut.toml', url: 'https://github.com/jingweno/nut'],
                        'gpm/johnny-deps': [files: 'Godeps', url: ['https://github.com/pote/gpm', 'https://github.com/VividCortex/johnny-deps']],
                        Makefile         : [files: 'makefile|Makefile'],
                        submodule        : [files: '.gitmodules']
    ]

    static void main(String[] args) {
        if(args.length == 0){
            println 'the location must be specified!'
            return
        }
        Map result = [:]
        File allProjects = Paths.get(args[0]).toFile()
        int i = 0
        allProjects.eachDir { project ->
            Set tools = processOneProject(project)
            tools.each { toolName ->
                if (result[toolName] == null) {
                    result[toolName] = 1
                } else {
                    result[toolName] += 1;
                }
            }
            i++
            print("\rFinished:${i}")
        }
        println('\n' + result)
        println('Markdown:\n' + toMarkdownTable(result))
    }

    static String toMarkdownTable(Map map) {
        List sortedEntries = map.entrySet().toList().sort { entry1, entry2 ->
            entry2.value - entry1.value
        }

        List rows = sortedEntries.collect { entry ->
            String name = entry.key
            String url = getUrls(entry.key)
            String referenceCount = entry.value
            return "|${name}|${url}|${referenceCount}|"
        }

        return rows.join('\n')
    }

    static String getUrls(String name) {
        def urls = tools[name].url
        if (urls instanceof List) {
            return urls.withIndex().collect({ url, index ->
                def names = name.split(/\//)
                return "[${names[index]}](${url})"
            }).join(" ")
        } else if (urls == null) {
            return ''
        } else {
            return "[${name}](${urls})"
        }
    }
    // return manage tool name used by this project
    static Set processOneProject(File projectDir) {
        Set result = tools.keySet().findAll {
            List files = tools[it].files.split(/\|/)

            return files.any { fileName ->
                File file = projectDir.toPath().resolve(fileName).toFile()
                return file.exists() && file.isFile()
            }
        }

        // Here we cannot use groovy's traverse/eachFileRecurse
        // because they will follow symbol links, which may result in infinite loops
        Files.walkFileTree(projectDir.toPath(),
                [] as Set,
                100,
                new SimpleFileVisitor<Path>() {
                    @Override
                    FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (file.toFile().name == '.gitmodules') {
                            result.add('submodule')
                        }
                        return FileVisitResult.CONTINUE
                    }
                })

        return result

    }

}
