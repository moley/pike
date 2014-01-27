File file = new File ("installPike.bat.orig")
File outputfile = new File ("installPike.bat")
outputfile.text = file.text.replaceAll('\n', '\n\r')

File file2 = new File ("configureHost.bat.orig")
File outputfile2 = new File ("configureHost.bat")
outputfile2.text = file2.text.replaceAll('\n', '\n\r')
