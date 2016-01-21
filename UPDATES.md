# DokuWiki-Cleaner

Cleans up the HTML created when you save a DokuWiki generated page from the browser.

Converts a DokuWiki page, saved from the browser as HTML source, to get rid of the crud, and hopefully, extract the relevant information. 

The HTML input file should have been saved from the browser using the "view source" feature and the resulting source saved as an HTML file. It can then be stuffed through this abysmal piece of Java code to extract the wheat from the chaff. 


# Updates

**21 January 2016**

- Added UPDATES.html to the repository. There always was an UPDATES.md, just not an html version.
- The README.html instructions somehow missed out the section on how to compile. That has been added.
- The usage instructions were a pile of poo! They said to run the `Convert.java` file. Rubbish, you don't run a java source file!

**27 October 2015**

- More refactoring of the ConvertHTML class.
- Convert itself needed to check that parameters were passed.
- Now System.exit(0) if all ok, else System.exit(1).

**27 April 2015**

- Updated to refactor the main code.
- Added some common routines.
- Added new source file ConverHTML.java.
- A few spelling mistakes were fixed.
- Images are no longer still embedded in ```<a>``` tags and thus, no longer attempt to act as links to a non-existent URL/URI.
- Updated README.md with new improved compiling instructions.

**17 April 2015**

- Initial Version.

