Xanthic
=======

_This repo contains an early prototype. I'm in the midst of completing my PhD and will get back to this project once I've defended._

An ANTLR4 based syntax highlighter written in Java.

### Goals
The goal of this project is to provide a syntax highlighting for [RichTextFX](https://github.com/TomasMikula/RichTextFX/) similar to [pygments](http://pygments.org). 

Features slated for the first version are:
* A RichTextFX formatter
* Support for a common set of languages with grammars in the antlr4 grammar repo.
* Function to convert pygments stylesheets to JavaFX style sheets.

### Ways to contribute
* Implement a `Highlighter` for your favourite language.
* Implement `Formatter` for your favourite type of output
