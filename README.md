<div align="center">

<h1 align="center">
  <img src="./src/main/resources/logo.png" width="86"/><br/>
  FileWatch
</h1>

<b>Your filesystem, on autopilot.</b>

<br/>

<p align="center">
Have you ever downloaded an image only to find it's in <code>.webp</code> or some obscure format?<br/>
Then you end up reuploading it to some conversion website — and redownloading it all over again?<br/>
<b>FileWatch</b> solves that problem without any of that hassle.
</p>

</div>

## How?

FileWatch watches for specified folders, such as your downloads folder or another accessible folder, and when events like a file is created is triggered, it
looks if there is a rule that can match. If there is, it will run the specified action, such as converting that file, deleting it, and much more.

Everything stays on your computer and only you get to decide what to watch and gets ran.

## Chores

Here are some things that are planned for the future

- [ ] Lua scripting to allow for custom conditions to be met as well as specifying jobs without having to use a GUI medium to create them.
- [ ] Parallellism for running jobs in parallel without blocking and to also reduce job runtime.
- [ ] More thorough error catching and showing them to the user when certain fields are not met for `VisualBuilder`s
