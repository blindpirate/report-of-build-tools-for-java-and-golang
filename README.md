# A Survey on Build Tools of Golang and Java

# Java

## Conclusion

In January 2017, the usage of build tools in [Github's top 1000 Java repositories](http://github-rank.com/star?language=Java) is as follows:

| Tool Name     | Reference Count  |
| ------------- | :-----:|
|Gradle|627|
|Maven|264|
|Ant|52|
|Npm|4|
|Bazel|3|
|Make|1|

And the trending over the past 8 years is:

![trending](https://raw.githubusercontent.com/blindpirate/report-of-build-tools-for-java-and-golang/master/trending.png)

## Algorithm

- Clone top 1000 Java repositories to local disk
- Analyze the repositories by identity files:

| Tool Name     |Identity Files|
| ------------- |:-----:|
|Gradle |build.gradle|
|Maven |pom.xml|
|Ant |build.xml|
|Npm |package.json|
|Bazel |BUILD|
|Make |Makefile/makefile|

## How 

- Make sure [Git](https://git-scm.com/)/[Groovy 2.4+](http://www.groovy-lang.org/download.html)/[JDK 1.7+](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) are installed.
- Run `groovy GithubTopRankCrawler.groovy -l java -d <path to store the 1000 repos>` to clone all repositories locally. 
- Run `groovy JavaBuildToolScanner.groovy -d <path to store the 1000 repos>` to analyze these repos.

# Golang

## Conclusion
There are various package management tools for golang as listed [here](https://github.com/golang/go/wiki/PackageManagementTools). But which one is the most popular? 

The usage of package manage tools in [Github's top 1000 Go repositories](http://github-rank.com/star?language=Go) is as follows:

| Tool Name     |Url           | Reference Count (Feb 2017) | Reference Count (Nov 2017) |
| ------------- |:-------------:| :-----------------:|:-----------------:|
|Makefile|[Makefile]()|199|181|
|dep|[dep](https://github.com/golang/dep)|N/A|94|
|godep|[godep](https://github.com/tools/godep)|119|90|
|govendor|[govendor](https://github.com/kardianos/govendor)|65|84|
|glide|[glide](https://github.com/Masterminds/glide)|64|77|
|gvt|[gvt](https://github.com/FiloSottile/gvt)|25|16|
|trash|[trash](https://github.com/rancher/trash)|7|13|
|submodule|[submodule]()|8|6|
|gpm/johnny-deps|[gpm](https://github.com/pote/gpm) [johnny-deps](https://github.com/VividCortex/johnny-deps)|7|6|
|glock|[glock](https://github.com/robfig/glock)|5|4|
|gom|[gom](https://github.com/mattn/gom)|4|2|
|gopack|[gopack](https://github.com/d2fn/gopack)|3|2|
|gopm|[gopm](https://github.com/gpmgo/gopm)|3|1|
|goop|[goop](https://github.com/nitrous-io/goop)|1|1|
|gvend|[gvend](https://github.com/govend/govend)|2|0|

[dep](https://github.com/golang/dep) had a first release in May 2017, did not exist for first stats.

Technically, `make` is not a package management tool, here it is just for comparison.

Submodule refers to a set of tools which use [git submodule](https://git-scm.com/docs/git-submodule) to manage dependencies such as [manul](https://github.com/kovetskiy/manul) and [Vendetta](https://github.com/dpw/vendetta) and so on.

## Algorithm

- Clone top 1000 Go repositories to local disk
- Analyze the repositories by identity files:

| Tool Name     |Identity Files|
| ------------- |:-----:|
|godep |Godeps/Godeps.json|
| govendor |vendor/vendor.json|
|gopm|.gopmfile|
| gvt |vendor/manifest|
| gvend |vendor.yml|
| glide |glide.yaml or glide.lock|
| trash |vendor.conf|
| gom | Gomfile |
| bunch | bunchfile |
| goop | Goopfile |
| goat |.go.yaml|                     
| glock | GLOCKFILE |
| gobs |goproject.json|
| gopack |gopack.config|
| nut |Nut.toml|
|gpm/johnny-deps| Godeps |
| Makefile |makefile or Makefile|
| submodule |.gitmodules|

## How

- Make sure [Git](https://git-scm.com/)/[Groovy 2.4+](http://www.groovy-lang.org/download.html)/[JDK 1.7+](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) are installed.
- Run `groovy GithubTopRankCrawler.groovy -l go -d <path to store the 1000 repos>` to clone all repositories locally. You can use `-s` to do the shallow clone and decrease disk usage.
- Run `groovy GoBuildToolScanner.groovy <path to store the 1000 repos>` to analyze these repos.




