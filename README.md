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


## Requirements for Use ##

Java 8 runtime; ffmpeg on the path (I’m working with 3.0.2 from Homebrew).


## Requirements for Building, Testing ##

Java 8 SDK; ffmpeg on the path (I’m working with 3.0.2 from Homebrew);
gradle 3.1+ required for Kotlin 1.1 M02 Gradle build scripts.


## Contributing ##

Please get in touch before writing code let alone creating a PR; if I’m not
interested in merging any particular change, I’d like not to waste your time.
