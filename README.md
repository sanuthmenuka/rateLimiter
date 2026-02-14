<h1 style="font-size: 56px; margin-bottom: 8px;">Rate Limiter</h1>

<p align="center">
  <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/redis/redis-original.svg" alt="Redis" width="120" />
</p>

<h2 align="center">Overview</h2>

A minimal distributed rate limiter built with Spring Boot and Redis.
It protects API endpoints from excessive traffic using a fixed-window policy with low overhead.

<h2 align="center">Functionality</h2>

- Applies rate limiting on `/api/**` requests.
- Uses Redis atomically (`INCR` + `EXPIRE` via Lua) for distributed counters.
- Returns standard headers:
  - `X-Ratelimit-Limit`
  - `X-Ratelimit-Remaining`
  - `X-Ratelimit-Retry-After`
- Returns HTTP `429` when the limit is exceeded.
- Uses `X-Client-Id` as identity key (falls back to client IP).
- Fails open if Redis is unavailable, so API traffic is not fully blocked.

<h2 align="center">Basic Installation</h2>

1. Clone the repository.
2. Ensure Java 21 and Maven are available.
3. Start Redis locally (default: `localhost:6379`).
4. Run the app:

```bash
./mvnw spring-boot:run
```

5. Test endpoint:

```bash
curl -i -H "X-Client-Id: user-1" http://localhost:8080/api/ping
```
