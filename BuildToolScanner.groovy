class BuildToolScanner {

    static OTHER_TOOL = new Tool(name: 'other')

    static LinkedHashMap<Date, Map<Tool, Integer>> scan(List<Tool> tools, List<Date> dates, List<File> projectDirs) {
        return dates.collectEntries { date ->
            [date, scanOneDate(tools, date, projectDirs)]
        }
    }

    static Map<Tool, Integer> scanOneDate(List<Tool> tools, Date date, List<File> projectDirs) {
        Map<Tool, Integer> counts = [:]
        projectDirs.each { dir ->
            if (date != null) {
                if (!checkoutToThatDate(dir, date)) {
                    // this repo does not exist on that date
                    return
                }
            }
            def matchedTool = tools.find { tool ->
                tool.match(dir)
            }

            def result = matchedTool ?: OTHER_TOOL
            println("Build tool of ${dir} is: ${result}")
            if (counts[result] == null) {
                counts[result] = 0
            }
            counts[result]++
        }

        return counts
    }

    // true if check out succeed, false otherwise
    static boolean checkoutToThatDate(File dir, Date date) {
        String startDate = date.format('yyyy-MM-dd HH:mm:ss')
        String defaultBranch = getDefaultBranch(dir)
        String rev = run(['git', 'rev-list', '-n', '1', "--before='${startDate}'".toString(), defaultBranch], dir).trim()
        if (!rev) {
            println "rev of ${dir} doesn't exist on ${startDate}"
            return false
        } else {
            try {
                println("checking out ${dir} to ${rev}")
                run(['git', 'checkout', rev], dir)
            } catch (IllegalStateException e) {
                // HEAD is now at xxxx
            }
            return true
        }
    }

    static String getDefaultBranch(File dir) {
        String branch = run(['git', 'branch'], dir)
        // * master
        //
        // or
        //
        // * (HEAD detached at 6d32f52)
        // master
        List lines = branch.split('\n') as List
        return lines.collect { it.replace('*', '').trim() }.find { !it.contains('detached at') }
    }

    static String run(List<String> args, File dir) {
        ProcessBuilder pb = new ProcessBuilder().command(args)
        pb.directory(dir)
        pb.start().waitFor()
        String stdout = pb.start().inputStream.getText('UTF8')
        String stderr = pb.start().errorStream.getText('UTF8')
        if (stderr) {
            throw new IllegalStateException("stderr:${stderr}")
        }
        return stdout
    }

}