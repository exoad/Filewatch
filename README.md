<div align="center">

<h1 align="center">
  <img src="./src/main/resources/logo.png" width="86"/><br/>
  FileWatch
</h1>

<b>Your filesystem, on autopilot.</b>

<br/>

<p align="center">
Have you ever downloaded an image only to find it's in <code>.webp</code> or another obscure format?<br/>
Then you're stuck reuploading it to a conversion website — and redownloading it again?<br/>
<b>FileWatch</b> eliminates that hassle by automating the process entirely.
</p>

</div>

---

## How It Works

FileWatch monitors specified folders — such as your Downloads folder — for real-time file system events. When a new file
is added, renamed, or modified, it checks against your defined rules. If a match is found, FileWatch automatically
performs the corresponding action, such as converting the file, moving it, renaming it, or deleting it.

Everything happens locally on your machine. You control what gets watched and what actions are triggered — nothing
leaves your system.

Additionally, the interface is written using Java Swing due to its easy integration with the dependence on the JVM.
---

## Roadmap

Planned features and improvements:

- [ ] **Lua plugin/scripting support** — Define custom conditions and job logic without relying solely on a GUI.
- [ ] **Parallel job execution** — Improve performance by running multiple jobs concurrently.
- [ ] **Improved error handling** — Better diagnostics and user feedback when rule definitions are incomplete or
  invalid, especially within `VisualBuilder`s.
