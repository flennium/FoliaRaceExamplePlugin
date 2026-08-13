# Real-server example runs

These artifacts were produced by running this plugin with FoliaRace on Java 25 and the available Folia server builds.

| Folia | Build | Exit code | Finding groups |
| --- | ---: | ---: | ---: |
| 1.21.6 | 6 | 0 | 2 |
| 1.21.8 | 6 | 0 | 4 |
| 1.21.11 | 14 | 0 | 9 |

Each run used `run-unsafe-examples: true` so the ownership and asynchronous-access findings are visible in the report. The server was started with the FoliaRace agent and stopped through the server console after the example completed.

Run-specific output:

- [Folia 1.21.6 build 6](1.21.6-6/)
- [Folia 1.21.8 build 6](1.21.8-6/)
- [Folia 1.21.11 build 14](1.21.11-14/)

The server logs are retained to show the startup, plugin execution, detector output, and clean shutdown. Reports may contain server or plugin details, so remove operational secrets before sharing them elsewhere.
