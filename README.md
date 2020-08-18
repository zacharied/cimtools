# cimtools

[![Release](https://img.shields.io/github/v/release/zacharied/cimtools.svg)](https://github.com/zacharied/cimtools/releases)

Convert to and from LR2 CIM skin image files.

A CIM file is just a LibGDX pixmap, dumped to a file. This tool simply opens the given file, loads it with LibGDX, and
writes the data of that image to the appropriate file format.

## Usage

`cimtools [<file> ...]` where `<file>` is a file of type CIM or PNG.

The `--to-cim` and `--from-cim` flags can be given if the files do not have extensions for cimtools to use to detect
filetype.

The `--preview` flag can be given to show the converted image in a window. Press Space to iterate through each converted
image.

## Known issues

Converting these images requires creating an OpenGL context, and in order to do that with LibGDX, a window has to be
spawned. Thus, even when `--preview` is not specified, a blank window will pop up very briefly
while conversion takes place. The LibGDX `headless` backend does **not** fix this issue, since running headless avoids
creating the OGL context entirely.