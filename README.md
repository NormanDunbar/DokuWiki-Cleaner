# DokuWiki-Cleaner

Cleans up the HTML created when you save a DokuWiki generated page from the browser.

Converts a DokuWiki page, saved from the browser as HTML source, to get rid of the crud, and hopefully, extract the relevant information. 

The HTML input file should have been saved from the browser using the "view source" feature and the resulting source saved as an HTML file. It can then be stuffed through this abysmal piece of Java code to extract the wheat the chaff. 


# WARNING

In order to compile this file on Windows, you will need the following environment variable creating:

	set JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF8"

The main java source file contains UTF8 characters for those damned smart quotes, which strangely, Microsoft insist on using in Word etc, but their system runs in CP1252 by default!

If your "javac" compile fails with this error:

    C:\Software\Wiki>javac Convert.java
    Convert.java: error: unmappable character for encoding Cp1252
                       aQuote = thisLine.indexOf("???");

Then you must set the Env Var as above, either permanently in Control Panel -> System, or in the session itself as per the above command.

It's possible that Java 1.6 (Java 6) will not compile this file. I haven't tested it to see, I only have Java 1.7 (aka 7).


# USAGE

	java Convert.java File_name.html > output.filename.html
or

	java Convert.java File_name.html > output.filename.html 2>filename.log


# LICENCE

MIT Licence. See LICENSE file in the source code:

Copyright (c) 2015 Norman Dunbar

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.



# AUTHOR

Norman @ Dunbar - it . co . uk  (You know the drill, remove the spaces.)


#CAVEATS

- Images need to be saved from the Wiki Page with the same name as per the message(s) output to the log file (on standard error) or console. The Wiki insists on absolute paths to images, why not, but I prefer to have mine relative to the source HTML file, so that's how it is. By default images are expected to be in the _media subdirectory beneath where the converted HTML page is located.
- Smilies. The Wiki keeps those in a different place. Check the output messages to find where you need to put yours, if you can be bothered! I personally, can't be! (Bothered.)
- Other media files - I have no idea, I don't use them!
- If future versions of DokuWiki change the end of section comments, we could be up that famous creek, without a canoe, never mind a paddle!
- There is no CSS extracted. All the class names, ID names etc remain in the converted HTML. This MIGHT be useful, perhaps.
- External links remain as external links. This means, they still work!
- Internal (to other Wiki pages I mean) will most likely no longer work.
- Older versions of DokuWiki didn't always put a class on <img tags. Sigh. In these cases, you will still get advised, but the filename will be "null", but you can always check it and see what is expected.  
- Also, those older versions didn't use URLs for some images, they used a "fetch.php" function call to get the image. Another sigh.



# BRIEF DESCRIPTION

Open the passed file for reading in UTF8 mode.
Scan through it, writing to System.out until we find <\title>.
Write out ```</head>``` and ```<body>``` tags.
Keep scanning but ignore everything until we reach ```"<!-- TOC END -->"```.
Keep scanning and write everything out until we hit ```"<!-- wikipage stop -->"```.
Advise the user if any image files are required.
Convert any damned "smart" quotes (E2809C and E2809D) to ```&quot;```.
Close the```<body>``` and ```<html>``` tags.
Done.



# BY THE WAY

I *never* said this would be the world's most efficient Java code, did I?
This is only my third ever Java program, and Java doesn't do efficient. :-)


# COMPILING

Assuming you have a Java development environment set up, it's a simple case of:

    cd DokuWiki-Cleaner
    javac Convert.java

Or, on Windows:

    cd DokuWiki-Cleaner
    set JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF8"
    javac Convert.java


# TESTING

There is a sample HTML file taken from my own website. The file, if you are interested, describes the hard disc format for a Sinclair QL hard disc. To convert it, proceed as follows:

    java Convert test.html >test.out.html 

or

    java Convert test.html >test.out.html  2>test.html.log

In the former case, informational messages will scroll up the screen, while in the latter case, they will be written to the file test.html.log - from where you can examine them to see if any images etc need to be dealt with.

There are two "LOL" images :-) in the original file, but these are not supplied. They can be edited out of the generated code at the line numbers indicated.

You should see something like the following:

    java Convert test.html >test.out.html

    Reading: test.html...
    Looking for the "</title>" tag....
    Found it at line 7.
    Deleting down to "<!-- TOC END -->"
    Finished deleting at line 80.
    Copying down to "<!-- wikipage stop -->"....
    Found <img> tag at line 778, column 285
    	The image file is "lib/images/smileys/icon_lol.gif"
    Found <img> tag at line 1024, column 166
    	The image file is "lib/images/smileys/icon_lol.gif"
    Finished copying at line 1130.

