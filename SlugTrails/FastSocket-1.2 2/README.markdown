FastSocket
===============

Description
---------------

A fast, synchronous Objective-C wrapper around BSD sockets for iOS and OS X.
Send and receive raw bytes over a socket as fast as possible. Includes methods
for transferring files while optionally computing a checksum for verification.

Use this class if fast network communication is what you need. If you want to
do something else while your network operations finish, then an asynchronous
API might be better.

For more information, please visit the [project homepage](http://github.com/dreese/fast-socket).

Download
---------------

Download the [latest release](https://github.com/dreese/fast-socket/releases) of FastSocket or try the [nightly version](https://github.com/dreese/fast-socket/archive/master.zip).

Examples
---------------

Create and connect a client socket.

	FastSocket *client = [[FastSocket alloc] initWithHost:@"localhost" andPort:@"34567"];
	[client connect];

Send a file.

	long sent = [client sendFile:@"/tmp/filetosend.txt"];

Receive a file of a given length.

	long received = [client receiveFile:@"/tmp/newlyreceivedfile.txt" length:1024];

Send raw bytes.

	char data[] = {42};
	long sent = [client sendBytes:data count:1];

Receive available raw bytes up to the given limit.

	char data[42];
	long received = [client receiveBytes:data limit:42];

Receive the exact number of raw bytes given.

	char data[1000];
	long received = [client receiveBytes:data count:1000];

Close the connection.

	[client close];

Please check out the unit tests for more examples of how to use these classes.

Release Notes
---------------

2014 Feb 3 — v1.2

	• Added -[FastSocket connect:] method for specifying a connection timeout, which is separate from the read/write timeout.
	• Added CocoaPod support with new podspec file.

2013 Oct 3 — v1.1

	• Converted to ARC.
	• Added -[FastSocket isConnected] method.
	• Added -[FastSocket receiveBytes:count:] method for receiving an exact number of bytes. This differs from -[FastSocket receiveBytes:limit:] in that the new method waits for the given number of bytes is received, or a timeout, before returning.
	• Added header documentation for use in Xcode 5.

2012 Jun 24 — v1.0

	• Initial release.

Creator
---------------

[Daniel Reese](http://www.danandcheryl.com/)  
[@dreese](http://twitter.com/dreese)

License
---------------

FastSocket is available under the [MIT license](http://opensource.org/licenses/MIT).
