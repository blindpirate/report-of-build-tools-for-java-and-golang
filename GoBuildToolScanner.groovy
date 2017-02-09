

class GoBuildToolScanner {
    static Map tools = [:]

    static {
        tools.put(new Tool(name: 'godep', identityFiles: ['Godeps/Godeps.json']), 'https://github.com/tools/godep')
        tools.put(new Tool(name: 'govendor', identityFiles: ['vendor/vendor.json']), 'https://github.com/kardianos/govendor')
        tools.put(new Tool(name: 'gopm', identityFiles: ['.gopmfile']), 'https://github.com/gpmgo/gopm')
        tools.put(new Tool(name: 'gvt', identityFiles: ['vendor/manifest']), 'https://github.com/FiloSottile/gvt')
        tools.put(new Tool(name: 'gvend', identityFiles: ['vendor.yml']), 'https://github.com/govend/govend')
        tools.put(new Tool(name: 'glide', identityFiles: ['glide.yaml|glide.lock']), 'https://github.com/Masterminds/glide')
        tools.put(new Tool(name: 'trash', identityFiles: ['vendor.conf']), 'https://github.com/rancher/trash')
        tools.put(new Tool(name: 'gom', identityFiles: ['Gomfile']), 'https://github.com/mattn/gom')
        tools.put(new Tool(name: 'bunch', identityFiles: ['bunchfile']), 'https://github.com/dkulchenko/bunch')
        tools.put(new Tool(name: 'goop', identityFiles: ['Goopfile|Goopfile.lock']), 'https://github.com/nitrous-io/goop')
        tools.put(new Tool(name: 'goat', identityFiles: ['.go.yaml']), 'https://github.com/mediocregopher/goat')
        tools.put(new Tool(name: 'glock', identityFiles: ['GLOCKFILE']), 'https://github.com/robfig/glock')
        tools.put(new Tool(name: 'gobs', identityFiles: ['goproject.json']), 'https://bitbucket.org/vegansk/gobs')
        tools.put(new Tool(name: 'gopack', identityFiles: ['gopack.config']), 'https://github.com/d2fn/gopack')
        tools.put(new Tool(name: 'nut', identityFiles: ['Nut.toml']), 'https://github.com/jingweno/nut')
        tools.put(new Tool(name: 'gpm/johnny-deps', identityFiles: ['Godeps']), ['https://github.com/pote/gpm', 'https://github.com/VividCortex/johnny-deps'])
        tools.put(new Tool(name: 'Makefile', identityFiles: ['makefile', 'Makefile']), '')
        tools.put(new Submodule(name: 'submodule'), '')
        tools.put(BuildToolScanner.OTHER_TOOL, '')
    }

    static main(String[] args) {
        if (args.length == 0) {
            println 'the location must be specified!'
            return
        }

        File allProjects = Paths.get(args[0]).toFile()
        List<File> subdirs = allProjects.listFiles().findAll { it.isDirectory() }

        Map<Tool, Integer> result = BuildToolScanner.scan(tools.keySet() as List, null, subdirs)

        println('Markdown:\n' + toMarkdownTable(result))
    }

    static String toMarkdownTable(Map map) {
        List sortedEntries = map.entrySet().toList().sort { entry1, entry2 ->
            entry2.value - entry1.value
        }

        List rows = sortedEntries.collect { entry ->
            String name = entry.key.name
            String url = getUrls(entry.key)
            String referenceCount = entry.value
            return "|${name}|${url}|${referenceCount}|"
        }

        return rows.join('\n')
    }

    static String getUrls(Tool tool) {
        def urls = tools[tool]
        if (urls instanceof List) {
            return urls.withIndex().collect({ url, index ->
                def names = tool.name.split(/\//)
                return "[${names[index]}](${url})"
            }).join(" ")
        } else if (urls == null) {
            return ''
        } else {
            return "[${tool.name}](${urls})"
        }
    }


    static class Submodule extends Tool {
        boolean match(File dir) {

        }
    }
}
