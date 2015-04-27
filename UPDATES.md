# DokuWiki-Cleaner

Cleans up the HTML created when you save a DokuWiki generated page from the browser.

Converts a DokuWiki page, saved from the browser as HTML source, to get rid of the crud, and hopefully, extract the relevant information. 

The HTML input file should have been saved from the browser using the "view source" feature and the resulting source saved as an HTML file. It can then be stuffed through this abysmal piece of Java code to extract the wheat the chaff. 


# Updates


27 April 2015		Updated to refactor the main code.
				Added some common routines.
				Added new source file ConverHTML.java.
				A few spelling mistakes were fixed.
				Images are no longer still embedded in ```<a>``` tags and thus, no longer attempt to act as links to a non-existent URL/URI.
				Updated README.md with new improved compiling instructions.
***
17 April 2015		Initail Version.
***
