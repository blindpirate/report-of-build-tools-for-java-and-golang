package com.github.blindpirate.gogradle.statistic

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.nio.file.Path
import java.nio.file.Paths

class GithubTopRankCrawler {
    static String TARGET_URL = 'https://api.github.com/search/repositories?q=stars%3A%3E1+language%3A${language}&sort=stars&order=desc&type=Repositories&page=${page}&per_page=100'
    static String TOP_JSON = 'top.json'

    static void main(String[] args) {
        if (args.length < 2) {
            println 'lauguage and location must be specified!'
            return
        }
        String language = args[0]
        Path baseDir = Paths.get(args[1])
        getTop1000(language, baseDir).each {
            cloneOne(baseDir, it.full_name, it.clone_url)
        }
    }

    static List getTop1000(String language, Path baseDir) {
        File topDotJson = baseDir.resolve(TOP_JSON).toFile()
        if (topDotJson.exists()) {
            return new JsonSlurper().parseText(topDotJson.getText())
        } else {
            String url = TARGET_URL.replace('${language}', language)
            List allItems = (1..10).collect({ it ->
                String json = new URL(url.replace('${page}', it.toString())).getText()
                return new JsonSlurper().parseText(json).items
            }).flatten()
            topDotJson.write(JsonOutput.toJson(allItems))
            return allItems
        }
    }

    // a/b  https://github.com/a/b.git
    static void cloneOne(Path baseDir, String fullName, String cloneUrl) {
        Path location = baseDir.resolve(fullName.replaceAll(/\//, '_'))
        if (location.toFile().exists()) {
            println("${fullName} exists, skip.")
            return
        }
        runInheritIO(['git', 'clone', '--depth', '1', '--shallow-submodules', cloneUrl, location.toAbsolutePath().toString()], [:])
    }

    static void runInheritIO(List<String> args, Map<String, String> envs) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder().command(args).inheritIO()
        pb.environment().putAll(envs)
        pb.start().waitFor()
    }

}

