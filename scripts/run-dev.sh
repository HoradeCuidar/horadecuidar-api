#!/usr/bin/env bash

set -Eeuo pipefail

project_root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
compose_file="$project_root/anotacoes/docker-compose.yaml"
env_file="$project_root/.env"

if ! command -v java >/dev/null 2>&1; then
    echo "Erro: Java não está disponível no PATH." >&2
    exit 1
fi

java_major_version="$(java -version 2>&1 | sed -n 's/.*version "\([0-9][0-9]*\).*/\1/p' | head -n 1)"
if [[ "$java_major_version" != "25" ]]; then
    echo "Erro: Java 25 é obrigatório; versão ativa: ${java_major_version:-desconhecida}." >&2
    exit 1
fi

if ! command -v docker >/dev/null 2>&1; then
    echo "Erro: Docker não está disponível no PATH." >&2
    exit 1
fi

if [[ ! -f "$env_file" ]]; then
    echo "Erro: arquivo .env não encontrado em $env_file." >&2
    exit 1
fi

if [[ ! -x "$project_root/mvnw" ]]; then
    echo "Erro: Maven Wrapper não encontrado ou sem permissão de execução." >&2
    exit 1
fi

echo "Java 25 detectado. Iniciando PostgreSQL e pgAdmin..."
docker compose --env-file "$env_file" -f "$compose_file" up -d

echo "Iniciando a aplicação Spring Boot..."
cd "$project_root"
exec ./mvnw spring-boot:run
