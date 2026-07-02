# Files

File, URI, and document-provider access facades belong here. Sandbox code should receive explicit capabilities instead of raw broad storage access.

This module currently provides `AppFileGateway`, a Hilt-bound facade for app-private file roots,
path traversal-safe file resolution, temporary files, FileProvider content URIs, and URI text IO.
