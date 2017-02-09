class JavaBuildToolScanner {
    static List<Tool> JAVA_TOOLS = [new Tool(name: 'Maven', identityFiles: ['pom.xml']),
                                    new Tool(name: 'Ant', identityFiles: ['build.xml']),
                                    new Tool(name: 'Gradle', identityFiles: ['build.gradle']),
                                    new Tool(name: 'Npm', identityFiles: ['package.json']),
                                    new Tool(name: 'Bazel', identityFiles: ['BUILD']),
                                    new Tool(name: 'Make', identityFiles: ['Makefile', 'makefile'])]

    static main(String[] args) {
        def cli = new CliBuilder(usage: 'groovy GithubTopRankCrawler <options>')
        cli.s(longOpt: 'start', args: 1, 'start date, yyyy-MM-dd')
        cli.e(longOpt: 'end', 'end date yyyy-MM-dd')
        cli.d(longOpt: 'dir', args: 1, 'specify the target directory')
        cli.i(longOpt: 'interval-days', args: 1, 'the interval in days')

        def options = cli.parse(args)
        if (!options) {
            return
        }

        if (!options.d) {
            println('target directory must be specified!')
            cli.usage
            return
        }

        def start = getStartDate(options)
        def end = getEndDate(options)
        def interval = getInterval(options)


        File allProjects = new File(options.d)
        List<File> subdirs = allProjects.listFiles().findAll { it.isDirectory() }

        LinkedHashMap<Date, Map<Tool, Integer>> result = BuildToolScanner.scan(JAVA_TOOLS, sample(start, end, interval), subdirs)
        println(toCsv(result))
    }

    static toCsv(LinkedHashMap<Date, Map<Tool, Integer>> result) {
        StringBuilder sb = new StringBuilder()
        writeHeader(sb)
        result.each { key, value ->
            writeRow(sb, key, value)
        }
        return sb.toString()
    }

    static writeHeader(StringBuilder sb) {
        sb.append('Date,')
        JAVA_TOOLS.each {
            sb.append(it.name).append(',')
        }
        sb.append('\n')
    }

    static writeRow(StringBuilder sb, Date date, Map<Tool, Integer> resultOnOneDay) {
        sb.append(date.format('yyyy-MM-dd')).append(',')
        JAVA_TOOLS.each {
            sb.append(resultOnOneDay[it] ?: 0).append(',')
        }
        sb.append('\n')
    }

    static getStartDate(options) {
        def start = options.s
        if (!start) {
            println('start date is set to 2000-01-01!')
            start = '2000-01-01'
        }
        return Date.parse('yyyy-MM-dd', start)
    }

    static getEndDate(options) {
        def end = options.e
        if (!end) {
            println("end date is set to ${new Date().format('yyyy-MM-dd')}!")
            end = new Date().format('yyyy-MM-dd')
        }
        return Date.parse('yyyy-MM-dd', end)
    }

    static getInterval(options) {
        def interval = options.i
        if (!interval) {
            println("interval is set to 365!")
            interval = 365
        }
        return interval.toInteger()
    }

    static List<Date> sample(Date start, Date end, int interval) {
        List ret = []
        Date current = start
        while (current < end) {
            ret.add(current)
            current += interval
        }
        return ret
    }
}