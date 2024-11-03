# Minimal Bug Sample

- Run the tests in `minimal-sample`, they pass
- Start the actual application and navigate to `/resource/foo.txt`. You get an empty response.

Loading resources like this is just a minimal example to show that `respondeSource` behaves differently in tests and in the actual application.

`call.respondBytes(source.readByteArray(), contentType)` actually works in both cases.
