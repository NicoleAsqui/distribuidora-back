#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
# Carga .env sin `source` (falla si hay espacios sin comillas, ej. nombres con Guayaquil).
eval "$(python3 - <<'PY'
from pathlib import Path
import shlex
for raw in Path(".env").read_text().splitlines():
    line = raw.strip()
    if not line or line.startswith("#") or "=" not in line:
        continue
    k, v = line.split("=", 1)
    k = k.strip()
    v = v.strip()
    if (len(v) >= 2) and ((v[0] == v[-1] == '"') or (v[0] == v[-1] == "'")):
        v = v[1:-1]
    print(f"export {k}={shlex.quote(v)}")
PY
)"
exec ./mvnw spring-boot:run
