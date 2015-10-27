//----------------------------------------------------------------------------
// W A R N I N G :
//----------------------------------------------------------------------------
// In order to compile this file on Windows, you will need the following 
// environment variable creating:
//
// set JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF8"
//
// This source file contains UTF8 characters for those damned smart quotes,
// which strangely, Microsoft insist on using in Word etc, but their system
// runs in CP1252 by default!
//
// If your "javac *.java" compile fails with this error:
//
//      C:\Software\Wiki>javac Convert.java
//      Convert.java: error: unmappable character for encoding Cp1252
//                    aQuote = thisLine.indexOf("ÔÇ?");
//                                                     ^
// Then you must set the Env Var as above, either permanently in Control
// Panel -> System, or in the session itself as per the above command.
//----------------------------------------------------------------------------
// It's possible that Java 1.6 (Java 6) will not compile this file. I haven't
// tested it to see, I only have Java 1.7 (aka 7).
//----------------------------------------------------------------------------

//----------------------------------------------------------------------------
// L I C E N C E:
//----------------------------------------------------------------------------
// The MIT License (MIT)
// 
// Copyright (c) 2015 Norman Dunbar
// 
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//----------------------------------------------------------------------------

//----------------------------------------------------------------------------
// A U T H O R:
//----------------------------------------------------------------------------
// Norman @ Dunbar - it . co . uk
//----------------------------------------------------------------------------

//----------------------------------------------------------------------------
// P U R P O S E:
//----------------------------------------------------------------------------
// A class to:
//
// Convert a DokuWiki page, saved from the browser as HTML source, to get rid 
// of the crud, and hopefully, extract the relevant information. 
// The HTML input file should have been saved from the browser using the "view
// source" feature and the resulting source saved as an HTML file. It can then
// be stuffed through this abysmal piece of Java code to extract the wheat
// the chaff. :-)
//----------------------------------------------------------------------------

//----------------------------------------------------------------------------
// B R I E F   D E S C R I P T I O N:
//----------------------------------------------------------------------------
// Open the passed file for reading in UTF8 mode.
// Scan through it, writing to System.out until we find <\title>.
// Write out </head> and <body>.
// Keep scanning but ignore everything until we reach "<!-- TOC END -->".
// Keep scanning and write everything out until we hit "<!-- wikipage stop -->".
// Advise the user if any image files are required.
// Convert any damned "smart" quotes (E2809C and E2809D) to &quot;
// Close the <body> and <html> tags.
// Done.
//----------------------------------------------------------------------------

import java.io.*;
import java.util.Date;


public class ConvertHTML {

    // Globals - sometimes are necessary!
    int     lineNumber = 0;         // Which line am I on?
    String  thisLine = null;        // A line of HTML from the input file.
    
    private void errMsg(String message) throws Exception {
        // Writes a message to System.err.
        System.err.println(message);
    }

    private void outMsg(String message) throws Exception {
        // Writes a message to System.out.
        System.out.println(message);
    }

    public boolean writeUntilWeFind(BufferedReader br, String lookFor)  throws Exception {
        // Reads & writes lines until we find one that INCLUDES the
        // passed parameter. There's no need to trim first here.

        while ((thisLine = br.readLine()) != null) {
            lineNumber++;

            outMsg(thisLine);
            if (thisLine.trim().contains(lookFor)) {
                errMsg("Found it at line " + lineNumber + ".");
                return true;
            }
        }
        
        // Oops, we didn't find what we came here for.
        errMsg("ERROR: Cannot find \"" + lookFor + "\".");
        return false;
    }
    

    public boolean readUntilWeFind(BufferedReader br, String lookFor)  throws Exception {
        // Reads lines until we find one that STARTSWITH the
        // passed parameter AFTER triming leading whitespace off of course.

        while ((thisLine = br.readLine()) != null) {
            lineNumber++;
            if (thisLine.trim().startsWith(lookFor)) {
                // We are done.
                errMsg("Finished deleting at line " + lineNumber + ".");
                return true;
            }
        }

        // Oops, we didn't find what we came here for.
        errMsg("ERROR: Cannot find \"" + lookFor + "\".");
        return false;
    }
    

