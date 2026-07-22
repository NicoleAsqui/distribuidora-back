# Distribuidora Guayaquil — Backend

API REST en **Spring Boot 4 / Java 21** con **arquitectura hexagonal**, lista para **Google Cloud Run** (Developer Connect + Dockerfile) y base de datos **Neon PostgreSQL**.

## Arquitectura

```
domain/          → modelos + puertos (in/out)
application/     → casos de uso (servicios)
infrastructure/
  adapter/in/web → controllers REST (driving)
  adapter/out/persistence → JPA + Neon (driven)
  config/        → CORS, auth, seed
```

## Requisitos

- Java 21+
- Cuenta Neon (proyecto `distribuidora-guayaquil` ya creado)
- Docker (opcional, para Cloud Run)

## Configuración local

```bash
cp .env.example .env
# Edita DATABASE_* con tu connection string de Neon
```

Exporta variables y arranca:

```bash
export $(grep -v '^#' .env | xargs)
./mvnw spring-boot:run
```

Escucha en `$PORT` (por defecto `8080`), requerido por Cloud Run.

## Endpoints principales

| Método | Ruta | Auth | Uso |
|--------|------|------|-----|
| GET | `/api/health` | No | Health check Cloud Run |
| GET | `/api/products` | No | Catálogo público (`?top=true`) |
| GET | `/api/products/{ref}` | No | Detalle producto |
| POST | `/api/orders` | No | Pedido desde carrito |
| POST | `/api/quotes` | No | Nueva cotización |
| POST | `/api/auth/login` | No | Login admin → token |
| GET/PUT | `/api/admin/catalog/**` | Bearer | Categorías, materiales, colores, acabados, tags, modelos |
| GET/PUT | `/api/quotes`, `/api/orders` | Bearer | Panel admin |
| GET/PUT | `/api/config` | GET público / PUT admin | WhatsApp, email, maps |

Login demo (igual que el frontend): `admin` / `admin123`

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin123"}'
```

Usa el token: `Authorization: Bearer <token>`

## Docker / Cloud Run

```bash
docker build -t distribuidora-back .
docker run -p 8080:8080 \
  -e PORT=8080 \
  -e DATABASE_URL=... \
  -e DATABASE_USERNAME=... \
  -e DATABASE_PASSWORD=... \
  distribuidora-back
```

En **Cloud Run → Developer Connect**:

1. Repositorio de código fuente: este repo
2. Tipo de compilación: **Dockerfile**
3. Ubicación: `/Dockerfile`
4. Variables de entorno: `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `ADMIN_*`, `CORS_ALLOWED_ORIGINS`
5. El contenedor escucha automáticamente en `$PORT`

## Frontend

Apunta el frontend a la URL del servicio, por ejemplo:

```
VITE_API_URL=https://tu-servicio-xxxx.run.app
```
