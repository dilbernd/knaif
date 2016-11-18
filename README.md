# Knaïf: A Tool for Cutting Video, Born of Total Inexperience #

Knaïf cuts sequences out of videos using ffmpeg.

I use it to cut videos of my talks – to remove prolonged digressions,
audience questions that weren’t picked up by the microphone, …

I can’t even into multimedia and this seems like the easiest thing I could do.
I’m grateful for suggestions and contributions.


## Invocation ##

```
java -jar knaif.jar \
  <input video file> <cut directions file> <output video file>
```

## Cut directions format ##

Each line is a duration to cut from the video, in seconds, with a dash between
start and end. Seconds can be specified as a simple number of seconds, or as
colon-separated hours-minutes-seconds `[[hh:]mm]:ss`. Notations can be mixed in
a file, and even within lines.

```
5-30
10:00-12:31
13:15-800
22:33-22:41
```

* The specified ranges will be cut out from the beginning of the second
  specified by the first number, to the end of the second specified by the
  second number. 
* Unless a range starting from 0 is specified as a range to cut, the output
  video will start at 0 seconds until the first specified range.  
* In the same way, the end of the last range until the end of the video will be
  kept.

No validation/merging for overlapping or invalid ranges will be done. Values
over Int.MAX_VALUE will probably fail, in case you need videos well over 60
years in duration. Rejoice, though: Inverted (end before start) and empty ranges
– beginning and starting with the same specified second – will be filtered out
though.

Note the definition above: Since a cut range is removed from the start of the
start second to the end of the end second, and ranges with the same start and
end time are removed, it is not actually possible to cut out one-second ranges
with this tool. 

(Technical Note: Values including colons will simply split along the colons. For
each part, it will be added to the already processed parts; if another part is
encountered, the accumulated value is multiplied by 60 and the next part is
added, and so on. The following are perfectly valid time values: 1:2:3:4:5,
meaning 13403045 seconds and 1:2054:13, meaning 126853. I wouldn’t recommend
using that – it will drive you mad.)


## Requirements for Use ##

Java 8 runtime; ffmpeg on the path (I’m working with 3.0.2 from Homebrew).


## Requirements for Building, Testing ##

Java 8 SDK; ffmpeg on the path (I’m working with 3.0.2 from Homebrew);
gradle 3.1+ required for Kotlin 1.1 M02 Gradle build scripts.


## Contributing ##

Please get in touch before writing code let alone creating a PR; if I’m not
interested in merging any particular change, I’d like not to waste your time.
