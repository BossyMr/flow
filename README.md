# Flow

Flow is a tool for analyzing the data flow of a program.

Flow splits code into snapshots, where each snapshot represents the program state at a specific point in the programs'
execution. A snapshot can be queried, and will return the range of all possible results returned by an expression.  

## Support

Flow depends on CVC5 and as such needs to be packaged with bindings matchings the current system and
architecture. Flow is currently only packaged with bindings for macOS.