# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [0.7.1] - 2202-06-12
### Added
- [#58](https://github.com/javahippie/clj-test-containers/issues/58): Add new `allowInsecure` flag to the HTTP wait strategy

### Changed
- [#57](https://github.com/javahippie/clj-test-containers/issues/57): Upgrade to testcontainers-java 1.17.2

## [0.7.0] - 2022-04-23
### Changed
- [#56](https://github.com/javahippie/clj-test-containers/issues/56): Update to Testcontainers 1.17.1

## [0.6.0] - 2022-03-26
### Changed
- [#55](https://github.com/javahippie/clj-test-containers/issues/55): Upgrade to latest Testcontainers version

### Added
- [#42](https://github.com/javahippie/clj-test-containers/issues/42): Extend wait strategies
- [#41](https://github.com/javahippie/clj-test-containers/issues/41): Complete logging mechanisms

## [0.5.0] - 2021-08-18
### Changed
- [#49](https://github.com/javahippie/clj-test-containers/issues/49): Updated to latest Testcontainers version
- [#50](https://github.com/javahippie/clj-test-containers/issues/50): supress Reflection warnings
- [#52](https://github.com/javahippie/clj-test-containers/pull/52): Fix unbalanced parens and braces

## [0.4.0] - 2020-12-16
### Added
- [#43](https://github.com/javahippie/clj-test-containers/issues/43): Provide a way to remove all running testcontainers instances in a REPL session

### Changed
- [#40](https://github.com/javahippie/clj-test-containers/issues/40): Increase Testcontainers version to 1.15.0 
- [#47](https://github.com/javahippie/clj-test-containers/issues/47): Increase Testcontainers version to 1.15.1
- [#44](https://github.com/javahippie/clj-test-containers/pull/44): Use .copyFileToContainer when the container is already started 

## [0.3.0] - 2020-10-23
### Added
- [#25](https://github.com/javahippie/clj-test-containers/issues/25): Add support for a container wait strategy
- [#35](https://github.com/javahippie/clj-test-containers/issues/35): Add support for docker version 2.4.0 - upgrading testcontainers-java version
- [#27](https://github.com/javahippie/clj-test-containers/issues/27): Add support for accessing container logs
- [#38](https://github.com/javahippie/clj-test-containers/pull/38): Add type hints to silence reflection warnings
- [#33](https://github.com/javahippie/clj-test-containers/pull/33): Add more options to the HTTP wait strategy
- [#28](https://github.com/javahippie/clj-test-containers/pull/28): Link to the java lib
- [#23](https://github.com/javahippie/clj-test-containers/pull/23): Configure clojure.spec, spec out a few functions
- [#24](https://github.com/javahippie/clj-test-containers/pull/24): cljstyle redux, with pre-commit hook
- [#21](https://github.com/javahippie/clj-test-containers/pull/21): Install and configure cljstyle

### Changed
- [#29](https://github.com/javahippie/clj-test-containers/issues/29): init-network should be called create-network!

## [0.2.0] - 2020-08-05
### Added
- [#2](https://github.com/javahippie/clj-test-containers/issues/2): Enable configuration of networking
- [#8](https://github.com/javahippie/clj-test-containers/issues/8): Improve Documentation for cljdoc
- [#15](https://github.com/javahippie/clj-test-containers/issues/15): Add a way to create an Image from a Dockerfile
- [#17](https://github.com/javahippie/clj-test-containers/issues/17): Enable the library to use predefined Containers
- [#18](https://github.com/javahippie/clj-test-containers/issues/18): Reuse more code between `create`, `init` and `create-from-dockerfile`
- [#19](https://github.com/javahippie/clj-test-containers/issues/19): Add Code of Conduct

### Changed
- [#9](https://github.com/javahippie/clj-test-containers/issues/9): using `:keys [......]`
- [#13](https://github.com/javahippie/clj-test-containers/issues/13): Update Git Configuration
- [#20](https://github.com/javahippie/clj-test-containers/issues/20): Release Milestone 0.2.0


## [0.1.0] - 2020-06-06
### Added
- [#1](https://github.com/javahippie/clj-test-containers/issues/1): Enable minimal GenericContainer usage - Set up a test pipeline
- [#3](https://github.com/javahippie/clj-test-containers/issues/3): Enable configuration of volumes
- [#4](https://github.com/javahippie/clj-test-containers/issues/4): Set up a test pipeline
- [#5](https://github.com/javahippie/clj-test-containers/issues/5): Set up clojars deployment
- [#6](https://github.com/javahippie/clj-test-containers/issues/6): Enable command execution in a running container
- [#7](https://github.com/javahippie/clj-test-containers/issues/7): Switch from Boot to Leiningen

