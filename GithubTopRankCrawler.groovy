import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.nio.file.Paths

class GithubTopRankCrawler {
    static String TARGET_URL = 'https://api.github.com/search/repositories?q=stars%3A%3E1+language%3A${language}&sort=stars&order=desc&type=Repositories&page=${page}&per_page=100'
    static String TOP_JSON = 'top.json'

    static void main(String[] args) {

        def cli = new CliBuilder(usage: 'groovy GithubTopRankCrawler <options>')
        cli.l(longOpt: 'language', args: 1, 'specify a language, e.g. go/java')
        cli.s(longOpt: 'shallow', 'use shallow clone')
        cli.d(longOpt: 'dir', args: 1, 'specify the target directory')

        def options = cli.parse(args)

        if (!options) {
            return
        }
        if (!options.l && !options.s && !options.d) {
            cli.usage()
            return
        }

        if (!options.l) {
            println 'language must be specified!'
            return
        }
        if (!options.d) {
            println 'target directory must be specified!'
            return
        }
        File baseDir = Paths.get(options.d).toFile()
        if (!baseDir.exists()) {
            baseDir.mkdir()
        }
        getTop1000(options.l, baseDir).each {
            cloneOne(baseDir, it.full_name, it.clone_url, options.s)
        }
    }

    static List getTop1000(String language, File baseDir) {
        File topDotJson = new File(baseDir, TOP_JSON)
        if (topDotJson.exists()) {
            println("${topDotJson} exists, use it.")
            return new JsonSlurper().parseText(topDotJson.getText())
        } else {
            println("Fetching repository list...")
            String url = TARGET_URL.replace('${language}', language)
            List allItems = (1..10).collect({ it ->
                println("Fetching page ${it} ")
                String json = new URL(url.replace('${page}', it.toString())).getText()
                return new JsonSlurper().parseText(json).items
            }).flatten()
            topDotJson.write(JsonOutput.toJson(allItems))
            return allItems
        }
    }

    // a/b  https://github.com/a/b.git
    static void cloneOne(File baseDir, String fullName, String cloneUrl, boolean shallow) {
        File location = new File(baseDir, fullName.replaceAll(/\//, '_'))
        if (new File(location, '.git').exists()) {
            println("${fullName} exists, skip.")
            return
        } else {
            location.mkdir()
        }
        if (shallow) {
            runInheritIO(['git', 'clone', '--depth', '1', cloneUrl, location.absolutePath])
        } else {
            runInheritIO(['git', 'clone', cloneUrl, location.absolutePath])
        }
    }

    static void runInheritIO(List<String> args) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder().command(args).inheritIO()
        pb.start().waitFor()
    }
}