    public boolean jfdi(String fileName) throws Exception 
    {
        //=================================================================
        // This is the bit that does all the work. Call jfdi() and pass a
        // filename to convert the file's HTML into some text on System.out
        // with logging messages on System.err.
        //=================================================================

        String  tempLine;               // Temp buffer to extract image file names.
        String  imgFile = null;         // Image file name.

        try
        {

            //---------------------------------------------------------------------------
            // So, I want to open a text file that's in UTF8 format, how hard can it be?
            // This hard ...
            //---------------------------------------------------------------------------
            BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileName), "UTF8")
            );


            //---------------------------------------------------------------------------
            // Scan until we reach the </title> tag. Write out each line as we go.
            //---------------------------------------------------------------------------
            errMsg("Reading: " + fileName + "...");
            errMsg("Looking for the \"</title>\" tag....");

            if (!writeUntilWeFind(br, "</title")) {
                errMsg("Cannot continue.");
                return false;
            }

            outMsg("</head>");
            outMsg("<!--\n" +
                    "     File tidied up using \"DokuWiki-Cleaner\" utility\n" +
                    "     which strips out the cruft we don't need in a saved\n" +
                    "     DokuWiki HTML file.\n");
            outMsg("     Did I mention? It's on GitHub at \n" +
                    "     https://github.com/NormanDunbar/DokuWiki-Cleaner \n" +
                    "-->");
            outMsg("<body>");

            
            //---------------------------------------------------------------------------
            // We are in the <body>.
            // Ignore everything until we reach the <!-- TOC END --> line. 
            //---------------------------------------------------------------------------
            errMsg("Deleting down to \"<!-- TOC END -->\"");

            if (!readUntilWeFind(br, "<!-- TOC END")) {
                errMsg("Cannot continue.");
                return false;
                }

            //---------------------------------------------------------------------------
            // Now we can copy the meat & gravy of the information to the output file!
            //---------------------------------------------------------------------------
            errMsg("Copying down to \"<!-- wikipage stop -->\"....");

            while ((thisLine = br.readLine()) != null) {
                lineNumber++;

                //---------------------------------------------------------------------------
                // Remove those pesky "edit" buttons...
                //---------------------------------------------------------------------------
                if (thisLine.trim().startsWith("<div class=\"secedit\"")) {
                    continue;
                }


                //---------------------------------------------------------------------------
                // Warn if we find an image within an <a> tag.
                //---------------------------------------------------------------------------
                int imgTag = thisLine.indexOf("<img ");
                if (imgTag > -1) {
                    //-----------------------------------------------------------------------
                    // We have an image. Find the source...
                    //-----------------------------------------------------------------------
                    errMsg("Found <img> tag at line " +
                            lineNumber + ", column " + imgTag);

                    //-----------------------------------------------------------------------
                    // We are doing it this way because there might be more than one
                    // src= or class= in the line. We are only interested in those we
                    // see AFTER the initial <img tag. Trust me, not all <img tags are
                    // the same.
                    // Normally an image tag is embedded in an <a> tag, like this:
                    // <a href="..." class="media" title="..."><img ...></a>
                    //-----------------------------------------------------------------------
                    tempLine = thisLine;
    
                    int srcTag = tempLine.substring(imgTag).indexOf("src=\"");
                    if (srcTag > -1) {
                        srcTag += imgTag;
                        int classTag = tempLine.substring(srcTag).indexOf("class=");
                        if (classTag > -1) {
                            // Extract the filename...
                            classTag += srcTag;

                            //---------------------------------------------------------------
                            // Trim off the leading slash from the image path. It's causing
                            // the resulting HTML to be absolute path, we want relative paths
                            // to images now.
                            //---------------------------------------------------------------
                            imgFile = "\"" + tempLine.substring(srcTag + 6, classTag).trim();

                            //---------------------------------------------------------------
                            // Now, change the line we write out to use relative paths.
                            //---------------------------------------------------------------
                            thisLine = thisLine.substring(0, srcTag) +
                                       "src=" + imgFile + " " +
                                       thisLine.substring(classTag) ;
                        }
                        //-------------------------------------------------------------------
                        // And inform the user about it.
                        //-------------------------------------------------------------------
                        errMsg("\tThe image file is " + imgFile);

                        //-------------------------------------------------------------------
                        // And write a suitable comment to the output HTML file.
                        //-------------------------------------------------------------------
                        outMsg("<!-- I M A G E   F I L E   " + imgFile +
                                "   R E Q U I R E D   H E R E -->");

                        //-------------------------------------------------------------------
                        // Now, does the line with the image begin with an <a> tag? If so, 
                        // strip it out.
                        //-------------------------------------------------------------------
                        if (thisLine.trim().startsWith("<a")) {
                            thisLine = thisLine.substring(thisLine.indexOf("<img "));
                            thisLine = thisLine.substring(0, thisLine.indexOf("</a>"));
                        }
                    } else {
                        //-------------------------------------------------------------------
                        // How strange, an image tag with no src attribute.
                        //-------------------------------------------------------------------
                        errMsg("\tThe image file has no src attribute!");
                    }
                }
                

                //---------------------------------------------------------------------------
                // Check if we are done yet?
                //---------------------------------------------------------------------------
                if (thisLine.trim().startsWith("<!-- wikipage stop")) {
                    break;
                }

                //---------------------------------------------------------------------------
                // Not done yet, copy this line out. However, we need to remove
                // the Wiki's insistence on using so called "smart" quotes. These
                // appear as a single character, but are actually three!
                //---------------------------------------------------------------------------
                int aQuote = thisLine.indexOf("“");  // E2 80 9C
                if (aQuote > -1) {
                    thisLine = thisLine.substring(0, aQuote) +
                               "&quot;" +
                                thisLine.substring(aQuote + 1);
                }
                
                aQuote = thisLine.indexOf("”");  // E2 80 9D
                if (aQuote > -1) {
                    thisLine = thisLine.substring(0, aQuote) +
                               "&quot;" +
                                thisLine.substring(aQuote + 1);
                }
                outMsg(thisLine);
            }

            errMsg("Finished copying at line " + lineNumber + ".");

            outMsg("\n<!--\n    Page created from DokuWiki HTML by DokuWiki-Cleaner on : " +
                    new Date().toString() + "\n-->" );

            outMsg("</body>");
            outMsg("</html>");

        } catch (Exception e) {
            // Looks like something went belly up!
            e.printStackTrace();
            return false;
        }

        // Looks like everything was fine.
        return true;
    }
}
