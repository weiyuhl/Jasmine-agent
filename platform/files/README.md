# Files

File, URI, and document-provider access facades belong here. Sandbox code should receive explicit capabilities instead of raw broad storage access.

This module currently provides `AppFileGateway`, a Hilt-bound facade for app-private file roots,
path traversal-safe file resolution, temporary files, FileProvider content URIs, and URI text IO.

Storage policy:

- Prefer app-specific internal/external directories for Agent runtime files; these do not require
  broad storage permissions.
- Use Storage Access Framework URIs for user-selected documents and persist URI permissions when
  long-lived access is required.
- Keep raw filesystem paths behind this module so sandbox and feature code receive explicit file or
  URI capabilities.
